package nl.backbase.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.*;

class ValueParserHelperTest {

    private static final String CSV_DATA_HEADERS = "Year,Category,Nominee,Additional Info,Won?,,,,,,";
    private static final String CSV_DATA_TEMPLATE = "%s %d,%s,%s %d,%s %d,%s,,,,,,";

    @Test
    @DisplayName("GIVEN an empty CSV content " +
                  "WHEN the content is parsed " +
                  "THEN the parser should return an empty collection")
    public void givenAnEmptyCSVContentWhenTheContentIsParsedThenTheParserShouldReturnAnEmptyCollection() {
        final var csvDataCollection = ValueParserHelper.getCSVDataCollectionFromInputStream(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
        assertTrue(csvDataCollection.isEmpty());
    }

    @Test
    @DisplayName("GIVEN a CSV content with 6 registers and a header " +
                  "WHEN the content contains 2 registers as Best Picture that won the Oscar " +
                  "THEN the parser should return only these 2 parsed values")
    public void givenCSVContentWith6RegistersWhenContentContains2RegistersBestPictureWonOscarThenReturnThese2() {
        final var csvDataCollection = ValueParserHelper.getCSVDataCollectionFromInputStream(getListInputStream(
                CSV_DATA_HEADERS,
                getData(1, "Best Actor", false),
                getData(2, "Best Picture", true),
                getData(3, "Best Picture", false),
                getData(4, "Best Actress", true),
                getData(5, "Best Picture", false),
                getData(6, "Best Picture", true)));

        assertEquals(2, csvDataCollection.size());

        final var csvDataList = new ArrayList<>(csvDataCollection);
        assertEquals("Fake Year 2", csvDataList.get(0).getYear());
        assertEquals("Best Picture", csvDataList.get(0).getCategory());
        assertEquals("Nominee 2", csvDataList.get(0).getNominee());
        assertEquals("Additional Info 2", csvDataList.get(0).getAdditionalInfo());
        assertEquals("YES", csvDataList.get(0).getWon());

        assertEquals("Fake Year 6", csvDataList.get(1).getYear());
        assertEquals("Best Picture", csvDataList.get(1).getCategory());
        assertEquals("Nominee 6", csvDataList.get(1).getNominee());
        assertEquals("Additional Info 6", csvDataList.get(1).getAdditionalInfo());
        assertEquals("YES", csvDataList.get(1).getWon());
    }

    @Test
    @DisplayName("GIVEN a ValueParserHelper " +
                  "WHEN getBigDecimalFromString is called with a null value set " +
                  "THEN the parser should return a BigDecimal ZERO")
    public void givenAValueParserHelperWhenGetBigDecimalFromStringCalledWithNullThenShouldReturnBigDecimalZERO() {
        final var bigDecimalResponse = ValueParserHelper.getBigDecimalFromString(null);
        assertEquals(BigDecimal.ZERO, bigDecimalResponse);
    }

    @Test
    @DisplayName("GIVEN a ValueParserHelper " +
                  "WHEN getBigDecimalFromString is called with an empty String value set " +
                  "THEN the parser should return a BigDecimal ZERO")
    public void givenAValueParserHelperWhenGetBigDecimalFromStringCalledWithEmptyStringThenShouldReturnBigDecimalZERO() {
        final var bigDecimalResponse = ValueParserHelper.getBigDecimalFromString("");
        assertEquals(BigDecimal.ZERO, bigDecimalResponse);
    }

    @Test
    @DisplayName("GIVEN a ValueParserHelper " +
                  "WHEN getBigDecimalFromString is called with a string with spaces values set " +
                  "THEN the parser should return a BigDecimal ZERO")
    public void givenAValueParserHelperWhenGetBigDecimalFromStringCalledWithSpacedStringThenShouldReturnBigDecimalZERO() {
        final var bigDecimalResponse = ValueParserHelper.getBigDecimalFromString("    ");
        assertEquals(BigDecimal.ZERO, bigDecimalResponse);
    }

    @Test
    @DisplayName("GIVEN a ValueParserHelper " +
                  "WHEN getBigDecimalFromString is called with a N/A value set " +
                  "THEN the parser should return a BigDecimal ZERO")
    public void givenAValueParserHelperWhenGetBigDecimalFromStringCalledWithNAThenShouldReturnBigDecimalZERO() {
        final var bigDecimalResponse = ValueParserHelper.getBigDecimalFromString("N/A");
        assertEquals(BigDecimal.ZERO, bigDecimalResponse);
    }

    @Test
    @DisplayName("GIVEN a ValueParserHelper " +
                  "WHEN getBigDecimalFromString is called with ABCDEFG value set " +
                  "THEN the parser should return a BigDecimal ZERO")
    public void givenAValueParserHelperWhenGetBigDecimalFromStringCalledWithABCDEFGThenShouldReturnBigDecimalZERO() {
        final var bigDecimalResponse = ValueParserHelper.getBigDecimalFromString("ABCDEFG");
        assertEquals(BigDecimal.ZERO, bigDecimalResponse);
    }

    @Test
    @DisplayName("GIVEN a ValueParserHelper " +
                  "WHEN getBigDecimalFromString is called with 12345 value set " +
                  "THEN the parser should return a BigDecimal of value 12345")
    public void givenValueParserHelperWhenGetBigDecimalFromStringCalledWith12345ValueSetThenParserShouldReturnBigDecimal12345() {
        final var bigDecimalResponse = ValueParserHelper.getBigDecimalFromString("12345");
        assertEquals(new BigDecimal("12345"), bigDecimalResponse);
    }

    @Test
    @DisplayName("GIVEN a ValueParserHelper " +
                  "WHEN getBigDecimalFromString is called with $123,456,789 value set " +
                  "THEN the parser should return a BigDecimal of value 123456789")
    public void givenValueParserHelperWhenGetBigDecimalFromStringCalledWith123456789ValueSetThenParserShouldReturnBigDecimal123456789() {
        final var bigDecimalResponse = ValueParserHelper.getBigDecimalFromString("$123,456,789");
        assertEquals(new BigDecimal("123456789"), bigDecimalResponse);
    }

    @Test
    @DisplayName("GIVEN a ValueParserHelper " +
                  "WHEN getBigDecimalFromString is called with $123,456.89 value set " +
                  "THEN the parser should return a BigDecimal of value 123456.89")
    public void givenValueParserHelperWhenGetBigDecimalFromStringCalledWith12345689ValueSetThenParserShouldReturnBigDecimal12345689() {
        final var bigDecimalResponse = ValueParserHelper.getBigDecimalFromString("$123,456.89");
        assertEquals(new BigDecimal("123456.89"), bigDecimalResponse);
    }

    private InputStream getListInputStream(final String... lines) {
        return new ByteArrayInputStream(Stream.of(lines).collect(joining(lineSeparator())).getBytes(StandardCharsets.UTF_8));
    }

    private String getData(final int index, final String category, final boolean isBestPictureOscarWinner) {
        return String.format(CSV_DATA_TEMPLATE, "Fake Year", index, category, "Nominee", index, "Additional Info", index, isBestPictureOscarWinner ? "YES" : "NO");
    }
}
