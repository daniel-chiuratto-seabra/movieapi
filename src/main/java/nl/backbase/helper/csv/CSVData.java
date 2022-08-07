package nl.backbase.helper.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.backbase.repository.MovieAPIRepository;

/**
 * This class is used to carry the deserialized data from the CSV file, and allow to fill the {@link MovieAPIRepository}
 * with the movie data available in it
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Data
@NoArgsConstructor
public class CSVData {
    @CsvBindByName(column = "Year")
    String year;
    @CsvBindByName(column = "Category")
    String category;
    @CsvBindByName(column = "Nominee")
    String nominee;
    @CsvBindByName(column = "Additional Info")
    String additionalInfo;
    @CsvBindByName(column = "Won?")
    String won;
}
