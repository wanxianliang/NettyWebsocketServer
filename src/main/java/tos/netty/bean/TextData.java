package tos.netty.bean;

import lombok.Data;

import java.io.Serializable;


@Data
public class TextData extends RequestWithTextData {
    //1.认证 2.数据透传 3.ping保活
    private Integer action;

    private String account;

    private String deviceId;

    private String toDeviceId;

    private String password;

    private String data;
}
