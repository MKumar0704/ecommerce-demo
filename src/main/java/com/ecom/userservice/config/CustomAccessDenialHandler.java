package com.ecom.userservice.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;

@Component
public class CustomAccessDenialHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 403
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Not Found\"\n");
        response.getWriter().write("\t\"path\":"+ request.getRequestURI()+"}");
    }
}
