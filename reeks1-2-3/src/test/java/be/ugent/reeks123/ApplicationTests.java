package be.ugent.reeks123;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.*;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("dev")
class ApplicationTests {

	@Autowired
	private WebTestClient webClient;

	@Autowired
	private BlogPostDaoMemory dao;

	@BeforeEach
	void setUp() {
		// Clear any existing posts by removing them (getAll returns an unmodifiable List)
		for (BlogPost p : new ArrayList<>(dao.getAll())) {
			if (p.getId() != null) {
				dao.remove(p.getId());
			}
		}
		// seed a single known post at index 0
		dao.add(new BlogPost("Hello World", "Hello World!"));
	}

	@Test
	public void testGetPosts() {
		EntityExchangeResult<List<BlogPost>> postsResponse = this.webClient.get()
				.uri("/posts")
				.header(ACCEPT, APPLICATION_JSON_VALUE)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody(new ParameterizedTypeReference<List<BlogPost>>() {})
				.returnResult();
		List<BlogPost> list = postsResponse.getResponseBody();
		assertThat(list).isNotNull().hasSize(1);
		BlogPost post = list.get(0);
		check(post, "Hello World", "Hello World!");
	}

	@Test
	public void testGetPostJSON() {
		// request single post as JSON
		EntityExchangeResult<BlogPost> result = this.webClient.get()
				.uri("/posts/{id}", 0)
				.header(ACCEPT, APPLICATION_JSON_VALUE)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody(BlogPost.class)
				.returnResult();
		BlogPost p = result.getResponseBody();
		check(p, "Hello World", "Hello World!");
	}

	private void check(BlogPost post, String title, String content) {
		assertThat(post).hasFieldOrPropertyWithValue("title", title)
				.hasFieldOrPropertyWithValue("content", content);
	}

	@Test
	public void testCreatePost() {
		for (BlogPost p : new ArrayList<>(dao.getAll())) {
			if (p.getId() != null) {
				dao.remove(p.getId());
			}
		}

		String title = "Title";
		String content = "Content";
		this.webClient.post()
				.uri("/posts")
				.contentType(APPLICATION_JSON)
				.bodyValue(new BlogPost(title,content ))
				//.body(BodyInserters.fromValue(new BlogPost(title, content)))
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().exists("Location")
				.expectBody().isEmpty();
		BlogPost post = this.webClient.get()
				.uri("/posts/{id}", 1)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody(BlogPost.class)
				.returnResult()
				.getResponseBody();
		check(post,title,content);
	}

	@Test
	public void testGetUnExistingPost() {
		this.webClient.get()
				.uri("/posts/{id}", 99)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testUpdatePost() {
		String title = "new title";
		String content = "new content";
		BlogPost updatedPost = new BlogPost(title,content);
		this.webClient.put()
				.uri("/posts/{id}", 0)
				.contentType(APPLICATION_JSON)
				.bodyValue(updatedPost)
//                .body(BodyInserters.fromValue(updatedPost))
				.exchange()
				.expectStatus().isNoContent();
		BlogPost post = this.webClient.get()
				.uri("/posts/{id}", 0)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody(BlogPost.class)
				.returnResult()
				.getResponseBody();
		check(post, title, content);
	}

	@Test
	public void testUpdateNonExistingPost() {
		String title = "new title";
		String content = "new content";
		BlogPost updatedPost = new BlogPost(title,content);
		this.webClient.put()
				.uri("/posts/{id}", 99)
				.contentType(APPLICATION_JSON)
				.bodyValue(updatedPost)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testUpdatePostWrongID() {
		String title = "new title";
		String content = "new content";
		BlogPost updatedPost = new BlogPost(title,content );
		this.webClient.put()
				.uri("/posts/{id}", 1)
				.contentType(APPLICATION_JSON)
				.bodyValue(updatedPost)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testDeletePost() {
		this.webClient.delete()
				.uri("/posts/{id}", 0)
				.exchange()
				.expectStatus().isNoContent();

		this.webClient.get()
				.uri("/posts/{id}", 0)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	public void testDeleteNonExistingPost() {
		this.webClient.delete()
				.uri("/posts/{id}", 99)
				.exchange()
				.expectStatus().isNotFound();

	}

}