package com.pe.inventoryapp.backend.logistics.model.mapper;

import com.pe.inventoryapp.backend.logistics.model.entity.Location;
import com.pe.inventoryapp.backend.logistics.model.response.LocationResponse;

public class RegionMapper {
    private Location location;

    private RegionMapper() {

    }

    public static RegionMapper builder() {
        return new RegionMapper();
    }

    public RegionMapper setLocation(Location location) {
        this.location = location;
        return this;
    }

    public LocationResponse buildLocationResponse() {

        if (location == null) {
            throw new RuntimeException("Debe pasar la entidad Location");
        }
        return new LocationResponse(
                location.getId(),
                location.getName().trim(),
                location.getRegion().getName().trim()
        );
    }

}
