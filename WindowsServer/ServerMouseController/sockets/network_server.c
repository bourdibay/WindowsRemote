
#include <stdio.h>

#include "network_server.h"
#include "errors/error_handling.h"
#include "sockets.h"

static SOCKET create_socket(socket_mode_e socket_mode)
{
    SOCKET sock;

    switch (socket_mode)
    {
    case UDP:
        sock = socket(AF_INET, SOCK_DGRAM, 0);
        break;
    case TCP:
        sock = socket(AF_INET, SOCK_STREAM, 0);
        break;
    default:
        // TODO: error
        return INVALID_SOCKET;
    }

    if (sock == INVALID_SOCKET)
    {
        print_error(GetLastError());
        return INVALID_SOCKET;
    }
    return sock;
}

static SOCKADDR_IN get_socket_configuration()
{
    SOCKADDR_IN sin = { 0 };
    sin.sin_addr.s_addr = htonl(INADDR_ANY);
    sin.sin_family = AF_INET;
    sin.sin_port = htons(PORT);
    printf("Listening on port %d\n", PORT);
    return sin;
}

static BOOL server_bind(SOCKET socket, const SOCKADDR_IN *sin)
{
    if (bind(socket, (SOCKADDR *)sin, sizeof(*sin)) == SOCKET_ERROR)
    {
        print_error(GetLastError());
        return FALSE;
    }
    return TRUE;
}

static BOOL server_listen(SOCKET socket)
{
    if (listen(socket, 1) == SOCKET_ERROR)
    {
        print_error(GetLastError());
        return FALSE;
    }
    return TRUE;
}

static SOCKET server_accept(SOCKET socket, SOCKADDR_IN *cin)
{
    SOCKET csocket;
    int sinsize = sizeof(*cin);

    csocket = accept(socket, (SOCKADDR *)cin, &sinsize);
    if (csocket == INVALID_SOCKET)
    {
        print_error(GetLastError());
        return INVALID_SOCKET;
    }
    return csocket;
}

static BOOL setup_socket_server(SOCKET server_socket)
{
    SOCKADDR_IN sin;

    sin = get_socket_configuration();
    if (server_socket == INVALID_SOCKET)
    {
        return FALSE;
    }

    if (server_bind(server_socket, &sin) == FALSE)
    {
        closesocket(server_socket);
        return FALSE;
    }
    return TRUE;
}

static SOCKET setup_udp_tcp_server(socket_mode_e mode)
{
    SOCKET server_socket;
    BOOL ret = FALSE;

    server_socket = create_socket(mode);
    if (server_socket == INVALID_SOCKET) {
        return FALSE;
    }
    ret = setup_socket_server(server_socket);
    return ret ? server_socket : INVALID_SOCKET;
}

SOCKET setup_udp_server()
{
    return setup_udp_tcp_server(UDP);
}

SOCKET setup_tcp_server()
{
    SOCKET client_socket;
    struct sockaddr_in client;
    SOCKET server_socket = setup_udp_tcp_server(TCP);

    if (server_socket == INVALID_SOCKET) {
        return INVALID_SOCKET;
    }
    if (server_listen(server_socket) == FALSE) {
        closesocket(server_socket);
        return INVALID_SOCKET;
    }
    client_socket = server_accept(server_socket, &client);
    if (client_socket == INVALID_SOCKET) {
        closesocket(server_socket);
        return INVALID_SOCKET;
    }
    return client_socket;
}
