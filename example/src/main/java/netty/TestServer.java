package netty;

import http.Server;

public class TestServer {

    public static void main(String[] args) {
        new Server("127.0.0.1", 8080).run();
    }
}
