package tos.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContinuationWebSocketFrameHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf data = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof BinaryWebSocketFrame) {
                if (!frame.isFinalFragment()) {
                    if (data != null) {
                        data.release();
                    }
                    ByteBuf buf = frame.content();
                    data = ctx.alloc().buffer(buf.readableBytes());
                    data.writeBytes(buf);
                    buf.release();
                    return;
                } else {
                    ctx.fireChannelRead(msg);
                    if (data != null) {
                        data.release();
                        data = null;
                    }
                    return;
                }
            } else if (frame instanceof ContinuationWebSocketFrame) {
                if (data == null) {
                    log.error("ContinuationWebSocketFrame找不到初始的BinaryWebSocketFrame");
                    frame.content().release();
                    return;
                }
                data.writeBytes(frame.content());
                frame.content().release();
                if (frame.isFinalFragment()) {
                    ctx.fireChannelRead(new BinaryWebSocketFrame(data));
                    data = null;
                    return;
                }
            } else {
                if (data != null) {
                    data.release();
                    data = null;
                }
                ctx.fireChannelRead(msg);
            }
        } else {
            if (data != null) {
                data.release();
                data = null;
            }
            ctx.fireChannelRead(msg);
        }
    }
}
