package com.alibaba.spring.web.servlet.config.annotation;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.spring.util.FieldUtils.getFieldValue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * {@link ConfigurableContentNegotiationManagerWebMvcConfigurer} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurableContentNegotiationManagerWebMvcConfigurer
 * @since 2017.03.23
 */
public class ConfigurableContentNegotiationManagerWebMvcConfigurerTest {


    @Test
    public void testConfigureContentNegotiationOnDefaultValues() {

        WebMvcConfigurer webMvcConfigurer =
                new ConfigurableContentNegotiationManagerWebMvcConfigurer(new HashMap<String, String>());

        ContentNegotiationConfigurer configurer = new ContentNegotiationConfigurer(new MockServletContext());

        webMvcConfigurer.configureContentNegotiation(configurer);

        ContentNegotiationManagerFactoryBean factoryBean = getContentNegotiationManagerFactoryBean(configurer);

        Assert.assertTrue(getFieldValue(factoryBean, "favorPathExtension", boolean.class));
        Assert.assertFalse(getFieldValue(factoryBean, "favorParameter", boolean.class));
        Assert.assertFalse(getFieldValue(factoryBean, "ignoreAcceptHeader", boolean.class));
        Assert.assertNull(getFieldValue(factoryBean, "useJaf", Boolean.class));
        Assert.assertEquals("format", getFieldValue(factoryBean, "parameterName", String.class));
        Assert.assertTrue(getFieldValue(factoryBean, "mediaTypes", Map.class).isEmpty());
        Assert.assertNull(getFieldValue(factoryBean, "defaultContentType", MediaType.class));

    }

    @Test
    public void testConfigureContentNegotiation() {

        Map<String, String> properties = new HashMap<String, String>();

        properties.put("favorPathExtension", "false");
        properties.put("favorParameter", "true");
        properties.put("ignoreAcceptHeader", "true");
        properties.put("useJaf", "true");
        properties.put("parameterName", "test-format");
        properties.put("mediaTypes.html", TEXT_HTML_VALUE);
        properties.put("mediaTypes.xml", APPLICATION_XML_VALUE);
        properties.put("mediaTypes.json", APPLICATION_JSON_VALUE);
        properties.put("mediaTypes.gif", IMAGE_GIF_VALUE);
        properties.put("mediaTypes.jpeg", IMAGE_JPEG_VALUE);
        properties.put("defaultContentType", TEXT_HTML_VALUE);


        WebMvcConfigurer webMvcConfigurer = new ConfigurableContentNegotiationManagerWebMvcConfigurer(properties);

        ContentNegotiationConfigurer configurer = new ContentNegotiationConfigurer(new MockServletContext());

        ContentNegotiationManagerFactoryBean factoryBean = getContentNegotiationManagerFactoryBean(configurer);

        webMvcConfigurer.configureContentNegotiation(configurer);

        Assert.assertFalse(getFieldValue(factoryBean, "favorPathExtension", boolean.class));
        Assert.assertTrue(getFieldValue(factoryBean, "favorParameter", boolean.class));
        Assert.assertTrue(getFieldValue(factoryBean, "ignoreAcceptHeader", boolean.class));

        // "userJaf" field is removed since Spring Framework 5.0
        if (ReflectionUtils.findField(factoryBean.getClass(), "userJaf", Boolean.class) != null) {
            Assert.assertTrue(getFieldValue(factoryBean, "useJaf", Boolean.class));
        }

        // "ignoreUnknownPathExtensions" field in introduced since Spring Framework 5.0
        if (ReflectionUtils.findField(factoryBean.getClass(), "ignoreUnknownPathExtensions", boolean.class) != null) {
            Assert.assertTrue(getFieldValue(factoryBean, "ignoreUnknownPathExtensions", boolean.class));
        }

        // "useRegisteredExtensionsOnly" field in introduced since Spring Framework 5.0
        if (ReflectionUtils.findField(factoryBean.getClass(), "useRegisteredExtensionsOnly", Boolean.class) != null) {
            Assert.assertFalse(getFieldValue(factoryBean, "useRegisteredExtensionsOnly", Boolean.class));
        }

        Assert.assertEquals("test-format", getFieldValue(factoryBean, "parameterName", String.class));

        Map<String, MediaType> mediaTypesMap = getFieldValue(factoryBean, "mediaTypes", Map.class);

        Assert.assertEquals("html", mediaTypesMap.get("html").getSubtype());
        Assert.assertEquals("xml", mediaTypesMap.get("xml").getSubtype());
        Assert.assertEquals("json", mediaTypesMap.get("json").getSubtype());
        Assert.assertEquals("gif", mediaTypesMap.get("gif").getSubtype());
        Assert.assertEquals("jpeg", mediaTypesMap.get("jpeg").getSubtype());

    }

    private ContentNegotiationManagerFactoryBean getContentNegotiationManagerFactoryBean(ContentNegotiationConfigurer configurer) {

        ContentNegotiationManagerFactoryBean factoryBean = getFieldValue(configurer, "factory", ContentNegotiationManagerFactoryBean.class);

        if (factoryBean == null) {
            factoryBean = getFieldValue(configurer, "factoryBean", ContentNegotiationManagerFactoryBean.class);
        }

        return factoryBean;
    }


}
