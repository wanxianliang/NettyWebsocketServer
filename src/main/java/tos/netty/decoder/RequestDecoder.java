package tos.netty.decoder;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpServerCodec;
import tos.netty.bean.RequestPlus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RequestDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> list) throws Exception {
        String msgStr = msg.toString(StandardCharsets.UTF_8);
        System.out.println("数据来了" + msgStr);
        if (msgStr.contains("java-jdk-business-request")) {
            RequestPlus requestPlus = JSONObject.parseObject(msgStr, RequestPlus.class);
            list.add(requestPlus);
        } else {
            ByteBuf newMSg = msg.copy();
            list.add(newMSg);
        }
    }

}
