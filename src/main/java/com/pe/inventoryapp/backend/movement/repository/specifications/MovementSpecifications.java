package com.pe.inventoryapp.backend.movement.repository.specifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class MovementSpecifications {

  /**
   * Reutiliza joins para evitar duplicados en queries complejas
   */
  private static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {
    return from.getJoins().stream()
        .filter(j -> j.getAttribute().getName().equals(attribute))
        .findFirst()
        .orElseGet(() -> from.join(attribute));
  }

  // 📦 Cantidad
  public static Specification<Movement> quantityBetween(Integer min, Integer max) {
    return (root, query, cb) -> {

      var predicate = cb.conjunction();

      if (min != null) {
        predicate = cb.and(predicate,
            cb.greaterThanOrEqualTo(root.get("quantity"), min));
      }

      if (max != null) {
        predicate = cb.and(predicate,
            cb.lessThanOrEqualTo(root.get("quantity"), max));
      }

      return predicate;
    };
  }

  // 📅 Fecha de creación
  public static Specification<Movement> createdAtBetween(
      LocalDateTime min,
      LocalDateTime max) {
    return (root, query, cb) -> {

      var predicate = cb.conjunction();

      if (min != null) {
        predicate = cb.and(predicate,
            cb.greaterThanOrEqualTo(root.get("createdAt"), min));
      }

      if (max != null) {
        predicate = cb.and(predicate,
            cb.lessThanOrEqualTo(root.get("createdAt"), max));
      }

      return predicate;
    };
  }

  // 🔄 Tipo de movimiento
  public static Specification<Movement> hasMovementType(MovementType type) {
    return (root, query, cb) -> {
      if (type == null)
        return cb.conjunction();
      return cb.equal(root.get("movementType"), type);
    };
  }

  // 👤 Búsqueda por usuario (mejorada: múltiples palabras)
  public static Specification<Movement> usernameContains(String username) {
    return (root, query, cb) -> {

      if (username == null || username.trim().isEmpty()) {
        return cb.conjunction();
      }

      String[] words = username.toLowerCase().split("\\s+");

      var user = getOrCreateJoin(root, "user");

      List<Predicate> predicates = new ArrayList<>();

      for (String word : words) {
        String like = "%" + word + "%";

        Predicate firstname = cb.like(cb.lower(user.get("firstname")), like);
        Predicate lastname = cb.like(cb.lower(user.get("lastname")), like);
        Predicate email = cb.like(cb.lower(user.get("email")), like);
        Predicate dni = cb.like(user.get("dni").as(String.class), like);

        predicates.add(cb.or(firstname, lastname, email, dni));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  // 🔎 Búsqueda por modelo / producto
  public static Specification<Movement> keywordContains(String keyword) {
    return (root, query, cb) -> {

      if (keyword == null || keyword.trim().isEmpty()) {
        return cb.conjunction();
      }

      String[] words = keyword.toLowerCase().split("\\s+");

      var model = getOrCreateJoin(root, "model");
      var product = getOrCreateJoin(model, "product");

      List<Predicate> predicates = new ArrayList<>();

      for (String word : words) {
        String like = "%" + word + "%";

        Predicate modelMatch = cb.like(cb.lower(model.get("name")), like);
        Predicate productMatch = cb.like(cb.lower(product.get("name")), like);

        predicates.add(cb.or(modelMatch, productMatch));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  // ⚡ FETCH opcional (evita N+1) (1 query princiapl + N queries adicionales)
  public static Specification<Movement> fetchRelations() {
    return (root, query, cb) -> {

      if (query != null) {
        root.fetch("model").fetch("product");
        root.fetch("user");
        root.fetch("deliveryLine", jakarta.persistence.criteria.JoinType.LEFT);
        root.fetch("stockLotReceiver", jakarta.persistence.criteria.JoinType.LEFT);

        query.distinct(true);
      }

      return cb.conjunction();
    };
  }
}