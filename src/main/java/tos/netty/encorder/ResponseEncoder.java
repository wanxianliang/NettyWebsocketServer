package tos.netty.encorder;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBufUtil;
import tos.netty.bean.ResponsePlus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class ResponseEncoder extends MessageToMessageEncoder<ResponsePlus> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponsePlus responsePlus, List<Object> list) throws Exception {
        if (Objects.equals(responsePlus.getMessageType(), 1)) {
            list.add(responsePlus);
        } else {
            String msg = JSONObject.toJSONString(responsePlus);
            list.add(ByteBufUtil.encodeString(channelHandlerContext.alloc(), CharBuffer.wrap(msg), Charset.defaultCharset()));
        }
    }
}
