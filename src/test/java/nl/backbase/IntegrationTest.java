package nl.backbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.dto.UserDTO;
import nl.backbase.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is the parent class that represents the Integration Tests using Spring Boot Test. Here the Spring Boot Test is
 * instantiated, as well the Mock Mvc instance, allowing to execute request to the controllers. Each integration test
 * needs to extend this class so it automatically will trigger the Spring Boot test instance
 *
 * @author Daniel Chiuratto Seabra
 * @since 07/08/2022
 */
@Slf4j
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {

    // This is the constant used as movie title field name, to allow the child classes to execute the requests specifying
    // the movie title
    protected static final String MOVIE_TITLE = "movieTitle";

    // These are the fake username and fake password just to singup and signin
    private static final String EXPECTED_FAKE_USERNAME = "fakeUser";
    private static final String EXPECTED_FAKE_PASSWORD = "fakePassword";

    // These are the available endpoints, protected to be accessible in the child classes
    protected static final String V1_MOVIE_TOP_10_ENDPOINT = "/v1/movie/top10";
    protected static final String V1_MOVIE_BEST_PICTURE_ENDPOINT = "/v1/movie/bestpicture";
    protected static final String V1_RATING_ENDPOINT = "/v1/rating";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected RestTemplate mockRestTemplate;

    @Autowired
    private UserRepository userRepository;

    protected String token;
    protected UserDTO userDto;

    @BeforeEach
    private void setUp() throws Exception {

        // Every beginning of a test, a Sing Up is necessary and then a Sign In, so a UserDTO is instantiated with
        // a fake username and a fake password
        this.userDto = new UserDTO();
        this.userDto.setUsername(EXPECTED_FAKE_USERNAME);
        this.userDto.setPassword(EXPECTED_FAKE_PASSWORD);

        // Then the Sign Up process is requested, and once with have an OK response status, it means that it can be signed in
        this.mockMvc.perform(post("/v1/signup").content(asJsonString(this.userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk());

        // Now the user is Signed In, and having an okay response, it means that the Authorization header is available in the header
        // with the needed Token for authentication
        final var mvcResult = this.mockMvc.perform(post("/v1/signin").content(asJsonString(this.userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        log.info("Signing In With User: {}", this.userDto.getUsername());

        // Here the Token is retrieved from the Authorization header, and set in the global variable to be accessible in the child
        // classes so they are able to execute the requests
        this.token = mvcResult.getResponse().getHeader(AUTHORIZATION);
    }

    @AfterEach
    public void afterEach() {
        // Here after the test, the user is requested from database and then deleted
        final var userEntity = this.userRepository.findByUsername(this.userDto.getUsername());

        log.info("Removing the user {} from the database", userEntity.getUsername());
        // Here the deletion is executed successfully
        this.userRepository.delete(userEntity);
    }

    /**
     * This method is intended of receiving a serializable object, and serialize it into a JSON {@link String}, to allow
     * the {@link HttpMethod#POST} requests to be made
     *
     * @param t serializable object to be serialized
     * @param <T> Generics specifying what kind of object is
     * @return {@link String} object as JSON
     *
     * @author Daniel Chiuratto Seabra
     * @since 07/08/2022
     */
    protected <T> String asJsonString(final T t) {
        try {
            return this.objectMapper.writeValueAsString(t);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
