package be.ugent.reeks5;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlogPostDao {
    Flux<BlogPost> getAll();
    Mono<BlogPost> add(BlogPost post);
    Mono<Void> remove(String id);
    Mono<BlogPost> update(String id, BlogPost post);
    Mono<BlogPost> get(String id);
    Flux<BlogPost> getChanges();
}
