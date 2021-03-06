package com.example.community.controller.interceptor;

import com.example.community.domain.LoginTicket;
import com.example.community.domain.User;
import com.example.community.service.UserService;
import com.example.community.utils.CookieUtil;
import com.example.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getTicket(request, "ticket");
        if(ticket!=null){
            LoginTicket loginTicket=userService.getLoginTicket(ticket);
            if(loginTicket!=null&&loginTicket.getStatus()!=1&&loginTicket.getExpired().after(new Date())){
                User user = userService.getUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
                Authentication token = new UsernamePasswordAuthenticationToken(user,user.getPassword(), userService.getAuthority(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(token));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null&&modelAndView!=null){
            modelAndView.addObject("user",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.remove();
        SecurityContextHolder.clearContext();
    }
}
