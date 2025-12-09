package com.pe.inventoryapp.backend.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.security.filter.JwtAuthenticationFilter;
import com.pe.inventoryapp.backend.security.filter.JwtValidationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

        @Autowired
        private AuthenticationConfiguration authenticationConfiguration;

        @Autowired
        private AuthService authService;

        @Bean
        AuthenticationManager authenticationManager() throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
                        throws Exception {

                // JwtAuthenticationFilter jwtAuthenticationFilter = new
                // JwtAuthenticationFilter(authenticationManager);
                // JwtValidationFilter jwtValidationFilter = new
                // JwtValidationFilter(authenticationManager);

                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ← FALTA ESTO

                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // AUTH
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // .requestMatchers("/api/auth/login").permitAll()
                                                // .requestMatchers("/api/auth/register").hasAuthority("ROLE_ADMIN")

                                                // USERS
                                                .requestMatchers(HttpMethod.POST, "/api/users/register")
                                                .hasAnyAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/users/profile")
                                                .hasAnyAuthority("ROLE_USER", "ROLE_OPERATOR", "ROLE_ADMIN")
                                                .requestMatchers("/api/users/listAll").hasAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/users/profile")
                                                .hasAnyAuthority("ROLE_USER", "ROLE_OPERATOR", "ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/users/update-password")
                                                .hasAnyAuthority("ROLE_USER", "ROLE_OPERATOR", "ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/users/**")
                                                .hasAuthority("ROLE_ADMIN")

                                                // PRODUCTS
                                                .anyRequest().permitAll())
                                // .anyRequest().authenticated())
                                .addFilter(new JwtAuthenticationFilter(
                                                authenticationManager(), authService))
                                .addFilter(new JwtValidationFilter(
                                                authenticationManager))
                                // .addFilter(jwtValidationFilter)
                                .build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

}
