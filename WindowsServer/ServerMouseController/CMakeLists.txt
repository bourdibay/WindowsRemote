cmake_minimum_required(VERSION 2.8)

if(POLICY CMP0020)
    cmake_policy(SET CMP0020 NEW)
endif()
if(POLICY CMP0043)
    cmake_policy(SET CMP0043 NEW)
endif()

project(ServerMouseController)

# only way to add headers in generated solution
file(GLOB headers "*.h" "errors/*.h" "sockets/*.h")
file(GLOB sources 
"errors/error_handling.c"
"sockets/network_server.c"
"sockets/bluetooth_server.c"
"sockets/sockets.c"
"json_msg_parser.c"
"main_loop.c"
"mouse_controller.c"
"main.c")

ADD_EXECUTABLE(ServerMouseController
    ${sources}
    ${headers}
)

TARGET_INCLUDE_DIRECTORIES(ServerMouseController PUBLIC ./ ../lib/)

TARGET_LINK_LIBRARIES(ServerMouseController RingBuffer Jsmn Ws2_32)
