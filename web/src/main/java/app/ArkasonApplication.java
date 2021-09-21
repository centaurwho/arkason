package app;

import annotation.AnnotationProcessor;
import http.RequestDispatcher;
import http.netty.Server;
import org.reflections.Reflections;
import rest.Endpoint;
import rest.Rest;

import java.lang.reflect.Method;
import java.util.Set;

public class ArkasonApplication {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;

    protected static void run(Class<?> clazz) {
        // scan annotations in the classpath

        // metaprogramming. generate classes

        RequestDispatcher dispatcher = new AnnotationProcessor().scanClassPath(clazz);
        Server server = new Server(HOST, PORT, dispatcher);

        server.run();
    }
}
