package tos.netty.enums;

import lombok.Getter;

/**
 * 请求类型枚举
 */
public enum RequestTypeEnum {

    TEXT(1),
    Binary(2),
    PONG(3);

    @Getter
    private Integer type;

    RequestTypeEnum(Integer type) {
        this.type = type;
    }
}
