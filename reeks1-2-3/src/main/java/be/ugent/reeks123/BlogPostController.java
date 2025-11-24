package be.ugent.reeks123;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

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
    public ResponseEntity<List<BlogPost>> getAll() {
        postsReadCounter.increment();
        return ResponseEntity.ok(dao.getAll());
    }

    @GetMapping("/posts/{index}")
    public ResponseEntity<BlogPost> get(@PathVariable int index) {
        postsReadCounter.increment();
        try {
            return ResponseEntity.ok(dao.get(index));
        } catch (BlogPostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/posts")
    public ResponseEntity<Void> add(@RequestBody BlogPost post) {
        postsCreateCounter.increment();
        dao.add(post);
        Integer createdId = post.getId();
        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, "/posts/" + createdId).build();

    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/posts/{index}")
    public ResponseEntity<Void> remove(@PathVariable int index) {
        postsDeleteCounter.increment();
        try {
            dao.remove(index);
            return ResponseEntity.noContent().build();
        } catch (BlogPostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping("/posts/{index}")
    public ResponseEntity<Void> update(@PathVariable int index, @RequestBody BlogPost post) {
        postsUpdateCounter.increment();
        try {
            dao.update(index, post);
            return ResponseEntity.noContent().build();
        } catch (BlogPostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
