package be.ugent.reeks5;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostMongoRepository
        extends ReactiveMongoRepository<BlogPost, String> { }
