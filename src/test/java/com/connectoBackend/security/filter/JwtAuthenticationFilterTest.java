package com.connectoBackend.security.filter;

import com.connectoBackend.security.enums.TokenType;
import com.connectoBackend.security.service.CustomUserDetailsService;
import com.connectoBackend.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = Mockito.mock(JwtService.class);
    private final CustomUserDetailsService userDetailsService = Mockito.mock(CustomUserDetailsService.class);
    private final ExposedFilter filter = new ExposedFilter(jwtService, userDetailsService);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void refreshTokenIsNotAcceptedAsApiAuthentication() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer refresh-token");
        when(jwtService.extractTokenType("refresh-token")).thenReturn(TokenType.REFRESH);

        filter.apply(request, response, chain);

        verify(chain).doFilter(request, response);
        org.junit.jupiter.api.Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private static final class ExposedFilter extends JwtAuthenticationFilter {
        private ExposedFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
            super(jwtService, userDetailsService);
        }

        private void apply(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws Exception {
            doFilterInternal(request, response, chain);
        }
    }
}
