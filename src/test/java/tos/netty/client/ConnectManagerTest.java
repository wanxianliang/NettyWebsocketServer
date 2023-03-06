package tos.netty.client;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import tos.netty.bean.*;
import tos.netty.enums.RequestTypeEnum;
import tos.netty.server.ServerRemote;

import java.util.function.Function;


@Slf4j
public class ConnectManagerTest {

    Function<RequestData, Void> handleRequest = requestPlus -> {
        if (RequestTypeEnum.TEXT.getType().equals(requestPlus.getType())) {
            RequestWithTextData requestWithTextData = (RequestWithTextData) requestPlus;
            String text = requestWithTextData.getText();
            log.info("server receive text data {}", text);
        } else if (RequestTypeEnum.Binary.getType().equals(requestPlus.getType())) {
            RequestWithBinaryData requestWithFileData = (RequestWithBinaryData) requestPlus;
            ByteBuf buf = requestWithFileData.getByteBuf();
            log.info("server receive binary data,buf size {}", buf.capacity());
        }
        return null;
    };

    @Test
    public void startServer() throws Exception {
        ServerRemote serverRemote = ServerRemote.newServerInstance(2222, requestPlus -> {
            if (RequestTypeEnum.TEXT.getType().equals(requestPlus.getType())) {
                RequestWithTextData requestWithTextData = (RequestWithTextData) requestPlus;
                String text = requestWithTextData.getText();
                log.info("server receive text data {}", text);
            } else if (RequestTypeEnum.Binary.getType().equals(requestPlus.getType())) {
                RequestWithBinaryData requestWithFileData = (RequestWithBinaryData) requestPlus;
                ByteBuf buf = requestWithFileData.getByteBuf();
                log.info("server receive binary data,buf size {}", buf.capacity());
            }
            return null;
        });
        serverRemote.run();
    }

}