@echo off

set directoryFinal="build_vs14_64bits_debug"
if exist %directoryFinal% rmdir /Q /S %directoryFinal%

rem Launch the creation of the VS solution first
mkdir %directoryFinal%
cd %directoryFinal%

setlocal ENABLEDELAYEDEXPANSION
set type_platform="Unknown"
if %processor_architecture% == AMD64 set type_platform="Win64"
if %processor_architecture% == x86 set type_platform="Win32"

if !type_platform! == "Unknown" (
    echo "Platform unknown (not AMD64 nor x86)"
    goto :exit_script
) else (
    echo Platform detected: !type_platform!
)

cmake -DCMAKE_CONFIGURATION_TYPES=Debug -DCMAKE_BUILD_TYPE=DEBUG -G "Visual Studio 14 2015 !type_platform!" ../

echo End of the process. Everything went well !

:exit_script

cd ../

pause
