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
                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // AUTH
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // USERS
                                                .requestMatchers(HttpMethod.POST, "/api/user/register")
                                                .hasAnyAuthority("ROLE_ADMIN")

                                                .requestMatchers(HttpMethod.GET, "/api/user")
                                                .hasAnyAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/user/profile").authenticated()

                                                .requestMatchers(HttpMethod.PUT, "/api/user/profile").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/user/roles")
                                                .hasAnyAuthority("ROLE_ADMIN")

                                                .requestMatchers(HttpMethod.DELETE, "/api/user/*")
                                                .hasAnyAuthority("ROLE_ADMIN")

                                                // CATEGORY
                                                .requestMatchers(HttpMethod.POST, "/api/category")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/category")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/category/active").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/category/*").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/category/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.PATCH, "/api/category/status/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

                                                // PRODUCT
                                                .requestMatchers(HttpMethod.POST, "/api/product")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/product")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/product/active").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/product/category/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/product/active/category/*")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/product/*").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/product/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.PATCH, "/api/product/status/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

                                                // COMPANY
                                                .requestMatchers(HttpMethod.POST, "/api/company")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/company").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/company/*").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/company/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

                                                // REGION
                                                .requestMatchers(HttpMethod.POST, "/api/company")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/region").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/region/*").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/region/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

                                                // LOCATION
                                                .requestMatchers(HttpMethod.POST, "/api/location")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/location")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/location/active").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/location/region/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/location/active/region/*")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/location/*").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/location/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.PATCH, "/api/location/status/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

                                                // .requestMatchers("/api/category").permitAll()
                                                .anyRequest().permitAll())
                                // .anyRequest().authenticated())
                                .addFilter(new JwtAuthenticationFilter(
                                                authenticationManager(), authService))
                                .addFilter(new JwtValidationFilter(
                                                authenticationManager))
                                .build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                // config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                config.setAllowedHeaders(Arrays.asList("*"));

                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

}
