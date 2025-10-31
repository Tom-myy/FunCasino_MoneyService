package com.evofun.money.shared.security.handler;

import com.evofun.money.shared.exception.code.ErrorCode;
import com.evofun.money.shared.exception.model.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Unauthorized request to {}: {}", request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ErrorDto error = new ErrorDto(
                ErrorCode.INVALID_TOKEN,
                null,
                "Invalid or missing access token.",
                null
        );

        String json = new ObjectMapper().writeValueAsString(error);
        response.getWriter().write(json);
    }
}