
#ifndef __SOCKETS_H__
#define __SOCKETS_H__

typedef enum socket_mode {
    UDP,
    TCP,
    BLUETOOTH,
    UNKNOWN
} socket_mode_e;

static const char *g_socket_mode_names[] = {
    "UDP",
    "TCP",
    "BLUETOOTH",
    "UNKNOWN"
};

int startup_sockets_mechanism();
void cleanup_sockets_mechanism();

#endif
