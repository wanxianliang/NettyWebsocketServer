package tos.netty.server;


import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import tos.netty.bean.RequestPlus;
import tos.netty.bean.ResponsePlus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
public class RemoteServerHandler extends SimpleChannelInboundHandler<Object> {

    private Function<RequestPlus, ResponsePlus> handleRead;

    public RemoteServerHandler(Function<RequestPlus, ResponsePlus> handleRead) {
        this.handleRead = handleRead;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object request) throws Exception {
        //建立websocket 连接
        if (request instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) request);
            return;
        }
        Integer messageType = 2;
        //业务请求处理
        RequestPlus requestPlus = null;
        if (request instanceof RequestPlus) {
            requestPlus = (RequestPlus) request;
        }
        if (request instanceof WebSocketFrame) {
            messageType = 2;
            //处理websocket客户端的消息
            String message = ((TextWebSocketFrame) request).text();
            System.out.println("收到消息:" + message);
            requestPlus = JSONObject.parseObject(message, RequestPlus.class);
        }
        String userId = requestPlus.getUserId();
        String deviceId = requestPlus.getDeviceId();
        if (StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(deviceId)) {
            log.info("参数异常");
            ctx.close();
        }
        ResponsePlus responsePlus = this.handleRead.apply(requestPlus);
        if (responsePlus == null) {
            responsePlus = ResponsePlus.build(500, null);
        }
        responsePlus.setRequestId(requestPlus.getRequestId());
        responsePlus.setMessageType(messageType);
        ConnectManager.storeConnect(userId + deviceId, ctx);
        RequestClient.writeMsg(ctx, responsePlus);
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
        ConnectManager.removeConnect(ctx);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) throws Exception {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "", null, true, 5 * 1024 * 1024);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void dealAuth(String authKey, JSONObject jsonObject, ChannelHandlerContext ctx) {
        if (StringUtil.isNullOrEmpty(authKey)) {
            return;
        }
        //建立连接标识
        if (Objects.equals(authKey, "wxllovexin")) {
            String userId = jsonObject.getString("userId");
            String deviceId = jsonObject.getString("deviceId");
            ConnectManager.storeConnect(userId + deviceId, ctx);
        } else {
            //断开链接
            ctx.close();
        }
    }

}