package com.pe.inventoryapp.backend.summary.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.repository.Model_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.summary.repository.Model_DeliveryOrder_RegionRepository;

@Service
public class Model_DeliveryOrder_RegionDomainService {
	private final Model_DeliveryOrder_RegionRepository model_DeliveryOrder_RegionRepository;
	private final DeliveryLineRepository deliveryLineRepository;
	private final Model_DeliveryOrderRepository model_DeliveryOrderRepository;


	public Model_DeliveryOrder_RegionDomainService(
			Model_DeliveryOrder_RegionRepository model_DeliveryOrder_RegionRepository,
			DeliveryLineRepository deliveryLineRepository,
			Model_DeliveryOrderRepository model_DeliveryOrderRepository) {
		this.model_DeliveryOrder_RegionRepository = model_DeliveryOrder_RegionRepository;
		this.deliveryLineRepository = deliveryLineRepository;
		this.model_DeliveryOrderRepository = model_DeliveryOrderRepository;
	}

	public void recalculateSummatoryModel_DeliveryOrderRegionsByDeliveryOrder(Long deliveryOrderId, Long modelId) {

		// Buscar la relación entre orden de entrega y modelo
		    Model_DeliveryOrder modelDeliveryOrder = model_DeliveryOrderRepository
        .findByModelIdAndDeliveryOrderId(
								modelId, deliveryOrderId)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.CONFLICT,
            "La relación entre orden de entrega y modelo no existe"));

		Long model_DeliveryOrderId = modelDeliveryOrder.getId();

		List<Model_DeliveryOrder_Region> regions = model_DeliveryOrder_RegionRepository
				.findAllModel_DeliveryOrder_RegionsByModel_DeliveryOrderId(model_DeliveryOrderId);
		
				// TODO: MANEJAR LA RELACION EN LA SUMATORIA POR REGIONES
		// if (regions.isEmpty()) {
		// 	throw new BusinessException(
		// 			ResponseStatus.INTERNAL_SERVER_ERROR,
		// 			"No se encontraron regiones para el model_delivery_order");
		// }

		Map<Long, Integer> totalsByRegion = deliveryLineRepository
				.sumRequiredGroupedByRegion(model_DeliveryOrderId)
				.stream()
				.collect(Collectors.toMap(
						row -> (Long) row[0],
						row -> ((Number) row[1]).intValue()));

		for (Model_DeliveryOrder_Region mdr : regions) {
			Integer total = totalsByRegion.getOrDefault(
					mdr.getRegion().getId(),
					0);
			mdr.setRequiredTotalQuantity(total);
		}

		model_DeliveryOrder_RegionRepository.saveAll(regions);
	}

}
