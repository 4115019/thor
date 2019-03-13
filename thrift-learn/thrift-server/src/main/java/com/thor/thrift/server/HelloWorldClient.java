package com.thor.thrift.server;

import com.thor.thrift.server.model.Request;
import com.thor.thrift.server.model.RequestType;
import com.thor.thrift.server.service.api.HelloWordService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * @author huangpin
 * @date 2018-12-03
 */
public class HelloWorldClient {

    public static void main(String[] args) throws TException {
        TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 8888));
        //transport =

        TProtocol protocol = new TBinaryProtocol(transport);
//        TProtocol protocol = new TCompactProtocol(transport);
//        TProtocol protocol = new TJSONProtocol(transport);
        HelloWordService.Client client = new HelloWordService.Client(protocol);
        transport.open();

        /**
         * 第一种请求方式
         */
        Request request = new Request()
                .setType(RequestType.SAY_HELLO).setName("huangpin").setAge(24);
        System.out.println(client.doAction(request));

        /**
         * 第二种请求方式
         */
//        request.setType(RequestType.QUERY_TIME).setName("huangpin");
//        System.out.println(client.doAction(request));

        transport.close();

    }
}
