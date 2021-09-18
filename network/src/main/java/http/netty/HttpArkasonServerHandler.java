package http.netty;

import http.netty.util.RequestUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpArkasonServerHandler extends SimpleChannelInboundHandler<HttpMessage> {

    private HttpRequest request;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) {
        StringBuilder responseData = new StringBuilder();
        if (msg instanceof HttpRequest req) {
            this.request = req;
            if (HttpUtil.is100ContinueExpected(req)) {
                writeResponse(ctx);
            }

            responseData.append(RequestUtils.formatParams(req));
        }
        responseData.append(RequestUtils.evaluateDecoderResult(request));
        if (msg instanceof HttpContent content) {
            responseData.append(RequestUtils.formatBody(content));
            responseData.append(RequestUtils.evaluateDecoderResult(request));

            if (msg instanceof LastHttpContent last) {
                responseData.append(RequestUtils.prepareLastResponse(last));
                writeResponse(ctx, last, responseData);
            }
        }

    }

    private void writeResponse(ChannelHandlerContext ctx, LastHttpContent last, StringBuilder responseData) {
        HttpResponseStatus status = determineStatus(last);
        ByteBuf content = Unpooled.copiedBuffer(responseData.toString(), StandardCharsets.UTF_8);

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, content);

        httpResponse.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (keepAlive) {
            httpResponse.headers().setInt(CONTENT_LENGTH, httpResponse.content().readableBytes());
            httpResponse.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(httpResponse);

        if (!keepAlive) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

    }

    private HttpResponseStatus determineStatus(LastHttpContent last) {
        // TODO: Status logic here

        if (last.decoderResult().isSuccess()) {
            return OK;
        } else {
            return BAD_REQUEST;
        }
    }

    private void writeResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
