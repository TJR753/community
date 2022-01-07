package com.example.community.controller.advice;

import com.example.community.utils.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice(annotations = Controller.class)
public class ControllerAdvices {
    private static final Logger logger= LoggerFactory.getLogger(ControllerAdvices.class);
    @ExceptionHandler({Exception.class})
    public void getErrorPage(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String header = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(header)){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().println(CommunityUtil.parseJson("0","服务器异常"));
        }else{
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
