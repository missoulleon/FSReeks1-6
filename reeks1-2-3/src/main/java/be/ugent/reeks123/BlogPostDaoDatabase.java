package be.ugent.reeks123;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("!dev")
@Service
public class BlogPostDaoDatabase implements BlogPostDao {

    private final BlogPostRepository repository;

    public BlogPostDaoDatabase(BlogPostRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BlogPost> getAll() {
        return repository.findAll();
    }

    @Override
    public void add(BlogPost post) {
        repository.save(post);
    }

    @Override
    public void remove(int id) {
        repository.deleteById(id);
    }

    @Override
    public void update(int id, BlogPost post) {
        repository.findById(id).ifPresentOrElse(
                p -> {
                    p.setTitle(post.getTitle());
                    p.setContent(post.getContent());
                    repository.save(p);
                },
                () -> {
                    throw new BlogPostNotFoundException("Blog post not found");
                }
        );
    }

    @Override
    public BlogPost get(int id) {
        return repository.findById(id).orElseThrow(BlogPostNotFoundException::new);
    }

    @Override
    public boolean exists(int id) {
        return repository.existsById(id);
    }
}
