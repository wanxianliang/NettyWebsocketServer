package tos.netty.bean;

import io.netty.buffer.ByteBuf;
import lombok.Data;


@Data
public class RequestWithBinaryData extends RequestData {

    private ByteBuf byteBuf;

}
