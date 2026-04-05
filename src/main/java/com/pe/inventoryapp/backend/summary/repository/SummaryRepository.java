package com.pe.inventoryapp.backend.summary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.summary.model.dto.SummaryDTO;

@Repository
public interface SummaryRepository extends JpaRepository<DeliveryLine, Long> {

    // REGION - MODEL - PRODUCT - TOTALQUANTITY
    // JPQL necesita el nombre completo del paquete.
    // Coloca la ubicación del DTO dentro del @Query, para devolver un DTO en lugar del clasico Entity
    @Query("""
                SELECT new com.pe.inventoryapp.backend.summary.model.dto.SummaryDTO(
                    d.location.subregion.id,
                    d.location.subregion.name,
                    d.location.subregion.region.id,
                    d.location.subregion.region.name,
                    d.model.id,
                    d.model.name,
                    d.model.product.id,
                    d.model.product.name,
                    COALESCE(SUM(d.requiredQuantity), 0)
                )
                FROM DeliveryLine d
                WHERE d.deliveryOrder.id = :deliveryOrderId
                  AND d.lineStatus <> com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus.LINE_CANCELED
                GROUP BY
                    d.location.subregion.id,
                    d.location.subregion.name,
                    d.location.subregion.region.id,
                    d.location.subregion.region.name,
                    d.model.id,
                    d.model.name,
                    d.model.product.id,
                    d.model.product.name
            """)
    List<SummaryDTO> summaryBySubregion(Long deliveryOrderId);

}
