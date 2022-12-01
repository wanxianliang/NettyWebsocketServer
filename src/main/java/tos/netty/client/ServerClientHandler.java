package tos.netty.client;

import com.alibaba.fastjson.JSONObject;
import tos.netty.bean.ResponsePlus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import tos.netty.server.ConnectManager;

import java.util.function.Function;

class ServerClientHandler extends SimpleChannelInboundHandler<ResponsePlus> {

    private Function<ResponsePlus, ResponsePlus> readHandle;

    public ServerClientHandler(Function<ResponsePlus, ResponsePlus> readHandle) {
        this.readHandle = readHandle;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponsePlus msg) throws Exception {
        this.readHandle.apply(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ClientManager.isActive = false;
        super.channelInactive(ctx);
        //todo 断线重连
        int tryTimes = 0;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
