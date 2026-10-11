package com.ridemap.backend.spot;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;


public interface SpotRespository extends JpaRepository<Spot, Integer>, JpaSpecificationExecutor<Spot>{
    @EntityGraph(attributePaths = "type")
    Page<Spot> findAllByDeletedFalse(Pageable pageable);

    Optional<Spot> findByGuidAndDeletedFalse(UUID guid);
}
