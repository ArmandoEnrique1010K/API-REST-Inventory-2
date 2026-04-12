package com.pe.inventoryapp.backend.user.repository.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pe.inventoryapp.backend.user.model.entity.User;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
  // return (root, query, cb) -> {

  // if (roleIds == null || roleIds.isEmpty()) {
  // return cb.conjunction();
  // }

  // var join = root.join("roles");

  // // Se evita la advertencia de "Potential null pointer access: The variable
  // query
  // // may be null at this location"
  // if (query != null) {
  // query.distinct(true);
  // }

  // // Cada vez que uses query.distinct, query.groupBy o query.having debes
  // validar
  // // que "query" sea distinto que null

  // return join.get("id").in(roleIds);
  // };
  // }

  /**
   * Filtro por roles EXACTOS
   *
   * PROBLEMA ORIGINAL:
   * - Se usaba join + groupBy + having
   * - MySQL (ONLY_FULL_GROUP_BY) lanzaba error
   * - Hibernate generaba queries inválidas
   *
   * NO HACER:
   * - root.join("roles")
   * - query.groupBy(...)
   * - query.distinct(...)
   *
   * SOLUCIÓN:
   * - Usar SUBQUERIES
   * - NO hacer joins en la query principal
   *
   * Ventaja:
   * - Compatible con paginación
   * - Compatible con MySQL strict
   * - Evita duplicados
   */

  public static Specification<User> hasExactRoles(List<Long> roleIds) {
    return (root, query, cb) -> {

      if (query == null) {
        return null;
      }

      if (roleIds == null || roleIds.isEmpty()) {
        return cb.conjunction();
      }

      // var join = root.join("roles");

      // Evita duplicados
      // query.distinct(true);

      // Agrupar por usuario
      // query.groupBy(root.get("id"));

      /**
       * Subquery 1:
       * Cuenta cuántos roles del usuario están dentro de la lista enviada
       */
      var matchCountSubquery = query.subquery(Long.class);
      var matchRoot = matchCountSubquery.from(User.class);
      var matchJoin = matchRoot.join("roles");

      matchCountSubquery.select(cb.countDistinct(matchJoin.get("id")))
          .where(
              cb.equal(matchRoot.get("id"), root.get("id")),
              matchJoin.get("id").in(roleIds));

      /**
       * Subquery 2:
       * Cuenta TODOS los roles del usuario
       */
      var totalCountSubquery = query.subquery(Long.class);
      var totalRoot = totalCountSubquery.from(User.class);
      var totalJoin = totalRoot.join("roles");

      totalCountSubquery.select(cb.countDistinct(totalJoin.get("id")))
          .where(
              cb.equal(totalRoot.get("id"), root.get("id")));

      /**
       * Condición final:
       *
       * 1. Tiene exactamente N roles de la lista
       * 2. No tiene roles adicionales
       */
      return cb.and(
          cb.equal(matchCountSubquery, (long) roleIds.size()),
          cb.equal(totalCountSubquery, (long) roleIds.size()));
    };

    // return join.get("id").in(roleIds);
  };

  /**
   * NO USAR fetchRelations en este caso
   *
   * Ejemplo de lo que NO debes hacer:
   *
   * root.fetch("roles")
   *
   * Problemas:
   * - Rompe paginación (Hibernate no puede aplicar LIMIT correctamente)
   * - Genera:
   * HHH90003004: applying in memory
   * - Puede duplicar resultados
   * - Aumenta consumo de memoria
   *
   * En MANY TO MANY:
   * - Se usa LAZY + @BatchSize
   * - No fetch
   */


  public static Join<?, ?> getOrCreateJoin(From<?, ?> root, String attribute) {
    return root.getJoins().stream()
        .filter(j -> j.getAttribute().getName().equals(attribute))
        .findFirst()
        .orElseGet(() -> root.join(attribute));
  }

}