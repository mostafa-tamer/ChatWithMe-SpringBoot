package com.mostafatamer.trysomethingcrazy.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;


    // Configuring security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Configuring Cross-Origin Resource Sharing (CORS)
        http.cors(Customizer.withDefaults())
                // Disabling Cross-Site Request Forgery (CSRF) protection
                .csrf(AbstractHttpConfigurer::disable)
                // Configuring authorization rules for HTTP requests

                .authorizeHttpRequests(
                        auth -> auth
                                // Permitting access without authentication for specific endpoints
                                .requestMatchers(
                                        "/auth/register",
                                        "/auth/authenticate",
                                        "/v3/api-docs",
                                        "/swagger-ui/index.html"
                                ).permitAll()
                                // Requiring authentication for any other request
                                .anyRequest()
                                .authenticated()
                )
                // Configuring session management to be stateless (using JWT)
                .sessionManagement(configure -> configure.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Setting custom authentication provider
                .authenticationProvider(authenticationProvider)
                // Adding JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // Building and returning the configured HTTP security object
        return http.build();
    }


}
