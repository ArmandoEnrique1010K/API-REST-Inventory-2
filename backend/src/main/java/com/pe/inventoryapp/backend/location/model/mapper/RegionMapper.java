package com.pe.inventoryapp.backend.location.model.mapper;

import com.pe.inventoryapp.backend.location.model.entity.Region;
import com.pe.inventoryapp.backend.location.model.response.RegionResponse;

public class RegionMapper {
    private Region region;

    private RegionMapper() {

    }

    public static RegionMapper builder() {
        return new RegionMapper();
    }

    public RegionMapper setRegion(Region region) {
        this.region = region;
        return this;
    }

    public RegionResponse buildRegionResponse() {

        if (region == null) {
            throw new RuntimeException("Debe pasar la entidad region");
        }
        return new RegionResponse(
                region.getId(),
                region.getName().trim());
    }

}
