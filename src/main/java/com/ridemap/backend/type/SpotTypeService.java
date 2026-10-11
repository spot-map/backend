package com.ridemap.backend.type;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ridemap.backend.type.dto.GetSpotTypeResponse;
import com.ridemap.backend.type.dto.SpotTypeResponse;

@Service
public class SpotTypeService {

    private final SpotTypeRepository repository;

    public SpotTypeService(SpotTypeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "spot-filters", key = "'all'")
    public GetSpotTypeResponse getFilters() {
        List<SpotTypeResponse> filters = repository.findAll()
            .stream()
            .map(type -> new SpotTypeResponse(
                type.getId(),
                type.getName()
            ))
            .toList();

        return new GetSpotTypeResponse(filters);
    }
}