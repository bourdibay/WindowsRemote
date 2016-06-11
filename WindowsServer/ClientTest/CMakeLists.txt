cmake_minimum_required(VERSION 2.8.11)

if(POLICY CMP0020)
    cmake_policy(SET CMP0020 NEW)
endif()
if(POLICY CMP0043)
    cmake_policy(SET CMP0043 NEW)
endif()

project(ClientTest)

file(GLOB sources 
"../ServerMouseController/errors/error_handling.c"
"../ServerMouseController/json_msg_parser.c"
"../ServerMouseController/main_loop.c"
"../ServerMouseController/mouse_controller.c"
"../ServerMouseController/sockets/network_server.c"
"../ServerMouseController/utils/ring_buffer.c"
"../ServerMouseController/utils/ring_buffer_extra.c"
"../ServerMouseController/sockets/sockets.c"
"main.c")
file(GLOB headers "*.h" ../ServerMouseController/*.h ../ServerMouseController/*/*.h)

ADD_EXECUTABLE( ClientTest
    ${sources}
    ${headers}
)

TARGET_INCLUDE_DIRECTORIES(ClientTest PUBLIC ./ ../lib/ ../ServerMouseController)
TARGET_LINK_LIBRARIES(ClientTest RingBuffer Jsmn Ws2_32)
