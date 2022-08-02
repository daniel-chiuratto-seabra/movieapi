package nl.backbase.controller;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MovieRestController {
    private final MovieService movieService;

    @ResponseBody
    @GetMapping(value = "/movies", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<MovieDTO> getMovie(@RequestParam("apiKey") final String apiKey, @RequestParam("movieTitle") final String movieTitle) {
        return ResponseEntity.ok(this.movieService.getMovie(apiKey, movieTitle));
    }

    @ResponseBody
    @GetMapping(value = "/movies/top10", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Collection<MovieTop10DTO>> getMovieTop10() {
        return ResponseEntity.ok(this.movieService.getMovieTop10());
    }

    @PostMapping(value = "/movies/ratings", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> postRating(@RequestBody final RatingRequestDTO ratingDTO) {
        this.movieService.postRating(ratingDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/movies", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Collection<CSVData>> postCSVFile(@RequestParam final String apiKey, @RequestParam("file") final MultipartFile multipartFile) {
        return ResponseEntity.ok(this.movieService.saveCSVFile(apiKey, multipartFile));
    }

    // TODO REMOVE AFTER IMPLEMENTATION
    @ResponseBody
    @GetMapping(value = "/movies/all", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Collection<MovieEntity>> getAllMovies() {
        return ResponseEntity.ok(this.movieService.getAllMovies());
    }
}
