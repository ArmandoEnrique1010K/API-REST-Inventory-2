package com.pe.inventoryapp.backend.summary.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.summary.model.dto.SummaryDTO;
import com.pe.inventoryapp.backend.summary.model.response.ModelProductSummaryDTO;
import com.pe.inventoryapp.backend.summary.model.response.RegionSummaryDTO;
import com.pe.inventoryapp.backend.summary.model.response.SubregionSummaryDTO;
import com.pe.inventoryapp.backend.summary.repository.SummaryRepository;

@Service
public class SummaryServiceImpl implements SummaryService {
    private final SummaryRepository summaryRepository;

    public SummaryServiceImpl(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    @Override
    public List<RegionSummaryDTO> buildSummary(Long deliveryOrderId) {
        List<SummaryDTO> rows = summaryRepository.summaryBySubregion(deliveryOrderId);

        Map<Long, RegionSummaryDTO> regionMap = new LinkedHashMap<>();

        for (var row : rows) {

            // ===== REGION =====
            regionMap.putIfAbsent(row.getRegionId(),
                    new RegionSummaryDTO(
                            row.getRegionId(),
                            row.getRegionName(),
                            0L,
                            new ArrayList<>()));

            RegionSummaryDTO region = regionMap.get(row.getRegionId());

            // ===== SUBREGION =====
            SubregionSummaryDTO subregion = region.getSubregions()
                    .stream()
                    .filter(s -> s.getSubregionId().equals(row.getSubregionId()))
                    .findFirst()
                    .orElseGet(() -> {
                        SubregionSummaryDTO newSub = new SubregionSummaryDTO(
                                row.getSubregionId(),
                                row.getSubregionName(),
                                0L,
                                new ArrayList<>());
                        region.getSubregions().add(newSub);
                        return newSub;
                    });

            // ===== MODEL / PRODUCT =====
            subregion.getItems().add(
                    new ModelProductSummaryDTO(
                            row.getModelId(),
                            row.getModelName(),
                            row.getProductId(),
                            row.getProductName(),
                            row.getTotalQuantity()));

            // ===== SUMAS =====
            subregion.setTotalQuantity(
                    subregion.getTotalQuantity() + row.getTotalQuantity());

            region.setTotalQuantity(
                    region.getTotalQuantity() + row.getTotalQuantity());
        }

        return new ArrayList<>(regionMap.values());
    }
}
