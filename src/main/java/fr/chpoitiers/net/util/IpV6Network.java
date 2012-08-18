package fr.chpoitiers.net.util;

public class IpV6Network implements IpNetwork {

    public final static int    VERSION   = 6;

    public final static String SEPARATOR = ":";

    private DualLong           ip;

    private int                cidrMask;

    /**
     * Constructor with IP and cidr netmask
     * 
     * @param address
     * @param cidrMask
     */
    public IpV6Network(final String address, final int cidrMask) {

        this.setCidrMask(cidrMask);
        this.ip = this.parseAddress(address);
    }

    /**
     * Constructor with a single cidr address (eg: fe80::aabb:ccff:fedd:eeff/64)
     * 
     * @param cidrAddress
     */
    public IpV6Network(final String cidrAddress) {

        String[] elts = cidrAddress.split("\\/");
        if (elts.length > 2) {
            throw new IllegalArgumentException("Invalid CIDR Address : " + cidrAddress);
        }
        if (elts.length == 2) {
            setCidrMask(Integer.parseInt(elts[1]));
        } else {
            cidrMask = 128;
        }
        ip = parseAddress(elts[0]);
    }

    /**
     * Parse IPv6 Address into a dual long
     * 
     * @param address
     *            Litteral address
     * @return DualLong object with numeric representation of the address
     */
    private DualLong parseAddress(final String address) {

        String[] elts = address.split("\\:");
        if (elts.length > 7) {
            throw new NumberFormatException("Invalid IPv6 address : " + address);
        }

        DualLong result = new DualLong();

        if (elts.length == 0) {
            return result;
        }

        long[] ipElts = new long[8];
        int j = 0;
        for (int i = 0; i < elts.length; i++) {
            if (elts[i].length() == 0) {
                // on arrive sur ::, il faut que i saute quelques éléments... sauf si le premier est vide
                if (i > 0) {
                    j = 8 - elts.length + i;
                }
            } else {
                ipElts[j] = Long.parseLong(elts[i], 16);
                if ((ipElts[j] & 0xffff) != ipElts[j]) {
                    throw new NumberFormatException("Invalid chunk " + ipElts[j] + " in IPv6 address : " + address);
                }
            }
            j++;
        }

        result.prefix = ipElts[0] << 48 | ipElts[1] << 32 | ipElts[2] << 16 | ipElts[3];
        result.suffix = ipElts[4] << 48 | ipElts[5] << 32 | ipElts[6] << 16 | ipElts[7];
        return result;
    }

    /**
     * Convert DualLong IPv6 address into 8 elements of 16 bits
     * 
     * @param address
     * @return sliced array of short
     */
    private long[] dualLongToArray(final DualLong address) {

        long[] result = new long[8];
        long parsed = address.suffix;
        int j;
        for (j = 7; j >= 4; j--) {
            result[j] = parsed & 0xffffL;
            parsed >>= 16;
        }
        parsed = address.prefix;
        for (j = 3; j >= 0; j--) {
            result[j] = parsed & 0xffffL;
            parsed >>= 16;
        }
        return result;
    }

    /**
     * Convert an array of 8 elements into String representation
     * 
     * @param elements
     * @return string representation of IP address
     */
    private String arrayToString(final long[] elements) {

        StringBuilder sb = new StringBuilder(39);
        int zeroEnd = 7;
        while (zeroEnd >= 0 && elements[zeroEnd] != 0) {
            zeroEnd--;
        }
        int zeroStart = zeroEnd;
        while (zeroStart >= 0 && elements[zeroStart] == 0) {
            zeroStart--;
        }
        zeroStart++;

        for (int i = 0; i <= 7; i++) {
            if ((i == zeroStart) && (zeroEnd >= zeroStart)) {
                if (i == 0) {
                    sb.append(SEPARATOR);
                }
                i = zeroEnd;
                sb.append(SEPARATOR);
            } else {
                sb.append(String.format("%x", elements[i]));
                if (i < 7) {
                    sb.append(SEPARATOR);
                }
            }
        }

        return sb.toString();
    }

    /**
     * Convert CIDR mask to dual long
     * 
     * @param cidrMask
     * @return DualLong object representation of netmask
     */
    private DualLong cidrMaskToDualLong(int cidrMask) {

        DualLong mask = new DualLong();

        if (cidrMask > 64) {
            mask.prefix = 0xffffffffffffffffL;
            mask.suffix = 0xffffffffffffffffL << (128 - cidrMask);
        } else {
            mask.prefix = 0xffffffffffffffffL << (64 - cidrMask);
            mask.suffix = 0;
        }
        return mask;
    }

    @Override
    public boolean isInNetwork(final IpNetwork container) {

        if (container instanceof IpV6Network) {

            DualLong containerMask = cidrMaskToDualLong(((IpV6Network) container).getNetmaskAsInt());
            long myPrefix = ip.prefix & containerMask.prefix;
            long mySuffix = ip.suffix & containerMask.suffix;

            long containerPrefix = ((IpV6Network) container).getPrefix() & containerMask.prefix;
            long containerSuffix = ((IpV6Network) container).getSuffix() & containerMask.suffix;

            return ((myPrefix == containerPrefix) && (mySuffix == containerSuffix));
        }

        return false;
    }

    @Override
    public String getAddress() {

        return arrayToString(dualLongToArray(ip));
    }

    @Override
    public String getNetmask() {

        return arrayToString(dualLongToArray(cidrMaskToDualLong(cidrMask)));
    }

    @Override
    public String getCidrAddress() {

        return getAddress() + "/" + cidrMask;
    }

    @Override
    public int getVersion() {

        return VERSION;
    }

    @Override
    public void setCidrMask(final int mask) throws IllegalArgumentException {

        if (mask < 0 || mask > 128) {
            throw new IllegalArgumentException("mask must be between 0 and 128");
        }
        this.cidrMask = mask;
    }

    @Override
    public int getNetmaskAsInt() {

        return cidrMask;
    }

    public long getSuffix() {

        return ip.suffix;
    }

    public long getPrefix() {

        return ip.prefix;
    }

    /**
     * Inner class to represent a full IPv6
     * 
     * @author Philippe Marasse
     */
    private class DualLong {

        public long prefix;

        public long suffix;
    }

}
