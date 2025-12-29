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
import com.pe.inventoryapp.backend.security.exception.CustomAccessDeniedHandler;
import com.pe.inventoryapp.backend.security.exception.CustomAuthenticationEntryPoint;
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

        @Autowired
        private CustomAccessDeniedHandler customAccessDeniedHandler;
        @Autowired
        private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
                        throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                                .accessDeniedHandler(customAccessDeniedHandler))

                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // TODO: ACTUALIZAR LOS ENDPOINTS

                                                // AUTH
                                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/validate-token").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/change-password/*").permitAll()

                                                // USERS
                                                .requestMatchers(HttpMethod.POST, "/api/users/register")
                                                .hasAnyAuthority("ROLE_ADMIN")

                                                .requestMatchers(HttpMethod.GET, "/api/users")
                                                .hasAnyAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()

                                                .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/users/roles")
                                                .hasAnyAuthority("ROLE_ADMIN")

                                                .requestMatchers(HttpMethod.DELETE, "/api/users/*")
                                                .hasAnyAuthority("ROLE_ADMIN")

                                                // ROLES
                                                .requestMatchers(HttpMethod.GET, "/api/roles").hasAnyAuthority("ROLE_ADMIN")

                                                // CATEGORY
                                                .requestMatchers(HttpMethod.POST, "/api/categories")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/categories")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/categories/active").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/categories/*").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/categories/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.PATCH, "/api/categories/*/status")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

                                                // PRODUCT
                                                .requestMatchers(HttpMethod.POST, "/api/products")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/products")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/products/active").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/products/category/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.GET, "/api/products/active/category/*")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/products/*").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/products/*")
                                                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
                                                .requestMatchers(HttpMethod.PATCH, "/api/products/*/status")
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
                                                .anyRequest().denyAll())
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
