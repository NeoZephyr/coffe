package jubi.service;

import jubi.config.JubiConf;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class ServerService extends CompositeService {

    private AtomicBoolean started = new AtomicBoolean(false);
    private CompositeService backendService;
    private List<CompositeService> frontendServices;

    public ServerService(String name) {
        super(name);
    }

    @Override
    public void initialize(JubiConf conf) {
        this.conf = conf;
        addService(backendService);
        for (CompositeService service : frontendServices) {
            addService(service);
        }
        super.initialize(conf);
    }

    @Override
    public void start() {
        if (!started.getAndSet(true)) {
            super.start();
        }
    }

    @Override
    public void stop() {
        try {
            if (started.getAndSet(false)) {
                super.stop();
            }
        } catch (Throwable t) {
            log.warn(String.format("Error stopping %s %s", getName(), t.getMessage()), t);
        } finally {
            try {
                stopServer();
            } catch (Throwable t) {
                log.warn(String.format("Error stopping %s %s", getName(), t.getMessage()), t);
            }
        }
    }

    protected abstract void stopServer();
}
