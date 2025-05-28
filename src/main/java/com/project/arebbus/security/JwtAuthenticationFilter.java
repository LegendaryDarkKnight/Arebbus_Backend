package com.project.arebbus.security;

import com.project.arebbus.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
            final String jwt;
            final String userEmail;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Cookie jwtCookie = Arrays.stream(cookies)
                        .filter(cookie -> "arebbus".equals(cookie.getName()))
                        .findFirst()
                        .orElse(null);

                if (jwtCookie != null) {
                    jwt = jwtCookie.getValue();
                    userEmail = jwtService.extractUsername(jwt);
                    if (userEmail == null) {
                        LOGGER.error("Id is null");
                    } else {
                        LOGGER.debug(userEmail);
                    }

                    if (userEmail != null && !userEmail.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                        LOGGER.debug("Authenticated: {}", userEmail);
                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            authToken.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );
                           SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                }
            }
            filterChain.doFilter(request, response);
    }
}
