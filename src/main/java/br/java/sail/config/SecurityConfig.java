package br.java.sail.config;

import br.java.sail.security.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers( "/index.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/vault-users/generate", "/vault-users/auth").permitAll()
                        .requestMatchers("/ws/**", "/ws").permitAll()
                        .requestMatchers("/test", "/test/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
