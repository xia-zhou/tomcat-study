import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by zhangsong on 15/8/15.
 */
public class PrimitiveServlet implements Servlet{

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("This is PrimitiveServlet");
        PrintWriter out = servletResponse.getWriter();
        out.println("This is PrimitiveServlet,");
        out.println("Test");
        out.print("There is something wrong!");
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
