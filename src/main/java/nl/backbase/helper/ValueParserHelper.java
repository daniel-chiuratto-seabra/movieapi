package nl.backbase.helper;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.MovieAPPIServiceFileLoadErrorException;
import nl.backbase.helper.csv.CSVData;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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

    public static Collection<CSVData> loadFileContent(final MultipartFile file) {
        try (final Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
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

    public static Double parseValueToDouble(final String stringValue) {
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
            log.error(String.format("An error occurred while parsing the RatingSourceDTO value attribute: %s", stringValue), e);
        }
        return 0D;
    }

    public static BigDecimal getBigDecimalFromString(final String boxOffice) {
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
