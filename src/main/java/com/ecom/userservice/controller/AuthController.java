package com.ecom.userservice.controller;

import com.ecom.userservice.cart.CartServiceClient;
import com.ecom.userservice.dto.LoginRequestDto;
import com.ecom.userservice.dto.LoginResponse;
import com.ecom.userservice.dto.UserRegistrationDto;
import com.ecom.userservice.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestController
public class AuthController {

    private final AuthService authService;
    private final CartServiceClient cartServiceClient;


    public AuthController(AuthService authService, CartServiceClient cartServiceClient) {
        this.authService = authService;
        this.cartServiceClient = cartServiceClient;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
        authService.register(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Account Created Successfully!! Login to Proceed");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto){
        String token=authService.validateUser(loginRequestDto);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/protected")
    public ResponseEntity<String> verifier(){
        return ResponseEntity.ok("Hey!! You are logged in and you can purchase any products");
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validateToken(@RequestHeader("Authorization") String authHeader) {
        return handleToken(authHeader, token -> {
            if (authService.validateToken(token)) {
                return ResponseEntity.ok().build();
            }

            Map<String, String> error = new HashMap<>();
            error.put("error", "TOKEN_INVALID");
            error.put("message", "The token is invalid.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        });
    }


    @GetMapping("/extract-user")
    public ResponseEntity<Map<String, String>> getUser(@RequestHeader("Authorization") String authHeader) {
        return handleToken(authHeader, token -> {
            String userId = authService.extractUserId(token);

            if (userId != null) {
                Map<String, String> response = new HashMap<>();
                response.put("userId", userId);
                return ResponseEntity.ok(response);
            }

            Map<String, String> error = new HashMap<>();
            error.put("error", "TOKEN_INVALID");
            error.put("message", "The token is invalid.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        });
    }



    private ResponseEntity<Map<String, String>> handleToken(String authHeader, Function<String, ResponseEntity<Map<String, String>>> handler) {
        Map<String, String> response = new HashMap<>();
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("error", "MISSING_TOKEN");
                response.put("message", "Authorization token is missing or malformed.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7); // Strip "Bearer "

            return handler.apply(token); // Call the specific logic (validate or extract)

        } catch (SignatureException ex) {
           response.put("error", "INVALID_SIGNATURE");
            response.put("message", "The token signature is not valid.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (ExpiredJwtException ex) {
            response.put("error", "TOKEN_EXPIRED");
            response.put("message", "Your session has expired. Please login again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }



}
