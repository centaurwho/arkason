package http;

import io.netty.handler.codec.http.HttpMethod;

import java.net.URI;
import java.util.Objects;

public class RequestParams {
    private final HttpMethod method;
    private final URI uri;

    public RequestParams(HttpMethod verb, URI uri) {
        this.method = verb;
        this.uri = uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestParams that = (RequestParams) o;
        return method == that.method && uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uri);
    }
}
