package com.pe.inventoryapp.backend.security.filter;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.common.dto.ErrorResponse;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.LoginResponse;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
    setFilterProcessesUrl("/api/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException {
    User user = null;
    String email = null;
    String password = null;

    try {
      user = new ObjectMapper().readValue(request.getInputStream(), User.class);
      email = user.getEmail();
      password = user.getPassword();
    } catch (StreamReadException e) {
      e.printStackTrace();
    } catch (DatabindException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

    return authenticationManager.authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {

    String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
        .getUsername();

    String role = authResult.getAuthorities().stream()
        .findAny()
        .map(GrantedAuthority::getAuthority)
        .orElse(null);

    Map<String, Object> claims = new HashMap<>();
    claims.put("authority", role);

    String token = Jwts.builder()
        .subject(username)
        .claims(claims)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 3600000))
        .signWith(SECRET_KEY)
        .compact();

    response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

    LoginResponse responseDto = new LoginResponse();
    responseDto.setType("success");
    responseDto.setToken(token);
    responseDto.setMessage(String.format("Has iniciado sesión con exito"));

    response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
    response.setStatus(200);
    response.setContentType("application/json");

  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {

    ErrorResponse errorLoginResponseDto = new ErrorResponse();
    errorLoginResponseDto.setType("error");
    errorLoginResponseDto.setMessage("Error en la autenticacion username o password incorrecto!");

    // body.put("error", failed.getMessage());

    response.getWriter().write(new ObjectMapper().writeValueAsString(errorLoginResponseDto));
    response.setStatus(401);
    response.setContentType("application/json");
  }

}