#
#  Created by Anastassios Martakos on 22/09/2015
#

wget -O ./app/src/main/res/raw/dhcpv6_base.zip http://cmmn.realmar.net/dhcp/dhcpv6_base.zip
wget -O ./app/src/main/res/raw/dhcpv6_update.zip http://cmmn.realmar.net/dhcp/dhcpv6_update.zip

echo "Done."
echo "You can verify ZIP files in ./app/src/main/res/raw/"
echo "Remember to enter your public key in app/src/main/java/org/daduke/realmar/dhcpv6client/BillingPublicKey.java"
