package com.pe.inventoryapp.backend.deliveryline.repository.specifications;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;

public class DeliveryLineSpecifications {

  /**
   * Obtiene un JOIN existente si ya fue creado previamente en la query,
   * o lo crea si aún no existe.
   *
   * ¿Por qué es necesario?
   * Cuando se usan múltiples Specifications que hacen join sobre el mismo
   * atributo
   * (por ejemplo "location"), JPA puede generar múltiples JOIN redundantes en el
   * SQL.
   *
   * Este método evita eso reutilizando el mismo JOIN si ya fue registrado en el
   * root
   * o en un join anterior.
   *
   * ¿Cómo funciona?
   * - Busca en los joins existentes (`from.getJoins()`)
   * - Filtra por el nombre del atributo (ej: "location", "subregion", etc.)
   * - Si lo encuentra → lo reutiliza
   * - Si no → crea un nuevo join
   *
   * Ventajas:
   * ✔ Evita joins duplicados
   * ✔ Mejora rendimiento en queries complejas
   * ✔ Hace el código más limpio y consistente
   *
   * Importante:
   * - Funciona tanto con root como con joins encadenados
   * (ej: root → location → subregion → region)
   * - No afecta el resultado funcional, solo optimiza la query generada
   *
   * Ejemplo de uso:
   * var location = getOrCreateJoin(root, "location");
   * var subregion = getOrCreateJoin(location, "subregion");
   *
   */
  private static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {
    return from.getJoins().stream()
        .filter(j -> j.getAttribute().getName().equals(attribute))
        .findFirst()
        .orElseGet(() -> from.join(attribute));
  }

  // 📌 Filtrar por deliveryOrderId
  public static Specification<DeliveryLine> hasDeliveryOrder(Long orderId) {
    return (root, query, cb) -> {
      if (orderId == null)
        return cb.conjunction();
      return cb.equal(root.get("deliveryOrder").get("id"), orderId);
    };
  }

  // ❌ Excluir líneas canceladas
  public static Specification<DeliveryLine> isNotCanceled() {
    return (root, query, cb) -> cb.notEqual(root.get("lineStatus"), LineStatus.LINE_CANCELED);
  }

  // 📦 Cantidad requerida
  public static Specification<DeliveryLine> requiredQuantityBetween(
      Integer min,
      Integer max) {
    return (root, query, cb) -> {

      var predicate = cb.conjunction();

      if (min != null) {
        predicate = cb.and(predicate,
            cb.greaterThanOrEqualTo(root.get("requiredQuantity"), min));
      }

      if (max != null) {
        predicate = cb.and(predicate,
            cb.lessThanOrEqualTo(root.get("requiredQuantity"), max));
      }

      return predicate;
    };
  }

  // 📅 Fecha límite
  public static Specification<DeliveryLine> limitDateBetween(
      LocalDateTime min,
      LocalDateTime max) {
    return (root, query, cb) -> {

      var predicate = cb.conjunction();

      if (min != null) {
        predicate = cb.and(predicate,
            cb.greaterThanOrEqualTo(root.get("limitDate"), min));
      }

      if (max != null) {
        predicate = cb.and(predicate,
            cb.lessThanOrEqualTo(root.get("limitDate"), max));
      }

      return predicate;
    };
  }

  // 🎯 Estado de línea
  public static Specification<DeliveryLine> hasLineStatus(LineStatus status) {
    return (root, query, cb) -> {
      if (status == null)
        return cb.conjunction();
      return cb.equal(root.get("lineStatus"), status);
    };
  }

  // 📍 Buscar por nombre de location
  public static Specification<DeliveryLine> locationContains(String location) {
    return (root, query, cb) -> {

      if (location == null || location.trim().isEmpty()) {
        return cb.conjunction();
      }

      String like = "%" + location.toLowerCase() + "%";

      // var locationJoin = root.join("location");
      var locationJoin = getOrCreateJoin(root, "location");

      return cb.like(cb.lower(locationJoin.get("name")), like);
    };
  }

  // 🌎 Subregion
  public static Specification<DeliveryLine> hasSubregion(Long subregionId) {
    return (root, query, cb) -> {
      if (subregionId == null)
        return cb.conjunction();

      // var subregion = root.join("location").join("subregion");
      var location = getOrCreateJoin(root, "location");
      var subregion = getOrCreateJoin(location, "subregion");

      return cb.equal(subregion.get("id"), subregionId);
    };
  }

  // 🌍 Region
  public static Specification<DeliveryLine> hasRegion(Long regionId) {
    return (root, query, cb) -> {
      if (regionId == null)
        return cb.conjunction();

      // var region = root.join("location")
      // .join("subregion")
      // .join("region");
      var location = getOrCreateJoin(root, "location");
      var subregion = getOrCreateJoin(location, "subregion");
      var region = getOrCreateJoin(subregion, "region");

      return cb.equal(region.get("id"), regionId);
    };
  }

  // 🧩 Model
  public static Specification<DeliveryLine> hasModel(Long modelId) {
    return (root, query, cb) -> {
      if (modelId == null)
        return cb.conjunction();

      return cb.equal(root.get("model").get("id"), modelId);
    };
  }

  // ⚡ FETCH (equivalente a JOIN FETCH)
  //* AQUI HACE UN FETCH HACIA LA RELACION DE REGION
  public static Specification<DeliveryLine> fetchAllRelations() {
    return (root, query, cb) -> {

      if (query != null) {
        root.fetch("model");
        var location = root.fetch("location");
        var subregion = location.fetch("subregion");

        subregion.fetch("region");

        query.distinct(true);
      }

      return cb.conjunction();
    };
  }
}