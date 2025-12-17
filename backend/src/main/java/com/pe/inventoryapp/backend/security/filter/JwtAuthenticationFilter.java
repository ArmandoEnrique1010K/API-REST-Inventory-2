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
import com.pe.inventoryapp.backend.auth.model.response.LoginSuccessfulResponse;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.data.ErrorCode;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.user.model.entity.User;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

    // String role = authResult.getAuthorities().stream()
    // .findAny()
    // .map(GrantedAuthority::getAuthority)
    // .orElse(null);

    // String role = authResult.getAuthorities().toString();

    // AQUI SE ENCUENTRA EL ORIGEN DE EXTRACCIÓN DEL USERNAME
    // OBTENER EL ID DEL USUARIO
    Long id_user = authService.findUserIdByEmail(username);

    Map<String, Object> claims = new HashMap<>();
    claims.put("authorities", authResult.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList());

    // claims.put("email", username);
    claims.put("id", id_user);

    String token = Jwts.builder()
        .subject(
            id_user.toString())
        // .subject(username)
        .claims(claims)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 3600000))
        .signWith(SECRET_KEY)
        .compact();

    response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

    LoginSuccessfulResponse loginSuccessfulResponse = new LoginSuccessfulResponse();
    loginSuccessfulResponse.setType("success");
    loginSuccessfulResponse.setToken(token);
    loginSuccessfulResponse.setMessage(String.format("Has iniciado sesión con exito"));

    response.getWriter().write(new ObjectMapper().writeValueAsString(loginSuccessfulResponse));
    response.setStatus(200);
    response.setContentType("application/json");

  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {

    // Aqui no se puede utilizar excepciones, ya que se va a devolver un json
    CommonResponse commonResponse = new CommonResponse();
    commonResponse.setCode(ErrorCode.AUTH_INVALID_CREDENTIALS.name());
    commonResponse.setMessage(ErrorCode.AUTH_INVALID_CREDENTIALS.getDefaultMessage());

    response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
    response.setStatus(401);

    response.setContentType("application/json");

    // throw new BusinessException(ErrorCode.AUTHENTICATION_ERROR,
    // ErrorCode.AUTHENTICATION_ERROR.getDefaultMessage());

  }

}