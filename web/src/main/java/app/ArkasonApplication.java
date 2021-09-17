package app;

import org.reflections.Reflections;
import rest.Endpoint;
import rest.Rest;

import java.lang.reflect.Method;
import java.util.Set;

public class ArkasonApplication {
    protected static void run(Class<?> clazz) {
        // scan annotations in the classpath

        // metaprogramming. generate classes

        Set<Class<?>> restClasses = new Reflections(clazz.getPackageName()).getTypesAnnotatedWith(Rest.class);
        for (Class<?> c : restClasses) {
            for (Method m : c.getMethods()) {
                if (m.isAnnotationPresent(Endpoint.class)) {
                    System.out.println(m.getName());
                }
            }
        }
    }
}
