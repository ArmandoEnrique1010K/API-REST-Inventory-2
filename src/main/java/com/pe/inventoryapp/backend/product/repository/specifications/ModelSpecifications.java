package com.pe.inventoryapp.backend.product.repository.specifications;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pe.inventoryapp.backend.product.model.entity.Model;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

// Una especificacion se crea en una clase
public class ModelSpecifications {

  // Cada uno de los filtros que se utilizan en una query se deben definir aqui

  // Si el parametro keyword contiene cierta palabra
  public static Specification<Model> keywordContains(String keyword) {
    return (root, query, cb) -> {

      if (keyword == null || keyword.trim().isEmpty()) {
        return cb.conjunction();
      }

      // Busqueda por palabras por campo (solamente si el nombre del producto o del
      // modelo tiene esa palabra clave)
      // String like = "%" + keyword.toLowerCase() + "%";

      // var productJoin = root.join("product");

      // return cb.or(
      // cb.like(cb.lower(root.get("name")), like),
      // cb.like(cb.lower(productJoin.get("name")), like));

      // Busqueda por varias palabras claves a la vez
      String[] words = keyword.toLowerCase().split("\\s+");

      var productJoin = getOrCreateJoin(root, "product");

      List<Predicate> predicates = new ArrayList<>();

      for (String word : words) {
        String like = "%" + word + "%";

        Predicate modelMatch = cb.like(cb.lower(root.get("name")), like);
        Predicate productMatch = cb.like(cb.lower(productJoin.get("name")), like);

        // cada palabra puede estar en model o product
        predicates.add(cb.or(modelMatch, productMatch));
      }

      // TODAS las palabras deben cumplirse
      return cb.and(predicates.toArray(new Predicate[0]));
    };

  }

  public static Specification<Model> stockBetween(Integer min, Integer max) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (min != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("totalQuantityAvailable"), min));
      }

      if (max != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("totalQuantityAvailable"), max));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<Model> entryDateBetween(LocalDate min, LocalDate max) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (min != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("entryDate"), min));
      }

      if (max != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("entryDate"), max));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<Model> hasStatus(Boolean status) {
    return (root, query, cb) -> {
      if (status == null)
        return cb.conjunction();
      return cb.equal(root.get("status"), status);
    };
  }

  public static Specification<Model> hasCategory(Long categoryId) {
    return (root, query, cb) -> {
      if (categoryId == null)
        return cb.conjunction();

      var product = getOrCreateJoin(root, "product");
      var category = getOrCreateJoin(product, "category");
      return cb.equal(category.get("id"), categoryId);
    };
  }

  public static Specification<Model> hasType(Long typeId) {
    return (root, query, cb) -> {
      if (typeId == null)
        return cb.conjunction();

      var product = getOrCreateJoin(root, "product");
      var type = getOrCreateJoin(product, "type");
      return cb.equal(type.get("id"), typeId);
    };
  }

  public static Specification<Model> isActive() {
    return (root, query, cb) -> {
      var product = getOrCreateJoin(root, "product");

      return cb.and(
          cb.isTrue(root.get("status")),
          cb.isTrue(product.get("status")));
    };
  }

  public static Specification<Model> fetchRelations() {
    return (root, query, cb) -> {
      if (query != null && !Long.class.equals(query.getResultType())) {
        var product = root.fetch("product", JoinType.LEFT);

        product.fetch("category", JoinType.LEFT);
        product.fetch("type", JoinType.LEFT);
      }

      return cb.conjunction();
    };
  }

  public static Join<?, ?> getOrCreateJoin(From<?, ?> root, String attribute) {
    return root.getJoins().stream()
        .filter(j -> j.getAttribute().getName().equals(attribute))
        .findFirst()
        .orElseGet(() -> root.join(attribute));
  }
}