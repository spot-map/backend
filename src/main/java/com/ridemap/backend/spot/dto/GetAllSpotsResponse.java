package com.ridemap.backend.spot.dto;

import java.util.List;

public record GetAllSpotsResponse(
    int totalItems,
    int page,
    int pageSize,
    int totalPages,
    List<SpotListItem> items
){}
