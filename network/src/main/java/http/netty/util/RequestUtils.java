package http.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestUtils {
    private static final String CRLF = "\r\n";

    public static String formatParams(HttpRequest req) {
        StringBuilder responseData = new StringBuilder();
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        Map<String, List<String>> params = decoder.parameters();

        for (Map.Entry<String, List<String>> param : params.entrySet()) {
            String key = param.getKey();
            List<String> values = param.getValue();

            for (String value : values) {
                responseData.append("PARAM: ")
                        .append(key)
                        .append(" = ")
                        .append(value)
                        .append(CRLF);
            }

            responseData.append(CRLF);
        }
        return responseData.toString();

    }

    public static String evaluateDecoderResult(HttpRequest req) {
        StringBuilder responseData = new StringBuilder();
        DecoderResult result = req.decoderResult();

        if (!result.isSuccess()) {
            responseData
                    .append("Error in decoder: ")
                    .append(result.cause())
                    .append(CRLF);
        }
        return responseData.toString();
    }

    // TODO: Serialization here
    // TODO: Validation here
    public static String formatBody(HttpContent httpContent) {
        StringBuilder responseData = new StringBuilder();
        ByteBuf content = httpContent.content();
        if (content.isReadable()) {
            responseData
                    .append(content.toString(StandardCharsets.UTF_8).toUpperCase(Locale.ROOT))
                    .append(CRLF);
        }
        return responseData.toString();
    }

    public static String prepareLastResponse(LastHttpContent last) {
        StringBuilder responseData = new StringBuilder();
        responseData.append("Last part is received" + CRLF);

        HttpHeaders headers = last.trailingHeaders();
        if (!headers.isEmpty()) {
            responseData.append(CRLF);
            for (String name : headers.names()) {
                for (String value : headers.getAll(name)) {
                    responseData
                            .append("Trailing header: ")
                            .append(name).append(" = ").append(value).append(CRLF);
                }
                responseData.append(CRLF);
            }
        }

        return responseData.toString();
    }
}
