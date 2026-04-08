package com.pe.inventoryapp.backend.stocklot.repository.specifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;

import jakarta.persistence.criteria.Predicate;

public class StockLotSpecifications {

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

      var model = root.join("model");

      var product = model.join("product");
      return cb.equal(product.get("category").get("id"), categoryId);
    };
  }

  public static Specification<StockLot> hasType(Long typeId) {
    return (root, query, cb) -> {
      if (typeId == null)
        return cb.conjunction();
      var model = root.join("model");

      var product = model.join("product");
      return cb.equal(product.get("type").get("id"), typeId);
    };
  }

  public static Specification<StockLot> hasModel(Long modelId) {
    return (root, query, cb) -> {
      if (modelId == null)
        return cb.conjunction();

      var model = root.join("model");
      return cb.equal(model.get("id"), modelId);
    };
  }

  public static Specification<StockLot> isNotZeroStock() {
    return (root, query, cb) -> cb.isFalse(root.get("zeroStock"));
  }
}
