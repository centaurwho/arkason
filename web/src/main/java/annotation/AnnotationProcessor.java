package annotation;

import http.RequestDispatcher;
import http.RequestParams;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.RequestType;
import rest.Endpoint;
import rest.Path;
import rest.Rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


public class AnnotationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationProcessor.class);
    private final AnnotationScanner scanner = new AnnotationScanner();

    public RequestDispatcher scanClassPath(Class<?> clazz) {
        Set<Class<?>> restClasses = new Reflections(clazz.getPackageName()).getTypesAnnotatedWith(Rest.class);
        return register(restClasses);
    }

    public RequestDispatcher register(Set<Class<?>> classes) {
        // TODO: Logic here is just a mockup. Doesnt consider any inheritance, overloading or other weird java features.
        //  Just assuming everything is perfect.
        List<Method> restMethods = scanner.fetchMethodsWithTypeInClasses(classes, Endpoint.class);

        Map<RequestParams, Function<HttpRequest, HttpResponse>> dispatchMap = new HashMap<>();
        for (Method method : restMethods) {
            // TODO: Remove these checks after deciding endpoint format
            if (!Modifier.isPublic(method.getModifiers())) {
                LOGGER.warn("Rest methods must be public.");
            }
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException("Rest method must have exactly one parameter.");
            }
            if (method.getParameterTypes()[0].equals(String.class)) {
                throw new IllegalArgumentException("Parameter must be of type string");
            }

            RequestParams params = extractRequestParams(method);
            Function<HttpRequest, HttpResponse> callback = convertMethodToLambda(method);
            dispatchMap.put(params, callback);
        }

        return new RequestDispatcher(dispatchMap);
    }

    private Function<HttpRequest, HttpResponse> convertMethodToLambda(Method method) {
        return (request -> {
            try {
                return (HttpResponse) method.invoke(method.getClass(), request);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // TODO: This part will never be reached but java doesnt believe it. Check some edge cases to be sure.
                LOGGER.debug("Error during method invocation", e);
            }
            return null;
        });
    }

    private RequestParams extractRequestParams(Method method) {
        RequestType type = method.getAnnotation(Endpoint.class).type();
        String pathValue = method.getAnnotation(Path.class).value();
        URI path = URI.create(pathValue);
        return new RequestParams(type.toHttpMethod(), path);
    }
}
