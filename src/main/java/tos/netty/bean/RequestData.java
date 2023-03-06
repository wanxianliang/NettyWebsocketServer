package tos.netty.bean;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class RequestData implements Serializable {

    private ChannelHandlerContext ctx;
    /**
     * @see tos.netty.enums.RequestTypeEnum
     */
    private Integer type;


}
