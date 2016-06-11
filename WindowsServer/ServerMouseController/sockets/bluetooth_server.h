
#ifndef __BLUETOOTH_SERVER_H__
#define __BLUETOOTH_SERVER_H__

#include <WinSock2.h>

// {B62C4E8D-62CC-404b-BBBF-BF3E3BBB1374}
DEFINE_GUID(g_guidServiceClass, 0xb62c4e8d, 0x62cc, 0x404b, 0xbb, 0xbf, 0xbf, 0x3e, 0x3b, 0xbb, 0x13, 0x74);

#define DEFAULT_LISTEN_BACKLOG        4
#define INSTANCE_STR L"BluetoothWindows"

SOCKET accept_client_connection(SOCKET server_socket);
SOCKET setup_bluetooth_server();

#endif
