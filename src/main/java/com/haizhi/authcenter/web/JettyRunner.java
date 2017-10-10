package com.haizhi.authcenter.web;

import com.haizhi.authcenter.config.SpringConfig;
import com.haizhi.authcenter.config.SpringConfigMVC;


import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.apache.logging.log4j.web.Log4jServletFilter;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import org.eclipse.jetty.webapp.WebAppContext;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import java.nio.charset.Charset;
import java.util.EnumSet;

/**
 * Created by haizhi on 2017/9/1.
 */
public class JettyRunner {

    private static int PORT = 8080;

    public static void run(){
        Server server = new Server(8080);
        QueuedThreadPool threadPool = new QueuedThreadPool(
                20,1,30000);

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath("src/main/webapp/WEB-INF/keystore");
        sslContextFactory.setKeyStorePassword(  "OBF:1xtb1uo71wg41y0q1y7z1y101wfu1unr1xu7");
        sslContextFactory.setKeyManagerPassword("OBF:1xtb1uo71wg41y0q1y7z1y101wfu1unr1xu7");

        HttpConfiguration httpsConfig = new HttpConfiguration();
        httpsConfig.setSecureScheme("https");
        httpsConfig.setSecurePort(8180);
        httpsConfig.setOutputBufferSize(32768);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        ServerConnector conn = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory,"http/1.1"),
                new HttpConnectionFactory(httpsConfig));
        conn.setPort(8180);
        conn.setIdleTimeout(60000);

        /*
        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");
*/
        //Connector c = new LocalConnector();
        //Connector c = new NetworkTrafficServerConnector();

        WebAppContext webAppContext = new WebAppContext();
        initWebappContext(webAppContext);

        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        initServletContext(servletContext);

        ContextHandlerCollection allContext = new ContextHandlerCollection();
        allContext.setHandlers(new Handler[]{
                webAppContext
                ,servletContext
        });


        server.setHandler(allContext);//webAppContext
        server.addBean(threadPool);

        Connector[] connectorList = {conn};
        //server.setConnectors(connectorList);

        try {
            server.setStopAtShutdown(true);
            server.start();
            server.join();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void initWebappContext(WebAppContext webAppContext){
        webAppContext.setContextPath("/");
        webAppContext.setResourceBase("src/main/webapp");
        webAppContext.setDescriptor("src/main/webapp/WEB-INF/web.xml");

        //webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        //webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        //webAppContext.setAttribute("metadata-complete",false);
        //webAppContext.setAttribute("org.eclipse.jetty.containerInitializerOrder","org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer, com.haizhi.webinfo, *");

    }

    private static void initServletContext(ServletContextHandler servletContext){
        servletContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        servletContext.setContextPath("/api");

        //String path = Thread.currentThread().getContextClassLoader().getResource("com/haizhi/webinfo/web/servlet").getPath();
        //servletContext.setResourceBase(path);
        //servletContext.addServlet(new ServletHolder(new TestServlet()),"/test");

        servletContext.setInitParameter("contextClass",
                AnnotationConfigWebApplicationContext.class.getName());
        servletContext.setInitParameter("contextConfigLocation", SpringConfig.class.getName());
        servletContext.setInitParameter("encoding", "UTF-8");

        servletContext.addEventListener(new ContextLoaderListener());
        servletContext.addEventListener(new RequestContextListener());

        //spring mvc
        DispatcherServlet springMVC = new DispatcherServlet();
        ServletHolder springMVCHolder = new ServletHolder(springMVC);
        springMVCHolder.setInitParameter("contextClass",AnnotationConfigWebApplicationContext.class.getName());
        springMVCHolder.setAsyncSupported(true);
        //springMVCHolder.setInitParameter("contextConfigLocation", SpringConfigMVC.class.getName());
        servletContext.addServlet(springMVCHolder,"/");

        //log4j2
        //servletContext.addEventListener(new Log4jServletContextListener());
        EnumSet<DispatcherType> allDispatcherType = EnumSet.noneOf(DispatcherType.class);
        servletContext.addFilter(Log4jServletFilter.class.getName(),"/*",allDispatcherType);
        servletContext.addEventListener(new Log4jServletContextListener());

        //编码
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        FilterHolder encodingFilterHolder = new FilterHolder(encodingFilter);
        servletContext.addFilter(encodingFilterHolder,"/*",allDispatcherType);

        /*spring-security
        FilterHolder securityHolder = new FilterHolder(new DelegatingFilterProxy());
        securityHolder.setName("springSecurityFilterChain");
        //securityHolder.setInitParameter("contextClass",AnnotationConfigWebApplicationContext.class.getName());
        //securityHolder.setInitParameter("contextConfigLocation",SpringConfigSecurity.class.getName());
        servletContext.addFilter(securityHolder,"/*",
                EnumSet.of(DispatcherType.REQUEST,DispatcherType.ASYNC));
*/

        /**
         *  shiro-spring
         */
        FilterHolder securityHolder = new FilterHolder(new DelegatingFilterProxy());
        securityHolder.setName("shiroFilter");
        securityHolder.setInitParameter("targetFilterLifecycle","true");
        servletContext.addFilter(securityHolder,"/*",
                EnumSet.of(DispatcherType.REQUEST,DispatcherType.ASYNC));
        //servletContext.addEventListener(new EnvironmentLoaderListener());
    }


}
