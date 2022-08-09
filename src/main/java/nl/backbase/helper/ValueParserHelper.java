package nl.backbase.helper;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.backbase.dto.source.MovieSourceDTO;
import nl.backbase.helper.csv.CSVData;
import nl.backbase.mapper.MovieMappers;

import java.io.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;

/**
 * This helper contains a couple of parsers used to parse values consumed by the Mappers.
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValueParserHelper {

    private static final String DOLLAR_SIGN = "$";
    private static final String COMMA_STRING = ",";
    private static final String BEST_PICTURE_OSCAR = "Best Picture";

    /**
     * This method receives a CSV File content as an {@link InputStream} and converts it into a {@link CSVData} {@link Collection},
     * where such conversion uses the {@code OpenCSV} dependency.
     *
     * @param inputStream {@link InputStream} containing the content to be converted by {@code OpenCSV}
     * @return {@link Collection<CSVData>} instance with the parsed values
     */
    public static Collection<CSVData> getCSVDataCollectionFromInputStream(final InputStream inputStream) {
        try (final Reader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            final var csvToBeanBuilder = new CsvToBeanBuilder<CSVData>(reader)
                    .withType(CSVData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBeanBuilder.parse().stream()
                                   .filter(csvData -> MovieMappers.BEST_PICTURE_OSCAR_WINNER_YES.equals(csvData.getWon()) &&
                                                      BEST_PICTURE_OSCAR.equals(csvData.getCategory()))
                                   .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method receives a {@link String} that represents the {@link MovieSourceDTO} Box Office, removing all the
     * characters that it may bring with it such as dollar sign ({@code $}) and commas ({@code ,}), to make the {@link String}
     * parsable into {@link BigDecimal}, but even so there are movies that it comes as {@code N/A}, for those cases the
     * {@link BigDecimal} parsing throws an {@link Exception}, where when this happens the {@link Exception} is catch,
     * and the value is parsed into {@link BigDecimal#ZERO}.
     *
     * @param boxOffice {@link String} instance containing the {@code Box Office} value that comes from the external Movie API Service
     * @return {@link BigDecimal} parsed value representing the {@code Box Office}
     */
    public static BigDecimal getBigDecimalFromString(final String boxOffice) {
        try {
            return new BigDecimal(boxOffice.replace(DOLLAR_SIGN, EMPTY_STRING)
                                           .replace(COMMA_STRING, EMPTY_STRING));
        } catch (final Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
