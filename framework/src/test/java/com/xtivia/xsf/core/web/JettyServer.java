package com.xtivia.xsf.core.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.bridge.SLF4JBridgeHandler;
 
public class JettyServer
{
    private static Server server;
 
    public static final String TEST_CONTEXT = "/delegate";
    public static final int    TEST_PORT = 8080;
 
    public static void startIfRequired() throws Exception
    {
        if (server == null) {
        	
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
             
            server = new Server(TEST_PORT);
 
            WebAppContext context = new WebAppContext();
            context.setDescriptor("src/test/resources/jetty/WEB-INF/web.xml");
            context.setResourceBase("src/main/webapp");
            context.setContextPath(TEST_CONTEXT);
            context.setParentLoaderPriority(true);
 
            server.setHandler(context);
 
            server.start();
        }
    }
     
    public static void stop() throws Exception
    {
        if (server != null) {
            server.stop();
            server.join();
            server.destroy();
            server = null;
        }
    }
 
    public static void main(String[] args)
    {
        try {
            startIfRequired();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}