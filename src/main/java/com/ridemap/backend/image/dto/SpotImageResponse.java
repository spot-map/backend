package com.ridemap.backend.image.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SpotImageResponse(
    UUID guid,
    UUID spotGuid,
    String contentType,
    long size,
    OffsetDateTime createdAt,
    String url
) {}
