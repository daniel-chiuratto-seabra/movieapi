package nl.backbase.controller;

import lombok.RequiredArgsConstructor;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.service.MovieAPIService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rating")
public class RatingAPIRestController {
    private final MovieAPIService movieAPIService;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> saveRatingDTO(@RequestBody final RatingRequestDTO ratingRequestDTO) {
        return ResponseEntity.ok().body(this.movieAPIService.saveRatingDTO(ratingRequestDTO));
    }

}
