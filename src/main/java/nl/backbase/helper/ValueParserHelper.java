package nl.backbase.helper;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.MovieAPPIServiceFileLoadErrorException;
import nl.backbase.helper.csv.CSVData;

import java.io.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValueParserHelper {

    private static final String DOLLAR_SIGN = "$";
    private static final String COMMA_STRING = ",";
    private static final String OSCAR_WINNER_YES = "YES";
    private static final String BEST_PICTURE_OSCAR = "Best Picture";

    public static Collection<CSVData> loadFileContent(final InputStream inputStream) {
        try (final Reader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            final var csvToBeanBuilder = new CsvToBeanBuilder<CSVData>(reader)
                    .withType(CSVData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBeanBuilder.parse().stream()
                                   .filter(csvData -> OSCAR_WINNER_YES.equals(csvData.getWon()) &&
                                                      BEST_PICTURE_OSCAR.equals(csvData.getCategory()))
                                   .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new MovieAPPIServiceFileLoadErrorException(e);
        }
    }

    public static BigDecimal getBigDecimalFromString(final String boxOffice) {
        try {
            return new BigDecimal(boxOffice.replace(DOLLAR_SIGN, EMPTY_STRING)
                                           .replace(COMMA_STRING, EMPTY_STRING));
        } catch (final Exception e) {
            log.error("An error occurred while parsing the String boxOffice into BigDecimal: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
