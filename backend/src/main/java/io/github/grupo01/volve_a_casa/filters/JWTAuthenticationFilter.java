package io.github.grupo01.volve_a_casa.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.security.UserAuthentication;
import io.github.grupo01.volve_a_casa.services.TokenService;
import io.github.grupo01.volve_a_casa.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] EXCLUDED_URLS = {
            "/api/auth",
            "/api/pets"
    };

    private final TokenService tokenService;
    private final UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }

        for (String excludedUrl : EXCLUDED_URLS) {
            if (path.startsWith(excludedUrl)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

       
        try {
            Long userId = tokenService.getUserIdFromToken(token);
            User user = userService.findById(userId);
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token inválido");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
