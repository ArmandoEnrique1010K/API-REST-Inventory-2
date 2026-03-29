package com.pe.inventoryapp.backend.summary.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.repository.Model_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Subregion;
import com.pe.inventoryapp.backend.summary.repository.Model_DeliveryOrder_SubregionRepository;

@Service
public class Model_DeliveryOrder_SubregionDomainService {
	private final Model_DeliveryOrder_SubregionRepository model_DeliveryOrder_SubregionRepository;
	private final DeliveryLineRepository deliveryLineRepository;
	private final Model_DeliveryOrderRepository model_DeliveryOrderRepository;

	public Model_DeliveryOrder_SubregionDomainService(
			Model_DeliveryOrder_SubregionRepository model_DeliveryOrder_SubregionRepository,
			DeliveryLineRepository deliveryLineRepository,
			Model_DeliveryOrderRepository model_DeliveryOrderRepository) {
		this.model_DeliveryOrder_SubregionRepository = model_DeliveryOrder_SubregionRepository;
		this.deliveryLineRepository = deliveryLineRepository;
		this.model_DeliveryOrderRepository = model_DeliveryOrderRepository;
	}
	@Transactional
	public void recalculateSummatoryModel_DeliveryOrderSubregionsByDeliveryOrder(Long deliveryOrderId, Long modelId) {

		// Buscar la relación entre orden de entrega y modelo
		    Model_DeliveryOrder modelDeliveryOrder = model_DeliveryOrderRepository
        .findByModelIdAndDeliveryOrderId(
								modelId, deliveryOrderId)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.CONFLICT,
            "La relación entre orden de entrega y modelo no existe"));

					Long model_DeliveryOrderId = modelDeliveryOrder.getId();

		List<Model_DeliveryOrder_Subregion> subregions = model_DeliveryOrder_SubregionRepository
				.findAllModel_DeliveryOrder_SubregionsByModel_DeliveryOrderId(model_DeliveryOrderId);
		
		// TODO: MANEJAR LA RELACION EN LA SUMATORIA POR SUBREGIONES
		// if (subregions.isEmpty()) {
		// 	throw new BusinessException(
		// 			ResponseStatus.INTERNAL_SERVER_ERROR,
		// 			"No se encontraron subregiones para el model_delivery_order");
		// }

		Map<Long, Integer> totalsBySubregion = deliveryLineRepository
				.sumRequiredGroupedBySubregion(model_DeliveryOrderId)
				.stream()
				.collect(Collectors.toMap(
						row -> (Long) row[0],
						row -> ((Number) row[1]).intValue()));

		for (Model_DeliveryOrder_Subregion mds : subregions) {
			Integer total = totalsBySubregion.getOrDefault(
					mds.getSubregion().getId(),
					0);
			mds.setRequiredTotalQuantity(total);
		}
		model_DeliveryOrder_SubregionRepository.saveAll(subregions);
	}

}
