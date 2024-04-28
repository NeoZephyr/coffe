package network.rpc.core.server;

public interface ServiceProviderRegistry {
    <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider);
}
