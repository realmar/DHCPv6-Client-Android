package org.daduke.realmar.dhcpv6client;

/**
 * Created by Anastassios Martakos on 8/12/15.
 */
public class IpCollector {
    private String ip;

    public IpCollector(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
