package nl.backbase.mapper.impl;

import nl.backbase.mapper.MovieMappers;
import nl.backbase.model.MovieAPISummaryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNull;

class MovieAPISummaryEntityMovieAPISummaryDTOMapperTest {

    private final MovieMappers movieMappers = Mappers.getMapper(MovieMappers.class);


    @Test
    @DisplayName("")
    public void test() {
        final var actualMovieAPISummaryDTO = this.movieMappers.movieAPISummaryEntityToMovieAPISummaryDTO((MovieAPISummaryEntity) null);
        assertNull(actualMovieAPISummaryDTO);
    }

}