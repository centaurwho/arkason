package http.netty.handler;

import http.RequestDispatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpArkasonInitializer extends ChannelInitializer<SocketChannel> {
    private final RequestDispatcher dispatcher;

    public HttpArkasonInitializer(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        // TODO: experiment with options here
        ch.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new HttpArkasonServerHandler(dispatcher));
    }
}
