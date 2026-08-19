package br.java.sail.security;

import br.java.sail.entities.VaultUser;
import br.java.sail.exceptions.NotFoundException;
import br.java.sail.repositories.VaultUserRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final VaultUserRepository vaultUserRepository;
    private final JwtCookieFactory jwtCookieFactory;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        return "OPTIONS".equalsIgnoreCase(method)
                || path.equals("/index.html")
                || path.equals("/ws")
                || path.startsWith("/ws/")
                || path.equals("/test")
                || path.startsWith("/test/")
                || ("POST".equals(method) && (path.equals("/vault-users/generate") || path.equals("/vault-users/auth")));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token == null) {
            unauthorized(response);
            return;
        }

        try {
            DecodedJWT decodedJWT = jwtTokenService.verify(token);
            String fingerprint = decodedJWT.getClaim("fingerprint").asString();

            VaultUser user = vaultUserRepository.findByVaultKeyFingerprint(fingerprint).orElseThrow();
            if (!fingerprint.equals(user.getVaultKeyFingerprint())) {
                throw new NotFoundException("Fingerprint não encontrada.");
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getUserName(),
                    token,
                    Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException | IllegalStateException | java.util.NoSuchElementException ex) {
            unauthorized(response);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (jwtCookieFactory.name().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"Unauthorized\",\"error\":true,\"data\":null}");
    }
}
