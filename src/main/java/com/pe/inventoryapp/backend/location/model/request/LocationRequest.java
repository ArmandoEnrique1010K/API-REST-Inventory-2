package com.pe.inventoryapp.backend.location.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {
    private String name;
    // private String regionName;
    private Long idRegion;
}
