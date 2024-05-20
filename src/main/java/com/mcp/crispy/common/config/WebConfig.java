package com.mcp.crispy.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/crispy/main");
        registry.addRedirectViewController("/crispy", "/crispy/main");
        registry.addRedirectViewController("/crispy/", "/crispy/main");
        registry.addRedirectViewController("/CRISPY", "/crispy/main");
        registry.addRedirectViewController("/CRISPY/", "/crispy/main");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///Users/baeyeong-ug/Desktop/image/signatures/");
        registry.addResourceHandler("/profiles/**")
                .addResourceLocations("file:///Users/baeyeong-ug/Desktop/image/profiles/");
        registry.addResourceHandler("/franchise/**")
                .addResourceLocations("file:///Users/baeyeong-ug/Desktop/image/profiles/");
    }
}