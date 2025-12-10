package com.pe.inventoryapp.backend.organization.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDetailsResponse {
    private Long id;
    private String name;
    private String regionName;
    private boolean status;
}
