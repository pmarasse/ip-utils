package fr.chpoitiers.net.util;


public interface IpNetwork {

    /**
     * Test if my Ip Address is included in container's network
     * @param container
     * @return 
     */
    public boolean isInNetwork(final IpNetwork container);
    
    /**
     * Get String representation of address
     * @return
     */
    public String getAddress();
    
    /**
     * Get String representation of netmask
     * @return
     */
    public String getNetmask();
    
    /**
     * Get String representation of address with CIDR netmask 
     * @return
     */
    public String getCidrAddress();
    
    /**
     * get version of IP address
     * @return
     */
    public int getVersion();
    
    /**
     * Set netmask as cidr number
     * @param mask
     * @throws IllegalArgumentException
     */
    public void setCidrMask(final int mask) throws IllegalArgumentException;
    
}
