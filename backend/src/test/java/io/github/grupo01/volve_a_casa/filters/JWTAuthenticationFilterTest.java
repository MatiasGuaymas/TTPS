package io.github.grupo01.volve_a_casa.filters;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.security.UserAuthentication;
import io.github.grupo01.volve_a_casa.services.TokenService;
import io.github.grupo01.volve_a_casa.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTAuthenticationFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_whenPathIsAuthEndpoint() throws ServletException {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getMethod()).thenReturn("POST");

        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        assertTrue(result);
    }

    @Test
    void shouldNotFilter_whenMethodIsOptions() throws ServletException {
        when(request.getRequestURI()).thenReturn("/api/pets");
        when(request.getMethod()).thenReturn("OPTIONS");

        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        assertTrue(result);
    }

    @Test
    void shouldFilter_whenPathIsProtectedEndpoint() throws ServletException {
        when(request.getRequestURI()).thenReturn("/api/pets");
        when(request.getMethod()).thenReturn("GET");

        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        assertFalse(result);
    }

    @Test
    void doFilterInternal_success_whenTokenIsValid() throws Exception {
        // Arrange
        String token = "Bearer valid.jwt.token";
        Long userId = 1L;
        User user = mock(User.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(tokenService.getUserIdFromToken(token)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(user);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenService).getUserIdFromToken(token);
        verify(userService).findById(userId);
        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UserAuthentication);
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_returnsUnauthorized_whenTokenIsExpired() throws Exception {
        // Arrange
        String token = "Bearer expired.jwt.token";
        ExpiredJwtException expiredException = mock(ExpiredJwtException.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(tokenService.getUserIdFromToken(token)).thenThrow(expiredException);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenService).getUserIdFromToken(token);
        verify(userService, never()).findById(anyLong());
        verify(filterChain, never()).doFilter(request, response);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_returnsForbidden_whenTokenIsInvalid() throws Exception {
        // Arrange
        String token = "Bearer invalid.jwt.token";
        JwtException jwtException = new JwtException("Invalid token");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(tokenService.getUserIdFromToken(token)).thenThrow(jwtException);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenService).getUserIdFromToken(token);
        verify(userService, never()).findById(anyLong());
        verify(filterChain, never()).doFilter(request, response);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Token invalido");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_returnsForbidden_whenUserNotFound() throws Exception {
        // Arrange
        String token = "Bearer valid.jwt.token";
        Long userId = 999L;
        RuntimeException userNotFoundException = new RuntimeException("User not found");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(tokenService.getUserIdFromToken(token)).thenReturn(userId);
        when(userService.findById(userId)).thenThrow(userNotFoundException);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenService).getUserIdFromToken(token);
        verify(userService).findById(userId);
        verify(filterChain, never()).doFilter(request, response);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Token invalido");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_returnsForbidden_whenGenericExceptionOccurs() throws Exception {
        // Arrange
        String token = "Bearer invalid.jwt.token";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(tokenService.getUserIdFromToken(token)).thenThrow(new IllegalArgumentException());

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenService).getUserIdFromToken(token);
        verify(userService, never()).findById(anyLong());
        verify(filterChain, never()).doFilter(request, response);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Token invalido");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_success_withValidTokenWithoutBearerPrefix() throws Exception {
        // Arrange
        String token = "valid.jwt.token.without.bearer";
        Long userId = 1L;
        User user = mock(User.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(userService, never()).findById(userId);
        verify(tokenService, never()).getUserIdFromToken(token);
        verify(response, never()).sendError(anyInt(), anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_returnsForbidden_whenTokenIsNull() throws Exception {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(tokenService, never()).getUserIdFromToken(null);
        verify(userService, never()).findById(anyLong());
        verify(response, never()).sendError(anyInt(), anyString());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

