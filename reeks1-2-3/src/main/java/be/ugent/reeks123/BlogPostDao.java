package be.ugent.reeks123;

import java.util.List;
import java.util.Map;

public interface BlogPostDao {
    List<BlogPost> getAll();
    void add(BlogPost post);
    void remove(int id);
    void update(int id, BlogPost post);
    BlogPost get(int id);
    boolean exists(int id);
}
