package fr.chpoitiers.net.util;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkDesriptionTest {

    private TreeMap<IpV4Network, NetworkDescription> networks = new TreeMap<IpV4Network, NetworkDescription>();

    @Test
    public void test() {

        log.info("Simple test");

        NetworkDescription mainDC = new NetworkDescription("main Datacenter", "Bat. A", "Servers");
        NetworkDescription backupDC = new NetworkDescription("backup Datacenter", "Bat. B", "Servers");
        NetworkDescription rfca = new NetworkDescription("RFC 1918 Class A CatchAll", "N/A", "N/A");
        NetworkDescription rfcb = new NetworkDescription("RFC 1918 Class B CatchAll", "N/A", "N/A");
        NetworkDescription rfcc = new NetworkDescription("RFC 1918 Class C CatchAll", "N/A", "N/A");

        NetworkDescription host = new NetworkDescription("Host", "Test", "Test");
        NetworkDescription testnet = new NetworkDescription("Network", "Test", "Test");
        NetworkDescription clients = new NetworkDescription("Clients", "Bat. A", "Main");
        NetworkDescription printers = new NetworkDescription("Printers", "Bat. A", "Main");

        networks.put(new IpV4Network("10.1.1.0/24"), mainDC);
        networks.put(new IpV4Network("10.2.1.0/24"), backupDC);
        networks.put(new IpV4Network("10.0.0.0/8"), rfca);
        networks.put(new IpV4Network("172.16.0.0/12"), rfcb);
        networks.put(new IpV4Network("192.168.0.0/16"), rfcc);
        networks.put(new IpV4Network("172.16.10.0/24"), clients);
        networks.put(new IpV4Network("172.16.11.0/24"), printers);
        networks.put(new IpV4Network("172.16.10.1"), host);
        networks.put(new IpV4Network("172.16.10.0/30"), testnet);

        NetworkDescription myNetwork = find("10.1.1.32");
        assertEquals(mainDC, myNetwork);

        myNetwork = find("10.5.5.6");
        assertEquals(rfca, myNetwork);

        myNetwork = find("172.16.10.1");
        assertEquals(host, myNetwork);

        myNetwork = find("172.16.10.2");
        assertEquals(testnet, myNetwork);

        myNetwork = find("172.16.10.5");
        assertEquals(clients, myNetwork);

        assertNull(find("8.8.8.8"));
    }

    /**
     * Find a description for the provided Address
     * 
     * @param ip
     *            IP to find
     * @return the network description or null if ip is not in the provided networks
     */
    public NetworkDescription find(String ip) {

        IpV4Network ipv4 = new IpV4Network(ip, 32);

        Set<IpV4Network> nets = networks.keySet();
        for (IpV4Network net : nets) {
            if (ipv4.isInNetwork(net)) {
                return networks.get(net);
            }
        }

        return null;
    }

}
