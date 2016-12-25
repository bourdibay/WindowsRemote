cmake_minimum_required(VERSION 2.8.11)

if(POLICY CMP0020)
    cmake_policy(SET CMP0020 NEW)
endif()
if(POLICY CMP0043)
    cmake_policy(SET CMP0043 NEW)
endif()
if(POLICY CMP0020)
    cmake_policy(SET CMP0054 NEW)
endif()

project(UnitTests)

# Avoid issue with GTest which is compiled as MTd in debug
set(CMAKE_CXX_FLAGS_DEBUG "/MTd")
set(CMAKE_CXX_FLAGS_RELEASE "/MT")

################################
# GTest
################################
ADD_SUBDIRECTORY (./thirdparty/gtest-1.7.0)
enable_testing()
include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})
include_directories(../ServerMouseController/)

if (("${CMAKE_CXX_COMPILER_ID}" STREQUAL "Clang") OR
   ("${CMAKE_CXX_COMPILER_ID}" STREQUAL "GNU"))
    set( CMAKE_CXX_FLAGS "-W -Wall -Wextra")
endif()

################################
# Tree
################################
SET(src_test "test_ring_buffer.cpp")
ADD_EXECUTABLE(UnitTests ${src_test})
set_property(TARGET UnitTests PROPERTY CXX_STANDARD 11)

TARGET_INCLUDE_DIRECTORIES(UnitTests PUBLIC ../lib/)
TARGET_LINK_LIBRARIES(UnitTests gtest_main RingBuffer Jsmn Ws2_32)
ADD_DEPENDENCIES(UnitTests ServerMouseController)
