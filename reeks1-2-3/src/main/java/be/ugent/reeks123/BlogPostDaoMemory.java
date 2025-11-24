package be.ugent.reeks123;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

@Profile("dev")
@Service
public class BlogPostDaoMemory implements BlogPostDao {

    private Map<Integer,BlogPost> posts = new HashMap<>();


    public List<BlogPost> getAll() {
        return posts.values().stream().toList();
    }

    public void add(BlogPost post) {
        // if id is not provided (null), generate one
        if (post.getId() == null) {
            int nextId = posts.isEmpty() ? 0 : Collections.max(posts.keySet()) + 1;
            post.setId(nextId);
        }
        posts.put(post.getId(), post);
    }

    public void remove(int id) {
        if (!exists(id)) {
            throw new BlogPostNotFoundException("Blog post not found");
        }
        posts.remove(id);
    }

    public void update(int id, BlogPost post) {
        if (!exists(id)) {
            throw new BlogPostNotFoundException("Blog post not found");
        }
        posts.put(id, post);
    }

    public BlogPost get(int id) {
        if (!exists(id)) {
            throw new BlogPostNotFoundException("Blog post not found");
        }
        return posts.get(id);
    }

    @Override
    public boolean exists(int id) {
        return posts.containsKey(id);
    }

}
