package http.netty;

import http.RequestDispatcher;
import http.netty.handler.HttpArkasonInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final String host;
    private final int port;
    private final RequestDispatcher dispatcher;

    public Server(String host, int port, RequestDispatcher dispatcher) {
        this.port = port;
        this.host = host;
        this.dispatcher = dispatcher;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = createServerBootstrap(bossGroup, workerGroup);

        try {
            Channel ch = bootstrap.bind(host, port).sync().channel();

            LOGGER.info("Server successfully initialized on: http://" + host + ":" + port);

            // Wait until termination
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("Error during syncing channel");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private ServerBootstrap createServerBootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        return new ServerBootstrap()
                .option(ChannelOption.SO_BACKLOG, 1024)
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new HttpArkasonInitializer(dispatcher));
    }

}
