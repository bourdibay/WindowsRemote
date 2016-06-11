
#include <WinSock2.h>
#include <stdio.h>
#include <initguid.h>
#include <ws2bth.h>
#include <strsafe.h>
#include <stdint.h>

#include "bluetooth_server.h"

static void print_error(char const *where, int code)
{
    fprintf(stderr, "Error on %s: code %d\n", where, code);
}

static BOOL bind_socket(SOCKET local_socket, SOCKADDR_BTH *sock_addr_bth_local)
{
    int addr_len = sizeof(SOCKADDR_BTH);

    /* Setting address family to AF_BTH indicates winsock2 to use Bluetooth port. */
    sock_addr_bth_local->addressFamily = AF_BTH;
    sock_addr_bth_local->port = BT_PORT_ANY;

    if (bind(local_socket, (struct sockaddr *) sock_addr_bth_local, sizeof(SOCKADDR_BTH)) == SOCKET_ERROR) {
        print_error("bind()", WSAGetLastError());
        return FALSE;
    }

    if (getsockname(local_socket, (struct sockaddr *)sock_addr_bth_local, &addr_len) == SOCKET_ERROR) {
        print_error("getsockname()", WSAGetLastError());
        return FALSE;
    }
    return TRUE;
}

static LPCSADDR_INFO create_addr_info(SOCKADDR_BTH *sock_addr_bth_local)
{
    LPCSADDR_INFO addr_info = calloc(1, sizeof(CSADDR_INFO));

    if (addr_info == NULL) {
        print_error("malloc(addr_info)", WSAGetLastError());
        return NULL;
    }

    addr_info[0].LocalAddr.iSockaddrLength = sizeof(SOCKADDR_BTH);
    addr_info[0].LocalAddr.lpSockaddr = (LPSOCKADDR)sock_addr_bth_local;
    addr_info[0].RemoteAddr.iSockaddrLength = sizeof(SOCKADDR_BTH);
    addr_info[0].RemoteAddr.lpSockaddr = (LPSOCKADDR)&sock_addr_bth_local;
    addr_info[0].iSocketType = SOCK_STREAM;
    addr_info[0].iProtocol = BTHPROTO_RFCOMM;
    return addr_info;
}

/*
instance_name is a pointer to wchar_t* which is malloc'ed by this function.
Must be free manually after.
*/
static BOOL advertise_service_accepted(LPCSADDR_INFO addr_info, wchar_t **instance_name)
{
    WSAQUERYSET wsa_query_set = { 0 };
    wchar_t computer_name[MAX_COMPUTERNAME_LENGTH + 1] = { 0 };
    DWORD len_computer_name = MAX_COMPUTERNAME_LENGTH + 1;
    size_t instance_name_size = 0;
    HRESULT res;

    if (!GetComputerName(computer_name, &len_computer_name)) {
        print_error("GetComputerName()", WSAGetLastError());
        return FALSE;
    }

    /*
    Adding a byte to the size to account for the space in the
    format string in the swprintf call. This will have to change if converted
    to UNICODE.
    */
    res = StringCchLength(computer_name, sizeof(computer_name), &instance_name_size);
    if (FAILED(res)) {
        print_error("ComputerName specified is too large", WSAGetLastError());
        return FALSE;
    }

    instance_name_size += sizeof(INSTANCE_STR) + 1;
    *instance_name = malloc(instance_name_size);
    if (*instance_name == NULL) {
        print_error("malloc(instance_name)", WSAGetLastError());
        return FALSE;
    }

    /* If we got an address, go ahead and advertise it. */
    ZeroMemory(&wsa_query_set, sizeof(wsa_query_set));
    wsa_query_set.dwSize = sizeof(wsa_query_set);
    wsa_query_set.lpServiceClassId = (LPGUID)&g_guidServiceClass;

    StringCbPrintf(*instance_name, instance_name_size, L"%s %s", computer_name, INSTANCE_STR);
    wsa_query_set.lpszServiceInstanceName = *instance_name;
    wsa_query_set.lpszComment = L"Example of server on Windows expecting bluetooth connections";
    wsa_query_set.dwNameSpace = NS_BTH;
    wsa_query_set.dwNumberOfCsAddrs = 1; /* Must be 1. */
    wsa_query_set.lpcsaBuffer = addr_info; /* Req'd */

                                           /*
                                           As long as we use a blocking accept(), we will have a race between advertising the service and actually being ready to
                                           accept connections.  If we use non-blocking accept, advertise the service after accept has been called.
                                           */
    if (WSASetService(&wsa_query_set, RNRSERVICE_REGISTER, 0) == SOCKET_ERROR) {
        free(instance_name);
        print_error("WSASetService()", WSAGetLastError());
        return FALSE;
    }
    return TRUE;
}

SOCKET setup_bluetooth_server()
{
    wchar_t * instance_name = NULL;
    SOCKET local_socket;
    SOCKADDR_BTH    sock_addr_bth_local = { 0 };
    LPCSADDR_INFO   addr_info = NULL;
    BOOL ret = FALSE;

    /* Open a bluetooth socket using RFCOMM protocol. */
    local_socket = socket(AF_BTH, SOCK_STREAM, BTHPROTO_RFCOMM);
    if (local_socket == INVALID_SOCKET) {
        print_error("socket()", WSAGetLastError());
        return INVALID_SOCKET;
    }

    ret = bind_socket(local_socket, &sock_addr_bth_local);
    if (!ret) {
        closesocket(local_socket);
        return INVALID_SOCKET;
    }
    addr_info = create_addr_info(&sock_addr_bth_local);
    if (!addr_info) {
        closesocket(local_socket);
        return INVALID_SOCKET;
    }
    ret = advertise_service_accepted(addr_info, &instance_name);
    if (!ret) {
        closesocket(local_socket);
        free(addr_info);
        if (instance_name) {
            free(instance_name);
        }
        return INVALID_SOCKET;
    }

    if (listen(local_socket, DEFAULT_LISTEN_BACKLOG) == SOCKET_ERROR) {
        print_error("listen()", WSAGetLastError());
        closesocket(local_socket);
        free(addr_info);
        free(instance_name);
        return INVALID_SOCKET;
    }

    /*
    free(addr_info);
    free(instance_name);
    closesocket(local_socket);
    */
    return local_socket;
}

SOCKET accept_client_connection(SOCKET server_socket)
{
    SOCKET client_socket;

    printf("Waiting for client connection...");
    client_socket = accept(server_socket, NULL, NULL);
    if (client_socket == INVALID_SOCKET) {
        print_error("accept()", WSAGetLastError());
        return FALSE;
    }
    printf("Client connected !\n");
    return client_socket;
}
