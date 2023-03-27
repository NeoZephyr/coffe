package jubi.service;

import jubi.JubiException;
import jubi.config.JubiConf;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class CompositeService extends AbstractService {

    private List<Service> services = new ArrayList<>();

    public CompositeService(String name) {
        super(name);
    }

    protected List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    protected void addService(Service service) {
        services.add(service);
    }

    @Override
    public void initialize(JubiConf conf) {
        for (Service service : services) {
            service.initialize(conf);
        }
        super.initialize(conf);
    }

    @SneakyThrows
    @Override
    public void start() {
        for (int i = 0; i < services.size(); ++i) {
            Service service = services.get(i);
            try {
                service.start();
            } catch (Exception e) {
                log.error(String.format("Error starting service %s", service.getName()), e);
                stop(i);
                throw new JubiException(String.format("Failed to Start %s", getName()), e);
            }
        }
        super.start();
    }

    @Override
    public void stop() {
        if (state == ServiceState.STOPPED) {
            log.warn(String.format("Service[%s] is stopped already", getName()));
        } else {
            stop(services.size());
            super.stop();
        }
    }

    private void stop(int startedNum) {
        for (int i = startedNum - 1; i >= 0; --i) {
            Service service = services.get(i);

            try {
                log.info(String.format("Service: [%s] is stopping.", getName()));
                service.stop();
            } catch (Throwable t) {
                log.warn(String.format("Error stopping %s", service.getName()));
            }
        }
    }
}
