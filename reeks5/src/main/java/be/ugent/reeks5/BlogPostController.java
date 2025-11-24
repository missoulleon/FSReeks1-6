package be.ugent.reeks5;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/")
public class BlogPostController {
    private final BlogPostDao dao;

    private final Counter postsCreateCounter;
    private final Counter postsReadCounter;
    private final Counter postsUpdateCounter;
    private final Counter postsDeleteCounter;

    public BlogPostController(BlogPostDao dao, MeterRegistry meterRegistry) {
        this.dao = dao;
        this.postsCreateCounter = Counter
                .builder("blogpost.operations")
                .description("number of blogpost operations")
                .tag("operation", "created")
                .register(meterRegistry);
        this.postsReadCounter = Counter
                .builder("blogpost.operations")
                .description("number of blogpost operations")
                .tag("operation", "read")
                .register(meterRegistry);
        this.postsUpdateCounter = Counter
                .builder("blogpost.operations")
                .description("number of blogpost operations")
                .tag("operation", "updated")
                .register(meterRegistry);
        this.postsDeleteCounter = Counter
                .builder("blogpost.operations")
                .description("number of blogpost operations")
                .tag("operation", "deleted")
                .register(meterRegistry);
    }


    @GetMapping("/posts")
    public Flux<BlogPost> getAll() {
        postsReadCounter.increment();
        return dao.getAll();
    }
    @GetMapping("/stream/posts-delay")
    public Flux<BlogPost> getPostsStreamV1() {
        this.postsReadCounter.increment();
        return dao.getAll().delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/stream/posts-text", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BlogPost> getPostsStreamV2() {
        this.postsReadCounter.increment();
        return dao.getAll().delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping("/posts/{id}")
    public Mono<BlogPost> getPost(@PathVariable("id") String id) {
        this.postsReadCounter.increment();
        return dao.get(id).switchIfEmpty(Mono.error(new BlogPostNotFoundException(id)));
    }

    @PostMapping("/posts")
    public Mono<ResponseEntity<Void>> addPost(@RequestBody BlogPost post, UriComponentsBuilder uriBuilder) {
        this.postsCreateCounter.increment();
        return dao.add(post)
                .map(savedPost -> ResponseEntity.created(
                                uriBuilder
                                        .path("/posts/{id}")
                                        .buildAndExpand(savedPost.getId())
                                        .toUri())
                        .build());
    }


    @DeleteMapping("/posts/{index}")
    public Mono<Void> remove(@PathVariable String index) {
        postsDeleteCounter.increment();
        return dao.remove(index);
    }

    @PutMapping("/posts/{index}")
    public Mono<BlogPost> update(@PathVariable String index, @RequestBody BlogPost post) {
        postsUpdateCounter.increment();
        return dao.update(index, post);
    }

    @GetMapping(value = "/posts/changes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BlogPost> getChanges() {
        return dao.getChanges();
    }
}
