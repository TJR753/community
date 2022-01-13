package com.example.community.controller;

import com.example.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author Administrator
 */
@Controller
public class DataController {
    @Autowired
    private DataService dataService;
    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String data(){
        return "/site/admin/data";
    }
    @RequestMapping(path = "/getUV",method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        Long countUV = dataService.countUV(start, end);
        model.addAttribute("countUV",countUV);
        model.addAttribute("startUV",start);
        model.addAttribute("endUV",end);
        return "forward:/data";
    }
    @RequestMapping(path = "/getDAU",method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        Long countDAU = dataService.countUV(start, end);
        model.addAttribute("countDAU",countDAU);
        model.addAttribute("startDAU",start);
        model.addAttribute("endDAU",end);
        return "forward:/data";
    }
}
