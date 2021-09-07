package tos.netty.encorder;

import com.alibaba.fastjson.JSONObject;
import tos.netty.bean.RequestPlus;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class RequestEncoder extends MessageToMessageEncoder<RequestPlus> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestPlus requestPlus, List<Object> list) throws Exception {
        String msg = JSONObject.toJSONString(requestPlus);
        if (msg.length() != 0) {
            list.add(ByteBufUtil.encodeString(channelHandlerContext.alloc(), CharBuffer.wrap(msg), Charset.defaultCharset()));
        }
    }
}
