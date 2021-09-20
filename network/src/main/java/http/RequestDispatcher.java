package http;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.util.Map;
import java.util.function.Function;

public class RequestDispatcher {
    Map<RequestParams, Function<HttpRequest, HttpResponse>> map;

    public RequestDispatcher(Map<RequestParams, Function<HttpRequest, HttpResponse>> map) {
        this.map = map;
    }

    public Function<HttpRequest, HttpResponse> dispatch(RequestParams params) {
        return map.get(params);
    }

    public HttpResponse dispatchAndApply(RequestParams params, HttpRequest request) {
        return dispatch(params).apply(request);
    }
}
