package nl.backbase.mapper;

import nl.backbase.dto.RatingDTO;
import nl.backbase.dto.source.MovieSourceDTO;
import nl.backbase.model.MovieEntity;
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
    @DisplayName("GIVEN a null MovieDTOEntity , WHEN the mapper tries to map it, THEN it should return null")
    public void givenANullMovieDTOEntityWhenTheMapperTriesToMapItThenItShouldReturnNull() {
        final var actualMovieDTO = this.movieMappers.movieEntityToBestPictureMovieDTO(null);
        assertNull(actualMovieDTO);
    }

    @Test
    @DisplayName("GIVEN a fake entity, WHEN the mapper tries to map it, THEN it should return an actual BestPictureMovieDTO with expected values")
    public void givenAFakeMovieEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieDTOWithExpectedValues() {
        final var expectedFakeTitle = "Fake Movie Title";
        final var expectedFakeRatingEntityCollection = getFakeRatingEntityCollection();

        final var expectedFakeMovieEntity = new MovieEntity();
        expectedFakeMovieEntity.setBoxOffice(new BigDecimal(12345));
        expectedFakeMovieEntity.setTitle(expectedFakeTitle);
        expectedFakeMovieEntity.setRatings(expectedFakeRatingEntityCollection);

        final var actualMovieDTO = this.movieMappers.movieEntityToBestPictureMovieDTO(expectedFakeMovieEntity);

        assertEquals(expectedFakeTitle, actualMovieDTO.getTitle());

        final var actualRatingDTOCollection = actualMovieDTO.getRatings();
        assertEquals(expectedFakeRatingEntityCollection.size(), actualRatingDTOCollection.size(), "The amount of RatingDTO collection items should be the same as the RatingEntity collection");

        final var actualRatingDTOList = new ArrayList<>(actualRatingDTOCollection);
        IntStream.range(0, expectedFakeRatingEntityCollection.size()).forEach(index -> {
            final var expectedRatingEntity = expectedFakeRatingEntityCollection.get(index);
            final var actualRatingDTO = actualRatingDTOList.get(index);
            assertRatingDTORatingEntity(index, expectedRatingEntity, actualRatingDTO);
        });
    }

    @Test
    @DisplayName("GIVEN a null fake MovieSourceDTO, WHEN the mapper tries to map it, THEN it should return null")
    public void givenANullFakeMovieSourceDTOWhenTheMapperTriesToMapItThenItShouldReturnNull() {
        final var actualMovieEntity = this.movieMappers.movieSourceDTOToMovieEntity(null);
        assertNull(actualMovieEntity);
    }

    @Test
    @DisplayName("GIVEN a fake MovieSourceDTO with fake values, WHEN the mapper tries to map it, THEN it should return a MovieEntity with the expected values")
    public void givenAFakeMovieSourceDTOWithFakeValuesWhenTheMapperTriesToMapItThenItShouldReturnAMovieEntityWithTheExpectedValues() {
        final var expectedFakeMovieSourceDTO = new MovieSourceDTO();
        expectedFakeMovieSourceDTO.setTitle("Fake Title");
        expectedFakeMovieSourceDTO.setBoxOffice("12345");

        final var actualMovieEntity = this.movieMappers.movieSourceDTOToMovieEntity(expectedFakeMovieSourceDTO);
        assertEquals(expectedFakeMovieSourceDTO.getTitle(), actualMovieEntity.getTitle());
        assertEquals(expectedFakeMovieSourceDTO.getBoxOffice(), actualMovieEntity.getBoxOffice().toString());
        assertTrue(actualMovieEntity.getRatings().isEmpty(), "The rating collection should be empty");
    }

    @Test
    @DisplayName("GIVEN a null MovieTop10Entity, WHEN the mapper tries to map it, THEN it should return a null value")
    public void givenANullMovieSummaryEntityWhenTheMapperTriesToMapItThenItShouldReturnANullValue() {
        final var actualMovieSummaryDTO = this.movieMappers.movieTop10EntityToMovieTop10DTO((MovieTop10Entity) null);
        assertNull(actualMovieSummaryDTO);
    }

    @Test
    @DisplayName("GIVEN a null MovieTop10Entity Collection, WHEN the mapper tries to map it, THEN it should return an empty collection")
    public void givenANullMovieSummaryEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnEmptyCollection() {
        final var actualMovieSummaryDTO = this.movieMappers.movieTop10EntityToMovieTop10DTO((Collection<MovieTop10Entity>) null);
        assertTrue(actualMovieSummaryDTO.isEmpty());
    }

    @Test
    @DisplayName("GIVEN a fake MovieTop10Entity, WHEN the mapper tries to map it, THEN it should return an actual MovieTop10DTO with the expected values")
    public void givenAFakeMovieSummaryEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieSummaryDTOWithTheExpectedValues() {
        final var expectedFakeMovieSummaryEntity = new MovieTop10Entity("Fake Title", 1234D, new BigDecimal("7654321"), true);
        final var actualMovieSummaryDTO = this.movieMappers.movieTop10EntityToMovieTop10DTO(expectedFakeMovieSummaryEntity);
        assertEquals(expectedFakeMovieSummaryEntity.getTitle(), actualMovieSummaryDTO.getTitle());
        assertEquals(parseBooleanToString(expectedFakeMovieSummaryEntity.getBestPictureOscarWinner()), actualMovieSummaryDTO.getBestPictureOscarWinner());
        assertEquals(expectedFakeMovieSummaryEntity.getAverage(), actualMovieSummaryDTO.getAverage());
        assertEquals(expectedFakeMovieSummaryEntity.getBoxOffice(), actualMovieSummaryDTO.getBoxOffice());
    }

    @Test
    @DisplayName("GIVEN a fake MovieTop10Entity collection, WHEN the mapper tries to map it, THEN it should return an actual MovieTop10DTO collection with the expected values")
    public void givenAFakeMovieSummaryEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieSummaryDTOCollectionWithTheExpectedValues() {
        final var expectedFakeMovieSummaryEntityCollection = new ArrayList<MovieTop10Entity>();
        IntStream.range(0, 5).forEach(index -> expectedFakeMovieSummaryEntityCollection.add(new MovieTop10Entity(String.format("Fake Title %d", index), 1234D * index, new BigDecimal("7654321").multiply(BigDecimal.valueOf(index)), index % 2 == 0)));

        final var actualMovieSummaryDTOCollection = new ArrayList<>(this.movieMappers.movieTop10EntityToMovieTop10DTO(expectedFakeMovieSummaryEntityCollection));

        assertEquals(expectedFakeMovieSummaryEntityCollection.size(), actualMovieSummaryDTOCollection.size(), "The amount of parsed MovieTop10DTO should be the same as the expected collection");

        IntStream.range(0, expectedFakeMovieSummaryEntityCollection.size()).forEach(index -> {
            final var expectedFakeMovieSummaryEntity = expectedFakeMovieSummaryEntityCollection.get(index);
            final var actualMovieSummaryDTO = actualMovieSummaryDTOCollection.get(index);
            assertEquals(expectedFakeMovieSummaryEntity.getTitle(), actualMovieSummaryDTO.getTitle());
            assertEquals(parseBooleanToString(expectedFakeMovieSummaryEntity.getBestPictureOscarWinner()), actualMovieSummaryDTO.getBestPictureOscarWinner());
            assertEquals(expectedFakeMovieSummaryEntity.getAverage(), actualMovieSummaryDTO.getAverage());
            assertEquals(expectedFakeMovieSummaryEntity.getBoxOffice(), actualMovieSummaryDTO.getBoxOffice());

        });
    }

    private String parseBooleanToString(final Boolean bestPictureOscarWinner) {
        if (bestPictureOscarWinner == null) { return MovieMappers.BEST_PICTURE_OSCAR_WINNER_NO; }
        return bestPictureOscarWinner ? MovieMappers.BEST_PICTURE_OSCAR_WINNER_YES : MovieMappers.BEST_PICTURE_OSCAR_WINNER_NO;
    }

    private void assertRatingDTORatingEntity(final int index, final RatingEntity expectedRatingEntity, final RatingDTO actualRatingDTO) {
        assertEquals(expectedRatingEntity.getSource(), actualRatingDTO.getSource(), "The Source values should not be different between the entity and the mapped DTO at index " + index);
        assertEquals(expectedRatingEntity.getValue(), actualRatingDTO.getValue(), "The Value values should not be different between the entity and the mapped DTO at index " + index);
    }

    private List<RatingEntity> getFakeRatingEntityCollection() {
        final var ratingEntityCollection = new ArrayList<RatingEntity>();
        IntStream.range(0, COLLECTION_SIZE).forEach(index -> {
            final var ratingEntity = new RatingEntity();
            ratingEntity.setId((long) index);
            ratingEntity.setValue((double) index);
            ratingEntity.setSource(String.format("Fake Source %d", index));
            ratingEntityCollection.add(ratingEntity);
        });
        return ratingEntityCollection;
    }
}
