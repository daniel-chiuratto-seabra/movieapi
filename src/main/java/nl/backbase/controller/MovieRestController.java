package nl.backbase.controller;

import nl.backbase.csv.CSVData;
import nl.backbase.dto.MovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.model.MovieEntity;
import nl.backbase.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
@RequestMapping("/movies")
public class MovieRestController {
    private final MovieService movieService;

    public MovieRestController(final MovieService movieService) {
        this.movieService = movieService;
    }

    @ResponseBody
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<MovieDTO> getMovie(@RequestParam("apiKey") final String apiKey, @RequestParam("movieTitle") final String movieTitle) {
        return ResponseEntity.ok(this.movieService.getMovie(apiKey, movieTitle));
    }

    @ResponseBody
    @GetMapping(value = "/top10", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Collection<MovieTop10DTO>> getMovieTop10() {
        return ResponseEntity.ok(this.movieService.getMovieTop10());
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> postRating(@RequestBody final RatingRequestDTO ratingDTO) {
        this.movieService.postRating(ratingDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Collection<CSVData>> postCSVFile(@RequestParam final String apiKey, @RequestParam("file") final MultipartFile multipartFile) {
        return ResponseEntity.ok(this.movieService.saveCSVFile(apiKey, multipartFile));
    }

    // TODO REMOVE AFTER IMPLEMENTATION
    @ResponseBody
    @GetMapping(value = "/all", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Collection<MovieEntity>> getAllMovies() {
        return ResponseEntity.ok(this.movieService.getAllMovies());
    }
}
