package tos.netty.client;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFuture;
import tos.netty.bean.RequestPlus;
import tos.netty.bean.ResponsePlus;
import tos.netty.decoder.ResponseDecoder;
import tos.netty.encorder.RequestEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import tos.netty.interfaceForHelp.CallbackFunction;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sean
 */
public class ClientManager {

    public static boolean isActive = false;

    Bootstrap bootstrap;

    EventLoopGroup workerGroup;

    String host;

    private ChannelFuture channelFuture;

    int port;

    ConcurrentHashMap<String, Long> requestRecordeList = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, CallbackFunction<ResponsePlus>> handleResultList = new ConcurrentHashMap<>();

    ThreadPoolExecutor requestThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 20, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(10000), new ThreadPoolExecutor.DiscardPolicy());

    AtomicLong requestIdGenerator = new AtomicLong(0);


    long timeout = 10000;

    long maxIdleTime = 10 * 1000;

    long lastActiveTime = 0;

    Thread tickThread;

    public static ClientManager newConnectInstance(String host, int port) {
        ClientManager connectManager = new ClientManager();
        connectManager.host = host;
        connectManager.port = port;
        connectManager.initManage();
        return connectManager;
    }

    public void initManage() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.AUTO_CLOSE, false);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new ResponseDecoder())
                        .addLast(new RequestEncoder())
                        .addLast(new ServerClientHandler(msg -> {
                            onRead(msg);
                            return msg;
                        }));
            }
        });
        try {
            channelFuture = bootstrap.connect(host, port).sync();
        } catch (Exception e) {
            return;
        }
        if (channelFuture.isSuccess()) {
            ClientManager.isActive = true;
        }
        //todo 临时重连解决方案
        channelFuture.channel().eventLoop().schedule(() -> {
            if (!ClientManager.isActive) {
                try {
                    channelFuture = bootstrap.connect(host, port).sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 30, TimeUnit.SECONDS);
    }

    public String sendMsg(RequestPlus requestPlus) {
        String requestId = String.valueOf(requestIdGenerator.getAndIncrement());
        requestPlus.setRequestId(requestId);
        RequestThread requestThread = RequestThread.newRequestThread(channelFuture, requestPlus);
        requestThreadPool.submit(requestThread);
        return requestId;
    }

    public void timeOutTicket() throws InterruptedException {
        new Thread() {
            @Override
            public void run() {
                for (; ; ) {
                    requestRecordeList.forEach((key, startTime) -> {
                        if (System.currentTimeMillis() - startTime > timeout) {
                            CallbackFunction<ResponsePlus> handle = handleResultList.get(key);
                            if (handle != null) {
                                handle.apply(ResponsePlus.buildForTimeOut());
                            }
                            requestRecordeList.remove(key);
                        }
                    });
                }
            }
        }.start();
    }

    public void onRead(ResponsePlus responsePlus) {
        String requestId = responsePlus.getRequestId();
        if (requestId == null) {
            //及时消息
            System.out.println(JSONObject.toJSONString(responsePlus));
            return;
        }
        CallbackFunction<ResponsePlus> handle = handleResultList.get(requestId);
        if (handle != null) {
            handle.apply(responsePlus);
        }
        //remove key
        requestRecordeList.remove(requestId);
        handleResultList.remove(requestId);
    }

    public void handleResult(String requestId, CallbackFunction<ResponsePlus> cb) {
        if (requestId == null || cb == null) {
            return;
        }
        requestRecordeList.put(requestId, System.currentTimeMillis());
        handleResultList.put(requestId, cb);
    }

}