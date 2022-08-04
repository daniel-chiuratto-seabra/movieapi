package nl.backbase.mapper;

import nl.backbase.dto.RatingDTO;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.MovieAPISummaryEntity;
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
        final var actualMovieAPIDTO = this.movieMappers.movieAPITEntityToMovieAPIDTO((MovieAPIEntity) null);
        assertNull(actualMovieAPIDTO);
    }

    @Test
    @DisplayName("GIVEN a null MovieAPIDTOEntity collection, WHEN the mapper tries to map it, THEN it should return an empty MovieAPIDTO Collection")
    public void givenANullEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnEmptyCollection() {
        final var actualMovieAPIDTOCollection = this.movieMappers.movieAPITEntityToMovieAPIDTO((Collection<MovieAPIEntity>) null);
        assertNotNull(actualMovieAPIDTOCollection);
        assertTrue(actualMovieAPIDTOCollection.isEmpty());
    }

    @Test
    @DisplayName("GIVEN a fake entity, WHEN the mapper tries to map it, THEN it should return an actual MovieAPIDTO with expected values")
    public void givenAFakeMovieAPIEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieAPIDTOWithExpectedValues() {
        final var expectedFakeTitle = "Fake Movie Title";
        final var expectedFakeRatingEntityCollection = getFakeRatingEntityCollection(1);

        final var expectedFakeMovieAPIEntity = new MovieAPIEntity();
        expectedFakeMovieAPIEntity.setBoxOffice(new BigDecimal(12345));
        expectedFakeMovieAPIEntity.setTitle(expectedFakeTitle);
        expectedFakeMovieAPIEntity.setRatings(expectedFakeRatingEntityCollection);

        final var actualMovieAPIDTO = this.movieMappers.movieAPITEntityToMovieAPIDTO(expectedFakeMovieAPIEntity);

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
    @DisplayName("GIVEN a fake MovieAPIEntity collection, WHEN the mapper tries to map it, THEN it should return an actual MovieAPIDTO collection with expected values")
    public void givenAFakeMovieAPIEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieAPIDTOCollectionWithExpectedValues() {
        final var expectedFakeTitle = "Fake Movie Title";
        final var expectedFakeMovieAPIEntityList = new ArrayList<MovieAPIEntity>();

        IntStream.range(0, COLLECTION_SIZE).forEach(index -> {
            final var expectedFakeMovieAPIEntity = new MovieAPIEntity();
            expectedFakeMovieAPIEntity.setBoxOffice(new BigDecimal(12345).multiply(BigDecimal.valueOf(index)));
            expectedFakeMovieAPIEntity.setTitle(String.format("%s %d", expectedFakeTitle, index));
            expectedFakeMovieAPIEntity.setRatings(getFakeRatingEntityCollection(index));
            expectedFakeMovieAPIEntityList.add(expectedFakeMovieAPIEntity);
        });

        final var actualMovieAPIDTOList = new ArrayList<>(this.movieMappers.movieAPITEntityToMovieAPIDTO(expectedFakeMovieAPIEntityList));

        assertNotNull(actualMovieAPIDTOList);
        assertFalse(actualMovieAPIDTOList.isEmpty());
        assertEquals(expectedFakeMovieAPIEntityList.size(), actualMovieAPIDTOList.size(), "The amount of MovieAPIDTO collection items should be the same as the MovieAPIEntity collection");

        IntStream.range(0, expectedFakeMovieAPIEntityList.size()).forEach(index -> {
            final var expectedFakeMovieAPIEntity = expectedFakeMovieAPIEntityList.get(index);
            final var actualMovieAPIDTO = actualMovieAPIDTOList.get(index);

            assertEquals(expectedFakeMovieAPIEntity.getTitle(), actualMovieAPIDTO.getTitle());

            final var actualRatingDTOCollection = actualMovieAPIDTO.getRatings();
            assertNotNull(actualRatingDTOCollection);
            assertFalse(actualRatingDTOCollection.isEmpty(), "The returned RatingDTO collection should not be empty");

            final var expectedFakeRatingEntityCollection = new ArrayList<>(expectedFakeMovieAPIEntity.getRatings());
            assertEquals(expectedFakeRatingEntityCollection.size(), actualRatingDTOCollection.size(), "The amount of RatingDTO collection items should be the same as the RatingEntity collection");

            final var actualRatingDTOList = new ArrayList<>(actualRatingDTOCollection);
            IntStream.range(0, expectedFakeRatingEntityCollection.size()).forEach(indexSrc -> {
                final var expectedRatingEntity = expectedFakeRatingEntityCollection.get(indexSrc);
                final var actualRatingDTO = actualRatingDTOList.get(indexSrc);

                assertNotNull(actualRatingDTO);
                assertRatingDTORatingEntity(indexSrc, expectedRatingEntity, actualRatingDTO);
            });
        });
    }

    @Test
    @DisplayName("GIVEN a null fake MovieAPISourceDTO, WHEN the mapper tries to map it, THEN it should return null")
    public void givenANullFakeMovieAPISourceDTOWhenTheMapperTriesToMapItThenItShouldReturnNull() {
        final var actualMovieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity((MovieAPISourceDTO) null);
        assertNull(actualMovieAPIEntity);
    }

    @Test
    @DisplayName("GIVEN a null fake MovieAPISourceDTO collection, WHEN the mapper tries to map it, THEN it should return an empty MovieAPIEntity collection")
    public void givenANullFakeMovieAPISourceDTOCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnEmptyMovieAPIEntityCollection() {
        final var actualMovieAPIEntityCollection = this.movieMappers.movieAPISourceDTOToMovieAPIEntity((Collection<MovieAPISourceDTO>) null);
        assertNotNull(actualMovieAPIEntityCollection);
        assertTrue(actualMovieAPIEntityCollection.isEmpty());
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
    @DisplayName("GIVEN a fake MovieAPISourceDTO collection with fake values, WHEN the mapper tries to map it, THEN it should return a MovieAPIEntity collection with the expected values")
    public void givenAFakeMovieAPISourceDTOCollectionWithFakeValuesWhenTheMapperTriesToMapItThenItShouldReturnAMovieAPIEntityCollectionWithTheExpectedValues() {
        final var expectedFakeMovieAPISourceDTOList = new ArrayList<MovieAPISourceDTO>();
        IntStream.range(0, COLLECTION_SIZE).forEach(index -> {
            final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
            expectedFakeMovieAPISourceDTO.setTitle(String.format("Fake Title %d", index));
            expectedFakeMovieAPISourceDTO.setBoxOffice("12345");
            expectedFakeMovieAPISourceDTOList.add(expectedFakeMovieAPISourceDTO);
        });

        final var actualMovieAPIEntityList = new ArrayList<>(this.movieMappers.movieAPISourceDTOToMovieAPIEntity(expectedFakeMovieAPISourceDTOList));
        assertFalse(actualMovieAPIEntityList.isEmpty());
        assertEquals(expectedFakeMovieAPISourceDTOList.size(), actualMovieAPIEntityList.size());

        IntStream.range(0, expectedFakeMovieAPISourceDTOList.size()).forEach(index -> {
            final var expectedFakeMovieAPISourceDTO = expectedFakeMovieAPISourceDTOList.get(index);
            final var actualMovieAPIEntity = actualMovieAPIEntityList.get(index);

            assertNotNull(actualMovieAPIEntityList);
            assertEquals(expectedFakeMovieAPISourceDTO.getTitle(), actualMovieAPIEntity.getTitle());
            assertNotNull(actualMovieAPIEntity.getBoxOffice());
            assertEquals(expectedFakeMovieAPISourceDTO.getBoxOffice(), actualMovieAPIEntity.getBoxOffice().toString());
        });
    }

    @Test
    @DisplayName("GIVEN a null MovieAPISummaryEntity, WHEN the mapper tries to map it, THEN it should return a null value")
    public void givenANullMovieAPISummaryEntityWhenTheMapperTriesToMapItThenItShouldReturnANullValue() {
        final var actualMovieAPISummaryDTO = this.movieMappers.movieAPISummaryEntityToMovieAPISummaryDTO((MovieAPISummaryEntity) null);
        assertNull(actualMovieAPISummaryDTO);
    }

    @Test
    @DisplayName("GIVEN a null MovieAPISummaryEntity Collection, WHEN the mapper tries to map it, THEN it should return an empty collection")
    public void givenANullMovieAPISummaryEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnEmptyCollection() {
        final var actualMovieAPISummaryDTO = this.movieMappers.movieAPISummaryEntityToMovieAPISummaryDTO((Collection<MovieAPISummaryEntity>) null);
        assertNotNull(actualMovieAPISummaryDTO);
        assertTrue(actualMovieAPISummaryDTO.isEmpty());
    }

    @Test
    @DisplayName("GIVEN a fake MovieAPISummaryEntity, WHEN the mapper tries to map it, THEN it should return an actual MovieAPISummaryDTO with the expected values")
    public void givenAFakeMovieAPISummaryEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieAPISummaryDTOWithTheExpectedValues() {
        final var expectedFakeMovieAPISummaryEntity = new MovieAPISummaryEntity("Fake Title", 1234D, new BigDecimal("7654321"), true);
        final var actualMovieAPISummaryDTO = this.movieMappers.movieAPISummaryEntityToMovieAPISummaryDTO(expectedFakeMovieAPISummaryEntity);
        assertNotNull(actualMovieAPISummaryDTO);
        assertEquals(expectedFakeMovieAPISummaryEntity.getTitle(), actualMovieAPISummaryDTO.getTitle());
        assertEquals(parseBooleanToString(expectedFakeMovieAPISummaryEntity.getOscarWinner()), actualMovieAPISummaryDTO.getOscarWinner());
        assertEquals(expectedFakeMovieAPISummaryEntity.getAverage(), actualMovieAPISummaryDTO.getAverage());
        assertEquals(expectedFakeMovieAPISummaryEntity.getBoxOffice(), actualMovieAPISummaryDTO.getBoxOffice());
    }

    @Test
    @DisplayName("GIVEN a fake MovieAPISummaryEntity collection, WHEN the mapper tries to map it, THEN it should return an actual MovieAPISummaryDTO collection with the expected values")
    public void givenAFakeMovieAPISummaryEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnActualMovieAPISummaryDTOCollectionWithTheExpectedValues() {
        final var expectedFakeMovieAPISummaryEntityCollection = new ArrayList<MovieAPISummaryEntity>();
        IntStream.range(0, 5).forEach(index -> expectedFakeMovieAPISummaryEntityCollection.add(new MovieAPISummaryEntity(String.format("Fake Title %d", index), 1234D * index, new BigDecimal("7654321").multiply(BigDecimal.valueOf(index)), index % 2 == 0)));

        final var actualMovieAPISummaryDTOCollection = new ArrayList<>(this.movieMappers.movieAPISummaryEntityToMovieAPISummaryDTO(expectedFakeMovieAPISummaryEntityCollection));

        assertEquals(expectedFakeMovieAPISummaryEntityCollection.size(), actualMovieAPISummaryDTOCollection.size(), "The amount of parsed MovieAPISummaryDTO should be the same as the expected collection");

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
        if (oscarWinner == null) { return "NO"; }
        return oscarWinner ? "YES" : "NO";
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
