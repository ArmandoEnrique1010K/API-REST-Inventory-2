package com.pe.inventoryapp.backend.movement.service;

import org.springframework.stereotype.Service;

@Service
public class MovementDomainService {

  // Genera un comentario para la linea de entrega, si el
  // comentario es nulo o vacio, se asigna un comentario por defecto
  public String generateComment(String comment, String defaultComment) {
    if (comment == null || comment.trim().isEmpty()) {
      return defaultComment;
    }
    return comment;
  }
}
