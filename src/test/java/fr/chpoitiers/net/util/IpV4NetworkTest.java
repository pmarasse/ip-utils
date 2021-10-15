package fr.chpoitiers.net.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpV4NetworkTest {

    private static final Logger log      = LoggerFactory.getLogger(IpV4NetworkTest.class);

    public static final String  IP_1     = "192.168.0.1";

    public static final String  NM_1     = "255.255.255.0";

    public static final String  NET_1    = "192.168.0.0";

    public static final String  CIDR_1   = NET_1 + "/24";

    public static final String  IP_2     = "192.168.1.1";

    public static final String  NM_2     = "255.255.0.0";

    public static final String  CIDR_2   = IP_2 + "/16";

    public static final String  INV_IP_1 = "322.168.0.1";

    public static final String  INV_NM_1 = "255.127.255.128";

    @Test
    public void IpV4Test() {

        log.info("IpV4Test...");

        IpV4Network ip = new IpV4Network(IP_1, NM_1);
        log.debug("IP renvoyée : " + ip.getAddress() + " / " + ip.getNetmask() + " soit " + ip.getCidrAddress());
        assertEquals(NET_1, ip.getAddress());
        assertEquals(NM_1, ip.getNetmask());
        assertEquals(CIDR_1, ip.getCidrAddress());

        try {
            ip = new IpV4Network(INV_IP_1, NM_1);
            fail("Should have failed with address : " + INV_IP_1);
        } catch (NumberFormatException e) {
        }

        try {
            ip = new IpV4Network(IP_1, INV_NM_1);
            fail("Should have failed with address : " + INV_IP_1);
        } catch (NumberFormatException e) {
        }

        // should work
        ip = new IpV4Network("0.0.0.0", "0.0.0.0");
        log.debug("IP renvoyée : " + ip.getAddress() + " / " + ip.getNetmask());

        // should work
        ip = new IpV4Network("255.255.255.255", "255.255.255.255");
        log.debug("IP renvoyée : " + ip.getAddress() + " / " + ip.getNetmask());

        // cidr to litteral test
        ip.setCidrMask(15);
        assertEquals("255.254.0.0", ip.getNetmask());
        log.debug("IP renvoyée : " + ip.getAddress() + " / " + ip.getNetmask());
        ip.setCidrMask(12);
        assertEquals("255.240.0.0", ip.getNetmask());
        log.debug("IP renvoyée : " + ip.getAddress() + " / " + ip.getNetmask());
        ip.setCidrMask(30);
        assertEquals("255.255.255.252", ip.getNetmask());
        log.debug("IP renvoyée : " + ip.getAddress() + " / " + ip.getNetmask());

        ip = new IpV4Network(CIDR_1);
        assertEquals(NET_1, ip.getAddress());
        assertEquals(NM_1, ip.getNetmask());
        log.debug("IP renvoyée : " + ip.getAddress() + " / " + ip.getNetmask());

        // This case : return an address with default /32 netmask
        ip = new IpV4Network(IP_1);
        log.debug("IP renvoyée : " + ip.getAddress() + " / " + ip.getNetmask());
        assertEquals(IP_1, ip.getAddress());
        assertEquals(32, ip.getNetmaskAsInt());

        try {
            ip = new IpV4Network(IP_1 + "/2/3");
            fail("Should have failed with non CIDR address : " + IP_1);
        } catch (NumberFormatException e) {
        }

    }

    @Test
    public void isInNetworkTest() {

        log.info("isInNetworkTest...");
        IpV4Network myIp = new IpV4Network(IP_1, NM_1);
        IpV4Network netIp = new IpV4Network(IP_2, NM_2);

        log.debug("Vérifie que " + myIp.getCidrAddress() + " est dans le réseau de " + netIp.getCidrAddress());
        assertTrue(myIp.isInNetwork(netIp));
        log.debug("Vérifie que " + netIp.getCidrAddress() + " n'est pas dans le réseau de " + myIp.getCidrAddress());
        assertFalse(netIp.isInNetwork(myIp));

        IpV4Network hostIp = new IpV4Network("172.16.10.32/32");
        netIp = new IpV4Network("172.16.10.0/24");
        
        assertTrue(hostIp.isInNetwork(netIp));
        
    }

    @Test
    public void naturalOrderTest() {

        log.info("Natual order test...");
        IpV4Network lowIp1 = new IpV4Network("10.0.0.1/24");
        IpV4Network lowIp2 = new IpV4Network("10.0.0.2/24");
        IpV4Network lowIp3 = new IpV4Network("10.0.0.0/16");
        IpV4Network medIp1 = new IpV4Network("172.16.10.0/16");
        IpV4Network host1 = new IpV4Network("172.16.10.32/32");
        IpV4Network lastIp = new IpV4Network("192.168.5.0/24");

        ArrayList<IpV4Network> liste = new ArrayList<>();
        liste.add(medIp1);
        liste.add(lastIp);
        liste.add(lowIp2);
        liste.add(host1);
        liste.add(lowIp3);
        liste.add(lowIp1);

        StringBuilder sb = new StringBuilder(80);
        sb.append("List before sorting : ");
        for (IpV4Network ipV4Network : liste) {
            sb.append(ipV4Network.toString());
            sb.append(' ');
        }
        log.info(sb.toString());

        liste.sort(null);

        sb = new StringBuilder(80);
        sb.append("List after sorting  : ");
        for (IpV4Network ipV4Network : liste) {
            sb.append(ipV4Network.toString());
            sb.append(' ');
        }
        log.info(sb.toString());

        assertEquals(liste.get(0), lowIp1);
        assertEquals(liste.get(1), lowIp2);
        assertEquals(liste.get(2), lowIp3);
        assertEquals(liste.get(3), host1);
        assertEquals(liste.get(4), medIp1);
        assertEquals(liste.get(5), lastIp);

    }

}
