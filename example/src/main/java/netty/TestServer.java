package netty;

import http.RequestDispatcher;
import http.RequestParams;
import http.netty.Server;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class TestServer {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;
    private static final URI LOCAL_URI = URI.create("http://" + HOST + ":" + PORT);

    public static void main(String[] args) {
        RequestParams params1 = new RequestParams(GET, LOCAL_URI);
        RequestParams params2 = new RequestParams(POST, LOCAL_URI);

        RequestDispatcher dispatcher = new RequestDispatcher(Map.of(
                params1, req -> makeResponse("get request"),
                params2, req -> makeResponse("post request")
        ));
        new Server(HOST, PORT, dispatcher).run();
    }

    private static HttpResponse makeResponse(String content) {
        ByteBuf contentBuf = Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8));
        HttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, contentBuf);
        httpResponse.headers().set(CONTENT_TYPE, "text/plain");
        httpResponse.headers().set(CONTENT_LENGTH, contentBuf.readableBytes());

        return httpResponse;
    }
}
