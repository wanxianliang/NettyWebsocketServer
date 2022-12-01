package tos.netty.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponsePlus implements Serializable {
    private String requestId;
    // 1:websocket 2:java-sdk
    private Integer messageType;
    private int code;
    private String message;
    private Object result;

    public static ResponsePlus buildForTimeOut() {
        ResponsePlus responsePlus = new ResponsePlus();
        responsePlus.setCode(-1);
        responsePlus.setMessage("request is timeout");
        return responsePlus;
    }

    public static ResponsePlus build(int code, Object result) {
        ResponsePlus responsePlus = new ResponsePlus();
        responsePlus.setCode(code);
        responsePlus.setResult(result);
        return responsePlus;
    }
}
