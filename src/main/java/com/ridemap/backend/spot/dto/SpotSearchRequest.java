package com.ridemap.backend.spot.dto;

public record SpotSearchRequest(
    String search,
    Integer type
) {}
