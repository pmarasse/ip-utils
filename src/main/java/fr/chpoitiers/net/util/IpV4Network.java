package fr.chpoitiers.net.util;

public class IpV4Network implements IpNetwork {

    public final static String SEPARATOR = ".";

    public final static int    VERSION   = 4;

    /**
     * raw IP Address stored as a single 32 bit integer
     */
    private int                rawIp;

    /**
     * Netmask stored as CIDR notation [0;32]
     */
    private int                cidrMask;

    /**
     * Constructor with full strings
     * 
     * @param ip
     *            string form of IPv4 addresses eg: 192.168.0.1
     * @param netmask
     *            string form of IPv4 network mask eg: 255.255.255.0
     */
    public IpV4Network(final String ip, final String netmask) {

        rawIp = ipAddressStringToInt(ip);
        cidrMask = getCidrMask(ipAddressStringToInt(netmask));
        if (cidrMask < 0) {
            throw new NumberFormatException("Invalid netmask : " + netmask);
        }

    }

    /**
     * Constructor with String ip address and CIDR netmask
     * 
     * @param ip
     *            string form of IPv4 addresses eg: 192.168.0.1
     * @param cidrMask
     *            integer netmask [0;32]
     */
    public IpV4Network(final String ip, final int cidrMask) {

        rawIp = ipAddressStringToInt(ip);
        setCidrMask(cidrMask);
    }

    /**
     * Constructor with a full cidr address, eg : 192.168.0.1/24
     * 
     * @param cidrIp
     *            full address with mask aa.bb.cc.dd/nn
     */
    public IpV4Network(final String cidrIp) {

        String[] elts = cidrIp.split("\\/");
        if (elts.length > 2) {
            throw new NumberFormatException("Invalid CIDR Address : " + cidrIp);
        }
        if (elts.length == 2) {
            setCidrMask(Integer.parseInt(elts[1]));
        } else {
            cidrMask = 32;
        }
        rawIp = ipAddressStringToInt(elts[0]);
    }

    /**
     * Parse IP string to raw integer
     * 
     * @param stringIp
     *            String representation of IP Address
     * @return raw IP address
     */
    private int ipAddressStringToInt(final String stringIp) {

        int intIp;
        String[] elements = stringIp.split("\\.");
        if (elements.length != 4) {
            throw new NumberFormatException("Invalid IP address : " + stringIp);
        }
        intIp = 0;
        for (int i = 0; i < elements.length; i++) {
            intIp <<= 8;
            int ipElement = Integer.parseInt(elements[i]);
            if ((ipElement & 0x000000FF) != ipElement) {
                throw new NumberFormatException("Invalid IP member " + ipElement + " in address " + stringIp);
            }
            intIp += ipElement;
        }
        return intIp;
    }

    /**
     * String conversion of integer IP address or netmask
     * 
     * @param intIp
     *            raw IP address
     * @return string representation
     */
    private String ipAddressIntToString(final int intIp) {

        StringBuilder sb = new StringBuilder(15);
        for (int i = 24; i >= 0; i -= 8) {
            int ipElement = (intIp >> i) & 0x000000ff;
            sb.append(ipElement);
            if (i > 0) {
                sb.append(SEPARATOR);
            }
        }

        return sb.toString();

    }

    /**
     * Convert a 32 bit netmask to CIDR netmask number
     * 
     * @param mask
     *            integer value of mask
     * @return int value of CIDR equivalent or -1 if invalid
     */
    private int getCidrMask(final int mask) {

        int testMask = 0xffffffff;

        for (int i = 0; i < 33; i++) {
            if (mask == testMask) {
                return 32 - i;
            }
            testMask <<= 1;
        }

        return -1;

    }

    @Override
    public boolean isInNetwork(final IpNetwork container) {

        if (container instanceof IpV4Network) {
            int containerNetwork = ((IpV4Network) container).getRawIp() & ((IpV4Network) container).getRawMask();
            int myNetwork = rawIp & ((IpV4Network) container).getRawMask();
            return containerNetwork == myNetwork;
        }
        return false;
    }

    @Override
    public String getAddress() {

        return ipAddressIntToString(rawIp);
    }

    @Override
    public String getNetmask() {

        return ipAddressIntToString(getRawMask());
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

        if (mask < 0 || mask > 32) {
            throw new IllegalAccessError("CIDR mask must be between 0 and 32");
        }
        cidrMask = mask;
    }
    
    @Override
    public int getNetmaskAsInt() {
        
        return cidrMask;
    }

    public int getRawIp() {

        return rawIp;
    }

    public void setRawIp(int rawIp) {

        this.rawIp = rawIp;
    }

    public int getRawMask() {

        return 0xffffffff << (32 - cidrMask);
    }

    public void setRawMask(final int rawMask) throws IllegalArgumentException {

        int cidrMask = getCidrMask(rawMask);
        if (cidrMask >= 0) {
            this.cidrMask = cidrMask;
        } else {
            throw new IllegalArgumentException("Illegal value of netmask " + ipAddressIntToString(rawMask));
        }
    }

    @Override
    public String toString() {

        return getCidrAddress();
    }

}
