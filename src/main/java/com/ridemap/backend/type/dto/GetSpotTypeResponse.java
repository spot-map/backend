package com.ridemap.backend.type.dto;

import java.util.List;

public record GetSpotTypeResponse(
    List<SpotTypeResponse> filters
) {}
