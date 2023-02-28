package tos.netty.bean;


import lombok.Data;

@Data
public class RequestWithTextData extends RequestData {
    private String text;
}
