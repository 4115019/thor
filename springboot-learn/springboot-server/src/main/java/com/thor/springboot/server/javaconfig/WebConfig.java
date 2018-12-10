package com.thor.springboot.server.javaconfig;

import com.thor.springboot.server.util.argument.resolver.JsonArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author huangpin
 * @date 2018-12-10
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(new JsonArgumentResolver());
    }
}
