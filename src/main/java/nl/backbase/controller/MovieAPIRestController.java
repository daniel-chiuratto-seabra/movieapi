package nl.backbase.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @GetMapping(value="/bestpicture", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Returns if the informed Movie Title won the Oscar for Best Picture. If not, it returns as Not Found Http Status")
    public ResponseEntity<MovieAPIDTO> bestpicture(@RequestParam("movieTitle") final String movieTitle) {
        return ResponseEntity.ok(this.movieService.getBestPictureMovieAPIDTO(movieTitle));
    }

    @ResponseBody
    @GetMapping(value = "/top10", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Get the Top 10 List of the most rated movies available in the database, ordered from the most voted " +
                         "to the less, and order by the Box Office, both descend")
    public ResponseEntity<Collection<MovieAPISummaryDTO>> top10() {
        return ResponseEntity.ok(this.movieService.getMovieAPISummaryDTOCollection());
    }
}
