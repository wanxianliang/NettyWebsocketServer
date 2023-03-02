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
import io.netty.handler.stream.ChunkedWriteHandler;
import tos.netty.bean.RequestData;
import tos.netty.decoder.ContinuationWebSocketFrameHandler;

import java.util.function.Function;

public class ServerRemote {

    private int port;

    private Function<RequestData, Void> handleRead;

    public static ServerRemote newServerInstance(int port, Function<RequestData, Void> handleRead) {
        ServerRemote serverRemote = new ServerRemote();
        serverRemote.port = port;
        serverRemote.handleRead = handleRead;
        return serverRemote;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Function<RequestData, Void> readHandle = this.handleRead;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // 解码成HttpRequest
                                    .addLast("http-codec", new HttpServerCodec(4096, 8192, 1073741824))
                                    // 解码成FullHttpRequest
                                    .addLast("aggregator", new HttpObjectAggregator(30 * 1024 * 1024))
                                    .addLast("file-trans", new ContinuationWebSocketFrameHandler())
                                    // 添加WebSocket解编码
                                    .addLast(new WebSocketServerProtocolHandler("/wss", null, false, 1024 * 1024 * 40))
                                    .addLast("server-handler", new RemoteServerHandler(readHandle));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_RCVBUF, 30 * 1024 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 30 * 1024 * 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}