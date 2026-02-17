package com.pe.inventoryapp.backend.summary.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Subregion;
import com.pe.inventoryapp.backend.summary.repository.Model_DeliveryOrder_SubregionRepository;

@Service
public class Model_DeliveryOrder_SubregionDomainService {
	private final Model_DeliveryOrder_SubregionRepository model_DeliveryOrder_SubregionRepository;
	private final DeliveryLineRepository deliveryLineRepository;

	public Model_DeliveryOrder_SubregionDomainService(
			Model_DeliveryOrder_SubregionRepository model_DeliveryOrder_SubregionRepository,
			DeliveryLineRepository deliveryLineRepository) {
		this.model_DeliveryOrder_SubregionRepository = model_DeliveryOrder_SubregionRepository;
		this.deliveryLineRepository = deliveryLineRepository;
	}
	@Transactional
	public void recalculateSummatoryModel_DeliveryOrderSubregionsByDeliveryOrder(Long model_DeliveryOrderId) {
		List<Model_DeliveryOrder_Subregion> subregions = model_DeliveryOrder_SubregionRepository
				.findAllModel_DeliveryOrder_SubregionsByModel_DeliveryOrderId(model_DeliveryOrderId);
		
		if (subregions.isEmpty()) {
			throw new BusinessException(
					ResponseStatus.INTERNAL_SERVER_ERROR,
					"No se encontraron subregiones para el model_delivery_order");
		}

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
