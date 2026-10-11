package com.ridemap.backend.spot.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SpotListItem(
    UUID guid,
    String title,
    BigDecimal lat,
    BigDecimal lng,
    String type
){}
