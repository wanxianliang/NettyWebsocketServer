package tos.netty.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务配置
 */
@Data
public class ServerConfig implements Serializable {

    private String webSocketPath;

    private Integer SO_RCVBUF;
    
    private Integer SO_SNDBUF;

    private Integer maxFrameSize;

    private Integer httpMaxContentSize;

    private Integer httpMaxChunkSize;
}
