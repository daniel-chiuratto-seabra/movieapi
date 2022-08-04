package nl.backbase.mapper.impl;

import nl.backbase.helper.ValueParserHelper;
import nl.backbase.dto.source.MovieAPISourceDTO;
import nl.backbase.dto.source.RatingSourceDTO;
import nl.backbase.mapper.MovieMappers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MovieAPISourceDTOMovieEntityMapperTest {

    private static final int COLLECTION_SIZE = 5;

    private final MovieMappers movieMappers = Mappers.getMapper(MovieMappers.class);

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
    public void givenAFakeMovieapiSourceDTOWithFakeValuesWhenTheMapperTriesToMapItThenItShouldReturnAMovieAPIEntityWithTheExpectedValues() {
        final var expectedFakeMovieAPISourceDTO = new MovieAPISourceDTO();
        expectedFakeMovieAPISourceDTO.setTitle("Fake Title");
        expectedFakeMovieAPISourceDTO.setBoxOffice("12345");

        final var expectedFakeMovieAPISourceRatingSourceDTOCollection = new ArrayList<RatingSourceDTO>();
        IntStream.range(0, COLLECTION_SIZE).forEach(index -> {
            final var expectedFakeRatingSourceDTO = new RatingSourceDTO();
            expectedFakeRatingSourceDTO.setSource(String.format("Fake Source %d", index));
            expectedFakeRatingSourceDTO.setValue(String.format("Fake Value %d", index));
            expectedFakeMovieAPISourceRatingSourceDTOCollection.add(expectedFakeRatingSourceDTO);
        });
        expectedFakeMovieAPISourceDTO.setRatings(expectedFakeMovieAPISourceRatingSourceDTOCollection);

        final var actualMovieAPIEntity = this.movieMappers.movieAPISourceDTOToMovieAPIEntity(expectedFakeMovieAPISourceDTO);
        assertNotNull(actualMovieAPIEntity);
        assertEquals(expectedFakeMovieAPISourceDTO.getTitle(), actualMovieAPIEntity.getTitle());
        assertNotNull(actualMovieAPIEntity.getBoxOffice());
        assertEquals(expectedFakeMovieAPISourceDTO.getBoxOffice(), actualMovieAPIEntity.getBoxOffice().toString());

        final var actualMovieAPIEntityRatingEntityList = new ArrayList<>(actualMovieAPIEntity.getRatings());
        assertEquals(expectedFakeMovieAPISourceRatingSourceDTOCollection.size(), actualMovieAPIEntityRatingEntityList.size(), "The amount of RatingEntity collection items should be the same as the RatingSourceDTO collection");
        IntStream.range(0, COLLECTION_SIZE).forEach(index -> {
            final var expectedFakeRatingSourceDTO = expectedFakeMovieAPISourceRatingSourceDTOCollection.get(index);
            final var actualRatingEntity = actualMovieAPIEntityRatingEntityList.get(index);
            assertEquals(expectedFakeRatingSourceDTO.getSource(), actualRatingEntity.getSource());
            assertEquals(ValueParserHelper.parseValueToDouble(expectedFakeRatingSourceDTO.getValue()), actualRatingEntity.getValue());
        });
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

            final var expectedFakeMovieAPISourceRatingSourceDTOCollection = new ArrayList<RatingSourceDTO>();
            IntStream.range(0, COLLECTION_SIZE).forEach(indexSrc -> {
                final var expectedFakeRatingSourceDTO = new RatingSourceDTO();
                expectedFakeRatingSourceDTO.setSource(String.format("Fake Source %d %d", index, indexSrc));
                expectedFakeRatingSourceDTO.setValue(String.format("Fake Value %d %d", index, indexSrc));
                expectedFakeMovieAPISourceRatingSourceDTOCollection.add(expectedFakeRatingSourceDTO);
            });
            expectedFakeMovieAPISourceDTO.setRatings(expectedFakeMovieAPISourceRatingSourceDTOCollection);
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

            final var actualMovieAPIEntityRatingEntityList = new ArrayList<>(actualMovieAPIEntity.getRatings());
            final var expectedFakeMovieAPISourceRatingSourceDTOList = new ArrayList<>(expectedFakeMovieAPISourceDTO.getRatings());
            assertEquals(expectedFakeMovieAPISourceRatingSourceDTOList.size(), actualMovieAPIEntityRatingEntityList.size(), "The amount of RatingEntity collection items should be the same as the RatingSourceDTO collection");
            IntStream.range(0, COLLECTION_SIZE).forEach(indexSrc -> {
                final var expectedFakeRatingSourceDTO = expectedFakeMovieAPISourceRatingSourceDTOList.get(indexSrc);
                final var actualRatingEntity = actualMovieAPIEntityRatingEntityList.get(indexSrc);
                assertEquals(expectedFakeRatingSourceDTO.getSource(), actualRatingEntity.getSource());
                assertEquals(ValueParserHelper.parseValueToDouble(expectedFakeRatingSourceDTO.getValue()), actualRatingEntity.getValue());
            });
        });
    }
}