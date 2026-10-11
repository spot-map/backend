package com.ridemap.backend.spot.dto;

import java.util.List;

public record SpotSearchResponse(
    List<SpotListItem> spots,
    int countAll
) {
}