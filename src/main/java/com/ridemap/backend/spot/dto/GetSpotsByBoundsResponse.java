package com.ridemap.backend.spot.dto;

import java.util.List;

public record GetSpotsByBoundsResponse(
    List<SpotListItem> spots
) {}
