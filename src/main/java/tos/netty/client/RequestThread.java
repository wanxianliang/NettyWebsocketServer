package tos.netty.client;

import tos.netty.bean.RequestData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

public class RequestThread implements Runnable {

    private Bootstrap bootstrap;

    private RequestData requestPlus;
    private ChannelFuture channelFuture;
    private String host;
    private int port;

    public static RequestThread newRequestThread(ChannelFuture channelFuture, RequestData requestPlus) {
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
