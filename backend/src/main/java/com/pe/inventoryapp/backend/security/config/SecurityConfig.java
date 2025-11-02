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
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login").permitAll()
            .requestMatchers("/api/users").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/api/users/profile").hasAuthority("ROLE_USER")
            .requestMatchers("/api/users/update-password").hasAnyAuthority("ROLE_USER",
                "ROLE_OPERATOR", "ROLE_ADMIN")
            .requestMatchers(HttpMethod.DELETE,
                "/api/users/**")
            .hasAuthority("ROLE_ADMIN")
            .anyRequest().permitAll())
        // .anyRequest().authenticated())
        .addFilter(new JwtAuthenticationFilter(
            authenticationManager(), authService))
        .addFilter(new JwtValidationFilter(
            authenticationManager))
        // .addFilter(jwtValidationFilter)
        .build();
  }
}
