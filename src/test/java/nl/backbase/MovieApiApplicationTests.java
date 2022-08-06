package nl.backbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.dto.UserDTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovieApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RestTemplate mockRestTemplate;

	@BeforeEach
	private void setUp() {
	}

	@Test
	void contextLoads() throws Exception {
		final var userDTO = new UserDTO();
		userDTO.setUsername("fakeUser");
		userDTO.setPassword("fakePassword");

		this.mockMvc.perform(post("/v1/signup").content(asJsonString(userDTO))
												   	   .contentType(MediaType.APPLICATION_JSON_VALUE)
												   	   .accept(MediaType.ALL))
													   .andExpect(status().isOk());
		var mvcResult = this.mockMvc.perform(post("/v1/signin")
						.content(asJsonString(userDTO))
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.accept(MediaType.ALL))
				.andExpect(status().isOk())
				.andReturn();
		final var token = mvcResult.getResponse().getHeader(AUTHORIZATION);

		final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
		expectedFakeMovieAPISourceDTO.setResponse("true");
		expectedFakeMovieAPISourceDTO.setTitle("Titanic");
		expectedFakeMovieAPISourceDTO.setBoxOffice("$453,656,456.24");
		final var expectedFakeResponseEntity = ResponseEntity.ok(expectedFakeMovieAPISourceDTO);

		when(this.mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class))).thenReturn(expectedFakeResponseEntity);

		final var ratingRequestDTO = new RatingRequestDTO();
		ratingRequestDTO.setMovieTitle("Titanic");
		ratingRequestDTO.setValue(7.56);

		mvcResult = this.mockMvc.perform(post("/v1/rating").content(asJsonString(ratingRequestDTO)).header(AUTHORIZATION, token).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		final var movieJson = new String(mvcResult.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);

		this.mockMvc.perform(get("/v1/movie/top10").param("movieTitle", "titanic").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());

		mvcResult = this.mockMvc.perform(get("/v1/movie/top10").param("movieTitle", "titanic").header(AUTHORIZATION, token).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

		System.out.println(new String(mvcResult.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8));

	}

	private <T> String asJsonString(final T t) {
		try {
			return this.objectMapper.writeValueAsString(t);
		} catch (final JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
