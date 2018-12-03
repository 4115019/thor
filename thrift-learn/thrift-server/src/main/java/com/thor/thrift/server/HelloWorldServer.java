package com.thor.thrift.server;

import com.thor.thrift.server.service.api.HelloWordService;
import com.thor.thrift.server.service.impl.HelloWorldServiceImpl;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;

import java.net.ServerSocket;

/**
 * @author huangpin
 * @date 2018-12-03
 */
public class HelloWorldServer {

    public static void main(String[] args) throws Exception {

        ServerSocket socket = new ServerSocket(8888);
        TServerSocket serverTransport = new TServerSocket(socket);

        HelloWordService.Processor processor = new HelloWordService.Processor(new HelloWorldServiceImpl());

        TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(serverTransport);
        serverArgs.processor(processor);
        TServer server = new TSimpleServer(serverArgs);

        System.out.println("Running server...");

        server.serve();
    }
}
