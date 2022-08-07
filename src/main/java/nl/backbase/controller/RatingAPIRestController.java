package nl.backbase.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.service.MovieAPIService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rating")
public class RatingAPIRestController {
    private final MovieAPIService movieAPIService;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "This endpoint is related in saving a rate to the informed Movie Title, the rate should be " +
                         "a number in between 0 and 10 (including both 0 and 10)")
    public ResponseEntity<?> saveRatingDTO(@RequestBody @Valid final RatingRequestDTO ratingRequestDTO) {
        return ResponseEntity.ok().body(this.movieAPIService.saveRatingDTO(ratingRequestDTO));
    }

}
