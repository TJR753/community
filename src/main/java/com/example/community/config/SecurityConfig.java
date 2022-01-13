package com.example.community.config;

import com.example.community.utils.CommunityConstant;
import com.example.community.utils.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Administrator
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
            "/user/**",
                        "/message/**",
                        "/like",
                        "/follow",
                        "/unfollow",
                        "/addDiscussPost",
                        "/comment/**"
                ).hasAnyAuthority(
                        AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR
                )
                .anyRequest().permitAll()
                .and().csrf().disable();
        http.exceptionHandling()
            .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String header = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(header)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter pw = response.getWriter();
                            pw.println(CommunityUtil.parseJson("403","您还没有登陆"));
                        }else{
                            response.sendRedirect(request.getContextPath()+"/login");
                        }
                    }
                })
            .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String header = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(header)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter pw = response.getWriter();
                            pw.println(CommunityUtil.parseJson("403","您没有权限访问"));
                        }else{
                            response.sendRedirect(request.getContextPath()+"/index");
                        }
                    }
                });
        http.logout().logoutUrl("/override");
    }
}
