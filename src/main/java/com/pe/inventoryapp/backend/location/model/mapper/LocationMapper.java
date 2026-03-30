package com.pe.inventoryapp.backend.location.model.mapper;

import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.model.response.LocationResponse;
import com.pe.inventoryapp.backend.location.model.response.SearchLocationResponse;

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
                location.getName(),
                location.getAddress(),
                location.isStatus(),
                location.getSubregion().getId(),
                location.getSubregion().getName(),
                location.getSubregion().getRegion().getId());
    }

    public SearchLocationResponse buildSearchLocationResponse() {
        if (location == null) {
            throw new RuntimeException("Debe pasar la entidad Location");
        }
        return new SearchLocationResponse(
                location.getId(),
                location.getName());
    }
}
