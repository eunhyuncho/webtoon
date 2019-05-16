package com.project.picktoon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**

 WebMvcConfigurer를 구현하고 있는 설정파일은 @WebMvcTest를 사용할 때 호출된다.
 ModelMapper를 다른 설정 파일에 넣으면 찾지 못할 수 가 있다.

 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // webjars 경로 추가...
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/static/webjars/", "classpath:/META-INF/resources/webjars/");
        registry
                .addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry
                .addResourceHandler("/icon/**")
                .addResourceLocations("classpath:/static/icon/");
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // sec 태그 사용 가능
    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver, SpringSecurityDialect sec) {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(sec); // Enable use of "sec"
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    // ConnectTimeout - 10초 , ReadTimeout - 10초
    // RestTemplate은 thread-safe한 객체!
    @Bean
    public RestTemplate restTemplate(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10*1000);
        factory.setReadTimeout(10*1000);
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper(){ return new ObjectMapper(); }
}
