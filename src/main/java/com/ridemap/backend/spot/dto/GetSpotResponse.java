package com.ridemap.backend.spot.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GetSpotResponse(
    UUID guid,
    String title,
    String description,
    BigDecimal lat,
    BigDecimal lng,
    String type
) {}
