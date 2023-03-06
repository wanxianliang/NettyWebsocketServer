package tos.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import tos.netty.bean.RequestData;
import tos.netty.bean.ServerConfig;
import tos.netty.consts.ServerDefaultConfig;
import tos.netty.decoder.ContinuationWebSocketFrameHandler;

import java.util.function.Function;

public class ServerRemote {

    private int port;

    private ServerConfig serverConfig;

    private Function<RequestData, Void> handleRead;

    public static ServerRemote newServerInstance(int port, Function<RequestData, Void> handleRead) {
        ServerRemote serverRemote = new ServerRemote();
        serverRemote.port = port;
        serverRemote.handleRead = handleRead;
        serverRemote.serverConfig = buildDefaultServerConfig();
        return serverRemote;
    }

    public static ServerRemote newServerInstance(int port, ServerConfig serverConfig, Function<RequestData, Void> handleRead) {
        ServerRemote serverRemote = new ServerRemote();
        serverRemote.port = port;
        serverRemote.handleRead = handleRead;
        serverRemote.serverConfig = checkAndRenderDefaultConfig(serverConfig);
        return serverRemote;
    }

    private static ServerConfig checkAndRenderDefaultConfig(ServerConfig serverConfig) {
        if (serverConfig == null) {
            return buildDefaultServerConfig();
        }
        if (serverConfig.getHttpMaxContentSize() == null) {
            serverConfig.setHttpMaxContentSize(ServerDefaultConfig.HTTP_MAX_CONTENT_SIZE);
        }
        if (serverConfig.getWebSocketPath() == null) {
            serverConfig.setWebSocketPath(ServerDefaultConfig.WEB_SOCKET_PATH);
        }
        if (serverConfig.getMaxFrameSize() == null) {
            serverConfig.setMaxFrameSize(ServerDefaultConfig.MAX_FRAME_SIZE);
        }
        if (serverConfig.getHttpMaxChunkSize() == null) {
            serverConfig.setHttpMaxChunkSize(ServerDefaultConfig.HTTP_MAX_CHUNK_SIZE);
        }
        return serverConfig;
    }

    private static ServerConfig buildDefaultServerConfig() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setHttpMaxContentSize(ServerDefaultConfig.HTTP_MAX_CONTENT_SIZE);
        serverConfig.setWebSocketPath(ServerDefaultConfig.WEB_SOCKET_PATH);
        serverConfig.setMaxFrameSize(ServerDefaultConfig.MAX_FRAME_SIZE);
        serverConfig.setHttpMaxChunkSize(ServerDefaultConfig.HTTP_MAX_CHUNK_SIZE);
        return serverConfig;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            startServer(bossGroup, workerGroup);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private void startServer(EventLoopGroup bossGroup, EventLoopGroup workerGroup) throws InterruptedException {
        final Function<RequestData, Void> readHandle = this.handleRead;
        ServerBootstrap b = new ServerBootstrap();
        ServerConfig serverConfig = this.serverConfig;
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                //解码成HttpRequest
                                .addLast("http-codec", new HttpServerCodec(4096, 8192, serverConfig.getHttpMaxChunkSize()))
                                // 解码成FullHttpRequest
                                .addLast("aggregator", new HttpObjectAggregator(serverConfig.getHttpMaxContentSize()))
                                .addLast("file-trans", new ContinuationWebSocketFrameHandler())
                                // 添加WebSocket解编码
                                .addLast(new WebSocketServerProtocolHandler(serverConfig.getWebSocketPath(), null, false, serverConfig.getMaxFrameSize()))
                                .addLast("server-handler", new RemoteServerHandler(readHandle));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        if (serverConfig.getSO_RCVBUF() != null) {
            b.option(ChannelOption.SO_RCVBUF, serverConfig.getSO_RCVBUF());
        }
        if (serverConfig.getSO_SNDBUF() != null) {
            b.option(ChannelOption.SO_SNDBUF, 30 * 1024 * 1024);
        }
        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind(port).sync();
        // Wait until the server socket is closed.
        // shut down your server.
        f.channel().closeFuture().sync();
    }

}