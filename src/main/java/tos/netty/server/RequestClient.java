package tos.netty.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import tos.netty.bean.ResponsePlus;

import java.util.Objects;

public class RequestClient {

    public static void sendMsg(String key, ResponsePlus responsePlus) {
        ChannelHandlerContext channelHandlerContext = ConnectManager.getConnect(key);
        if (channelHandlerContext == null) {
            System.out.println("没有符合链接");
            return;
        }
        System.out.println("开始向web发送记录");
        channelHandlerContext.channel().writeAndFlush(responsePlus);
    }

    public static void sendMsgWebSocket(String key, ResponsePlus responsePlus) {
        ChannelHandlerContext channelHandlerContext = ConnectManager.getConnect(key);
        if (channelHandlerContext == null) {
            System.out.println("没有符合链接");
            return;
        }
        System.out.println("开始向web发送记录");
        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(responsePlus)));
    }

    public static void writeMsg(ChannelHandlerContext ctx, ResponsePlus responsePlus) {
        responsePlus.setMessageType(1);
        if (Objects.equals(1, responsePlus.getMessageType())) {
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(responsePlus)));
        } else {
            ctx.channel().writeAndFlush(responsePlus);
        }
    }

}
