package com.pe.inventoryapp.backend.security.exception;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler{
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    HttpStatus extractHttpStatus = ResponseStatusCodes.AUTH_FORBIDDEN.getStatus();
    String extractMessage = ResponseStatusCodes.AUTH_FORBIDDEN.getDefaultMessage();
    
    Map<String, Object> body = Map.of(
        "type", "error",
        // "status", HttpStatus.UNAUTHORIZED.value(),
        "error", 
        extractHttpStatus,
        "message", extractMessage
        // "path", request.getRequestURI()
        );
            response.getWriter().write(objectMapper.writeValueAsString(body));


  }
  
}
//   private ResponseEntity<CommonResponse> buildCommonError(
//       ResponseStatusCodes code,
//       String message) {

//     CommonResponse response = new CommonResponse();
//     response.setType("error");
//     response.setCode(code.name());
//     response.setMessage(message);

//     HttpStatus status = code.getStatus();

//     if (status != null) {
//       return ResponseEntity.status(status).body(response);
//     } else {
//       status = ResponseStatusCodes.COMMON_ERROR.getStatus();
//       response.setCode(ResponseStatusCodes.COMMON_ERROR.name());
//       response.setMessage(ResponseStatusCodes.COMMON_ERROR.getDefaultMessage());
//       return ResponseEntity.status(status.value()).body(response);
//     }
//   }
// }