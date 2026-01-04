package com.pe.inventoryapp.backend.security.filter;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.auth.model.request.LoginRequest;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  // No se utiliza @Autowired, ya que es un constructor
  private final AuthenticationManager authenticationManager;
  private final AuthService authService;
  private final ResponseService responseService;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthService authService, ResponseService responseService) {
    this.authenticationManager = authenticationManager;
    this.authService = authService;
    this.responseService = responseService;
    setFilterProcessesUrl("/api/auth/login");
  }

  // Método de autenticación
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    try {
      LoginRequest login = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          login.getEmail(),
          login.getPassword());

      return authenticationManager.authenticate(authToken);
    } catch (IOException e) {
        throw new AuthenticationServiceException("Ha ocurrido un error inesperado al leer credenciales");
    }
  }

  // Metodo de autenticación exitosa
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {

    // Origen de extracción del username, toma el ID del usuario (no cambia en el
    // tiempo)
    String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
        .getUsername();

    Long id_user = authService.findUserIdByEmail(username);

    // Generación de claims
    Map<String, Object> claims = new HashMap<>();

    claims.put("authorities", authResult.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList());

    claims.put("id", id_user);

    // Generación del token
    String token = Jwts.builder()
        .subject(
            id_user.toString())
        .claims(claims)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 3600000 * 24))
        .signWith(SECRET_KEY)
        .compact();

    // TODO: HABILITAR LAS COOKIES EN EL FRONTEND
    // Configuración de cookies
    Cookie jwtCookie = new Cookie("ACCESS_TOKEN", token);
    jwtCookie.setHttpOnly(true); // NO accesible por JS
    jwtCookie.setSecure(true); // HTTPS (false solo en local)
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(TOKEN_EXPIRATION); 
    jwtCookie.setAttribute("SameSite", "Strict");

    response.addCookie(jwtCookie);

    // Respuesta de autenticación exitosa
    CommonResponse res = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS, "Has iniciado sesión con éxito");
    response.setStatus(res.getStatus());
    response.setContentType("application/json");
    response.getWriter().write(new ObjectMapper().writeValueAsString(res));
  }

  // Metodo de autenticación fallida
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {
    // Declara una variable de respuesta
    CommonResponse commonResponse;

    // Validar cada tipo de excepción de autenticación
    // No es DisabledException, sino LockedException
    if (failed instanceof LockedException) {
      commonResponse = responseService.generateErrorResponse(ResponseStatus.UNAUTHORIZED, "El usuario ha sido bloqueado por el administrador");
    } else if (failed instanceof BadCredentialsException) {
      commonResponse = responseService.generateErrorResponse(ResponseStatus.UNAUTHORIZED, "Las credenciales son inválidas, verifique su correo o contraseña");
    } else {
      commonResponse = responseService.generateErrorResponse(ResponseStatus.UNAUTHORIZED, "");
    }

    // Respuesta de autenticación fallida
    response.setStatus(commonResponse.getStatus());
    response.setContentType("application/json");
    response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
  }

}