package nl.backbase.csv;

import com.opencsv.bean.CsvToBeanBuilder;
import nl.backbase.controller.exception.MovieAPPIServiceFileLoadErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.stream.Collectors;

public interface CSVFileLoaderHelper {

    String OSCAR_WINNER_YES = "YES";
    String BEST_PICTURE_OSCAR = "Best Picture";

    static Collection<CSVData> loadFileContent(final MultipartFile file) {
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
}
