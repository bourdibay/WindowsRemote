cmake_minimum_required(VERSION 2.8.11)

if(POLICY CMP0020)
    cmake_policy(SET CMP0020 NEW)
endif()
if(POLICY CMP0043)
    cmake_policy(SET CMP0043 NEW)
endif()

add_subdirectory(Jsmn)
add_subdirectory(RingBuffer)
