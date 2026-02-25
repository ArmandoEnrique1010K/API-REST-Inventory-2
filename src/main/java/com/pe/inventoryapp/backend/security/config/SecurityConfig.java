package com.pe.inventoryapp.backend.security.config;

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

import java.util.List;

@Configuration
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final ResponseService responseService;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, UserRepository userRepository,
			AuthService authService, ResponseService responseService,
			CustomAccessDeniedHandler customAccessDeniedHandler,
			CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.userRepository = userRepository;
		this.authService = authService;
		this.responseService = responseService;
		this.customAccessDeniedHandler = customAccessDeniedHandler;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}

	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CsrfTokenRequestHandler csrfTokenRequestHandler() {
		return new CsrfTokenRequestAttributeHandler();
	}


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
						.requestMatchers(HttpMethod.PUT, "/api/auth/change-password")
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/api/auth/current-session").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

						// ROLES
						.requestMatchers(HttpMethod.GET, "/api/roles")
						.hasAnyAuthority("ROLE_ADMIN")

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

						// CATEGORY
						.requestMatchers(HttpMethod.POST, "/api/categories")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/categories")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/categories/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/categories/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// TYPE
						.requestMatchers(HttpMethod.POST, "/api/types")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/types")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/types/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/types/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// PRODUCT
						.requestMatchers(HttpMethod.POST, "/api/products/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/products/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/products/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/products/*/status")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// MODEL
						.requestMatchers(HttpMethod.POST, "/api/models/product/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/models/product/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/models/products")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/models/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/models/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/models/*/status")
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

						// SUBREGION
						.requestMatchers(HttpMethod.POST, "/api/subregions")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/subregions")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/subregions/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/subregions/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// LOCATION
						.requestMatchers(HttpMethod.POST, "/api/locations")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/locations")
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
						.requestMatchers(HttpMethod.GET, "/api/stock-lots/")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/stock-lots/exclude/*/company/*/model/*")
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
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// Nota, se especifica que sea solamente el rol de USER
						.requestMatchers(HttpMethod.GET, "/api/delivery-orders/*/client")
						.hasAnyAuthority("ROLE_USER")
						.requestMatchers(HttpMethod.PUT, "/api/delivery-orders/*/cancel")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-orders/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-orders/*/send")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// MODEL - DELIVERY ORDER
						.requestMatchers(HttpMethod.POST,
								"/api/models-delivery-orders/model/*/deliveryOrder/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET,
								"/api/models-delivery-orders/models/deliveryOrder/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.DELETE, "/api/models-delivery-orders/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// DELIVERY LINE
						.requestMatchers(HttpMethod.POST,
								"/api/delivery-lines/model-delivery-order/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/delivery-lines/delivery-order/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.GET, "/api/delivery-lines/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/cancel")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/deliver")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/missing")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*/lost")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*/return")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						.requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*/allocate-stock")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						
						// STOCK LOT - DELIVERY LINE
						.requestMatchers(HttpMethod.PUT, "/api/stock-lot-delivery-lines/delivery-line/*")
						.hasAnyAuthority("ROLE_ADMIN","ROLE_SECRETARY", "ROLE_OPERATOR")

						// MOVEMENTS
						.requestMatchers(HttpMethod.GET, "/api/movements")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						.requestMatchers(HttpMethod.GET, "/api/movements/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// MODEL - DELIVERY ORDER - REGION
						.requestMatchers(HttpMethod.GET, "/api/model-delivery-order-region/delivery-order/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// MODEL - DELIVERY ORDER - SUBREGION
						.requestMatchers(HttpMethod.GET, "/api/model-delivery-order-subregion/delivery-order/*")
						.hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// TODO: ELIMINAR LOS SIGUIENTES ENDPOINTS
						// ENDPOINTS DE PRUEBA 
						.requestMatchers(HttpMethod.GET, "/api").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/dotenv").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/database").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/csrf").permitAll()
						.anyRequest().denyAll())
				.addFilter(new JwtAuthenticationFilter(
						authenticationManager(), authService, responseService))
				.addFilter(new JwtValidationFilter(
						authenticationManager, responseService, userRepository))
				

				// Configuracion de CSRF
				.csrf(csrf -> csrf.disable())

				// * ADVERTENCIA: NO HABILITAR CSRF PORQUE SE ESTA TRABAJANDO CON JWT Y COOKIES, SI SE HABILITA CSRF SE DEBE CONFIGURAR PARA QUE IGNORE LAS RUTAS DE AUTH, PERO AUN ASI PUEDE CAUSAR PROBLEMAS CON EL FRONTEND SI NO SE CONFIGURA CORRECTAMENTE


				// .csrf(csrf -> csrf
				// 		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				// 		.csrfTokenRequestHandler(csrfTokenRequestHandler())
				// 		.ignoringRequestMatchers(
				// 				"/api/auth/login",
				// 				"/api/auth/forgot-password",
				// 				"/api/auth/validate-token",
				// 				"/api/auth/change-password",
				// 				"/api/auth/current-session",
				// 				"/api/auth/logout"
				// 		)
				.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		//*  ALTERNAR ESTAS CONFIGURACIONES PARA TRABAJAR EN POSTMAN O DESDE EL FRONTEND
		// Configuracion de CORS para permitir solicitudes desde cualquier origen, con cualquier método y encabezados
		// config.setAllowedOrigins(List.of("*"));
		// config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
		// config.setAllowedHeaders(List.of("*"));

		config.setAllowedOrigins(List.of("http://localhost:5173"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
		config.setExposedHeaders(List.of("Set-Cookie"));
	
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

}
