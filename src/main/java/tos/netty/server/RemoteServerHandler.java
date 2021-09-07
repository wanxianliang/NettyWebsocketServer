package tos.netty.server;


import com.alibaba.fastjson.JSONObject;
import tos.netty.bean.RequestPlus;
import tos.netty.bean.ResponsePlus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.function.Function;

public class RemoteServerHandler extends SimpleChannelInboundHandler<RequestPlus> {

    private Function<RequestPlus, ResponsePlus> handleRead;

    public RemoteServerHandler(Function<RequestPlus, ResponsePlus> handleRead) {
        this.handleRead = handleRead;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestPlus requestPlus) throws Exception {
        ResponsePlus responsePlus = this.handleRead.apply(requestPlus);
        if (responsePlus == null) {
            responsePlus = ResponsePlus.build(500, null);
        }
        responsePlus.setRequestId(requestPlus.getRequestId());
        ctx.writeAndFlush(responsePlus);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}