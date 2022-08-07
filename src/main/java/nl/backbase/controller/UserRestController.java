package nl.backbase.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nl.backbase.dto.UserDTO;
import nl.backbase.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PostMapping(value = "/signup", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "This endpoint is related in creating an User account so it is possible to SignIn with it")
    public ResponseEntity<?> signup(@RequestBody @Valid final UserDTO userDTO) {
        this.userService.saveUserDTO(userDTO);
        return ResponseEntity.ok().build();
    }

    /*
     * This method is only to trigger OpenAPI to render the Spring Security Sign In endpoint
     */
    @PostMapping(value = "/signin", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "This endpoint is related in signing in the User and acquiring the JWT Token for authentication")
    public ResponseEntity<?> signIn(@RequestBody @Valid final UserDTO userDTO) {
        throw new IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.");
    }
}
