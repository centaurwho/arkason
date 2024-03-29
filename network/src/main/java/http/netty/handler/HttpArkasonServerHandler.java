package http.netty.handler;

import http.RequestDispatcher;
import http.RequestParams;
import http.netty.util.RequestUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Behavior;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpArkasonServerHandler extends SimpleChannelInboundHandler<HttpMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpArkasonServerHandler.class);
    private final Behavior<HttpObject, ChannelHandlerContext> behavior;
    private final RequestDispatcher dispatcher;

    public HttpArkasonServerHandler(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        behavior = new Behavior<HttpObject, ChannelHandlerContext>()
                .entry(HttpContent.class, this::onHttpContent)
                .entry(HttpRequest.class, this::onHttpRequest);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void onHttpRequest(HttpRequest req, ChannelHandlerContext ctx) {
        if (HttpUtil.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }
        RequestParams params = extractParams(req);

        boolean keepAlive = HttpUtil.isKeepAlive(req);
        HttpResponse response = dispatcher.dispatchAndApply(params, req);

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            // For some reason KEEP_ALIVE is deprecated. So we cant use that constant.
            response.headers().set(CONNECTION, "keep-alive");
            ctx.write(response);
        }
    }

    private RequestParams extractParams(HttpRequest req) {
        String uriString = "http://" + req.headers().get("Host");
        return new RequestParams(req.method(), URI.create(uriString));
    }

    private void onHttpContent(HttpContent content, ChannelHandlerContext ctx) {
        // TODO: make this a class variable
        StringBuilder responseData = new StringBuilder();
        responseData.append(RequestUtils.formatBody(content));

        if (content instanceof LastHttpContent) {
            LastHttpContent last = (LastHttpContent) content;
            responseData.append(RequestUtils.prepareLastResponse(last));
            ByteBuf responseBuf = Unpooled.copiedBuffer(responseData.toString().getBytes(StandardCharsets.UTF_8));
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, responseBuf);
            ctx.write(response);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) {
        behavior.apply(msg, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("Error in http handler.", cause);
        ctx.close();
    }
}
