package net.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PunkServer {

    private Server server;

    void start() throws IOException {
        int port = 5858;
        // ServerInterceptors.intercept
        server = ServerBuilder.forPort(port)
                .addService(new PunkService())
                .build()
                .start();
        log.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("shutting down gRPC server since JVM is shutting down");
                try {
                    PunkServer.this.stop();
                } catch (InterruptedException ex) {
                    log.error("stop server failed", ex);
                }
                log.info("server shut down");
            }
        });
    }

    void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        PunkServer punkServer = new PunkServer();
        punkServer.start();
        punkServer.blockUntilShutdown();
    }
}
