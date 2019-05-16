package com.project.picktoon.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.net.URL;

@Configuration
public class WebDriverConfig {
//    @Bean
//    public ChromeDriverService chromeDriverService(){
//        String classpathRelativeLocation = "driver/chromedriver";
//        URL url = this.getClass().getClassLoader().getResource(classpathRelativeLocation);
//        File file = new File(url.getFile());
//        ChromeDriverService.Builder bldr = (new ChromeDriverService.Builder())
//                .usingDriverExecutable(file)
//                .usingAnyFreePort();
//        return bldr.build();
//    }

    @Bean
    @Scope("prototype")
    public WebDriver webDriver(){
//        System.setProperty("webdriver.chrome.driver",
//                "/Users/jungmisu/driver/chromedriver");
        System.setProperty("webdriver.chrome.driver",
                "/home/ubuntu/driver/chromedriver");
        ChromeOptions options = new ChromeOptions();
//        options.setBinary("/usr/bin/google-chrome-stable");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1920x1080");
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        //return new ChromeDriver(chromeDriverService, options);
        return new ChromeDriver(options);
    }

//    @Bean(name = "poolTargetSourceWebDriver", initMethod = "initializeObjects")
//    WebDriverObjectPool myObjectPool() {
//        WebDriverObjectPool pool = new WebDriverObjectPool();
//        pool.setMaxSize(15);
//        pool.setTargetClass(WebDriver.class);
//        pool.setMinIdle(15);
//        pool.setTargetBeanName("webDriver");
//        return pool;
//    }
//    @Bean(name = "webDrvierPool")
//    CommonsPool2TargetSource webDriverPool(){
//        CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
//        pool.setMaxSize(5);
//        //pool.setTargetClass(WebDriver.class);
//        pool.setMaxIdle(2);
//        pool.setTargetBeanName("mydrvier");
//        return pool;
//    }
//
//    @Bean
//    ProxyFactoryBean proxyFactoryBean() {
//        ProxyFactoryBean factoryBean = new ProxyFactoryBean();
//        factoryBean.setTargetSource(webDriverPool());
//        return factoryBean;
//    }
}
