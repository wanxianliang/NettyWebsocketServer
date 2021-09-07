package tos.netty.client;

import tos.netty.bean.RequestPlus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

public class RequestThread implements Runnable {

    private Bootstrap bootstrap;

    private RequestPlus requestPlus;
    private String host;
    private int port;

    public static RequestThread newRequestThread(Bootstrap bootstrap, String host, int port, RequestPlus requestPlus) {
        RequestThread requestThread = new RequestThread();
        requestThread.bootstrap = bootstrap;
        requestThread.requestPlus = requestPlus;
        requestThread.host = host;
        requestThread.port = port;
        return requestThread;
    }

    @Override
    public void run() {
        ChannelFuture channelFuture = null;
        long startConnectTime = System.currentTimeMillis();
        try {
            channelFuture = bootstrap.connect(host, port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    System.out.print("connect failed");
                } else {
                    channelFuture.channel().writeAndFlush(requestPlus);
                }
            }
        });
    }

}
