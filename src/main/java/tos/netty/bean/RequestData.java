package tos.netty.bean;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class RequestData implements Serializable {

    private ChannelHandlerContext ctx;
    /**
     * 类型 1:普通文本 2:文件
     */
    private Integer type;


}
