package com.pe.inventoryapp.backend.summary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.summary.model.response.DeliveryOrderSummaryResponse;
import com.pe.inventoryapp.backend.summary.model.response.LocationSummaryResponse;
import com.pe.inventoryapp.backend.summary.model.response.ProductSummaryResponse;
import com.pe.inventoryapp.backend.summary.model.response.RegionSummaryResponse;
import com.pe.inventoryapp.backend.summary.model.response.SubregionSummaryResponse;

@Service
public class DeliveryOrderSummaryDomainService {
    private final DeliveryLineRepository deliveryLineRepository;

    public DeliveryOrderSummaryDomainService(DeliveryLineRepository deliveryLineRepository) {
        this.deliveryLineRepository = deliveryLineRepository;
    }

    public DeliveryOrderSummaryResponse getSummary(Long orderId) {

        List<DeliveryLine> lines = deliveryLineRepository.findAllByDeliveryOrderId(orderId);

        Map<Long, RegionSummaryResponse> regionMap = new HashMap<>();

        for (DeliveryLine line : lines) {

            var location = line.getLocation();
            var subregion = location.getSubregion();
            var region = subregion.getRegion();
            var product = line.getModel();

            int quantity = line.getRequiredQuantity();

            // ===== REGION =====
            RegionSummaryResponse regionDTO = regionMap.computeIfAbsent(
                    region.getId(),
                    id -> {
                        RegionSummaryResponse r = new RegionSummaryResponse();
                        r.setId(region.getId());
                        r.setName(region.getName());
                        r.setTotalQuantity(0);
                        r.setSubregions(new ArrayList<>());
                        return r;
                    });

            regionDTO.setTotalQuantity(regionDTO.getTotalQuantity() + quantity);

            // ===== SUBREGION =====
            SubregionSummaryResponse subregionDTO = regionDTO.getSubregions()
                    .stream()
                    .filter(sr -> sr.getId().equals(subregion.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        SubregionSummaryResponse sr = new SubregionSummaryResponse();
                        sr.setId(subregion.getId());
                        sr.setName(subregion.getName());
                        sr.setTotalQuantity(0);
                        sr.setLocations(new ArrayList<>());
                        regionDTO.getSubregions().add(sr);
                        return sr;
                    });

            subregionDTO.setTotalQuantity(subregionDTO.getTotalQuantity() + quantity);

            // ===== LOCATION =====
            LocationSummaryResponse locationDTO = subregionDTO.getLocations()
                    .stream()
                    .filter(l -> l.getId().equals(location.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        LocationSummaryResponse l = new LocationSummaryResponse();
                        l.setId(location.getId());
                        l.setName(location.getName());
                        l.setTotalQuantity(0);
                        l.setProducts(new ArrayList<>());
                        subregionDTO.getLocations().add(l);
                        return l;
                    });

            locationDTO.setTotalQuantity(locationDTO.getTotalQuantity() + quantity);

            // ===== PRODUCT =====
            ProductSummaryResponse productDTO = locationDTO.getProducts()
                    .stream()
                    .filter(p -> p.getProductModelId().equals(product.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        ProductSummaryResponse p = new ProductSummaryResponse();
                        p.setProductModelId(product.getId());
                        p.setProductModelName(product.getName());
                        p.setQuantity(0);
                        locationDTO.getProducts().add(p);
                        return p;
                    });

            productDTO.setQuantity(productDTO.getQuantity() + quantity);
        }

        DeliveryOrderSummaryResponse response = new DeliveryOrderSummaryResponse();
        response.setDeliveryOrderId(orderId);
        response.setRegions(new ArrayList<>(regionMap.values()));

        return response;    
    }
}
