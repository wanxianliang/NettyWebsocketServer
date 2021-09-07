package tos.netty.client;

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
import java.util.function.Function;

/**
 * @author sean
 */
public class ConnectManager {

    Bootstrap bootstrap;

    EventLoopGroup workerGroup;

    String host;

    int port;

    ConcurrentHashMap<String, Long> requestRecordeList = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, CallbackFunction<ResponsePlus>> handleResultList = new ConcurrentHashMap<>();

    ThreadPoolExecutor requestThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 20, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(10000), new ThreadPoolExecutor.DiscardPolicy());

    AtomicLong requestIdGenerator = new AtomicLong(0);


    long timeout = 10000;

    long maxIdleTime = 10 * 1000;

    long lastActiveTime = 0;

    Thread tickThread;

    public static ConnectManager newConnectInstance(String host, int port) {
        ConnectManager connectManager = new ConnectManager();
        connectManager.initManage();
        connectManager.host = host;
        connectManager.port = port;
        return connectManager;
    }

    public void initManage() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
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
            this.timeOutTicket();
        } catch (Exception e) {
        }
    }


    public String sendMsg(RequestPlus requestPlus) {
        String requestId = String.valueOf(requestIdGenerator.getAndIncrement());
        requestPlus.setRequestId(requestId);
        RequestThread requestThread = RequestThread.newRequestThread(bootstrap, host, port, requestPlus);
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

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ConnectManager connectManager = ConnectManager.newConnectInstance("127.0.0.1", 2222);
        String re = connectManager.sendMsg(new RequestPlus());
        connectManager.handleResult(re, responsePlus -> {
        });
    }
}