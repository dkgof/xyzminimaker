/*    */ package dk.fambagge.xyzminimaker;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.PrintStream;
/*    */ import org.eclipse.jetty.annotations.AnnotationConfiguration;
/*    */ import org.eclipse.jetty.plus.webapp.EnvConfiguration;
/*    */ import org.eclipse.jetty.plus.webapp.PlusConfiguration;
/*    */ import org.eclipse.jetty.server.Handler;
/*    */ import org.eclipse.jetty.server.Server;
/*    */ import org.eclipse.jetty.server.handler.ContextHandlerCollection;
/*    */ import org.eclipse.jetty.servlet.DefaultServlet;
/*    */ import org.eclipse.jetty.servlet.ServletContextHandler;
/*    */ import org.eclipse.jetty.servlet.ServletHolder;
/*    */ import org.eclipse.jetty.util.resource.ResourceCollection;
/*    */ import org.eclipse.jetty.webapp.Configuration;
/*    */ import org.eclipse.jetty.webapp.FragmentConfiguration;
/*    */ import org.eclipse.jetty.webapp.MetaInfConfiguration;
/*    */ import org.eclipse.jetty.webapp.WebAppContext;
/*    */ import org.eclipse.jetty.webapp.WebInfConfiguration;
/*    */ import org.eclipse.jetty.webapp.WebXmlConfiguration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Main
/*    */ {
/*    */   public static void main(String[] paramArrayOfString)
/*    */   {
/*    */     try
/*    */     {
/* 34 */       int i = Integer.parseInt(System.getProperty("jetty.port", "8083"));
/*    */       
/* 36 */       Server localServer = new Server(i);
/*    */       
/* 38 */       ContextHandlerCollection localContextHandlerCollection = new ContextHandlerCollection();
/*    */       
/* 40 */       AliasEnhancedWebAppContext localAliasEnhancedWebAppContext = new AliasEnhancedWebAppContext();
/*    */       
/* 42 */       localAliasEnhancedWebAppContext.setContextPath("/");
/*    */       
/* 44 */       localAliasEnhancedWebAppContext.setConfigurations(new Configuration[] { new AnnotationConfiguration(), new WebXmlConfiguration(), new WebInfConfiguration(), new PlusConfiguration(), new MetaInfConfiguration(), new FragmentConfiguration(), new EnvConfiguration() });
/*    */       
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 51 */       File localFile = new File("./webapp");
/*    */       
/* 53 */       if ((localFile.exists()) && (localFile.isDirectory()))
/*    */       {
/* 55 */         System.out.println("Deployed zipfile!");
/* 56 */         localAliasEnhancedWebAppContext.setBaseResource(new ResourceCollection(new String[] { "./webapp" }));
/*    */         
/*    */ 
/* 59 */         localAliasEnhancedWebAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/*.jar");
/*    */       }
/*    */       else {
/* 62 */         System.out.println("Netbeans run!");
/* 63 */         localAliasEnhancedWebAppContext.setBaseResource(new ResourceCollection(new String[] { "./src/main/webapp", "./target" }));
/*    */         
/*    */ 
/*    */ 
/* 67 */         localAliasEnhancedWebAppContext.setResourceAlias("/WEB-INF/classes/", "/classes/");
/* 68 */         localAliasEnhancedWebAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");
/*    */       }
/*    */       
/* 71 */       ServletContextHandler localServletContextHandler = new ServletContextHandler();
/* 72 */       localServletContextHandler.setContextPath("/uploads");
/*    */       
/* 74 */       ServletHolder localServletHolder = new ServletHolder("static-home", DefaultServlet.class);
/* 75 */       localServletHolder.setInitParameter("resourceBase", "./uploads");
/* 76 */       localServletHolder.setInitParameter("dirAllowed", "true");
/* 77 */       localServletHolder.setInitParameter("pathInfoOnly", "true");
/* 78 */       localServletContextHandler.addServlet(localServletHolder, "/*");
/*    */       
/* 80 */       localContextHandlerCollection.setHandlers(new Handler[] { localAliasEnhancedWebAppContext, localServletContextHandler });
/*    */       
/* 82 */       localServer.setHandler(localContextHandlerCollection);
/*    */       
/* 84 */       localServer.start();
/* 85 */       localServer.join();
/*    */     } catch (Exception localException) {
/* 87 */       localException.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\Misc\Downloads\XYZPrint\XYZMinimaker-1.0-SNAPSHOT.jar!\dk\fambagge\xyzminimaker\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */