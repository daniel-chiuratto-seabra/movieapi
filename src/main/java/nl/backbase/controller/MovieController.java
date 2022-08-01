package nl.backbase.controller;

import nl.backbase.model.MovieSourceDTO;
import nl.backbase.service.MovieSourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@RestController
public class MovieController {
    private final MovieSourceService movieService;

    public MovieController(final MovieSourceService movieService)
    {
        this.movieService = movieService;
    }
    @GetMapping(name="movie", params = { "apiKey", "movieTitle" })
    public ResponseEntity<MovieSourceDTO> getMovieSourceDTO(final @PathParam("apiKey") String apiKey, final @PathParam("movieTitle") String movieTitle)
    {
        return ResponseEntity.ok(this.movieService.getMovieSourceDTO(apiKey, movieTitle));
    }
}
