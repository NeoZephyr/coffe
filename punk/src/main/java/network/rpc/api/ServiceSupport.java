package network.rpc.api;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceSupport {

    private final static Map<String, Object> services = new HashMap<>();

    public synchronized static <T> T load(Class<T> service) {
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::filter)
                .findFirst()
                .orElseThrow(ServiceLoadException::new);
    }

    public synchronized static <T> Collection<T> loadAll(Class<T> service) {
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::filter)
                .collect(Collectors.toList());
    }

    private static <T> T filter(T service) {
        if (service.getClass().isAnnotationPresent(Sharable.class)) {
            String className = service.getClass().getCanonicalName();
            return (T) services.putIfAbsent(className, service);
        } else {
            return service;
        }
    }
}