package tos.netty.client;

import org.junit.Test;
import tos.netty.bean.RequestPlus;
import tos.netty.bean.ResponsePlus;
import tos.netty.server.RequestClient;
import tos.netty.server.ServerRemote;

import java.util.concurrent.ExecutionException;


public class ConnectManagerTest {

    @Test
    public void sendMsgApp() throws ExecutionException, InterruptedException {
        String remoteAddress = "127.0.0.1";
        int port = 2222;
        ClientManager connectManager = ClientManager.newConnectInstance(remoteAddress, port);
        RequestPlus requestPlus = new RequestPlus();
        requestPlus.setUserId("wxl");
        requestPlus.setDeviceId("app");
        requestPlus.setBody("我是来自app的内容23434");
        String requestId = connectManager.sendMsg(requestPlus);
        connectManager.handleResult(requestId, responsePlus -> {
        });
        Thread.sleep(2000000);
    }

    @Test
    public void sendMsgWeb() throws ExecutionException, InterruptedException {
        String remoteAddress = "127.0.0.1";
        int port = 2222;
        ClientManager connectManager = ClientManager.newConnectInstance(remoteAddress, port);
        RequestPlus requestPlus = new RequestPlus();
        requestPlus.setUserId("wxl");
        requestPlus.setDeviceId("web");
        requestPlus.setBody("我是来自web的内容");
        String requestId = connectManager.sendMsg(requestPlus);
        connectManager.handleResult(requestId, responsePlus -> {
        });
        Thread.sleep(1000000L);
    }

    @Test
    public void startServer() throws Exception {
        ServerRemote serverRemote = ServerRemote.newServerInstance(2222, requestPlus -> {
            if (requestPlus.getDeviceId().contains("app")) {
                ResponsePlus responsePlus = new ResponsePlus();
                responsePlus.setCode(200);
                responsePlus.setMessage("success");
                responsePlus.setResult(requestPlus.getBody());
                RequestClient.sendMsgWebSocket(requestPlus.getUserId() + "web", responsePlus);
            }
            return ResponsePlus.build(0, "收到消息了:" + requestPlus.getBody());
        });
        serverRemote.run();
    }

}