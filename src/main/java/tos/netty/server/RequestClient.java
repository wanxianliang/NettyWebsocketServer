package tos.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class RequestClient {

    public static void writeMsg(ChannelHandlerContext ctx, String res) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame(res));
    }

    public static void writeByteBuf(ChannelHandlerContext ctx, ByteBuf buf) {
        ctx.channel().writeAndFlush(new BinaryWebSocketFrame(buf));
    }

}
