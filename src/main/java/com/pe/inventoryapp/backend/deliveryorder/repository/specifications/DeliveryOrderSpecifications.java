package com.pe.inventoryapp.backend.deliveryorder.repository.specifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;

import jakarta.persistence.criteria.Predicate;

public class DeliveryOrderSpecifications {

  // 🔎 Buscar por nombre del cliente (firstname o lastname)
  public static Specification<DeliveryOrder> userClientNameContains(String name) {
    return (root, query, cb) -> {

      if (name == null || name.trim().isEmpty()) {
        return cb.conjunction();
      }

        String[] words = name.toLowerCase().split("\\s+");

        var userJoin = root.join("userClient");

        List<Predicate> predicates = new ArrayList<>();

        for (String word : words) {
            String like = "%" + word + "%";

            Predicate firstname = cb.like(cb.lower(userJoin.get("firstname")), like);
            Predicate lastname = cb.like(cb.lower(userJoin.get("lastname")), like);

            // cada palabra puede estar en firstname o lastname
            predicates.add(cb.or(firstname, lastname));
        }

        // TODAS las palabras deben cumplirse
        return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  // 🔎 Buscar por batch
  public static Specification<DeliveryOrder> batchContains(String batch) {
    return (root, query, cb) -> {

      if (batch == null || batch.trim().isEmpty()) {
        return cb.conjunction();
      }

      return cb.like(root.get("batch"), "%" + batch + "%");
    };
  }

  // 📅 Rango de fechas (priorityDate)
  public static Specification<DeliveryOrder> priorityDateBetween(
      LocalDateTime start,
      LocalDateTime end) {
    return (root, query, cb) -> {

      var predicates = cb.conjunction();

      if (start != null) {
        predicates = cb.and(predicates,
            cb.greaterThanOrEqualTo(root.get("priorityDate"), start));
      }

      if (end != null) {
        predicates = cb.and(predicates,
            cb.lessThanOrEqualTo(root.get("priorityDate"), end));
      }

      return predicates;
    };
  }

  // 🎯 Filtrar por status
  public static Specification<DeliveryOrder> hasStatus(OrderStatus status) {
    return (root, query, cb) -> {
      if (status == null)
        return cb.conjunction();
      return cb.equal(root.get("orderStatus"), status);
    };
  }

  // ❌ Excluir cancelados
  public static Specification<DeliveryOrder> isNotCanceled() {
    return (root, query, cb) -> cb.notEqual(root.get("orderStatus"), OrderStatus.ORDER_CANCELED);
  }

  // 🔥 Estados activos (READY + PENDING)
  public static Specification<DeliveryOrder> isActiveForOperator() {
    return (root, query, cb) -> cb.or(
        cb.equal(root.get("orderStatus"), OrderStatus.ORDER_READY),
        cb.equal(root.get("orderStatus"), OrderStatus.ORDER_PENDING));
  }

  // 👤 Filtrar por usuario cliente
  public static Specification<DeliveryOrder> hasUserClient(Long userId) {
    return (root, query, cb) -> {
      if (userId == null)
        return cb.conjunction();
      return cb.equal(root.get("userClient").get("id"), userId);
    };
  }
}