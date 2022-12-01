package tos.netty.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestPlus implements Serializable {
    private String requestType = "java-jdk-business-request";
    /**
     * request uuid
     */
    private String requestId;
    /**
     * 当前时间
     */
    private Long currentTime = System.currentTimeMillis();
    /**
     * request path
     */
    private String path;

    private String userId;
    private String deviceId;

    private Object body;

}
