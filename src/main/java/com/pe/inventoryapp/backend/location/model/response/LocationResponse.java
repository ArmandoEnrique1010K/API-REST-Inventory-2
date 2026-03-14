package com.pe.inventoryapp.backend.location.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String name;
    private String address; 
    private boolean status;

    private Long subregionId;
    private String subregionName;

    private Long regionId;
}
