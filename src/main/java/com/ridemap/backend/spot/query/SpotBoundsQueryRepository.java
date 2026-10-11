package com.ridemap.backend.spot.query;

import java.math.BigDecimal;
import java.util.List;
import com.ridemap.backend.spot.dto.SpotListItem;

public interface SpotBoundsQueryRepository {
    List<SpotListItem> findWithinBounds(
        BigDecimal latMin,
        BigDecimal latMax,
        BigDecimal lngMin,
        BigDecimal lngMax
    );
}
