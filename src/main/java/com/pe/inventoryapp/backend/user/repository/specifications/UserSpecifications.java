package com.pe.inventoryapp.backend.user.repository.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pe.inventoryapp.backend.user.model.entity.User;

import jakarta.persistence.criteria.Predicate;

public class UserSpecifications {

  // Búsqueda por texto (mejorada: múltiples palabras)
  public static Specification<User> keywordContains(String keyword) {
    return (root, query, cb) -> {

      if (keyword == null || keyword.trim().isEmpty()) {
        return cb.conjunction();
      }

      String[] words = keyword.toLowerCase().split("\\s+");

      List<Predicate> predicates = new ArrayList<>();

      for (String word : words) {
        String like = "%" + word + "%";

        Predicate firstname = cb.like(cb.lower(root.get("firstname")), like);
        Predicate lastname = cb.like(cb.lower(root.get("lastname")), like);
        Predicate email = cb.like(cb.lower(root.get("email")), like);
        Predicate dni = cb.like(root.get("dni").as(String.class), like);

        predicates.add(cb.or(firstname, lastname, email, dni));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  // Usuario activo
  public static Specification<User> isActive() {
    return (root, query, cb) -> cb.isTrue(root.get("active"));
  }

  // // Filtro por roles (al menos uno)
  // public static Specification<User> hasRoles(List<Long> roleIds) {
  //   return (root, query, cb) -> {

  //     if (roleIds == null || roleIds.isEmpty()) {
  //       return cb.conjunction();
  //     }

  //     var join = root.join("roles");

  //     // Se evita la advertencia de "Potential null pointer access: The variable query
  //     // may be null at this location"
  //     if (query != null) {
  //       query.distinct(true);
  //     }

  //     // Cada vez que uses query.distinct, query.groupBy o query.having debes validar
  //     // que "query" sea distinto que null

  //     return join.get("id").in(roleIds);
  //   };
  // }

  // Filtro por roles EXACTOS (equivalente a HAVING COUNT)
  public static Specification<User> hasExactRoles(List<Long> roleIds) {
    return (root, query, cb) -> {

      if (roleIds == null || roleIds.isEmpty()) {
        return cb.conjunction();
      }

      var join = root.join("roles");

      if (query != null) {
        query.groupBy(root.get("id"));

        query.having(
            cb.equal(
                cb.countDistinct(join.get("id")),
                roleIds.size()));
      }

      return join.get("id").in(roleIds);
    };
  }
}