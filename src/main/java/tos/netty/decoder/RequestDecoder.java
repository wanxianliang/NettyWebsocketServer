package tos.netty.decoder;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import tos.netty.bean.RequestData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RequestDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list) throws Exception {
        String msgStr = msg.toString(StandardCharsets.UTF_8);
        System.out.println("数据来了" + msgStr);
        if (msgStr.contains("java-jdk-business-request")) {
            RequestData requestPlus = JSONObject.parseObject(msgStr, RequestData.class);
            list.add(requestPlus);
        } else {
            ByteBuf newMSg = msg.copy();
            list.add(newMSg);
        }
    }

}
