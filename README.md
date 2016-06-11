# WindowsRemote
========================================

Android application which controls the mouse on Windows.

This is a merge of my very first two Android experiments on a real application !
Those are [Android_WindowsRemote_UDP](https://github.com/bourdibay/Android_WindowsRemote_UDP) and [Android_WindowsRemote_Bluetooth](https://github.com/bourdibay/Android_WindowsRemote_Bluetooth).


The goal of it is to control the mouse of a computer on Windows with the Android smartphone.
The connection between Windows and Android can be either over Wifi or Bluetooth.

### How to build

#### The Windows server

I use the json minimalistic library [Jsmn](https://github.com/zserge/jsmn).
You just need to run CMake with no additional options. The server has been coded in C. It compiles with Visual Studio 2015 and has been tested on Windows 10.

The cursor cannot be moved within windows that have been started with higher privileges. Thus it is adivsed to run it as administrator.

I used the code sample provided by Microsoft [there](https://code.msdn.microsoft.com/windowsdesktop/Bluetooth-Connection-e3263296) to get started with Bluetooth technology. The server should NOT work if you have a Broadcom Bluetooth device, since you have to use their own SDK.

#### The Android client

I use Android Studio 2 to build it. Tested on Lollipop and Marshmallow Android.

### Run

##### Windows (server):

With Bluetooth connection:

- **ServerMouseController.exe --socket bluetooth**

With UDP connection:

- **ServerMouseController.exe --socket udp**

With TCP connection:

- **ServerMouseController.exe --socket tcp**

###### Notes:

- Wifi:
When using UDP protocol, some packets may be dropped.
When using TCP protocol, cursor moving may not be optimal.

- Bluetooth:
To make it work correctly on Windows, do not forget to turn on Bluetooth, and I also had to allow Bluetooth devices to detect my PC (in Bluetooth's advanced parameters).

##### Android (client):

Start the app and configure it via the settings to enable Bluetooth or Wifi connection.
      
###### Notes:

- Wifi:
Enable Wifi first :) otherwise you'll get errors.

- Bluetooth:
Enable Bluetooth and pair to the Windows device. Then in the app's settings you'll be able to select which paired device to communicate with.

### Screenshots

[screen1](https://github.com/bourdibay/WindowsRemote/blob/master/Screenshots/home.png)
[screen2](https://github.com/bourdibay/WindowsRemote/blob/master/Screenshots/settings.png)
[screen3](https://github.com/bourdibay/WindowsRemote/blob/master/Screenshots/settings_bluetooth.png)
[screen4](https://github.com/bourdibay/WindowsRemote/blob/master/Screenshots/home_connected.png)
[screen5](https://github.com/bourdibay/WindowsRemote/blob/master/Screenshots/home_horizontal.png)
