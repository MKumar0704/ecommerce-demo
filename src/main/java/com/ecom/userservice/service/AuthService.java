package com.ecom.userservice.service;

import com.ecom.userservice.cart.CartServiceClient;
import com.ecom.userservice.dto.LoginRequestDto;
import com.ecom.userservice.dto.UserRegistrationDto;
import com.ecom.userservice.exception.EmailAlreadyExistsException;
import com.ecom.userservice.exception.UsernameAlreadyExists;
import com.ecom.userservice.model.Users;
import com.ecom.userservice.repository.AuthRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {


    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;
    private final CartServiceClient cartService;

    public AuthService(BCryptPasswordEncoder passwordEncoder, AuthRepository authRepository, JwtService jwtService, AuthenticationManager authManager, HttpServletRequest servletRequest, HttpServletResponse servletResponse, CartServiceClient cartService) {
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        this.cartService = cartService;
    }

    public void register(UserRegistrationDto userRegistrationDto) {

        if(authRepository.findByEmail(userRegistrationDto.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email Already exists");
        }

        if(authRepository.findByUsername(userRegistrationDto.getUsername()).isPresent()){
            throw new UsernameAlreadyExists("Username Already exists");
        }

        Users createUser = Users.builder()
                .username(userRegistrationDto.getUsername())
                .email(userRegistrationDto.getEmail())
                .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
                .build();

        authRepository.save(createUser);

    }

    public String validateUser(LoginRequestDto loginRequestDto) {

        Authentication authentication;

        try {
            authentication=authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );
        }catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authentication failed", e);
        }


        Users user= authRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("user not found!!"));

        if(authentication.isAuthenticated()){
            String token= jwtService.generateToken(user.getUsername(),user.getId());
            log.info("----------------------------------Cart MERGING IS GOING TO INITIATE----------------");
            String guestId=mergeCart(user.getId(),servletRequest);
            if (guestId!=null){
                Cookie cookie=new Cookie("X-Guest-Id",guestId);
                cookie.setHttpOnly(true);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                servletResponse.addCookie(cookie);
            }

            log.info("=======================CART MERGING IS COMPLETED==================");
            return token;
        }else {
            return "fail";
        }

    }

    private String mergeCart(UUID userId, HttpServletRequest request){
        log.info("________________________CART MERGING INITIATED FOR THE USER ID : {}",userId);
        Optional<String> guestId;
        guestId=Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "X-Guest-Id".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
        if(guestId.isPresent()){
            log.info("----------------GUEST ID is present routing to the cart service-------{}",guestId);
            cartService.mergeCart(userId, UUID.fromString(guestId.get()));
            return guestId.get();
        }
        return null;
    }


    public boolean validateToken(String token) {
        return jwtService.isTokenValid(token);
    }

    public String extractUserId(String token){
        return jwtService.extractUserId(token);
    }
}
