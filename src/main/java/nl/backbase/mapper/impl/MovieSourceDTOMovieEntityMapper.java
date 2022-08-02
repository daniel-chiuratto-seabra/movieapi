package nl.backbase.mapper.impl;

import nl.backbase.dto.source.MovieSourceDTO;
import nl.backbase.dto.source.RatingSourceDTO;
import nl.backbase.mapper.Mapper;
import nl.backbase.model.MovieEntity;
import nl.backbase.model.RatingEntity;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;

@Component
public class MovieSourceDTOMovieEntityMapper implements Mapper<MovieSourceDTO, MovieEntity> {
    public static final Logger LOGGER = LoggerFactory.getLogger(MovieSourceDTOMovieEntityMapper.class);

    private static final String DOLLAR_SIGN = "$";
    private static final String COMMA_STRING = ",";

    @Override
    public MovieEntity map(final MovieSourceDTO movieSourceDTO) {
        final var movieEntity = new MovieEntity();
        movieEntity.setBoxOffice(getBigDecimalFromString(movieSourceDTO.getBoxOffice()));
        movieEntity.setTitle(movieSourceDTO.getTitle());

        final var ratingRequests = movieSourceDTO.getRatings();
        if (CollectionUtils.isNotEmpty(ratingRequests)) {
            final var ratingEntityCollection = new ArrayList<RatingEntity>();
            ratingRequests.forEach(ratingDTO -> ratingEntityCollection.add(parseRatingDTOToRatingEntity(ratingDTO)));
            movieEntity.setRatings(ratingEntityCollection);
        }

        return movieEntity;
    }

    private RatingEntity parseRatingDTOToRatingEntity(final RatingSourceDTO ratingDTO) {
        final var ratingEntity = new RatingEntity();
        ratingEntity.setSource(ratingDTO.getSource());
        ratingEntity.setValue(parseValueToDouble(ratingDTO.getValue()));
        return ratingEntity;
    }

    private Double parseValueToDouble(final String stringValue) {
        if (stringValue == null || stringValue.trim().isEmpty()) {
            return 0D;
        }
        try {
            if (stringValue.contains("%")) {
                final var valuePercent = Double.parseDouble(stringValue.replace("%", ""));
                return 10D * (valuePercent / 100D);
            } else if (stringValue.contains("/")) {
                final var valueArr = stringValue.split("/");
                final var num = Double.parseDouble(valueArr[0]);
                final var denum = Double.parseDouble(valueArr[1]);
                return (num / denum) * 10D;
            }
        } catch (final Exception e) {
            LOGGER.error(String.format("An error occurred while parsing the RatingSourceDTO value attribute: %s", stringValue), e);
        }
        return 0D;
    }

    private BigDecimal getBigDecimalFromString(final String boxOffice) {
        if (boxOffice == null || boxOffice.trim().isEmpty() || "N/A".equals(boxOffice)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(boxOffice.replace(DOLLAR_SIGN, EMPTY_STRING)
                    .replace(COMMA_STRING, EMPTY_STRING));
        } catch (final  NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
