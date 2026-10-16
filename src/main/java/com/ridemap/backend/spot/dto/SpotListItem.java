package com.ridemap.backend.spot.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.ridemap.backend.image.dto.SpotImageResponse;

public record SpotListItem(
    UUID guid,
    String title,
    BigDecimal lat,
    BigDecimal lng,
    String type,
    List<SpotImageResponse> images
) {
    public SpotListItem(
        UUID guid,
        String title,
        BigDecimal lat,
        BigDecimal lng,
        String type
    ) {
        this(guid, title, lat, lng, type, List.of());
    }
}