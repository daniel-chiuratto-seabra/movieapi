package nl.backbase.helper.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

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
