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
            // if ("reverse-proxy".equalsIgnoreCase(requestedResource)) {
            //     requestedResource = "";
            // }


            LOGGER.info(String.format(
                "The user requested the resource %s from the remote address", requestedResource
            ));
            PrintWriter out = null;
            BufferedOutputStream os = null;
//          ignore this commented block
//           response.setContentType(contentType);
//            if (requestedResource.contains(".svg") == true) {
//                response.setContentType("image/svg+xml");
//            } else if (requestedResource.contains(".png")) {
//                response.setContentType("image/png");
//            } else if (requestedResource.contains(".jpeg")) {
//                response.setContentType("image/jpeg");
//            } else if (requestedResource.contains(".ico")) {
//                response.setContentType("image/x-icon");
//            } else
//            if (requestedResource.contains(".html")){
//                response.setContentType("text/html;charset=UTF-8");
//            } else if (requestedResource.contains(".css")){
//                response.setContentType("text/css;charset=UTF-8");
//            } else if (requestedResource.contains(".js")){
//                response.setContentType("text/javascript;charset=UTF-8");
//            } else if (requestedResource.length() == 0) {
//                response.setContentType("text/html;charset=UTF-8");
//            } else if (requestedResource.contains(".") == false){
//                response.setContentType("application/json;charset=UTF-8");
//            }

            try {
                Response result = loadBalancer.sendToAvailableServer(requestedResource);
                //TODO remove this comment
                //LOGGER.log(Level.INFO,String.format("---- Content Type 1 ---- %s", response.getHeader("Content-Type")));
                //TODO remove this comment
                //LOGGER.log(Level.INFO,String.format("---- Content Type 2 ---- %s", response.getHeader("Content-Type")));

                result.getHeaders().forEach((key, value) -> {
                    if (key != null
                            && !"Connection".equalsIgnoreCase(key)
                            && !"Transfer-Encoding".equalsIgnoreCase(key)) {
                        response.setHeader(key, value.get(0));
                    }
                });
                try {
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
                
                //TODO remove this comment
                // LOGGER.log(Level.INFO,String.format("---- Content Type 3 ---- %s", response.getHeader("Content-Type")));
                // LOGGER.log(Level.INFO,String.format("---- Content Type 4 ---- %s", response.getHeader("Content-Type")));
                // LOGGER.log(Level.INFO,String.format("---- Content Type 5 ---- %s", response.getCharacterEncoding()));
            } catch (LoadBalancerException loadNotForwarded) {
                response.setContentType("text/html;charset=UTF-8");
                out = response.getWriter();
                out.println("<!DOCTYPE html>");
                out.println("<html><head>");
                out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
                out.println("<title>My Loader</title></head>");
                out.println("<body>");
                out.println("<p>We are under maintenance <br> We applogise for the inconvenience.</p>");
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
        try {
            LOGGER.info("Inside Exposed Servlet Post Mapping Handler");
            String requestedURI = request.getRequestURI();
            String remoteAddress = request.getRemoteAddr();
            LOGGER.info(String.format(
                "The user requested the resource %s from the remote address",
                requestedURI,
                remoteAddress
            ));
            loadBalancer.sendToAvailableServer(requestedURI);


        } catch (Exception e) {
            LOGGER.severe("Error occurred inside Exposed Servlet Post Mapping Handler");
            LOGGER.severe(e.getMessage());
        }
    }
}