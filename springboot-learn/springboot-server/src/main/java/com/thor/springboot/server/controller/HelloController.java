package com.thor.springboot.server.controller;

import com.thor.springboot.server.model.HelloReqDTO;
import com.thor.springboot.server.util.argument.resolver.JSONParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author huangpin
 * @date 2018-12-10
 */
@Controller
public class HelloController {

    @RequestMapping(value = "/hello",
            method = RequestMethod.POST)
    @ResponseBody
    public String index(@JSONParam HelloReqDTO reqDTO) {
        return "helloWorld";
    }
}