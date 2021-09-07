package tos.netty.decoder;

import com.alibaba.fastjson.JSONObject;
import tos.netty.bean.RequestPlus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

public class RequestDecoder extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, String msg, List<Object> list) throws Exception {
        RequestPlus requestPlus = JSONObject.parseObject(msg, RequestPlus.class);
        list.add(requestPlus);
    }
}
