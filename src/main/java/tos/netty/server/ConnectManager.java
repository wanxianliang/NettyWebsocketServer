package tos.netty.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 链接管理
 */
public class ConnectManager {

    public static final Map<String, ChannelHandlerContext> ONLINE_LIST = new HashMap<>(2);

    public static boolean storeConnect(String key, ChannelHandlerContext channelHandlerContext) {
        System.out.println("存储链接");
        ONLINE_LIST.put(key, channelHandlerContext);
        return true;
    }

    public static ChannelHandlerContext getConnect(String key) {
        return ONLINE_LIST.get(key);
    }

    public static boolean removeConnect(ChannelHandlerContext channelHandlerContext) {
        Set<String> keys = ONLINE_LIST.keySet();
        for (String key : keys) {
            if (Objects.equals(ONLINE_LIST.get(key), channelHandlerContext)) {
                ONLINE_LIST.remove(key);
                break;
            }
        }
        return true;
    }

}
