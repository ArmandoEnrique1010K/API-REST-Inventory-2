package com.pe.inventoryapp.backend.organization.model.mapper;

import com.pe.inventoryapp.backend.organization.model.entity.Location;
import com.pe.inventoryapp.backend.organization.model.response.LocationDetailsResponse;
import com.pe.inventoryapp.backend.organization.model.response.LocationListResponse;

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

    public LocationListResponse buildLocationListResponse() {
        if (location == null) {
            throw new RuntimeException("Debe pasar la entidad Location");
        } else {
            return new LocationListResponse(
                    location.getId(),
                    location.getName().trim(),
                    location.isStatus());
        }

    }

    public LocationDetailsResponse buildLocationDetailsResponse() {

        if (location == null) {
            throw new RuntimeException("Debe pasar la entidad Location");
        }
        return new LocationDetailsResponse(
                location.getId(),
                location.getName().trim(),
                location.getRegion().getName().trim(),
                location.isStatus());
    }

}
