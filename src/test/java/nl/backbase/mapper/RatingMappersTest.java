package nl.backbase.mapper;

import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.RatingEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class RatingMappersTest {

    private final RatingMappers ratingMappers = new RatingMappers();

    @Test
    @DisplayName("GIVEN fakes RatingRequestDTO, Authentication and MovieAPIEntity, WHEN the mapper tries to map without having all of them set, THEN it should return null")
    public void givenFakesRatingRequestDTOAuthenticationAndMovieAPIEntityWhenTheMapperTriesToMapWithoutHavingAllOfThemSetThenItShouldReturnNull() {
        final var expectedFakeRatingRequestDTO = getFakeRatingRequestDTO();
        final var expectedFakeAuthentication = getFakeAuthentication();
        final var expectedFakeMovieAPIEntity = getFakeMovieAPIEntity();

        var actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(null, null, null);
        assertNull(actualRatingEntity);

        actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(expectedFakeRatingRequestDTO, null, null);
        assertNull(actualRatingEntity);

        actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(null, expectedFakeAuthentication, null);
        assertNull(actualRatingEntity);

        actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(null, null, expectedFakeMovieAPIEntity);
        assertNull(actualRatingEntity);

        actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(expectedFakeRatingRequestDTO, null, expectedFakeMovieAPIEntity);
        assertNull(actualRatingEntity);
    }

    @Test
    @DisplayName("GIVEN fakes RatingRequestDTO, Authentication and MovieAPIEntity, WHEN the mapper tries to map with all of them set, THEN it should return an actual RatingEntity with expected values")
    public void givenFakesRatingRequestDTOAuthenticationAndMovieAPIEntityWhenTheMapperTriesToMapWithAllOfThemSetThenItShouldTeturnAnActualRatingEntityWithExpectedValues() {
        final var expectedFakeRatingRequestDTO = getFakeRatingRequestDTO();
        final var expectedFakeAuthentication = getFakeAuthentication();
        final var expectedFakeMovieAPIEntity = getFakeMovieAPIEntity();

        final var actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(expectedFakeRatingRequestDTO, expectedFakeAuthentication, expectedFakeMovieAPIEntity);
        assertNotNull(actualRatingEntity);
        assertEquals(expectedFakeAuthentication.getPrincipal(), actualRatingEntity.getSource());
        assertEquals(Integer.parseInt(expectedFakeRatingRequestDTO.getValue()), actualRatingEntity.getValue().intValue());
        assertEquals(expectedFakeMovieAPIEntity, actualRatingEntity.getMovieAPIEntity());
    }

    @Test
    @DisplayName("GIVEN an expected fake RatingEntity, WHEN the mapper tries to map it, THEN it should return an actual RatingDTO with the expected values")
    public void givenAnExpectedFakeRatingEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualRatingDTOWithTheExpectedValues() {
        final var expectedFakeRatingEntity = new RatingEntity();
        expectedFakeRatingEntity.setSource("Fake Source");
        expectedFakeRatingEntity.setValue(1234D);
        expectedFakeRatingEntity.setMovieAPIEntity(getFakeMovieAPIEntity());

        final var actualRatingDTO = this.ratingMappers.ratingEntityRatingDTO(expectedFakeRatingEntity);
        assertNotNull(actualRatingDTO);
        assertEquals(expectedFakeRatingEntity.getSource(), actualRatingDTO.getSource());
        assertEquals(expectedFakeRatingEntity.getValue(), actualRatingDTO.getValue());
    }

    @Test
    @DisplayName("GIVEN an expected fake RatingEntity with null value, WHEN the mapper tries to map it, THEN it should return an actual RatingDTO with the expected values with 0 as value")
    public void givenAnExpectedFakeRatingEntityWithNullValueWhenTheMapperTriesToMapItThenItShouldReturnAnActualRatingDTOWithTheExpectedValuesWith0AsValue() {
        final var expectedFakeRatingEntity = new RatingEntity();
        expectedFakeRatingEntity.setSource("Fake Source");
        expectedFakeRatingEntity.setValue(null);
        expectedFakeRatingEntity.setMovieAPIEntity(getFakeMovieAPIEntity());

        final var actualRatingDTO = this.ratingMappers.ratingEntityRatingDTO(expectedFakeRatingEntity);
        assertNotNull(actualRatingDTO);
        assertEquals(expectedFakeRatingEntity.getSource(), actualRatingDTO.getSource());
        assertEquals(0D, actualRatingDTO.getValue());
    }

    private RatingRequestDTO getFakeRatingRequestDTO() {
        final var fakeRatingRequestDTO = new RatingRequestDTO();
        fakeRatingRequestDTO.setValue("123");
        fakeRatingRequestDTO.setMovieTitle("Fake Movie Title");
        return fakeRatingRequestDTO;
    }

    private UsernamePasswordAuthenticationToken getFakeAuthentication() {
        return new UsernamePasswordAuthenticationToken("Fake Principal", "Fake Credentials", Collections.emptyList());
    }

    private MovieAPIEntity getFakeMovieAPIEntity() {
        final var fakeMovieAPIEntity = new MovieAPIEntity();
        fakeMovieAPIEntity.setOscarWinner(true);
        fakeMovieAPIEntity.setTitle("Fake Movie Title");
        fakeMovieAPIEntity.setBoxOffice(new BigDecimal(65432));

        final var ratingEntityCollection = new ArrayList<RatingEntity>();
        IntStream.range(0, 5).forEach(index -> {
            final var fakeRatingEntity = new RatingEntity();
            fakeRatingEntity.setSource(String.format("Fake Rating Source %d", index));
            fakeRatingEntity.setValue(123D * index);
            fakeRatingEntity.setMovieAPIEntity(fakeMovieAPIEntity);
            ratingEntityCollection.add(fakeRatingEntity);
        });

        fakeMovieAPIEntity.setRatings(ratingEntityCollection);
        return fakeMovieAPIEntity;
    }
}
