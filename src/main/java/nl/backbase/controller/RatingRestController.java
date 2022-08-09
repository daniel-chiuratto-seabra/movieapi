package nl.backbase.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.model.RatingEntity;
import nl.backbase.service.MovieService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * This is the controller related to the {@link RatingEntity} creations, where the user can rate a given Movie Title:
 * <ul>
 *     <li><b>/v1/rating (POST):</b> Saves a rating between 0 and 10 for the specified Movie Title, with the username as a rating source</li>
 * </ul>
 *
 * @author Daniel Chiuratto Seabra
 * @since 04/08/2022
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rating")
@SecurityRequirement(name = "Bearer Authentication")
public class RatingRestController {
    private final MovieService movieService;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "This endpoint is related in saving a rate to the informed Movie Title, the rate should be " +
                         "a number in between 0 and 10 (including both 0 and 10)")
    public ResponseEntity<?> saveRatingDTO(@RequestBody @Valid final RatingRequestDTO ratingRequestDTO) {
        return ResponseEntity.ok().body(this.movieService.saveRatingRequestDTO(ratingRequestDTO));
    }

}
