package com.pain.gray.cloud;

import com.pain.gray.cloud.service.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@EnableAsync
@SpringBootApplication
//@PropertySource({"abc.properties"})
public class CloudApplication implements CommandLineRunner {

	@Autowired
	private FooService fooService;

	public static void main(String[] args) {
		SpringApplication.run(CloudApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fooService.foo();
	}

	class Handler {
		public Mono<ServerResponse> times(ServerRequest request) {
			return ok().contentType(MediaType.TEXT_EVENT_STREAM)
					.body(
							Flux.interval(Duration.ofSeconds(1))
									.map(it -> new SimpleDateFormat("HH:mm:ss").format(new Date())),
							String.class);
		}

		public Mono<ServerResponse> query(ServerRequest request) {
			Integer id = Integer.valueOf(request.pathVariable("id"));
			return ok().contentType(MediaType.APPLICATION_JSON)
					.body(Mono.just(new Result()), Result.class);
		}
	}

	static class Result {}

	// DispatcherHandler#initStrategies
	// RouterFunctionMapping
	// RouterFunctionMapping#initRouterFunctions

	// DispatcherHandler#handle

	private Handler handler;

	@Bean
	public RouterFunction<ServerResponse> router() {
		return route(GET("hello"), handler::times)
				.andRoute(GET("times"), handler::times)
				.andRoute(GET("query/{id}"), handler::query);
	}
}
