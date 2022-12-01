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
    private ChannelFuture channelFuture;
    private String host;
    private int port;

    public static RequestThread newRequestThread(ChannelFuture channelFuture, RequestPlus requestPlus) {
        RequestThread requestThread = new RequestThread();
        requestThread.requestPlus = requestPlus;
        requestThread.channelFuture = channelFuture;
        return requestThread;
    }

    @Override
    public void run() {
        try {
            channelFuture.channel().writeAndFlush(requestPlus).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
