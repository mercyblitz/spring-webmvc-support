package com.alibaba.spring.web.servlet.mvc.util;

import com.alibaba.spring.web.TestWebMvcConfigurer;
import com.alibaba.spring.web.servlet.mvc.controller.TestController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.appendContextInitializerClassInitParameter;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.appendGlobalInitializerClassInitParameter;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.appendInitParameter;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.appendInitParameters;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.currentRequest;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.currentServletContext;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.currentWebApplicationContext;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.getRequestMappingHandlerMapping;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.getWebApplicationContext;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.isControllerAdviceBeanType;
import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.isPageRenderRequest;
import static org.junit.Assert.assertNull;
import static org.springframework.web.context.ContextLoader.CONTEXT_INITIALIZER_CLASSES_PARAM;
import static org.springframework.web.context.ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM;
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;

/**
 * {@link WebMvcUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see WebMvcUtils
 * @since 2017.01.13
 */
@ControllerAdvice
public class WebMvcUtilsTest {

    private MockServletContext servletContext;

    private MockHttpServletRequest request;

    @Before
    public void init() {

        servletContext = new MockServletContext();

        request = new MockHttpServletRequest(servletContext);

        RequestContextHolder.resetRequestAttributes();

    }

    @Test
    public void testIsControllerAdviceBeanType() {

        Assert.assertFalse(isControllerAdviceBeanType(WebMvcUtils.class));

        Assert.assertTrue(isControllerAdviceBeanType(WebMvcUtilsTest.class));

    }

    @Test
    public void testCurrentRequest() {

        MockHttpServletRequest request = new MockHttpServletRequest();

        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(requestAttributes);

        HttpServletRequest httpServletRequest = currentRequest();

        Assert.assertEquals(request, httpServletRequest);

    }

    @Test(expected = IllegalStateException.class)
    public void testCurrentRequestWithoutRequestAttributes() {
        currentRequest();
    }

    @Test(expected = IllegalStateException.class)
    public void testCurrentRequestInNotServletContainer() {

        RequestContextHolder.setRequestAttributes(new RequestAttributes() {

            @Override
            public Object getAttribute(String name, int scope) {
                return null;
            }

            @Override
            public void setAttribute(String name, Object value, int scope) {

            }

            @Override
            public void removeAttribute(String name, int scope) {

            }

            @Override
            public String[] getAttributeNames(int scope) {
                return new String[0];
            }

            @Override
            public void registerDestructionCallback(String name, Runnable callback, int scope) {

            }

            @Override
            public Object resolveReference(String key) {
                return null;
            }

            @Override
            public String getSessionId() {
                return null;
            }

            @Override
            public Object getSessionMutex() {
                return null;
            }

        });

        currentRequest();


    }

    @Test
    public void testCurrentServletContext() {

        MockHttpServletRequest request = new MockHttpServletRequest();

        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(requestAttributes);

        ServletContext servletContext = currentServletContext();

        Assert.assertNotNull(servletContext);

        request = new MockHttpServletRequest(new MockServletContext());

        requestAttributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(requestAttributes);

        servletContext = currentServletContext();

        Assert.assertNotNull(servletContext);

    }

    @Test
    public void testAppendInitParameter() {

        String parameterValue = appendInitParameter("value1");

        Assert.assertEquals("value1", parameterValue);

        parameterValue = appendInitParameter("value1", "value2");

        Assert.assertEquals("value1,value2", parameterValue);

    }

    @Test
    public void testAppendInitParameters() {

        MockServletContext servletContext = new MockServletContext();

        String parameterName = "name";

        String parameterValue = "value";

        assertNull(servletContext.getInitParameter(parameterName));

        appendInitParameters(servletContext, parameterName);

        assertNull(servletContext.getInitParameter(parameterName));

        appendInitParameters(servletContext, parameterName, parameterValue, parameterValue);

        Assert.assertEquals("value,value", servletContext.getInitParameter(parameterName));

    }

    @Test
    public void testAppendGlobalInitializerClassInitParameter() {

        MockServletContext servletContext = new MockServletContext();

        appendGlobalInitializerClassInitParameter(servletContext, ApplicationContextInitializer.class);

        Assert.assertEquals(ApplicationContextInitializer.class.getName(),
                servletContext.getInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM));

    }

    @Test
    public void testAppendContextInitializerClassInitParameter() {

        MockServletContext servletContext = new MockServletContext();

        appendContextInitializerClassInitParameter(servletContext, ApplicationContextInitializer.class);

        Assert.assertEquals(ApplicationContextInitializer.class.getName(),
                servletContext.getInitParameter(CONTEXT_INITIALIZER_CLASSES_PARAM));

    }

    @Test
    public void testIsPageRenderRequest() {

        Assert.assertFalse(isPageRenderRequest(null));

        ModelAndView modelAndView = new ModelAndView();

        Assert.assertFalse(isPageRenderRequest(modelAndView));

        modelAndView.setViewName("hello");

        Assert.assertTrue(isPageRenderRequest(modelAndView));

    }

    @Test
    public void testGetWebApplicationContextWithoutServletContext() {
        assertNull(getWebApplicationContext(request, null));
    }

    @Test
    public void testGetWebApplicationContextWithoutWebApplicationContext() {
        assertNull(getWebApplicationContext(request, servletContext));
    }

    @Test
    public void testGetWebApplicationContext() {

        WebApplicationContext webApplicationContext = new GenericWebApplicationContext();

        request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        WebApplicationContext applicationContext = getWebApplicationContext(request, servletContext);

        Assert.assertEquals(webApplicationContext, applicationContext);

        applicationContext = getWebApplicationContext(request);

        Assert.assertEquals(webApplicationContext, applicationContext);

    }

    @Test
    public void testCurrentWebApplicationContext() {

        WebApplicationContext webApplicationContext = new GenericWebApplicationContext();

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        servletContext.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        WebApplicationContext applicationContext = currentWebApplicationContext();

        Assert.assertEquals(webApplicationContext, applicationContext);

    }

    @Test
    public void testGetRequestMappingHandlerMapping() {

        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();

        servletContext.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        webApplicationContext.setServletContext(servletContext);

        webApplicationContext.register(TestController.class);
        webApplicationContext.register(DelegatingWebMvcConfiguration.class);
        webApplicationContext.register(TestWebMvcConfigurer.class);

        webApplicationContext.refresh();

        request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        RequestMappingHandlerMapping requestMappingHandlerMapping = getRequestMappingHandlerMapping(request, servletContext);

        Assert.assertNotNull(requestMappingHandlerMapping);

        Assert.assertFalse(requestMappingHandlerMapping.getHandlerMethods().isEmpty());

        requestMappingHandlerMapping = getRequestMappingHandlerMapping(request);

        Assert.assertNotNull(requestMappingHandlerMapping);

        Assert.assertFalse(requestMappingHandlerMapping.getHandlerMethods().isEmpty());

    }

}
