package com.ridemap.backend.spot.query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ridemap.backend.spot.dto.SpotListItem;

@Repository
public class SpotBoundsQueryRepositoryImpl implements SpotBoundsQueryRepository {
    @PersistenceContext
    private EntityManager manager;

    @Override
    @Transactional(readOnly = true)
    public List<SpotListItem> findWithinBounds(
        BigDecimal latMin,
        BigDecimal latMax,
        BigDecimal lngMin,
        BigDecimal lngMax
    ) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = manager
            .createNativeQuery("""
                SELECT
                    s.guid,
                    s.title,
                    s.lat,
                    s.lng,
                    t.name AS type
                FROM spots s
                JOIN types t ON t.id = s.type_id
                WHERE s.deleted = FALSE
                  AND s.location && ST_MakeEnvelope(
                      :lngMin,
                      :latMin,
                      :lngMax,
                      :latMax,
                      4326
                  )::geography
                  AND ST_Intersects(
                      s.location,
                      ST_MakeEnvelope(
                          :lngMin,
                          :latMin,
                          :lngMax,
                          :latMax,
                          4326
                      )::geography
                  )
                """, Object[].class)
            .setParameter("latMin", latMin)
            .setParameter("latMax", latMax)
            .setParameter("lngMin", lngMin)
            .setParameter("lngMax", lngMax)
            .getResultList();

        return rows.stream()
            .map(row -> new SpotListItem(
                (UUID) row[0],
                (String) row[1],
                (BigDecimal) row[2],
                (BigDecimal) row[3],
                (String) row[4]
            ))
            .toList();
    }
}