package nl.backbase.controller;

import lombok.RequiredArgsConstructor;
import nl.backbase.csv.CSVData;
import nl.backbase.dto.MovieAPIDTO;
import nl.backbase.dto.MovieAPISummaryDTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.service.MovieAPIService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MovieAPIRestController {
    private final MovieAPIService movieService;

    @ResponseBody
    @GetMapping(value = "/movies", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<MovieAPIDTO> getMovie(@RequestParam("apiKey") final String apiKey, @RequestParam("movieTitle") final String movieTitle) {
        return ResponseEntity.ok(this.movieService.getMovie(apiKey, movieTitle));
    }

    @ResponseBody
    @GetMapping(value = "/movies/top10", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Collection<MovieAPISummaryDTO>> getMovieTop10() {
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
}
