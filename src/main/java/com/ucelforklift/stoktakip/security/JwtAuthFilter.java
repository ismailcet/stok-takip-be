package com.ucelforklift.stoktakip.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthUserService authUserService;

    public JwtAuthFilter(JwtService jwtService, AuthUserService authUserService) {
        this.jwtService = jwtService;
        this.authUserService = authUserService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parseClaims(header.substring(7));
                Long userId = Long.valueOf(claims.getSubject());

                // Silinmis (pasif) kullanicinin token'i gecerli olsa bile kimlik kurulmaz.
                // Rol veritabanindan okunur (onbellekli, degisiklikte temizlenir).
                var authUser = authUserService.findActive(userId);
                if (authUser != null) {
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + authUser.role().name()));
                    var authentication = new UsernamePasswordAuthenticationToken(authUser.id(), null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                // gecersiz/suresi gecmis token -> istek kimliksiz devam eder, asagida engellenir
            }
        }

        filterChain.doFilter(request, response);
    }
}