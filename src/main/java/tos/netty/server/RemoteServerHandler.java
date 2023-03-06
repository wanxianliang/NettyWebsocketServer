package tos.netty.server;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import tos.netty.bean.RequestData;
import tos.netty.bean.RequestWithBinaryData;
import tos.netty.bean.RequestWithPong;
import tos.netty.bean.RequestWithTextData;
import tos.netty.enums.RequestTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class RemoteServerHandler extends SimpleChannelInboundHandler<Object> {

    public static Map<ChannelHandlerContext, ByteBuf> continueData = new HashMap<>();

    private Function<RequestData, Void> handleRead;

    private ByteBuf data = null;

    public RemoteServerHandler(Function<RequestData, Void> handleRead) {
        this.handleRead = handleRead;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object request) throws Exception {
        //建立websocket 连接
        if (request instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) request);
            return;
        }
        if (request instanceof TextWebSocketFrame) {
            //处理websocket客户端的消息
            String message = ((TextWebSocketFrame) request).text();
            System.out.println("收到消息:" + message);
            RequestWithTextData requestData = new RequestWithTextData();
            requestData.setCtx(ctx);
            requestData.setType(RequestTypeEnum.TEXT.getType());
            requestData.setText(message);
            this.handleRead.apply(requestData);
        } else if (request instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) request;
            ByteBuf byteBuf = binaryWebSocketFrame.content();
            RequestWithBinaryData requestData = new RequestWithBinaryData();
            requestData.setCtx(ctx);
            requestData.setType(2);
            requestData.setByteBuf(byteBuf);
            this.handleRead.apply(requestData);
        } else if (request instanceof PongWebSocketFrame) {
            RequestWithPong requestData = new RequestWithPong();
            requestData.setCtx(ctx);
            requestData.setType(RequestTypeEnum.PONG.getType());
            this.handleRead.apply(requestData);
        }

    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立了一个链接");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭了一个链接");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
//        ConnectManager.removeConnect(ctx);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) throws Exception {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "", null, true, 10 * 1024 * 1024);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

}