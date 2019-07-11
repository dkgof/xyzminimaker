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

    public static void main(String[] paramArrayOfString) {
        try {
            int i = Integer.parseInt(System.getProperty("jetty.port", "8083"));

            Server localServer = new Server(i);

            ContextHandlerCollection localContextHandlerCollection = new ContextHandlerCollection();

            AliasEnhancedWebAppContext localAliasEnhancedWebAppContext = new AliasEnhancedWebAppContext();

            localAliasEnhancedWebAppContext.setContextPath("/");

            localAliasEnhancedWebAppContext.setConfigurations(new Configuration[]{new AnnotationConfiguration(), new WebXmlConfiguration(), new WebInfConfiguration(), new PlusConfiguration(), new MetaInfConfiguration(), new FragmentConfiguration(), new EnvConfiguration()});

            File localFile = new File("./webapp");

            if ((localFile.exists()) && (localFile.isDirectory())) {
                System.out.println("Deployed zipfile!");
                localAliasEnhancedWebAppContext.setBaseResource(new ResourceCollection(new String[]{"./webapp"}));

                localAliasEnhancedWebAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/*.jar");
            } else {
                System.out.println("Netbeans run!");
                localAliasEnhancedWebAppContext.setBaseResource(new ResourceCollection(new String[]{"./src/main/webapp", "./target"}));

                localAliasEnhancedWebAppContext.setResourceAlias("/WEB-INF/classes/", "/classes/");
                localAliasEnhancedWebAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");
            }

            ServletContextHandler localServletContextHandler = new ServletContextHandler();
            localServletContextHandler.setContextPath("/uploads");

            ServletHolder localServletHolder = new ServletHolder("static-home", DefaultServlet.class);
            localServletHolder.setInitParameter("resourceBase", "./uploads");
            localServletHolder.setInitParameter("dirAllowed", "true");
            localServletHolder.setInitParameter("pathInfoOnly", "true");
            localServletContextHandler.addServlet(localServletHolder, "/*");

            localContextHandlerCollection.setHandlers(new Handler[]{localAliasEnhancedWebAppContext, localServletContextHandler});

            localServer.setHandler(localContextHandlerCollection);

            localServer.start();
            localServer.join();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }
}
