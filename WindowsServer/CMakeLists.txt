cmake_minimum_required(VERSION 2.8.11)

if(POLICY CMP0020)
    cmake_policy(SET CMP0020 NEW)
endif()
if(POLICY CMP0043)
    cmake_policy(SET CMP0043 NEW)
endif()

Project (WindowsRemote)

macro (set_flag_msvc regex flag)
  if(CMAKE_CXX_FLAGS MATCHES "${regex}")
    string(REGEX REPLACE "${regex}" ${flag} CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
  else()
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")
  endif()
endmacro (set_flag_msvc)

# Enable/disable optimization flags
IF(CMAKE_BUILD_TYPE MATCHES RELEASE)
  message(STATUS "Enable optimization flags for release")
  if (MSVC)
    set_flag_msvc("/O." "/Ox")
  endif()
ELSE()
  message(STATUS "Disable optimization flags for debug")
  if (MSVC)
    set_flag_msvc("/O." "/Od")
  endif()
ENDIF()

# For Windows, we explicitly set unicode to use wchar_t instead of char
add_definitions(-DUNICODE)

set(BUILD_DIRECTORY "${CMAKE_SOURCE_DIR}/builds")
set(EXECUTABLE_OUTPUT_PATH "${BUILD_DIRECTORY}/bin")
# libraries
IF (WIN32)
  set(LIBRARY_OUTPUT_PATH "${BUILD_DIRECTORY}/bin")
ELSE (WIN32)
  set(LIBRARY_OUTPUT_PATH "${BUILD_DIRECTORY}/lib")
ENDIF()

add_subdirectory (lib)
add_subdirectory (ServerMouseController)
add_subdirectory (ClientTest)
add_subdirectory (UnitTests)
