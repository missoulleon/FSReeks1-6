package be.ugent.reeks123;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class BlogPost {
    @Id
    @GeneratedValue
    private Integer id; // changed from int to Integer to allow null (not provided)
    private String title;
    private String content;

    // no-args constructor for Jackson
    public BlogPost() {
    }

    public BlogPost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
