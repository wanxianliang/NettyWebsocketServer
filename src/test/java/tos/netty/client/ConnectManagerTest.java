package tos.netty.client;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import tos.netty.bean.RequestPlus;
import tos.netty.bean.ResponsePlus;
import tos.netty.server.ServerRemote;

import java.util.concurrent.ExecutionException;


public class ConnectManagerTest {

    @Test
    public void sendMsg() throws ExecutionException, InterruptedException {
        ConnectManager connectManager = ConnectManager.newConnectInstance("127.0.0.1", 8080);
        RequestPlus requestPlus = new RequestPlus();
        String requestId = connectManager.sendMsg(requestPlus);
        connectManager.handleResult(requestId, responsePlus -> {
            System.out.println("responsePlus:" + JSONObject.toJSONString(responsePlus));
        });
    }

    @Test
    public void startServer() throws Exception {
        ServerRemote serverRemote = ServerRemote.newServerInstance(8080, requestPlus -> {
            return ResponsePlus.build(0, "ok");
        });
        serverRemote.run();
    }

}