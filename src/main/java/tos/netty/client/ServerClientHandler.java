package tos.netty.client;

import com.alibaba.fastjson.JSONObject;
import tos.netty.bean.ResponsePlus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

class ServerClientHandler extends SimpleChannelInboundHandler<ResponsePlus> {

    private Function<ResponsePlus, ResponsePlus> readHandle;

    public ServerClientHandler(Function<ResponsePlus, ResponsePlus> readHandle) {
        this.readHandle = readHandle;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponsePlus msg) throws Exception {
        this.readHandle.apply(msg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
