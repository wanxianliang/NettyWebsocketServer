package tos.netty.client;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import tos.netty.bean.*;
import tos.netty.server.ConnectManager;
import tos.netty.server.RequestClient;
import tos.netty.server.ServerRemote;

import java.net.URI;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


@Slf4j
public class ConnectManagerTest {

    @Test
    public void startServer() throws Exception {
        ServerRemote serverRemote = ServerRemote.newServerInstance(2222, requestPlus -> {
            if (requestPlus.getType().equals(1)) {
                RequestWithTextData requestWithTextData = (RequestWithTextData) requestPlus;
                String text = requestWithTextData.getText();
                TextData textData = JSONObject.parseObject(text, TextData.class);
                if (textData.getAction() == 1) {
                    //认证
                    if (Objects.equals(textData.getAccount(), "wanxianliang") && Objects.equals(textData.getPassword(), "wxllovexin")) {
                        //存储连接
                        ConnectManager.checkAndStoreConnect(textData.getAccount(), textData.getDeviceId(), requestPlus.getCtx());
                    } else {
                        //关闭连接
                        requestWithTextData.getCtx().close();
                    }
                } else if (textData.getAction() == 2) {
                    //透传
                    ConnectClient connectClient = ConnectManager.getConnect(textData.getAccount(), textData.getToDeviceId());
                    if (connectClient == null) {
                        log.info("没有找到客户端");
                        return null;
                    }
                    RequestClient.writeMsg(connectClient.getCtx(), textData.getData());
                } else if (textData.getAction() == 3) {
                    //ping 更新在线时间
                    ConnectManager.checkAndStoreConnect(textData.getAccount(), textData.getDeviceId(), requestPlus.getCtx());
                }
            } else if (requestPlus.getType().equals(2)) {
                RequestWithFileData requestWithFileData = (RequestWithFileData) requestPlus;
                ByteBuf buf = requestWithFileData.getByteBuf();

                ByteBuf byteBuf = buf.copy(0, 6);
                Integer size = Integer.parseInt(byteBuf.toString(Charset.defaultCharset()));
                JSONObject headerInfo = JSONObject.parseObject(buf.copy(6, size).toString(Charset.defaultCharset()));
                String toDeviceId = headerInfo.getString("toDeviceId");
                String account = headerInfo.getString("account");
                log.info("获得头信息{}", byteBuf.toString(Charset.defaultCharset()));
                ConnectClient connectClient = ConnectManager.getConnect(account, toDeviceId);
                if (connectClient == null) {
                    log.info("没有找到客户端");
                    return null;
                }
                System.out.println(buf.capacity());
                RequestClient.writeByteBuf(connectClient.getCtx(), buf.copy());

            }
            return null;
        });
        serverRemote.run();
    }

    public static void main(String[] args) {
        String id = UUID.randomUUID().toString().replace("-", "");
        ByteBuf in = Unpooled.wrappedBuffer(id.getBytes(StandardCharsets.UTF_8));
        System.out.println(in.capacity());
    }
}