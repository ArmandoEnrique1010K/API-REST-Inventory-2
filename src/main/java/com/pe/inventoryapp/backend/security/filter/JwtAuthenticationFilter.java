package com.pe.inventoryapp.backend.security.filter;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.auth.model.request.LoginRequest;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final AuthenticationManager authenticationManager;
  private final AuthService authService;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthService authService) {
    this.authenticationManager = authenticationManager;
    this.authService = authService;
    setFilterProcessesUrl("/api/auth/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    try {
      LoginRequest login = new ObjectMapper()
          .readValue(request.getInputStream(), LoginRequest.class);
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          login.getEmail(),
          login.getPassword());

      return authenticationManager.authenticate(authToken);
    } catch (StreamReadException e) {
      e.printStackTrace();
    } catch (DatabindException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {

    String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
        .getUsername();

    // AQUI SE ENCUENTRA EL ORIGEN DE EXTRACCIÓN DEL USERNAME
    // OBTENER EL ID DEL USUARIO
    Long id_user = authService.findUserIdByEmail(username);

    Map<String, Object> claims = new HashMap<>();
    claims.put("authorities", authResult.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList());

    claims.put("id", id_user);

    String token = Jwts.builder()
        .subject(
            id_user.toString())
        .claims(claims)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 3600000 * 24))
        .signWith(SECRET_KEY)
        .compact();

    // Configuración de cookies
    Cookie jwtCookie = new Cookie("ACCESS_TOKEN", token);
    jwtCookie.setHttpOnly(true); // NO accesible por JS
    jwtCookie.setSecure(true); // HTTPS (false solo en local)
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(60 * 60 * 24); // 1 dia
    jwtCookie.setAttribute("SameSite", "Strict");

    response.addCookie(jwtCookie);

    CommonResponse commonResponse = new CommonResponse();
    commonResponse.setType("success");
    commonResponse.setCode(ResponseStatusCodes.SUCCESS_RESPONSE.name());
    commonResponse.setMessage("Has iniciado sesión con éxito");

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {

    // Aqui no se puede utilizar excepciones, ya que se va a devolver un json
    CommonResponse commonResponse = new CommonResponse();
    commonResponse.setType("error");
    commonResponse.setCode(ResponseStatusCodes.AUTH_INVALID_CREDENTIALS.name());
    commonResponse.setMessage(ResponseStatusCodes.AUTH_INVALID_CREDENTIALS.getDefaultMessage());

    response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
    response.setStatus(401);
    response.setContentType("application/json");
  }

}