package tos.netty.server;

import tos.netty.bean.RequestPlus;
import tos.netty.bean.ResponsePlus;
import tos.netty.decoder.RequestDecoder;
import tos.netty.encorder.ResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import javax.annotation.Resource;
import java.util.function.Function;

public class ServerRemote {

    private int port;

    private Function<RequestPlus, ResponsePlus> handleRead;

    public static ServerRemote newServerInstance(int port, Function<RequestPlus, ResponsePlus> handleRead) {
        ServerRemote serverRemote = new ServerRemote();
        serverRemote.port = port;
        serverRemote.handleRead = handleRead;
        return serverRemote;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Function<RequestPlus, ResponsePlus> readHandle = this.handleRead;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new RequestDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new ResponseEncoder())
                                    .addLast(new RemoteServerHandler(readHandle));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
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