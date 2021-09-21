package annotation;

import rest.Endpoint;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationScanner {

    public List<Method> fetchMethodsWithTypeInClasses(Collection<Class<?>> classes, Class<Endpoint> endpointClass) {
        return classes.stream()
                .map(c -> fetchMethodWithType(c, endpointClass))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Method> fetchMethodWithType(Class<?> c, Class<Endpoint> endpointClass) {
        return Arrays.stream(c.getMethods())
                .filter(m -> m.isAnnotationPresent(endpointClass))
                .collect(Collectors.toList());
    }
}
