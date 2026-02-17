package com.pe.inventoryapp.backend.summary.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Subregion;
import com.pe.inventoryapp.backend.summary.repository.Model_DeliveryOrder_RegionRepository;
import com.pe.inventoryapp.backend.summary.repository.Model_DeliveryOrder_SubregionRepository;

@Service
public class Model_DeliveryOrder_RegionDomainService {
	private final Model_DeliveryOrder_RegionRepository model_DeliveryOrder_RegionRepository;
	private final Model_DeliveryOrder_SubregionRepository model_DeliveryOrder_SubregionRepository;
	private final DeliveryLineRepository deliveryLineRepository;


	public Model_DeliveryOrder_RegionDomainService(
			Model_DeliveryOrder_RegionRepository model_DeliveryOrder_RegionRepository,
			Model_DeliveryOrder_SubregionRepository model_DeliveryOrder_SubregionRepository,
			DeliveryLineRepository deliveryLineRepository) {
		this.model_DeliveryOrder_RegionRepository = model_DeliveryOrder_RegionRepository;
		this.model_DeliveryOrder_SubregionRepository = model_DeliveryOrder_SubregionRepository;
		this.deliveryLineRepository = deliveryLineRepository;
	}

	// MÉTODO PARA RECALCULAR LAS CANTIDADES REQUERIDAS DE LAS REGIONES DE UNA ORDEN
	// DE ENTREGA, ESTE MÉTODO SE DEBE LLAMAR CUANDO SE CREA O SE ACTUALIZA UNA
	// LÍNEA DE ENTREGA, YA QUE LA CANTIDAD REQUERIDA DE UNA REGIÓN DEPENDE DE LAS
	// LÍNEAS DE ENTREGA QUE PERTENECEN A ESA ORDEN DE ENTREGA Y A ESA REGIÓN

	// @Transactional
	// public void recalculateSummatoryModel_DeliveryOrderRegions(Long
	// model_DeliveryOrderId) {
	// // Obtiene la lista de los resumenes de las sumatorias calculadas por modelo,
	// orden de entrega y region
	// List<Model_DeliveryOrder_Region> model_DeliveryOrder_Regions =
	// model_DeliveryOrder_RegionRepository
	// .findAllByModel_DeliveryOrderId(model_DeliveryOrderId);

	// for (Model_DeliveryOrder_Region model_DeliveryOrder_Region :
	// model_DeliveryOrder_Regions) {

	// // Solamente hay un campo para la cantidad total requerida
	// Integer requiredTotal =
	// deliveryLineRepository.sumRequiredByModelDeliveryOrderAndRegion(
	// model_DeliveryOrderId,
	// model_DeliveryOrder_Region.getRegion().getId());

	// model_DeliveryOrder_Region.setRequiredTotalQuantity(requiredTotal);
	// }

	// if (model_DeliveryOrder_Regions.isEmpty()) {
	// throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR,
	// "No se encontraron regiones para el model_delivery_order");
	// }

	// model_DeliveryOrder_RegionRepository.saveAll(model_DeliveryOrder_Regions);
	// }

	// Este método devuelve:
	// Object[0] → regionId
	// Object[1] → requiredTotal
	@Transactional
	public void recalculateSummatoryModel_DeliveryOrderRegionsByDeliveryOrder(Long model_DeliveryOrderId) {
		List<Model_DeliveryOrder_Region> regions = model_DeliveryOrder_RegionRepository
				.findAllModel_DeliveryOrder_RegionsByModel_DeliveryOrderId(model_DeliveryOrderId);
		
		if (regions.isEmpty()) {
			throw new BusinessException(
					ResponseStatus.INTERNAL_SERVER_ERROR,
					"No se encontraron regiones para el model_delivery_order");
		}

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
