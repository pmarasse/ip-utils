package fr.chpoitiers.net.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IpNetworkFactoryTest {

    // IPv4 Constants
    public static final String IPV4_1          = "192.168.0.1";

    public static final String NMV4_1          = "255.255.255.0";

    public static final String CIDRV4_1        = IPV4_1 + "/24";

    public static final String IPV4_2          = "192.168.1.1";

    public static final String NMV4_2          = "255.255.0.0";

    public static final String CIDRV4_2        = IPV4_2 + "/16";

    public static final String INV_IPV4_1      = "322.168.0.1";

    public static final String INV_NMV4_1      = "255.127.255.128";

    // IPv6 Constants
    public static final String LOCALHOST       = "::1";

    public static final int    LOCALHOST_MASK  = 128;

    public static final String LOCAL_LINK      = "fe80::";

    public static final int    LOCAL_LINK_MASK = 10;

    public static final String MULTICAST       = "ff00::";

    public static final int    MULTICAST_MASK  = 8;

    public static final String TEST            = "2a01:1234:5678:abcd::1";

    public static final int    TEST_MASK       = 64;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testFactory() {

        // Constructor with CIDR
        IpNetwork ip = IpNetworkFactory.getFromString(CIDRV4_1);
        assertTrue(ip instanceof IpV4Network);

        IpNetwork ip6 = IpNetworkFactory.getFromString(TEST);
        assertTrue(ip6 instanceof IpV6Network);
        assertEquals(128, ip6.getNetmaskAsInt());

        assertFalse(ip.isInNetwork(ip6));
        assertFalse(ip6.isInNetwork(ip));
        
        // Constructor Address / Netmask
        ip = IpNetworkFactory.getFromString(IPV4_1, NMV4_1);
        assertTrue(ip instanceof IpV4Network);
        
        // Non existent constructor
        ip6 = IpNetworkFactory.getFromString(TEST, INV_NMV4_1);
        assertNull(ip6);

        // Constructor
        ip = IpNetworkFactory.getFromString(IPV4_1, 24);
        assertTrue(ip instanceof IpV4Network);
        assertEquals(24, ip.getNetmaskAsInt());
        
        ip6 = IpNetworkFactory.getFromString(TEST, 64);
        assertTrue(ip6 instanceof IpV6Network);
        assertEquals(64, ip6.getNetmaskAsInt());
        
    }

}
