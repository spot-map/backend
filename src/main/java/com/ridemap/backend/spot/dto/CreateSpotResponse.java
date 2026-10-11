package com.ridemap.backend.spot.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateSpotResponse(
    UUID guid,
    String title,
    String description,
    String type,
    BigDecimal lat,
    BigDecimal lng,
    OffsetDateTime createdAt
){}
