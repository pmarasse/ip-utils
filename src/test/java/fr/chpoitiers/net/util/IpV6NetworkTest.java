package fr.chpoitiers.net.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpV6NetworkTest {

    private static final Logger log             = LoggerFactory.getLogger(IpV6NetworkTest.class);

    public static final String  LOCALHOST       = "::1";

    public static final int     LOCALHOST_MASK  = 128;

    public static final String  LOCAL_LINK      = "fe80::";

    public static final int     LOCAL_LINK_MASK = 10;

    public static final String  MULTICAST       = "ff00::";

    public static final int     MULTICAST_MASK  = 8;

    public static final String  TEST            = "2a01:1234:5678:abcd::1";

    public static final int     TEST_MASK       = 64;

    public static final String  FULL_FF         = "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff";

    public static final String  LOCAL_TEST      = "fe80::2e41:38ff:fea4:423";

    public static final int     LOCAL_TEST_MASK = 64;

    public static final String  LOCAL_TEST_CIDR = LOCAL_TEST + "/" + LOCAL_TEST_MASK;

    public static final String  NONE_CIDR       = "::/128";

    @Test
    public void testIpV6Network() {

        log.info("testIpV6Network...");
        IpV6Network ip = new IpV6Network(LOCALHOST, LOCALHOST_MASK);
        log.info("Adresse récupérée : " + ip.getCidrAddress());
        assertEquals(LOCALHOST, ip.getAddress());
        assertEquals(LOCALHOST_MASK, ip.getNetmaskAsInt());
        assertEquals(FULL_FF, ip.getNetmask());

        ip = new IpV6Network(LOCAL_LINK, LOCAL_LINK_MASK);
        log.info("Adresse récupérée : " + ip.getCidrAddress());
        assertEquals(LOCAL_LINK, ip.getAddress());
        assertEquals(LOCAL_LINK_MASK, ip.getNetmaskAsInt());

        ip = new IpV6Network(TEST, TEST_MASK);
        log.info("Adresse récupérée : " + ip.getCidrAddress());
        assertEquals(TEST, ip.getAddress());
        assertEquals(TEST_MASK, ip.getNetmaskAsInt());

        ip = new IpV6Network(LOCAL_TEST_CIDR);
        log.info("Adresse récupérée : " + ip.getCidrAddress());
        assertEquals(LOCAL_TEST, ip.getAddress());
        assertEquals(LOCAL_TEST_MASK, ip.getNetmaskAsInt());
        
        ip = new IpV6Network(NONE_CIDR);
        log.info("Adresse récupérée : " + ip.getCidrAddress());
        assertEquals(NONE_CIDR, ip.getCidrAddress());
    }

    @Test
    public void testIsInNetwork() {

        log.info("testIsInNetwork...");

        log.debug("Tests vrais...");
        IpV6Network net = new IpV6Network(LOCAL_LINK, LOCAL_LINK_MASK);
        IpV6Network ip = new IpV6Network(LOCAL_TEST_CIDR);

        assertTrue(ip.isInNetwork(net));
        // Vrai aussi grace au subnet à /64
        assertTrue(net.isInNetwork(ip));

        log.debug("Tests faux...");
        IpV6Network localLink = new IpV6Network(LOCAL_LINK, LOCAL_LINK_MASK);
        IpV6Network multicast = new IpV6Network(MULTICAST, MULTICAST_MASK);

        assertFalse(localLink.isInNetwork(multicast));
        assertFalse(multicast.isInNetwork(localLink));

    }

}
