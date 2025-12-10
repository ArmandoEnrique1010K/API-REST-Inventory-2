package com.pe.inventoryapp.backend.organization.model.mapper;

import com.pe.inventoryapp.backend.organization.model.entity.Location;
import com.pe.inventoryapp.backend.organization.model.response.LocationResponse;

public class LocationMapper {
    private Location location;

    private LocationMapper() {

    }

    public static LocationMapper builder() {
        return new LocationMapper();
    }

    public LocationMapper setLocation(Location location) {
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
                location.getRegion().getName().trim());
    }

}
