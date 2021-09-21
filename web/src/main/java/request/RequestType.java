package request;

import io.netty.handler.codec.http.HttpMethod;

public enum RequestType {
    GET("get"),
    POST("post"),
    PUT("put"),
    DELETE("delete");

    private final String val;

    RequestType(String val) {
        this.val = val;
    }

    public HttpMethod toHttpMethod() {
        return new HttpMethod(val);
    }
}
