package org.sbolbin.crpt.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(ApplicationConfig.class);
        springContext.setServletContext(servletContext);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(springContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

        ServletRegistration.Dynamic dispatcherServletReg = servletContext.addServlet("dispatcher", dispatcherServlet);
        dispatcherServletReg.setLoadOnStartup(1);
        dispatcherServletReg.addMapping("/");
    }
}
