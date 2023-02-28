package tos.netty.server;

import io.netty.channel.ChannelHandlerContext;
import tos.netty.bean.ConnectClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 链接管理
 */
public class ConnectManager {

    public static final Map<String, List<ConnectClient>> ONLINE_DEVICE_LIST = new HashMap<>(2);


    public static boolean checkAndStoreConnect(String account, String deviceId, ChannelHandlerContext channelHandlerContext) {
        System.out.println("存储链接");
        List<ConnectClient> connectClients = ONLINE_DEVICE_LIST.get(account);
        if (connectClients == null) {
            connectClients = new ArrayList<>();
        }
        boolean alreadyStoreConnect = false;
        for (ConnectClient connectClient : connectClients) {
            if (Objects.equals(connectClient.getDeviceId(), deviceId)) {
                connectClient.setLastPingTime(System.currentTimeMillis());
                connectClient.setCtx(channelHandlerContext);
                alreadyStoreConnect = true;
            }
        }
        if (!alreadyStoreConnect) {
            ConnectClient connectClient = new ConnectClient();
            connectClient.setCtx(channelHandlerContext);
            connectClient.setLastPingTime(System.currentTimeMillis());
            connectClient.setAccount(account);
            connectClient.setDeviceId(deviceId);
            connectClients.add(connectClient);
            ONLINE_DEVICE_LIST.put(account, connectClients);
        }
        return true;
    }


    public static List<ConnectClient> getConnectClients(String account) {
        return ONLINE_DEVICE_LIST.get(account);
    }

    public static ConnectClient getConnect(String account, String deviceId) {
        List<ConnectClient> connectClients = ONLINE_DEVICE_LIST.get(account);
        for (ConnectClient connectClient : connectClients) {
            if (Objects.equals(connectClient.getDeviceId(), deviceId)) {
                return connectClient;
            }
        }
        return null;
    }

    public static boolean removeConnectClient(String account, ChannelHandlerContext channelHandlerContext) {
        List<ConnectClient> connectClients = ONLINE_DEVICE_LIST.get(account);
        List<ConnectClient> clientsAfterRemove = connectClients.stream().filter(o -> !Objects.equals(o.getCtx(), channelHandlerContext)).collect(Collectors.toList());
        ONLINE_DEVICE_LIST.put(account, clientsAfterRemove);
        return true;
    }

}
