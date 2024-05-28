package com.mcp.crispy.common.config;

import com.mcp.crispy.common.interceptor.PrincipalInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final PrincipalInterceptor principalInterceptor;

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(principalInterceptor)
                .addPathPatterns("/crispy/**")
                .excludePathPatterns("/crispy/login", "/crispy/employee/findEmpId", "/crispy/employee/findEmpPw",
                                     "/crispy/employee/find/username", "/crispy/employee/find/username/result", "/crispy/employee/change/password"); // 로그인 페이지와 같은 예외 URL을 지정합니다.
    }
}