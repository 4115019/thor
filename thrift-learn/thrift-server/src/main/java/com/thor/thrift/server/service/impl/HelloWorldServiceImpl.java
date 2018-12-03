package com.thor.thrift.server.service.impl;

import com.thor.thrift.server.model.Request;
import com.thor.thrift.server.model.RequestException;
import com.thor.thrift.server.model.RequestType;
import com.thor.thrift.server.service.api.HelloWordService;
import org.apache.thrift.TException;

import java.util.Date;

/**
 * @author huangpin
 * @date 2018-12-03
 */
public class HelloWorldServiceImpl implements HelloWordService.Iface {

    @Override
    public String doAction(Request request) throws TException {
        System.out.println("Get request: " + request);

        if (request.getName() != null || request.getType() == null) {

            throw new RequestException();

        }

        String result = "Hello, " + request.getName();

        if (request.getType() == RequestType.SAY_HELLO) {

            result += ", Welcome!";

        } else {

            result += ", Now is " + new Date().toLocaleString();

        }

        return result;
    }
}
