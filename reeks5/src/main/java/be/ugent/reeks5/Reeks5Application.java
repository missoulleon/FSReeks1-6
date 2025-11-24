package be.ugent.reeks5;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class Reeks5Application {

	public static void main(String[] args) {
		SpringApplication.run(Reeks5Application.class, args);
	}

	@Bean
	CommandLineRunner test(BlogPostMongoRepository repository) {
		return args -> {
			repository.deleteAll()
					.thenMany(
							Flux.just("Reactive Spring Boot", "Reactive Spring Data", "Reactive MongoDB")
									.map(title -> new BlogPost(title, "Some content ..."))
									.flatMap(repository::save)
					)
					.thenMany(repository.findAll())
					.subscribe(System.out::println);
		};
	}


}
