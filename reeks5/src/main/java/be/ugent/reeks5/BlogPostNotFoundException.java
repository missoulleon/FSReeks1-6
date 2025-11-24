package be.ugent.reeks5;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
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
