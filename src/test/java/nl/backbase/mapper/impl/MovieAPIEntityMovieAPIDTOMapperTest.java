package nl.backbase.mapper.impl;

import nl.backbase.dto.RatingDTO;
import nl.backbase.mapper.MovieMappers;
import nl.backbase.model.MovieAPIEntity;
import nl.backbase.model.RatingEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class MovieAPIEntityMovieAPIDTOMapperTest {

    private static final int COLLECTION_SIZE = 5;

    private final MovieMappers movieMappers = Mappers.getMapper(MovieMappers.class);

    @Test
    @DisplayName("GIVEN a null entity, WHEN the mapper tries to map it, THEN it should return null")
    public void givenANullEntityWhenTheMapperTriesToMapItThenItShouldReturnNull() {
        final var actualMovieAPIDTO = this.movieMappers.movieAPITEntityToMovieAPIDTO((MovieAPIEntity) null);
        assertNull(actualMovieAPIDTO);
    }

    @Test
    @DisplayName("GIVEN a null entity collection, WHEN the mapper tries to map it, THEN it should return an empty MovieAPIDTO Collection")
    public void givenANullEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnEmptyCollection() {
        final var actualMovieAPIDTOCollection = this.movieMappers.movieAPITEntityToMovieAPIDTO((Collection<MovieAPIEntity>) null);
        assertNotNull(actualMovieAPIDTOCollection);
        assertTrue(actualMovieAPIDTOCollection.isEmpty());
    }

    @Test
    @DisplayName("GIVEN a fake entity, WHEN the mapper tries to map it, THEN it should return an actual DTO")
    public void givenAFakeEntityWhenTheMapperTriesToMapItThenItShouldReturnAnActualFakeDto() {
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
    @DisplayName("GIVEN a fake entity collection, WHEN the mapper tries to map it, THEN it should return an actual DTO collection")
    public void givenAFakeEntityCollectionWhenTheMapperTriesToMapItThenItShouldReturnAnActualFakeDTOCollection() {
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

    private void assertRatingDTORatingEntity(final int index, final RatingEntity expectedRatingEntity, final RatingDTO actualRatingDTO) {
        assertEquals(expectedRatingEntity.getSource(), actualRatingDTO.getSource(), "The Source values should not be different between the entity and the mapped DTO at index " + index);
        assertEquals(expectedRatingEntity.getValue(), actualRatingDTO.getValue(), "The Value values should not be different between the entity and the mapped DTO at index " + index);
    }

    private List<RatingEntity> getFakeRatingEntityCollection(final int indexSrc) {
        final var ratingEntityCollection = new ArrayList<RatingEntity>();
        IntStream.range(0, COLLECTION_SIZE).forEach(index -> {
            final var ratingEntity = new RatingEntity();
            ratingEntity.setId((long) index * (long) indexSrc);
            ratingEntity.setMovieId((long) COLLECTION_SIZE - index * (long) indexSrc);
            ratingEntity.setValue((double) index * 5 * indexSrc);
            ratingEntity.setSource(String.format("Fake Source %d %d", index, indexSrc));
            ratingEntityCollection.add(ratingEntity);
        });
        return ratingEntityCollection;
    }
}