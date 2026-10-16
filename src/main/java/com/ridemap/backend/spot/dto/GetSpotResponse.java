package com.ridemap.backend.spot.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.ridemap.backend.image.dto.SpotImageResponse;

public record GetSpotResponse(
    UUID guid,
    String title,
    String description,
    BigDecimal lat,
    BigDecimal lng,
    String type,
    List<SpotImageResponse> images
) {}
