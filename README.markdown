# [DEPRECATED] Use [Mygod/DHCPv6-Client-Android](https://github.com/Mygod/DHCPv6-Client-Android) instead!

I no longer maintain this project. Please use [MyGod's fork](https://github.com/Mygod/DHCPv6-Client-Android)
instead. Not only is their version maintained but also comes with *massive improvements*:

 - Supports Android 5.0+, this app struggles with [Nougat](https://github.com/realmar/DHCPv6-Client-Android/issues/8)
 - Completely systemless and doesn't require Busybox; (no extra steps for install/uninstall)
 - No closed source components and licensed in Apache 2.0;
 - Modern codebase with automated test to ensure integrity

You can get MyGod's fork here:

<a href="https://play.google.com/store/apps/details?id=be.mygod.dhcpv6client" target="_blank"><img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height="60"></a>

[XDA Labs](https://labs.xda-developers.com/store/app/be.mygod.dhcpv6client)

[GitHub](https://github.com/Mygod/DHCPv6-Client-Android)

DHCPv6 Client
=============

Description
-----------
This app requires ROOT PREMISSIONS because of the following reasons:
  - the wide-dhcpv6 client uses the privileged port 546
  - the client needs to configure network interfaces

Android doesn't support DHCPv6 out of the box, because Google doesn't want to implement this feature. For some people (especially universities and corporations) this is a serious issue, because they require control over their IPv6 addresses.

This app fixes this issue. It requests an IPv6 address using DHCPv6 and makes your Android future-proof.

For a full explanation on how this app works please refer to the xda-developer thread.

Discussion: http://forum.xda-developers.com/android/apps-games/app-dhcpv6-client-t3176443
Download: https://play.google.com/store/apps/details?id=org.daduke.realmar.dhcpv6client

Google Issue Thread: https://code.google.com/p/android/issues/detail?can=2&start=0&num=100&q=&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars&groupby=&sort=&id=32621

Third Party Software
--------------------
I use the DHCPv6 client binary from wide-dhcpv6 (http://wide-dhcpv6.sourceforge.net/). I didn't compile this binary by myself. Instead I've taken it from the Fairphone source (https://www.fairphone.com/). I also use Fairphone's scripts around this binary.

wide-dhcpv6 is licensed under the BSD License

Fairphones source contains open source software including software released under the GNU General Public License (GPL) version 2 and Library/Lesser General Public License version 2/2.1.

Those binaries and scripts will be downloaded an put in the right folder when executing install.sh

Installation
------------
  - Run install.sh

Known Issues
------------
### DHCPv6 Client ask me everytime if I want to install the client
Update or reinstall busybox
