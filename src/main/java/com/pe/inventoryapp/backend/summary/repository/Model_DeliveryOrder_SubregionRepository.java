package com.pe.inventoryapp.backend.summary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Subregion;

public interface Model_DeliveryOrder_SubregionRepository extends JpaRepository<Model_DeliveryOrder_Subregion, Long>{
   @Query("""
         SELECT mds
         FROM Model_DeliveryOrder_Subregion mds
         WHERE mds.model_DeliveryOrder.id = :model_DeliveryOrderId
         """)
   List<Model_DeliveryOrder_Subregion> findAllModel_DeliveryOrder_SubregionsByModel_DeliveryOrderId(Long model_DeliveryOrderId);
}
