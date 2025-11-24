package be.ugent.reeks123;

public class BlogPostNotFoundException extends RuntimeException {
    public BlogPostNotFoundException() {
        super();
    }

    public BlogPostNotFoundException(String message) {
        super(message);
    }

    public BlogPostNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
