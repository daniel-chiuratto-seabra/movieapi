package nl.backbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.dto.UserDTO;
import nl.backbase.mapper.UserMappers;
import nl.backbase.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {

    protected static final String MOVIE_TITLE = "movieTitle";

    protected static final MockHttpServletRequestBuilder POST_REQUEST_MOVIE_RATING = post("/v1/rating").contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);

    private static final String EXPECTED_FAKE_USER = "fakeUser";
    private static final String EXPECTED_FAKE_PASSWORD = "fakePassword";

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

    @Autowired
    private UserMappers userMappers;

    protected String token;

    protected UserDTO userDto;

    @BeforeEach
    private void setUp() throws Exception {
        this.userDto = new UserDTO();
        this.userDto.setUsername(EXPECTED_FAKE_USER);
        this.userDto.setPassword(EXPECTED_FAKE_PASSWORD);

        this.mockMvc.perform(post("/v1/signup").content(asJsonString(this.userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk());

        final var mvcResult = this.mockMvc.perform(post("/v1/signin").content(asJsonString(this.userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();

        log.info("Signing In With User: {}", this.userDto.getUsername());

        this.token = mvcResult.getResponse().getHeader(AUTHORIZATION);
    }

    @AfterEach
    public void afterAll() {
        final var userEntity = this.userRepository.findByUsername(this.userDto.getUsername());

        log.info("Removing the user {} from the database", userEntity.getUsername());

        this.userRepository.delete(userEntity);
    }

    protected <T> String asJsonString(final T t) {
        try {
            return this.objectMapper.writeValueAsString(t);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
