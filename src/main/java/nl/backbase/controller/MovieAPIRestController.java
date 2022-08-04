package nl.backbase.controller;

import lombok.RequiredArgsConstructor;
import nl.backbase.dto.MovieAPIDTO;
import nl.backbase.dto.MovieAPISummaryDTO;
import nl.backbase.service.MovieAPIService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/v1/movie")
@RequiredArgsConstructor
public class MovieAPIRestController {
    private final MovieAPIService movieService;

    @ResponseBody
    @GetMapping(value="bestpicture", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<MovieAPIDTO> bestPicute(@RequestParam("movieTitle") final String movieTitle) {
        return ResponseEntity.ok(this.movieService.getMovie(movieTitle));
    }

    @ResponseBody
    @GetMapping(value = "/top10", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Collection<MovieAPISummaryDTO>> getMovieTop10() {
        return ResponseEntity.ok(this.movieService.getMovieTop10());
    }
}
