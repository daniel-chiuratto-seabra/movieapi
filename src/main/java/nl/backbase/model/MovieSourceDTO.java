package nl.backbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Data
@NoArgsConstructor
public class MovieSourceDTO implements Serializable {
    @JsonProperty(value = "Title")
    String title;
    @JsonProperty(value = "Year")
    Integer year;
    @JsonProperty(value = "Rated")
    String rated;
    @JsonProperty(value = "Released")
    String released;
    @JsonProperty(value = "Runtime")
    String runtime;
    @JsonProperty(value = "Genre")
    String genre;
    @JsonProperty(value = "Director")
    String director;
    @JsonProperty(value = "Writer")
    String writer;
    @JsonProperty(value = "Actors")
    String actors;
    @JsonProperty(value = "Plot")
    String plot;
    @JsonProperty(value = "Language")
    String language;
    @JsonProperty(value = "Country")
    String country;
    @JsonProperty(value = "Awards")
    String awards;
    @JsonProperty(value = "Poster")
    String poster;
    @JsonProperty(value = "Ratings")
    Collection<Map.Entry<String, String>> ratings;
    @JsonProperty(value = "Metascore")
    Integer metascore;
    @JsonProperty(value = "imdbRating")
    Double imdbRating;
    @JsonProperty(value = "imdbRating")
    BigDecimal imdbVotes;
    @JsonProperty(value = "imdbID")
    String imdbID;
    @JsonProperty(value = "Type")
    String type;
    @JsonProperty(value = "DVD")
    LocalDate dvd;
    @JsonProperty(value = "BoxOffice")
    BigDecimal boxOffice;
    @JsonProperty(value = "Production")
    String production;
    @JsonProperty(value = "Website")
    String website;
    @JsonProperty(value = "Response")
    Boolean response;
}
