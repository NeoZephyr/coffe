package jubi.service;

import jubi.config.JubiConf;
import lombok.extern.slf4j.Slf4j;

import static jubi.service.ServiceState.*;

@Slf4j
public abstract class AbstractService implements Service {

    private String name;
    protected JubiConf conf;
    protected ServiceState state = ServiceState.LATENT;
    protected Long startTime;

    public AbstractService(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getStartTime() {
        return startTime;
    }

    @Override
    public ServiceState getState() {
        return state;
    }

    @Override
    public JubiConf getConf() {
        return conf;
    }

    @Override
    public void initialize(JubiConf conf) {
        ensureCurrentState(LATENT);
        this.conf = conf;
        changeState(INITIALIZED);
        log.info(String.format("Service[%s] is initialized.", name));
    }

    @Override
    public void start() {
        ensureCurrentState(INITIALIZED);
        this.startTime = System.currentTimeMillis();
        changeState(STARTED);
        log.info(String.format("Service[%s] is started.", name));
    }

    @Override
    public void stop() {
        switch (state) {
            case LATENT:
            case INITIALIZED:
            case STOPPED:
                log.warn(String.format("Service[%s] is not started(%s) yet.", name, state));
            default:
                ensureCurrentState(STARTED);
                changeState(STOPPED);
                log.info(String.format("Service[%s] is stopped.", name));
        }
    }

    private void ensureCurrentState(ServiceState currentState) {
        if (state != currentState) {
            throw new IllegalStateException(String.format("For this operation, the current service state must be %s instead of %s", currentState, state));
        }
    }

    private void changeState(ServiceState state) {
        this.state = state;
    }
}
