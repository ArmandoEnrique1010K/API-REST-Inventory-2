package com.pe.inventoryapp.backend.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;

import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.security.exception.CustomAccessDeniedHandler;
import com.pe.inventoryapp.backend.security.exception.CustomAuthenticationEntryPoint;
import com.pe.inventoryapp.backend.security.filter.JwtAuthenticationFilter;
import com.pe.inventoryapp.backend.security.filter.JwtValidationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration

// Habilita la configuración de @PreAuthorize
@EnableMethodSecurity
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final ResponseService responseService;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
			 ResponseService responseService,
			CustomAccessDeniedHandler customAccessDeniedHandler,
			CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.authenticationConfiguration = authenticationConfiguration;
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

						// .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
						// .requestMatchers(HttpMethod.GET, "/api/auth/current-session")
						// .authenticated()

						.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/api/dashboard/**").authenticated()
						.requestMatchers("/api/roles/**").hasRole("ADMIN")
						.requestMatchers("/api/users/**").hasRole("ADMIN")
						.requestMatchers("/api/profile/**").authenticated()
						.requestMatchers("/api/categories/**").hasRole("OPERATOR")
						.requestMatchers("/api/types/**").hasRole("OPERATOR")
						.requestMatchers("/api/products/**").hasRole("OPERATOR")
						.requestMatchers("/api/models/**").hasRole("OPERATOR")
						.requestMatchers("/api/regions/**").hasRole("OPERATOR")
						.requestMatchers("/api/subregions/**").hasRole("OPERATOR")
						.requestMatchers("/api/locations/**").hasRole("OPERATOR")
						.requestMatchers("/api/companies/**").hasRole("OPERATOR")
						.requestMatchers("/api/stock-lots/**").hasRole("OPERATOR")
						.requestMatchers("/api/delivery-orders/**").hasRole("USER")
						.requestMatchers("/api/models-delivery-orders/**").hasRole("USER")
						.requestMatchers("/api/delivery-lines/**").hasRole("USER")
						.requestMatchers("/api/stock-lot-delivery-lines/**").hasRole("OPERATOR")
						.requestMatchers("/api/movements/**").hasRole("ADMIN")
						.requestMatchers("/api/movement-stocklots/**").hasRole("ADMIN")
						.requestMatchers("/api/summary/**").hasRole("OPERATOR")

						
						// AUTH
						// .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
						// .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password")
						// .permitAll()
						// .requestMatchers(HttpMethod.POST, "/api/auth/validate-token")
						// .permitAll()
						// .requestMatchers(HttpMethod.PUT, "/api/auth/change-password")
						// .permitAll()
						// .requestMatchers(HttpMethod.GET, "/api/auth/current-session")
						// .authenticated()
						// .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

						// ROLES
						// .requestMatchers(HttpMethod.GET, "/api/roles")
						// .hasAnyAuthority("ROLE_ADMIN")

						// USERS
						// .requestMatchers(HttpMethod.POST, "/api/users/register")
						// .hasAnyAuthority("ROLE_ADMIN")
						// .requestMatchers(HttpMethod.GET, "/api/users")
						// .hasAnyAuthority("ROLE_ADMIN")
						// .requestMatchers(HttpMethod.GET, "/api/users/role/user")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/users/*/roles").hasAnyAuthority("ROLE_ADMIN") 
						// .requestMatchers(HttpMethod.PUT, "/api/users/*/roles")
						// .hasAnyAuthority("ROLE_ADMIN")
						// .requestMatchers(HttpMethod.PATCH, "/api/users/*/status")
						// .hasAnyAuthority("ROLE_ADMIN")

						// PROFILE
						// .requestMatchers(HttpMethod.GET, "/api/profile").authenticated()
						// .requestMatchers(HttpMethod.PUT, "/api/profile").authenticated()

						// CATEGORY
						// .requestMatchers(HttpMethod.POST, "/api/categories")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/categories")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/categories/active")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// .requestMatchers(HttpMethod.GET, "/api/categories/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/categories/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/categories/*/status")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// TYPE
						// .requestMatchers(HttpMethod.POST, "/api/types")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/types")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/types/active")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// .requestMatchers(HttpMethod.GET, "/api/types/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/types/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/types/*/status")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// PRODUCT
						// .requestMatchers(HttpMethod.POST, "/api/products")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/products")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/products/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/products/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/products/*/status")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// MODEL
						// .requestMatchers(HttpMethod.POST, "/api/models/product/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/models/product/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/models")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/models/search")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// Listar los primeros 10 modelos
						// .requestMatchers(HttpMethod.GET, "/api/models/search/models")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// .requestMatchers(HttpMethod.GET, "/api/models/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/models/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/models/*/status")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// REGION
						// .requestMatchers(HttpMethod.POST, "/api/regions")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/regions")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/regions/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/regions/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// SUBREGION
						// .requestMatchers(HttpMethod.POST, "/api/subregions")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/subregions/region/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/subregions/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/subregions/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// LOCATION
						// .requestMatchers(HttpMethod.POST, "/api/locations")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/locations")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/locations/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/locations/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/locations/*/status")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/locations/search/region/*/subregion/*")
						// .hasAnyAuthority("ROLE_OPERATOR", "ROLE_ADMIN", "ROLE_SECRETARY")

						// COMPANY
						// .requestMatchers(HttpMethod.POST, "/api/companies")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/companies")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/companies/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/companies/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// STOCK LOT
						// .requestMatchers(HttpMethod.POST, "/api/stock-lots")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/stock-lots")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/stock-lots/model/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/stock-lots/exclude/*/company/*/model/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/stock-lots/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/stock-lots/*/increase")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/stock-lots/*/decrease")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/stock-lots/*/recovery")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/stock-lots/*/transfer")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// DELIVERY ORDER
						// .requestMatchers(HttpMethod.POST, "/api/delivery-orders")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/delivery-orders")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/delivery-orders/in-progress")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/delivery-orders/client")
						// .authenticated()
						// .requestMatchers(HttpMethod.GET, "/api/delivery-orders/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// // Nota, se especifica que sea solamente el rol de USER
						// .requestMatchers(HttpMethod.GET, "/api/delivery-orders/*/client")
						// .hasAnyAuthority("ROLE_USER")
						// .requestMatchers(HttpMethod.PUT, "/api/delivery-orders/*/cancel")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/delivery-orders/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/delivery-orders/*/send")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// MODEL - DELIVERY ORDER
						// .requestMatchers(HttpMethod.POST,
						// 		"/api/models-delivery-orders/model/*/deliveryOrder/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET,
						// 		"/api/models-delivery-orders/models/deliveryOrder/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PATCH, "/api/models-delivery-orders/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// DELIVERY LINE
						// .requestMatchers(HttpMethod.POST,
						// 		"/api/delivery-lines/delivery-order/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/delivery-lines/delivery-order/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.GET, "/api/delivery-lines/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/cancel")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/deliver")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PATCH, "/api/delivery-lines/*/missing")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*/lost")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*/return")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						// .requestMatchers(HttpMethod.PUT, "/api/delivery-lines/*/allocate-stock")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")
						
						// STOCK LOT - DELIVERY LINE
						// .requestMatchers(HttpMethod.GET, "/api/stock-lot-delivery-lines/delivery-line/*")
						// .hasAnyAuthority("ROLE_ADMIN","ROLE_SECRETARY", "ROLE_OPERATOR")

						// MOVEMENTS
						// .requestMatchers(HttpMethod.GET, "/api/movements")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")
						// .requestMatchers(HttpMethod.GET, "/api/movements/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// MOVEMENT - STOCK LOTS
						// .requestMatchers(HttpMethod.GET, "/api/movement-stocklots/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY")

						// SUMMARY
						// .requestMatchers(HttpMethod.GET, "/api/summary/*")
						// .hasAnyAuthority("ROLE_ADMIN", "ROLE_SECRETARY", "ROLE_OPERATOR")

						// ENDPOINTS DE PRUEBA 
						.requestMatchers(HttpMethod.GET, "/api").permitAll()
						// .requestMatchers(HttpMethod.GET, "/api/dotenv").permitAll()
						// .requestMatchers(HttpMethod.GET, "/api/database").permitAll()
						// .requestMatchers(HttpMethod.GET, "/api/csrf").permitAll()
						.anyRequest().denyAll())
				.addFilter(new JwtAuthenticationFilter(
						authenticationManager(), responseService))
				.addFilter(new JwtValidationFilter(
						authenticationManager, responseService))
				

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

	// Se establecen el nivel de jerarquia dado que todo ROL mayor incluye los endpoints que estan definidos en el nivel inferior
	// JERARQUIA DE ROLES:
	// USER < OPERATOR < ADMIN
	@Bean
 RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("""
        ROLE_ADMIN > ROLE_OPERATOR
        ROLE_OPERATOR > ROLE_USER
    """);
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
