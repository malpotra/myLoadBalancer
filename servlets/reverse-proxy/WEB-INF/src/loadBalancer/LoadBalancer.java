package loadBalancer;

import java.io.InputStream;
/**
 * @author malpotra.harshit
 * A Load Balancer using which we can perform load balancing action
 */
public interface LoadBalancer {
    /**
     * forward the request to an available server
     * @param 
     * requestedURI the resource client actually wants
     * @return
     * InputStream object as response from the server
     * will return the requested content
     */
    public Response sendToAvailableServer(String requestedURI) throws LoadBalancerException;
    /**
     * Get a list of all available servers
     * @return
     * InputStream object as response from the server will contain a list
     *
     */
    public InputStream getAvailableServers() throws LoadBalancerException;
}
