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
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;

import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.security.exception.CustomAccessDeniedHandler;
import com.pe.inventoryapp.backend.security.exception.CustomAuthenticationEntryPoint;
import com.pe.inventoryapp.backend.security.filter.JwtAuthenticationFilter;
import com.pe.inventoryapp.backend.security.filter.JwtValidationFilter;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthService authService;

	@Autowired
	private ResponseService responseService;

	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CsrfTokenRequestHandler csrfTokenRequestHandler() {
		return new CsrfTokenRequestAttributeHandler();
	}

	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
			throws Exception {
		return http
				// Configuracion de CORS
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(customAuthenticationEntryPoint)
						.accessDeniedHandler(customAccessDeniedHandler))

				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// AUTH
						.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/forgot-password")
						.permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/validate-token")
						.permitAll()
						.requestMatchers(HttpMethod.PUT, "/api/auth/change-password/*")
						.permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

						// USERS
						.requestMatchers(HttpMethod.POST, "/api/users/register")
						.hasAnyAuthority("ROLE_ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/users")
						.hasAnyAuthority("ROLE_ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/users/role/user")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()

						.requestMatchers(HttpMethod.PUT, "/api/users/*/roles")
						.hasAnyAuthority("ROLE_ADMIN")
						.requestMatchers(HttpMethod.PATCH, "/api/users/*/status")
						.hasAnyAuthority("ROLE_ADMIN")

						// ROLES
						.requestMatchers(HttpMethod.GET, "/api/roles")
						.hasAnyAuthority("ROLE_ADMIN")

						// CATEGORY
						.requestMatchers(HttpMethod.POST, "/api/categories")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/categories")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/categories/active")
						.authenticated()
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

						// REGION
						.requestMatchers(HttpMethod.POST, "/api/regions")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/regions")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/regions/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/regions/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// LOCATION
						.requestMatchers(HttpMethod.POST, "/api/locations")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						.requestMatchers(HttpMethod.GET, "/api/locations")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/locations/active")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/locations/region/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/locations/active/region/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/locations/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						.requestMatchers(HttpMethod.PUT, "/api/locations/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/locations/*/status")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// COMPANY
						.requestMatchers(HttpMethod.POST, "/api/companies")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/companies")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/companies/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/companies/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// STOCK LOT
						.requestMatchers(HttpMethod.POST, "/api/stock-lots")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/stock-lots")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/stock-lots/product/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/stock-lots/some")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/stock-lots/some/product/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET,
								"/api/stock-lots/exclude/*/some/product/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						.requestMatchers(HttpMethod.GET, "/api/stock-lots/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						.requestMatchers(HttpMethod.PUT, "/api/stock-lots/*/increase")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/stock-lots/*/decrease")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/stock-lots/*/recovery")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/stock-lots/*/transfer")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// DELIVERY ORDER
						.requestMatchers(HttpMethod.POST, "/api/delivery-orders")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/delivery-orders")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/delivery-orders/in-progress")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/delivery-orders/client")
						.authenticated()
						.requestMatchers(HttpMethod.GET, "/api/delivery-orders/*")
						.authenticated()
						// Nota, se especifica que sea solamente el rol de USER
						.requestMatchers(HttpMethod.GET, "/api/delivery-orders/*/client")
						.hasAnyAuthority("ROLE_USER")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-orders/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-orders/*/send")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-orders/*/cancel")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// PRODUCT DELIVERYORDER
						.requestMatchers(HttpMethod.POST,
								"/api/products-delivery-orders/product/*/deliveryOrder/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET,
								"/api/products-delivery-orders/products/deliveryOrder/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.DELETE, "/api/products-delivery-orders/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// DELIVERY LINE
						.requestMatchers(HttpMethod.POST,
								"/api/delivery-lines/product-delivery-order/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/delivery-lines/delivery-order/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/delivery-lines/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/cancel").hasAnyAuthority("ROLE_ADMIN",
								"ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/deliver").hasAnyAuthority("ROLE_ADMIN",
								"ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/missing").hasAnyAuthority("ROLE_ADMIN",
								"ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/return").hasAnyAuthority("ROLE_ADMIN",
								"ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/allocate-stock").hasAnyAuthority("ROLE_ADMIN",
								"ROLE_SECRETARY", "ROLE_OPERATOR")

						// MOVEMENTS
						.requestMatchers(HttpMethod.GET, "/api/movements").hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/movements/delivery-line/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/movements/stock-lot/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/movements/product/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/movements/user/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/movements/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// PRODUCT DELIVERY ORDER REGION
						.requestMatchers(HttpMethod.GET, "/api/product-delivery-order-region/delivery-order/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// STOCK LOT DELIVERY LINE
						.requestMatchers(HttpMethod.GET, "/api/stock-lot-delivery-lines/delivery-line/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")



						// OTHERS
						// .requestMatchers(HttpMethod.GET, "/csrf").permitAll()
						// .requestMatchers(HttpMethod.GET, "/api").permitAll()
						// .requestMatchers("/api/category").permitAll()
						// .anyRequest().denyAll())
						.anyRequest().authenticated())
				.addFilter(new JwtAuthenticationFilter(
						authenticationManager(), authService, responseService))
				.addFilter(new JwtValidationFilter(
						authenticationManager, responseService, userRepository))

				// Configuracion de CSRF
				.csrf(csrf -> csrf.disable())

				// ACTIVAR ESTO
				// .csrf(csrf -> csrf
				// .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				// .csrfTokenRequestHandler(csrfTokenRequestHandler())
				// .ignoringRequestMatchers(
				// "/api/auth/login",
				// "/api/auth/forgot-password",
				// "/api/auth/validate-token",
				// "/api/auth/change-password/**"))

				.build();
	}

	// TODO: REPARAR ESTO
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "*"));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
		// config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		config.setAllowedHeaders(Arrays.asList("*"));

		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

}
