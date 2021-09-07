package tos.netty.encorder;

import com.alibaba.fastjson.JSONObject;
import tos.netty.bean.ResponsePlus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

public class ResponseEncoder extends MessageToMessageEncoder<ResponsePlus> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponsePlus responsePlus, List<Object> list) throws Exception {
        list.add(JSONObject.toJSONString(responsePlus));
    }
}
