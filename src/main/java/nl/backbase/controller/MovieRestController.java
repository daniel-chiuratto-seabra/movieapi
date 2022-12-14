package nl.backbase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * This is the controller related to all Movie related requests such as:
 * <ul>
 *     <li><b>/v1/movie/bestpicture (GET):</b> Returns if the informed Movie Title won a Best Picture Oscar, otherwise it returns a Not Found response</li>
 *     <li><b>/v1/movie/top10 (GET):</b> Returns a list of the 10 film titles that had the most ratings considering their average, sorted by box office in descending order</li>
 * </ul>
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Slf4j
@RestController
@RequestMapping("/v1/movie")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class MovieRestController {
    private final MovieService movieService;

    @ResponseBody
    @GetMapping(value="/bestpicture", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Returns if the informed Movie Title won the Oscar for Best Picture. If not, it returns a Not Found Http Status")
    public ResponseEntity<BestPictureMovieDTO> bestPicture(@RequestParam("movieTitle") final String movieTitle) {
        log.info("Requesting if the '{}' movie won the Oscar for Best Picture", movieTitle);
        return ResponseEntity.ok(this.movieService.getBestPictureMovieDTO(movieTitle));
    }

    @ResponseBody
    @GetMapping(value = "/top10", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Get the Top 10 List of the most rated movies available in the database, ordered from the most voted " +
                         "to the less, and order by the Box Office, both descend")
    public ResponseEntity<Collection<MovieTop10DTO>> top10() {
        log.info("Requesting Movie Top 10 list");
        return ResponseEntity.ok(this.movieService.getMovieTop10DTOCollection());
    }
}
