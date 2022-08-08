package nl.backbase.mapper;

import nl.backbase.dto.RatingDTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieTop10Entity;
import nl.backbase.model.RatingEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class MovieMappersTest {
    private static final int COLLECTION_SIZE = 5;

    private final MovieMappers movieMappers = new MovieMappers(new RatingMappers());

    @Test
    @DisplayName("GIVEN a null MovieAPIDTOEntity , WHEN the mapper tries to map it, THEN it should return null")
    public void givenANullMovieAPIDTOEntityWhenTheMapperTriesToMapItThenItShouldReturnNull() {
        final var actualMovieAPIDTO = this.movieMappers.movieAPIEntityToBestPictureMovieDTO((MovieAPIEntity) null);
        assertNull(actualMovieAPIDTO);
    }

    @Test
    @DisplayName("GIVEN a fake entity, WHEN the mapper tries to map it, THEN it should return an actual BestPictureMovieDTO with expected values")
    public void givenAFakeMovieAPIEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieAPIDTOWithExpectedValues() {
        final var expectedFakeTitle = "Fake Movie Title";
        final var expectedFakeRatingEntityCollection = getFakeRatingEntityCollection(1);

        final var expectedFakeMovieAPIEntity = new MovieAPIEntity();
        expectedFakeMovieAPIEntity.setBoxOffice(new BigDecimal(12345));
        expectedFakeMovieAPIEntity.setTitle(expectedFakeTitle);
        expectedFakeMovieAPIEntity.setRatings(expectedFakeRatingEntityCollection);

        final var actualMovieAPIDTO = this.movieMappers.movieAPIEntityToBestPictureMovieDTO(expectedFakeMovieAPIEntity);

        assertNotNull(actualMovieAPIDTO);
        assertEquals(expectedFakeTitle, actualMovieAPIDTO.getTitle());

        final var actualRatingDTOCollection = actualMovieAPIDTO.getRatings();
        assertNotNull(actualRatingDTOCollection);
        assertFalse(actualRatingDTOCollection.isEmpty(), "The returned RatingDTO collection should not be empty");
        assertEquals(expectedFakeRatingEntityCollection.size(), actualRatingDTOCollection.size(), "The amount of RatingDTO collection items should be the same as the RatingEntity collection");

        final var actualRatingDTOList = new ArrayList<>(actualRatingDTOCollection);
        IntStream.range(0, expectedFakeRatingEntityCollection.size()).forEach(index -> {
            final var expectedRatingEntity = expectedFakeRatingEntityCollection.get(index);
            final var actualRatingDTO = actualRatingDTOList.get(index);

            assertNotNull(actualRatingDTO);
            assertRatingDTORatingEntity(index, expectedRatingEntity, actualRatingDTO);
        });
    }

    @Test
    @DisplayName("GIVEN a null fake MovieAPISourceDTO, WHEN the mapper tries to map it, THEN it should return null")
    public void givenANullFakeMovieAPISourceDTOWhenTheMapperTriesToMapItThenItShouldReturnNull() {
        final var actualMovieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity((MovieAPISourceDTO) null);
        assertNull(actualMovieAPIEntity);
    }

    @Test
    @DisplayName("GIVEN a fake MovieAPISourceDTO with fake values, WHEN the mapper tries to map it, THEN it should return a MovieAPIEntity with the expected values")
    public void givenAFakeMovieAPISourceDTOWithFakeValuesWhenTheMapperTriesToMapItThenItShouldReturnAMovieAPIEntityWithTheExpectedValues() {
        final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
        expectedFakeMovieAPISourceDTO.setTitle("Fake Title");
        expectedFakeMovieAPISourceDTO.setBoxOffice("12345");

        final var actualMovieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(expectedFakeMovieAPISourceDTO);
        assertNotNull(actualMovieAPIEntity);
        assertEquals(expectedFakeMovieAPISourceDTO.getTitle(), actualMovieAPIEntity.getTitle());
        assertNotNull(actualMovieAPIEntity.getBoxOffice());
        assertEquals(expectedFakeMovieAPISourceDTO.getBoxOffice(), actualMovieAPIEntity.getBoxOffice().toString());
        assertNotNull(actualMovieAPIEntity.getRatings());
        assertTrue(actualMovieAPIEntity.getRatings().isEmpty(), "The rating collection should be empty");
    }

    @Test
    @DisplayName("GIVEN a null MovieTop10Entity, WHEN the mapper tries to map it, THEN it should return a null value")
    public void givenANullMovieAPISummaryEntityWhenTheMapperTriesToMapItThenItShouldReturnANullValue() {
        final var actualMovieAPISummaryDTO = this.movieMappers.movieTop10EntityToMovieTop10DTO((MovieTop10Entity) null);
        assertNull(actualMovieAPISummaryDTO);
    }

    @Test
    @DisplayName("GIVEN a null MovieTop10Entity Collection, WHEN the mapper tries to map it, THEN it should return an empty collection")
    public void givenANullMovieAPISummaryEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnEmptyCollection() {
        final var actualMovieAPISummaryDTO = this.movieMappers.movieTop10EntityToMovieTop10DTO((Collection<MovieTop10Entity>) null);
        assertNotNull(actualMovieAPISummaryDTO);
        assertTrue(actualMovieAPISummaryDTO.isEmpty());
    }

    @Test
    @DisplayName("GIVEN a fake MovieTop10Entity, WHEN the mapper tries to map it, THEN it should return an actual MovieTop10DTO with the expected values")
    public void givenAFakeMovieAPISummaryEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieAPISummaryDTOWithTheExpectedValues() {
        final var expectedFakeMovieAPISummaryEntity = new MovieTop10Entity("Fake Title", 1234D, new BigDecimal("7654321"), true);
        final var actualMovieAPISummaryDTO = this.movieMappers.movieTop10EntityToMovieTop10DTO(expectedFakeMovieAPISummaryEntity);
        assertNotNull(actualMovieAPISummaryDTO);
        assertEquals(expectedFakeMovieAPISummaryEntity.getTitle(), actualMovieAPISummaryDTO.getTitle());
        assertEquals(parseBooleanToString(expectedFakeMovieAPISummaryEntity.getOscarWinner()), actualMovieAPISummaryDTO.getOscarWinner());
        assertEquals(expectedFakeMovieAPISummaryEntity.getAverage(), actualMovieAPISummaryDTO.getAverage());
        assertEquals(expectedFakeMovieAPISummaryEntity.getBoxOffice(), actualMovieAPISummaryDTO.getBoxOffice());
    }

    @Test
    @DisplayName("GIVEN a fake MovieTop10Entity collection, WHEN the mapper tries to map it, THEN it should return an actual MovieTop10DTO collection with the expected values")
    public void givenAFakeMovieAPISummaryEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieAPISummaryDTOCollectionWithTheExpectedValues() {
        final var expectedFakeMovieAPISummaryEntityCollection = new ArrayList<MovieTop10Entity>();
        IntStream.range(0, 5).forEach(index -> expectedFakeMovieAPISummaryEntityCollection.add(new MovieTop10Entity(String.format("Fake Title %d", index), 1234D * index, new BigDecimal("7654321").multiply(BigDecimal.valueOf(index)), index % 2 == 0)));

        final var actualMovieAPISummaryDTOCollection = new ArrayList<>(this.movieMappers.movieTop10EntityToMovieTop10DTO(expectedFakeMovieAPISummaryEntityCollection));

        assertEquals(expectedFakeMovieAPISummaryEntityCollection.size(), actualMovieAPISummaryDTOCollection.size(), "The amount of parsed MovieTop10DTO should be the same as the expected collection");

        IntStream.range(0, expectedFakeMovieAPISummaryEntityCollection.size()).forEach(index -> {
            final var expectedFakeMovieAPISummaryEntity = expectedFakeMovieAPISummaryEntityCollection.get(index);
            final var actualMovieAPISummaryDTO = actualMovieAPISummaryDTOCollection.get(index);
            assertEquals(expectedFakeMovieAPISummaryEntity.getTitle(), actualMovieAPISummaryDTO.getTitle());
            assertEquals(parseBooleanToString(expectedFakeMovieAPISummaryEntity.getOscarWinner()), actualMovieAPISummaryDTO.getOscarWinner());
            assertEquals(expectedFakeMovieAPISummaryEntity.getAverage(), actualMovieAPISummaryDTO.getAverage());
            assertEquals(expectedFakeMovieAPISummaryEntity.getBoxOffice(), actualMovieAPISummaryDTO.getBoxOffice());

        });
    }

    private String parseBooleanToString(final Boolean oscarWinner) {
        if (oscarWinner == null) { return MovieMappers.OSCAR_WINNER_NO; }
        return oscarWinner ? MovieMappers.OSCAR_WINNER_YES : MovieMappers.OSCAR_WINNER_NO;
    }

    private void assertRatingDTORatingEntity(final int index, final RatingEntity expectedRatingEntity, final RatingDTO actualRatingDTO) {
        assertEquals(expectedRatingEntity.getSource(), actualRatingDTO.getSource(), "The Source values should not be different between the entity and the mapped DTO at index " + index);
        assertEquals(expectedRatingEntity.getValue(), actualRatingDTO.getValue(), "The Value values should not be different between the entity and the mapped DTO at index " + index);
    }

    private List<RatingEntity> getFakeRatingEntityCollection(final int indexSrc) {
        final var ratingEntityCollection = new ArrayList<RatingEntity>();
        IntStream.range(0, COLLECTION_SIZE).forEach(index -> {
            final var ratingEntity = new RatingEntity();
            ratingEntity.setId((long) index * (long) indexSrc);
            ratingEntity.setValue((double) index * 5 * indexSrc);
            ratingEntity.setSource(String.format("Fake Source %d %d", index, indexSrc));
            ratingEntityCollection.add(ratingEntity);
        });
        return ratingEntityCollection;
    }
}
