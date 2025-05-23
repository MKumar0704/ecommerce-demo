package com.ecom.userservice.config;

import com.ecom.userservice.service.JwtService;
import com.ecom.userservice.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {



    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.info("Processing the Internal Filter");

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        logger.info(request.getRequestURI());
        if(request.getRequestURI().equals("/extract-user") || request.getRequestURI().equals("/validate")){
            filterChain.doFilter(request,response);
            return;
        }
try {

    jwt= authHeader.substring(7);
    username = jwtService.extractUsername(jwt);

    if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
        UserDetails userDetails =  userDetailsService.loadUserByUsername(username);
        System.out.println(userDetails);
        if(jwtService.isTokenValid(jwt)){
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    filterChain.doFilter(request, response);
    }
    catch (SignatureException ex){
        logger.error("Signature is invalid",ex);
        request.setAttribute("JWT_EXCEPTION",ex);
        throw new InsufficientAuthenticationException("",ex.getCause());
    }
    catch (ExpiredJwtException ex){
        logger.error("Token is expired",ex);
        request.setAttribute("JWT_EXCEPTION",ex);
        throw new InsufficientAuthenticationException("Token Expired",ex);
    }
    catch (JwtException ex){
        logger.error("Issue related to the JWT token");
        request.setAttribute("JWT_EXCEPTION",ex);
        throw new InsufficientAuthenticationException("Issue with Token",ex.getCause());
    }
    }
}