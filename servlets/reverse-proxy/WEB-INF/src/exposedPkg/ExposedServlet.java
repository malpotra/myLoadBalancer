package exposedPkg;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import loadBalancer.LoadBalancer;
import loadBalancer.LoadBalancerException;
import loadBalancer.LoadBalancerImplementation;
import loadBalancer.Response;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class ExposedServlet extends HttpServlet {

    private static Logger LOGGER = Logger.getLogger(ExposedServlet.class.getName());
    private static LoadBalancer loadBalancer = new LoadBalancerImplementation();
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws 
    ServletException, IOException{
        try {
            LOGGER.info("Inside Exposed Servlet Get Mapping Handler"+ ":" + request.getRequestURI());
            String requestedURI = request.getRequestURI();
            String[] requestedURISSplit = requestedURI.split("/", 0);
            String requestedResource = "";
            for (int i=2;i<requestedURISSplit.length;i++) {
                requestedResource += requestedURISSplit[i];
                if ( i != requestedURISSplit.length -1) {
                    requestedResource+= "/";
                }
            }


            LOGGER.info(String.format(
                "The user requested the resource %s from the remote address", requestedResource
            ));
            PrintWriter out = null;
            BufferedOutputStream os = null;

            try {
                Response result = loadBalancer.sendToAvailableServer(requestedResource);

                result.getHeaders().forEach((key, value) -> {
                    if (key != null
                            && !"Connection".equalsIgnoreCase(key)
                            && !"Transfer-Encoding".equalsIgnoreCase(key)) {
                        response.setHeader(key, value.get(0));
                    }
                });
                try {
                    //important as content such as html,js,css, images must
                    //be written as raw bytes to the output stream
                    //for that the this flag is checked
                    if (result.shouldBeByteStream() == true) {
                        os = new BufferedOutputStream(response.getOutputStream());
                        List<Integer> resultSet = result.getStream();
                        for (Integer num : resultSet) {
                            os.write(num.intValue());
                        }
                        os.close();
                    } else {
                        out = response.getWriter();
                        out.print(result.getContent());
                        out.close();
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "An error occurred", e);
                    throw new LoadBalancerException(e.toString());
                } 

            } catch (LoadBalancerException loadNotForwarded) {
                response.setContentType("text/html;charset=UTF-8");
                out = response.getWriter();
                out.println("<!DOCTYPE html>");
                out.println("<html><head>");
                out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
                out.println("<title>My Loader</title></head>");
                out.println("<body>");
                out.println("<p>We are under maintenance <br> We apologize for the inconvenience.</p>");
                out.close();
            }

        } catch (Exception e ) {
            LOGGER.log(Level.SEVERE, "Error occurred inside Exposed Servlet Get Mapping Handler", e);
        }
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
        //Empty for now
        //
    }
}