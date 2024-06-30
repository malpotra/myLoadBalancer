import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestServlet extends HttpServlet {

    private static Logger LOGGER = Logger.getLogger(TestServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws 
    ServletException, IOException{
        try {
            LOGGER.info("Inside Test Servlet Get Mapping Handler");
            response.setContentType("application/json;charset=UTF-8");
	    PrintWriter out = null;
            out = response.getWriter();
            out.println("{");
            out.println("\"serverName\":\"Server 2\"");
            out.println("}");
            out.close();
        } catch (Exception e ) {
            LOGGER.log(Level.SEVERE, "Error occurred inside Test Servlet Get Mapping Handler", e);
        }
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
    {
    }
}
