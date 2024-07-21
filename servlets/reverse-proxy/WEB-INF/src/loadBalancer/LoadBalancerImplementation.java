package loadBalancer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadBalancerImplementation implements LoadBalancer {

    private static Logger LOGGER = Logger.getLogger(LoadBalancerImplementation.class.getName());
    private static int serverPool = 2;
    private static int lastUsedID = -1;

    private static List<String> servers = new ArrayList<>();

    public LoadBalancerImplementation() {
    /**
     * I have serverPool number of servers only
     * and address of servers is like 
     * localhost:8000, localhost:8001, .....
     */
        for (int i =0;i<serverPool;i++) {
            String address = "http://localhost:900";
            Integer serverID = i;
            servers.add(address + serverID.toString());
        }
    }
    /**
     * Simple Round Robin Scheduling
     */
    private String serverToBeUsed() {
        lastUsedID = (lastUsedID + 1) % serverPool;
        LOGGER.info(String.format("Server Number %d is used", lastUsedID));
        return servers.get(lastUsedID);
    }
    

    @Override
    public Response sendToAvailableServer(String requestedResource)
    throws LoadBalancerException {
        int retryCount = 0;
        while (retryCount < LoadBalancerImplementation.serverPool) {
            try {
                //TODO
                //Remove the information Logger
                String serverEndPoint = this.serverToBeUsed();
                String completePath = serverEndPoint + "/" + requestedResource;
                LOGGER.log(Level.INFO, String.format("The complete path for the resource %s", completePath));
                return this.forwardPayloadAndReturnResponse(completePath);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "A server is down, redirecting to another", e);
                retryCount++;
            }
        }
        throw new LoadBalancerException(
            "All Servers are down"
        );
    }
    private Response forwardPayloadAndReturnResponse(String serverURL) throws IOException {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(serverURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000*5);
            int responseCode = connection.getResponseCode();
            Map<String, List<String>> headers = connection.getHeaderFields();
            headers.forEach((key, value) -> {
                LOGGER.log(Level.INFO, key);
                value.forEach(v -> {
                    LOGGER.log(Level.INFO, v);
                });
            });
            Response response = null;
            if (headers.containsKey("Content-Type")) {
                String contentType = headers.get("Content-Type").get(0);
                if (contentType.contains("image/") 
                    || (contentType.contains("text/") && contentType.contains("css"))
                    || (contentType.contains("text/") && contentType.contains("html"))) {
                    LOGGER.log(Level.INFO, String.format("Response Code From the Server %d", responseCode));
                    BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    int ch = 0;
                    List<Integer> attachment = new ArrayList<Integer>();
                    while((ch = inputStream.read())!= -1) {
                        attachment.add(ch);
                    }
                    inputStream.close();
                    response = new Response(null, headers, true, attachment);
                } else if (contentType.contains("json")) {
                    LOGGER.log(Level.INFO, String.format("Response Code From the Server %d", responseCode));
                    BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    int ch = 0;
                    List<Integer> attachment = new ArrayList<Integer>();
                    while((ch = inputStream.read())!= -1) {
                        attachment.add(ch);
                    }
                    inputStream.close();
                    response = new Response(null, headers, true, attachment);
                } else {
                    LOGGER.log(Level.INFO, String.format("Response Code From the Server %d", responseCode));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuffer resp = new StringBuffer();
                    while ((line = reader.readLine()) != null ) {
                        resp.append(line);
                    }
                    reader.close();
                    LOGGER.log(Level.INFO,resp.toString());
                    response  = new Response(resp.toString(),headers,false, null);
                }
            }
            return response;
        } catch (IOException e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    @Override
    public InputStream getAvailableServers()
    throws LoadBalancerException {
        try {
            throw new FileNotFoundException();
        } catch (FileNotFoundException e) {
            throw new LoadBalancerException(e.toString());
        }

    }
}
