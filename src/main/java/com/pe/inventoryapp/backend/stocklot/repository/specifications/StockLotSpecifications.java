package com.pe.inventoryapp.backend.stocklot.repository.specifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class StockLotSpecifications {

  //TODO: AGREGAR COMENTARIOS EXPLICATIVOS SOBRE EL CONFLICTO DEL USO DE PARAMETROS OPCIONALES QUE SE RELACIONAN: CATEGORY Y TYPE
  public static Specification<StockLot> quantityReceivedBeetween(Integer min, Integer max) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (min != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("quantityReceived"), min));
      }

      if (max != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("quantityReceived"), max));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<StockLot> quantityAvailableBeetween(Integer min, Integer max) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (min != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("quantityAvailable"), min));
      }

      if (max != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("quantityAvailable"), max));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<StockLot> createdAtBetween(LocalDateTime min, LocalDateTime max) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (min != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), min));
      }

      if (max != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), max));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<StockLot> keywordContains(String keyword) {
    return (root, query, cb) -> {

      if (keyword == null || keyword.trim().isEmpty()) {
        return cb.conjunction();
      }

      String[] words = keyword.toLowerCase().split("\\s+");

      var modelJoin = root.join("model");
      var productJoin = modelJoin.join("product");

      List<Predicate> predicates = new ArrayList<>();

      for (String word : words) {
        String like = "%" + word + "%";

        Predicate modelMatch = cb.like(cb.lower(modelJoin.get("name")), like);
        Predicate productMatch = cb.like(cb.lower(productJoin.get("name")), like);

        predicates.add(cb.or(modelMatch, productMatch));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

  }

  public static Specification<StockLot> hasCompany(Long companyId) {
    return (root, query, cb) -> {
      if (companyId == null)
        return cb.conjunction();

      var company = root.join("company");
      return cb.equal(company.get("id"), companyId);
    };
  }

  public static Specification<StockLot> hasCategory(Long categoryId) {
    return (root, query, cb) -> {
      if (categoryId == null)
        return cb.conjunction();

      var model = getOrCreateJoin(root, "model");

      var product = getOrCreateJoin(model, "product");
      return cb.equal(product.join("category").get("id"), categoryId);
    };
  }

  public static Specification<StockLot> hasType(Long typeId) {
    return (root, query, cb) -> {
      if (typeId == null)
        return cb.conjunction();
      var model = getOrCreateJoin(root, "model");

      var product = getOrCreateJoin(model, "product");
      return cb.equal(product.join("type").get("id"), typeId);
    };
  }

  public static Specification<StockLot> hasModel(Long modelId) {
    return (root, query, cb) -> {
      if (modelId == null)
        return cb.conjunction();

      var model = getOrCreateJoin(root, "model");
      return cb.equal(model.get("id"), modelId);
    };
  }

  public static Specification<StockLot> isNotZeroStock() {
    return (root, query, cb) -> cb.isFalse(root.get("zeroStock"));
  }

  // * IMPORTANTE, ESTE QUERY SIRVE PARA FORZAR JOIN EN EL SPECIFICATION,
  // AUNQUE NO SE FILTRE SE TIENE QUE RELACIONAR HACIA UN TIPO Y CATEGORIA
  public static Specification<StockLot> fetchRelations() {
    return (root, query, cb) -> {

      // IMPORTANTE: solo en query principal (no count)
      // if (query != null && query.getResultType() != Long.class) {
      //   var model = root.join("model", JoinType.LEFT);
      //   var product = model.join("product", JoinType.LEFT);
      //   product.join("category", JoinType.LEFT);
      //   product.join("type", JoinType.LEFT);
      //   root.join("company", JoinType.LEFT);
      // }
      if (query.getResultType() != Long.class) {

        root.fetch("company", JoinType.LEFT);

        var model = root.fetch("model", JoinType.LEFT);
        var product = ((Fetch<?, ?>) model).fetch("product", JoinType.LEFT);

        product.fetch("category", JoinType.LEFT);
        product.fetch("type", JoinType.LEFT);
      }

      return cb.conjunction();
    };
  }

  // REUTILIZA JOINS
  public static Join<?, ?> getOrCreateJoin(From<?, ?> root, String attribute) {
    return root.getJoins().stream()
        .filter(j -> j.getAttribute().getName().equals(attribute))
        .findFirst()
        .orElseGet(() -> root.join(attribute));
  }
}
