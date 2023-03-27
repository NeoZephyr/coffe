package jubi

import jubi.service.ServerService
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@Slf4j
@SpringBootApplication
class JubiServer extends ServerService implements CommandLineRunner {

    JubiServer() {
        super("jubi")
    }

    static void main(String[] args) {
        SpringApplication.run(JubiServer.class, args);
    }

    @Override
    void run(String... args) throws Exception {

    }

    @Override
    protected void stopServer() {}
}
