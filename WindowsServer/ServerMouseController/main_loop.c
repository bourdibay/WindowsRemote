
#include <stdio.h>

#include "main_loop.h"
#include "sockets/network_server.h"
#include "json_msg_parser.h"
#include "mouse_controller.h"
#include "errors/error_handling.h"
#include <RingBuffer/ring_buffer.h>
#include <RingBuffer/ring_buffer_extra.h>

#define BUFFER_READ 100
#define BUFFER_CAPACITY (BUFFER_READ * 3)

static void interpret_message(broadcast_message_t *message)
{
#if defined _DEBUG
    printf("Message type to interpret = %d\n", message->type);
#endif
    if (message->type == MOVE) {
#if defined _DEBUG
        printf("Move the mouse\n");
#endif
        move_mouse(&message->delta_point.from_point, &message->delta_point.to_point);
    }
    else if (message->type == LEFT_DOWN_CLICK) {
        left_down();
    }
    else if (message->type == RIGHT_DOWN_CLICK) {
        right_down();
    }
    else if (message->type == LEFT_UP_CLICK) {
        left_up();
    }
    else if (message->type == RIGHT_UP_CLICK) {
        right_up();
    }
}

static BOOL receive_data(SOCKET *client_socket, ring_buffer_t *rb)
{
    char *buffer = NULL;
    int len_read = 0;

    buffer = calloc(BUFFER_READ, sizeof(char*));
    if (buffer == NULL) {
        print_error(WSAGetLastError());
        return FALSE;
    }

    len_read = recv(*client_socket, buffer, BUFFER_READ, 0);
    if (len_read == SOCKET_ERROR) {
        free(buffer);
        print_error(WSAGetLastError());
        return FALSE;
    }
    if (len_read == 0) {
        free(buffer);
        fprintf(stderr, "Nothing read, end of communication\n");
        return FALSE;
    }
#if defined _DEBUG
    printf("Received [%.*s] (%d / %d)\n", len_read, buffer, len_read, BUFFER_READ);
#endif
    push_data_in_ring_buffer(rb, buffer, len_read);
    free(buffer);
    return TRUE;
}

void run_main_loop(SOCKET *client_socket)
{
    broadcast_message_t previous_message = { 0 };
    broadcast_message_t message = { 0 };
    ring_buffer_t *rb = NULL;

    rb = create_ring_buffer(BUFFER_CAPACITY);
    if (rb == NULL) {
        fprintf(stderr, "Cannot allocate ring buffer\n");
        return;
    }

    printf("Running main loop...\n");
    while (1)
    {
#if defined _DEBUG
        printf("Reading over network...\n");
#endif
        if (!receive_data(client_socket, rb)) {
            break;
        }

        size_t len_msg = 1;
        while (len_msg > 0) {
            char *msg = extract_next_json_msg(rb, &len_msg);

            if (msg) {
#if defined _DEBUG
                printf("Got message [%s]\n", msg);
#endif
                if (parse_msg_json(msg, len_msg, &message) != 0) {
                    fprintf(stderr, "Fail parse json\n");
                }
                else {
                    if (!are_same_messages(&previous_message, &message))
                    {
#if defined _DEBUG
                        print_message(&previous_message);
                        print_message(&message);
#endif
                        interpret_message(&message);
                    }
                }
                previous_message = message;
                free(msg);
            }
        }

    }

    printf("Good bye, we leave the running loop !\n");
    delete_ring_buffer(rb);
}
