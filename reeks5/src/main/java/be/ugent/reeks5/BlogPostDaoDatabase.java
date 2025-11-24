package be.ugent.reeks5;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import reactor.core.publisher.Flux;

@Profile("!dev")
@Service
public class BlogPostDaoDatabase implements BlogPostDao {

    private final BlogPostMongoRepository repository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public BlogPostDaoDatabase(BlogPostMongoRepository repository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.repository = repository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Flux<BlogPost> getAll() {
        return repository.findAll();
    }

    @Override
    public Mono<BlogPost> add(BlogPost post) {
        return repository.save(post);
    }

    @Override
    public Mono<Void> remove(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<BlogPost> update(String id, BlogPost post) {
        return repository.save(post);
    }

    @Override
    public Mono<BlogPost> get(String id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new BlogPostNotFoundException()));
    }

    public Flux<BlogPost> getChanges() {
        return reactiveMongoTemplate
                .changeStream(BlogPost.class)
                .watchCollection("posts")
                .filter(Criteria.where("operationType").in("insert", "replace", "update"))
                .listen()
                .mapNotNull(ChangeStreamEvent::getBody);
    }

}
