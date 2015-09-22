DHCPv6 Client
=============

Description
-----------
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
