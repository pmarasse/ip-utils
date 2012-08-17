package fr.chpoitiers.net.util;

public class IpNetworkFactory {

    public static IpNetwork getFromString(final String ip, final String netmask) throws NumberFormatException {

        if (ip.contains(IpV4Network.SEPARATOR)) {
            return new IpV4Network(ip, netmask);
        }
        return null;
    }

    public static IpNetwork getFromString(final String ip, final int netmask) throws NumberFormatException {

        if (ip.contains(IpV4Network.SEPARATOR)) {
            return new IpV4Network(ip, netmask);
        } 
        if (ip.contains(IpV6Network.SEPARATOR)) {
            return new IpV6Network(ip, netmask);
        }
        return null;
    }

    public static IpNetwork getFromString(final String ipCidr) throws NumberFormatException {

        if (ipCidr.contains(IpV4Network.SEPARATOR)) {
            return new IpV4Network(ipCidr);
        } 
        if (ipCidr.contains(IpV6Network.SEPARATOR)) {
            return new IpV6Network(ipCidr);
        }
        return null;
    }
}
