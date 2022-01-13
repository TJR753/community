package com.example.community.controller.interceptor;

import com.example.community.service.DataService;
import com.example.community.utils.HostHolder;
import com.example.community.utils.RedisKeyUtil;
import org.elasticsearch.action.support.HandledTransportAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Administrator
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private DataService dataService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String remoteAddr = request.getRemoteAddr();
        dataService.recordUV(remoteAddr);
        if(hostHolder.getUser()!=null){
            dataService.recordDAU(hostHolder.getUser().getId());
        }
        return true;
    }
}
