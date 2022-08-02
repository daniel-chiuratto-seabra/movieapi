package nl.backbase.dto.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSourceDTO implements Serializable {
    @JsonProperty(value = "Title")
    String title;
    @JsonProperty(value = "Year")
    String year;
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
    Collection<RatingSourceDTO> ratings;
    @JsonProperty(value = "Metascore")
    String metascore;
    @JsonProperty(value = "imdbRating")
    String imdbRating;
    @JsonProperty(value = "imdbVotes")
    String imdbVotes;
    @JsonProperty(value = "imdbID")
    String imdbID;
    @JsonProperty(value = "Type")
    String type;
    @JsonProperty(value = "DVD")
    String dvd;
    @JsonProperty(value = "BoxOffice")
    String boxOffice;
    @JsonProperty(value = "Production")
    String production;
    @JsonProperty(value = "Website")
    String website;
    @JsonProperty(value = "Response")
    String response;
}
