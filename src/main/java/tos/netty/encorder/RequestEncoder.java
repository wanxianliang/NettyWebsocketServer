package tos.netty.encorder;

import com.alibaba.fastjson.JSONObject;
import tos.netty.bean.RequestData;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class RequestEncoder extends MessageToMessageEncoder<RequestData> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestData requestPlus, List<Object> list) throws Exception {
        String msg = JSONObject.toJSONString(requestPlus);
        if (msg.length() != 0) {
            list.add(ByteBufUtil.encodeString(channelHandlerContext.alloc(), CharBuffer.wrap(msg), Charset.defaultCharset()));
        }
    }
}
