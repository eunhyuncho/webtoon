package com.project.picktoon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// spring security 설정 파일은 보통
// WebSecurityConfigurerAdapter 를 상속받아서 만든다.
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public AuthenticationSuccessHandler loginSuccessHandler;


    // 아예 인가처리를 하지 않는 (무시하는 URL설정) - 이미지 or css, javascript
    //"/static/**", "/js/**", "/css/**", "/img/**", "/json/**"
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(new AntPathRequestMatcher("/**.html"))
                .requestMatchers(new AntPathRequestMatcher("/static/**"))
                .requestMatchers(new AntPathRequestMatcher("/icon/**"))
                .requestMatchers(new AntPathRequestMatcher("/jquery/**"))
                .requestMatchers(new AntPathRequestMatcher("/js/**"))
                .requestMatchers(new AntPathRequestMatcher("/css/**"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .logout() // logout설정
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/main")
                .permitAll().and()
                .authorizeRequests() // 인가에 대한 설정
                .antMatchers("/addwebtoon").permitAll()
                .antMatchers("/webtoons/mywebtoons").hasAnyRole("USER","ADMIN")
                .antMatchers("/webtoons/**").permitAll()
                .antMatchers("/users/join").permitAll()
                .antMatchers("/users/welcome").permitAll()
                .antMatchers("/users/login").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin() // 사용자가 정의하는 로그인 화면을 만들겠다.
                .loginProcessingUrl("/users/login") // 로그인 화면
                .loginPage("/users/login") // 사용자가 입력한 id, password가 전달되는 url경로(필터가처리)
                .usernameParameter("email")
                .passwordParameter("password")
                .failureUrl("/users/login?fail=true")
                .successHandler(loginSuccessHandler)    // 로그인 후 처리...
                //.defaultSuccessUrl("/main", true)
                .and()
                .csrf().ignoringAntMatchers("/**")
                .and()
                .headers().frameOptions().sameOrigin();
        //api 사용시 csrf문제..
    }
}
