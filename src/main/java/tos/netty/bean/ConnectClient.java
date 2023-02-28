package tos.netty.bean;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.io.Serializable;

@Data
public class ConnectClient implements Serializable {

    private String deviceId;

    private ChannelHandlerContext ctx;

    private String account;

    private long lastPingTime;

}