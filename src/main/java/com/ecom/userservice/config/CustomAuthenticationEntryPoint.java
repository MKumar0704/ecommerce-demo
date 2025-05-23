package com.ecom.userservice.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements  AuthenticationEntryPoint{

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException
       {
           log.info("Custom Authentication Entry point handling the exception");

        Throwable cause = (authException.getCause()==null) ? (Throwable) request.getAttribute("JWT_EXCEPTION")
                                                    : authException.getCause();
           String message = "Unauthorized";

           if (cause instanceof SignatureException) {
               message = "INVALID_SIGNATURE";
           } else if (cause instanceof ExpiredJwtException) {
               message = "JWT_EXPIRED";
           }else if(cause instanceof JwtException){
               message = "INVALID_TOKEN";
           }
           else if (cause instanceof BadCredentialsException) {
               message = "Bad credentials";
           }else if (cause instanceof MethodArgumentNotValidException){
               message = "Method argument not valid";
           }else if(cause instanceof HttpRequestMethodNotSupportedException){
               response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
               response.setContentType("application/json");
               response.getWriter().write("{\"error\": \"Request Method is not Matching\"}");
               return;
           }

           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           response.setContentType("application/json");
           response.getWriter().write("{\"error\": \"" + message + "\"}");
   }

}
