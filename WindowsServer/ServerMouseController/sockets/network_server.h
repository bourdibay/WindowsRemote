
#ifndef __NETWORK_SERVER_H__
#define __NETWORK_SERVER_H__

#include <WinSock2.h>

#define PORT 10500

SOCKET setup_udp_server();
SOCKET setup_tcp_server();

#endif
