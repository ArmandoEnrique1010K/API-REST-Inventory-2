package com.pe.inventoryapp.backend.security.filter;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.auth.model.request.LoginRequest;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.GlobalExceptionHandler;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.user.model.entity.UserPrincipal;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // No se utiliza @Autowired, ya que es un constructor
  private final AuthenticationManager authenticationManager;
  private final ResponseService responseService;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ResponseService responseService) {
    this.authenticationManager = authenticationManager;
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

    // Se utiliza la entidad UserPrincipal en lugar de User de
    // org.springframework.security.core.userdetails.User
    // String username = ((UserPrincipal) authResult.getPrincipal())
    //     .getUsername();

    // Long id_user = authService.findUserIdByEmail(username);
    UserPrincipal userPrincipal = (UserPrincipal) authResult.getPrincipal();
    Long id_user = userPrincipal.getId();

    // Generación de claims
    Map<String, Object> claims = new HashMap<>();

    // claims.put("authorities", authResult.getAuthorities().stream()
    //     .map(GrantedAuthority::getAuthority)
    //     .toList());

    claims.put("role", userPrincipal.getRole());

    
    claims.put("email", userPrincipal.getUsername());
    // claims.put("id", id_user);

    // Generación del token
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
    jwtCookie.setSecure(true); // HTTPS
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(TOKEN_EXPIRATION); 
    
    // *Strict bloquea cookies en requests cross-site, incluso en enlaces normales, lo que puede causar problemas de usabilidad en algunos casos (como redirecciones después del login). Lax permite cookies en solicitudes cross-site solo para métodos seguros (GET) y en enlaces normales, lo que mejora la compatibilidad sin comprometer significativamente la seguridad. 

          // ???
    // jwtCookie.setAttribute("SameSite", "Strict");

    //* CONFIGURACION EN UN ENTORNO DE DESARROLLO, SEA DESDE POSTMAN O DESDE EL FRONTEND EN LOCALHOST
    // jwtCookie.setAttribute("SameSite", "Lax");

    //* ESTA CONFIGURACION SE UTILIZA EN UN ENTORNO DE PRODUCCIÓN, EN DONDE LOS DOMINIOS DEL FRONTEND Y DEL BACKEND SON DISTINTOS
    jwtCookie.setAttribute("SameSite", "None");

    response.addCookie(jwtCookie);

    // Respuesta de autenticación exitosa
    CommonResponse res = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS, "Has iniciado sesión con éxito");
    response.setStatus(res.status());
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
      commonResponse = responseService.generateErrorResponse(ResponseStatus.BAD_REQUEST, "Las credenciales son inválidas, verifique su correo o contraseña");
    } else {
      commonResponse = responseService.generateErrorResponse(ResponseStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error desconocido");
      log.error("Ha ocurrido un error desconocido", failed);
    }

    // Respuesta de autenticación fallida
    response.setStatus(commonResponse.status());
    response.setContentType("application/json");
    response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
  }

}