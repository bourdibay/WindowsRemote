
#include <stdio.h>
#include <stdlib.h>
#include <WinSock2.h>

#include "errors/error_handling.h"
#include "sockets/network_server.h"
#include "sockets/bluetooth_server.h"
#include "sockets/sockets.h"
#include "main_loop.h"

typedef struct cmd_args_s
{
    socket_mode_e socket_mode;
} cmd_args_t;

static void print_usage(char const * const exe_name)
{
    fprintf(stderr, "Usage: %s --socket [udp|tcp|bluetooth]\n", exe_name);
}

/*
TODO: rewrite correctly the argument line parsing.
*/
static BOOL parse_cmd_line(int argc, char * const * const argv, cmd_args_t *arguments)
{
    int i = 1;

    arguments->socket_mode = UNKNOWN;
    while (i < argc)
    {
        if (strcmp(argv[i], "--socket") == 0)
        {
            ++i;
            if (i < argc)
            {
                if (strcmp(argv[i], "udp") == 0) {
                    arguments->socket_mode = UDP;
                }
                else if (strcmp(argv[i], "tcp") == 0) {
                    arguments->socket_mode = TCP;
                }
                else if (strcmp(argv[i], "bluetooth") == 0) {
                    arguments->socket_mode = BLUETOOTH;
                }
                else {
                    fprintf(stderr, "Unknown argument for --socket option\n");
                    return FALSE;
                }
            }
        }
        ++i;
    }

    if (arguments->socket_mode == UNKNOWN) {
        fprintf(stderr, "No socket mode provided\n");
        return FALSE;
    }
    return TRUE;
}


static BOOL run_udp_server()
{
    SOCKET server_socket;

    server_socket = setup_udp_server();
    if (server_socket == INVALID_SOCKET)
    {
        return FALSE;
    }
    run_main_loop(&server_socket);
    closesocket(server_socket);
    return TRUE;
}

static BOOL run_tcp_server()
{
    SOCKET server_socket;

    server_socket = setup_tcp_server();
    if (server_socket == INVALID_SOCKET)
    {
        return FALSE;
    }
    run_main_loop(&server_socket);
    closesocket(server_socket);
    return TRUE;
}

static BOOL run_bluetooth_server()
{
    SOCKET server_socket;
    SOCKET client_socket;

    server_socket = setup_bluetooth_server();
    if (server_socket == INVALID_SOCKET)
    {
        return FALSE;
    }

    while (1)
    {
        client_socket = accept_client_connection(server_socket);
        if (client_socket == INVALID_SOCKET)
        {
            closesocket(server_socket);
            return FALSE;
        }

        run_main_loop(&client_socket);

        closesocket(client_socket);
    }
    return TRUE;
}


int main(int argc, char **argv)
{
    cmd_args_t arguments = { 0 };
    BOOL res = FALSE;

    res = parse_cmd_line(argc, argv, &arguments);
    if (!res) {
        print_usage(argv[0]);
        return EXIT_FAILURE;
    }

    printf("Socket type provided: %s\n", g_socket_mode_names[arguments.socket_mode]);

    if (startup_sockets_mechanism() < 0)
    {
        return EXIT_FAILURE;
    }

    switch (arguments.socket_mode)
    {
    case UDP:
        run_udp_server();
        break;
    case TCP:
        run_tcp_server();
        break;
    case BLUETOOTH:
        run_bluetooth_server();
        break;
    default:
        break;
    }


    cleanup_sockets_mechanism();
    return EXIT_SUCCESS;
}
