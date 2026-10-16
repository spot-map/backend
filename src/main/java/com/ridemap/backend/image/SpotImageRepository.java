package com.ridemap.backend.image;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotImageRepository extends JpaRepository<SpotImage, Integer> {
    List<SpotImage> findAllBySpot_GuidAndSpot_DeletedFalseOrderByCreatedAtAsc(UUID spotGuid);

    Optional<SpotImage> findByGuidAndSpot_GuidAndSpot_DeletedFalse(UUID imageGuid, UUID spotGuid);

    List<SpotImage> findAllBySpot_GuidInAndSpot_DeletedFalseOrderByCreatedAtAsc(Collection<UUID> spotGuids);
}
