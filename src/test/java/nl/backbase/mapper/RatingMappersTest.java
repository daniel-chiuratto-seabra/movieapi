package nl.backbase.mapper;

import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.model.MovieEntity;
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
    @DisplayName("GIVEN fakes RatingRequestDTO, Authentication and MovieEntity, WHEN the mapper tries to map without having all of them set, THEN it should return null")
    public void givenFakesRatingRequestDTOAuthenticationAndMovieEntityWhenTheMapperTriesToMapWithoutHavingAllOfThemSetThenItShouldReturnNull() {
        final var expectedFakeRatingRequestDTO = getFakeRatingRequestDTO();
        final var expectedFakeAuthentication = getFakeAuthentication();
        final var expectedFakeMovieEntity = getFakeMovieEntity();

        var actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(null, null, null);
        assertNull(actualRatingEntity);

        actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(expectedFakeRatingRequestDTO, null, null);
        assertNull(actualRatingEntity);

        actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(null, expectedFakeAuthentication, null);
        assertNull(actualRatingEntity);

        actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(null, null, expectedFakeMovieEntity);
        assertNull(actualRatingEntity);

        actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(expectedFakeRatingRequestDTO, null, expectedFakeMovieEntity);
        assertNull(actualRatingEntity);
    }

    @Test
    @DisplayName("GIVEN fakes RatingRequestDTO, Authentication and MovieEntity, WHEN the mapper tries to map with all of them set, THEN it should return an actual RatingEntity with expected values")
    public void givenFakesRatingRequestDTOAuthenticationAndMovieEntityWhenTheMapperTriesToMapWithAllOfThemSetThenItShouldTeturnAnActualRatingEntityWithExpectedValues() {
        final var expectedFakeRatingRequestDTO = getFakeRatingRequestDTO();
        final var expectedFakeAuthentication = getFakeAuthentication();
        final var expectedFakeMovieEntity = getFakeMovieEntity();

        final var actualRatingEntity = this.ratingMappers.ratingRequestDTORatingEntity(expectedFakeRatingRequestDTO, expectedFakeAuthentication, expectedFakeMovieEntity);
        assertEquals(expectedFakeAuthentication.getPrincipal(), actualRatingEntity.getSource());
        assertEquals(Integer.parseInt(expectedFakeRatingRequestDTO.getValue()), actualRatingEntity.getValue().intValue());
        assertEquals(expectedFakeMovieEntity, actualRatingEntity.getMovieEntity());
    }

    @Test
    @DisplayName("GIVEN an expected fake RatingEntity, WHEN the mapper tries to map it, THEN it should return an actual RatingDTO with the expected values")
    public void givenAnExpectedFakeRatingEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualRatingDTOWithTheExpectedValues() {
        final var expectedFakeRatingEntity = new RatingEntity();
        expectedFakeRatingEntity.setSource("Fake Source");
        expectedFakeRatingEntity.setValue(1234D);
        expectedFakeRatingEntity.setMovieEntity(getFakeMovieEntity());

        final var actualRatingDTO = this.ratingMappers.ratingEntityRatingDTO(expectedFakeRatingEntity);
        assertEquals(expectedFakeRatingEntity.getSource(), actualRatingDTO.getSource());
        assertEquals(expectedFakeRatingEntity.getValue(), actualRatingDTO.getValue());
    }

    @Test
    @DisplayName("GIVEN an expected fake RatingEntity with null value, WHEN the mapper tries to map it, THEN it should return an actual RatingDTO with the expected values with 0 as value")
    public void givenAnExpectedFakeRatingEntityWithNullValueWhenTheMapperTriesToMapItThenItShouldReturnAnActualRatingDTOWithTheExpectedValuesWith0AsValue() {
        final var expectedFakeRatingEntity = new RatingEntity();
        expectedFakeRatingEntity.setSource("Fake Source");
        expectedFakeRatingEntity.setValue(null);
        expectedFakeRatingEntity.setMovieEntity(getFakeMovieEntity());

        final var actualRatingDTO = this.ratingMappers.ratingEntityRatingDTO(expectedFakeRatingEntity);
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

    private MovieEntity getFakeMovieEntity() {
        final var fakeMovieEntity = new MovieEntity();
        fakeMovieEntity.setOscarWinner(true);
        fakeMovieEntity.setTitle("Fake Movie Title");
        fakeMovieEntity.setBoxOffice(new BigDecimal(65432));

        final var ratingEntityCollection = new ArrayList<RatingEntity>();
        IntStream.range(0, 5).forEach(index -> {
            final var fakeRatingEntity = new RatingEntity();
            fakeRatingEntity.setSource(String.format("Fake Rating Source %d", index));
            fakeRatingEntity.setValue(123D * index);
            fakeRatingEntity.setMovieEntity(fakeMovieEntity);
            ratingEntityCollection.add(fakeRatingEntity);
        });

        fakeMovieEntity.setRatings(ratingEntityCollection);
        return fakeMovieEntity;
    }
}
