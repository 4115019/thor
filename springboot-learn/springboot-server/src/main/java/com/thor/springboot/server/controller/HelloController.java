package com.thor.springboot.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author huangpin
 * @date 2018-12-10
 */
@Controller
public class HelloController {

    @RequestMapping("/hello")
    @ResponseBody
    public String index(){

        return "helloWorld";
    }
}
