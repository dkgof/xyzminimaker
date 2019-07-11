package dk.fambagge.xyzminimaker;

import java.io.File;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

public class Main {

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(System.getProperty("jetty.port", "8083"));

            Server server = new Server(port);

            ContextHandlerCollection handlerCollection = new ContextHandlerCollection();

            AliasEnhancedWebAppContext context = new AliasEnhancedWebAppContext();

            context.setContextPath("/");

            context.setConfigurations(new Configuration[]{new AnnotationConfiguration(), new WebXmlConfiguration(), new WebInfConfiguration(), new PlusConfiguration(), new MetaInfConfiguration(), new FragmentConfiguration(), new EnvConfiguration()});

            File localFile = new File("./webapp");

            if ((localFile.exists()) && (localFile.isDirectory())) {
                System.out.println("Deployed zipfile!");
                context.setBaseResource(new ResourceCollection(new String[]{"./webapp"}));

                context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/*.jar");
            } else {
                System.out.println("Netbeans run!");
                context.setBaseResource(new ResourceCollection(new String[]{"./src/main/webapp", "./target"}));

                context.setResourceAlias("/WEB-INF/classes/", "/classes/");
                context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");
            }

            ServletContextHandler handler = new ServletContextHandler();
            handler.setContextPath("/uploads");

            ServletHolder holder = new ServletHolder("static-home", DefaultServlet.class);
            holder.setInitParameter("resourceBase", "./uploads");
            holder.setInitParameter("dirAllowed", "true");
            holder.setInitParameter("pathInfoOnly", "true");
            handler.addServlet(holder, "/*");

            handlerCollection.setHandlers(new Handler[]{context, handler});

            server.setHandler(handlerCollection);

            server.start();
            server.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
