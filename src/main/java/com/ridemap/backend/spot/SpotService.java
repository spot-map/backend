package com.ridemap.backend.spot;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ridemap.backend.image.SpotImageRepository;
import com.ridemap.backend.image.dto.SpotImageResponse;
import com.ridemap.backend.spot.dto.CreateSpotRequest;
import com.ridemap.backend.spot.dto.CreateSpotResponse;
import com.ridemap.backend.spot.dto.GetAllSpotsResponse;
import com.ridemap.backend.spot.dto.GetSpotResponse;
import com.ridemap.backend.spot.dto.GetSpotsByBoundsResponse;
import com.ridemap.backend.spot.dto.SpotListItem;
import com.ridemap.backend.spot.dto.SpotSearchRequest;
import com.ridemap.backend.spot.dto.SpotSearchResponse;
import com.ridemap.backend.spot.query.SpotBoundsQueryRepository;
import com.ridemap.backend.type.SpotType;
import com.ridemap.backend.type.SpotTypeRepository;

@Service
public class SpotService {

    private final SpotRespository respository;
    private final SpotBoundsQueryRepository boundsRepository;
    private final SpotTypeRepository typeRepository;
    private final SpotImageRepository imageRepository;

    public SpotService(
        SpotRespository respository,
        SpotBoundsQueryRepository boundsRepository,
        SpotTypeRepository typeRepository,
        SpotImageRepository imageRepository
    ) {
        this.respository = respository;
        this.boundsRepository = boundsRepository;
        this.typeRepository = typeRepository;
        this.imageRepository = imageRepository;
    }

    @Transactional(readOnly = true)
    public GetAllSpotsResponse getAll(int page, int pageSize) {
        PageRequest pageable = PageRequest.of(
            page - 1,
            pageSize,
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Spot> spots = respository.findAllByDeletedFalse(pageable);

        List<UUID> spotGuids = spots.getContent()
            .stream()
            .map(Spot::getGuid)
            .toList();

        Map<UUID, List<SpotImageResponse>> imagesBySpot = getImagesBySpotGuid(spotGuids);

        List<SpotListItem> items = spots.getContent()
            .stream()
            .map(spot -> new SpotListItem(
                spot.getGuid(),
                spot.getTitle(),
                spot.getLat(),
                spot.getLng(),
                spot.getSpotType().getName(),
                imagesBySpot.getOrDefault(spot.getGuid(), List.of())
            ))
            .toList();

        return new GetAllSpotsResponse(
            Math.toIntExact(spots.getTotalElements()),
            page,
            pageSize,
            spots.getTotalPages(),
            items
        );
    }

    @Cacheable(cacheNames = "spots", key = "#guid")
    @Transactional(readOnly = true)
    public GetSpotResponse getByGuid(UUID guid) {
        Spot spot = respository.findByGuidAndDeletedFalse(guid)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Спот не найден"
            ));

        List<SpotImageResponse> images = getImagesBySpotGuid(List.of(spot.getGuid()))
            .getOrDefault(spot.getGuid(), List.of());

        return new GetSpotResponse(
            spot.getGuid(),
            spot.getTitle(),
            spot.getDescription(),
            spot.getLat(),
            spot.getLng(),
            spot.getSpotType().getName(),
            images
        );
    }

    @Transactional(readOnly = true)
    public GetSpotsByBoundsResponse getByBounds(
        BigDecimal latMin,
        BigDecimal latMax,
        BigDecimal lngMin,
        BigDecimal lngMax
    ) {
        List<SpotListItem> spots = boundsRepository.findWithinBounds(latMin, latMax, lngMin, lngMax);

        return new GetSpotsByBoundsResponse(spots);
    }

    @Transactional(readOnly = true)
    public SpotSearchResponse search(SpotSearchRequest request) {
        Specification<Spot> specification = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        if (request.search() != null && !request.search().isBlank()) {
            String search = "%" + request.search().toLowerCase() + "%";

            specification = specification.and((root, query, cb) ->
                cb.like(cb.lower(root.get("title")), search)
            );
        }

        if (request.type() != null) {
            specification = specification.and((root, query, cb) ->
                cb.equal(root.get("type").get("id"), request.type())
            );
        }

        List<Spot> spots = respository.findAll(
            specification,
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        List<UUID> spotGuids = spots.stream()
            .map(Spot::getGuid)
            .toList();

        Map<UUID, List<SpotImageResponse>> imagesBySpot = getImagesBySpotGuid(spotGuids);

        List<SpotListItem> items = spots.stream()
        .map(spot -> new SpotListItem(
            spot.getGuid(),
            spot.getTitle(),
            spot.getLat(),
            spot.getLng(),
            spot.getSpotType().getName(),
            imagesBySpot.getOrDefault(spot.getGuid(), List.of())
        ))
        .toList();

        return new SpotSearchResponse(items, items.size());
    }

    @Transactional
    @CacheEvict(cacheNames = "spots", allEntries = true)
    public CreateSpotResponse create(CreateSpotRequest request) {
        SpotType type = typeRepository.findById(request.type())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Тип не найден"
            ));

        Spot spot = Spot.create(
            request.title().trim(),
            request.description().trim(),
            type,
            request.lat(),
            request.lng()
        );

        Spot savedSpot = respository.save(spot);

        return new CreateSpotResponse(
            savedSpot.getGuid(),
            savedSpot.getTitle(),
            savedSpot.getDescription(),
            savedSpot.getSpotType().getName(),
            savedSpot.getLat(),
            savedSpot.getLng(),
            savedSpot.getCreatedAt()
        );
    }

    @Transactional
    @CacheEvict(cacheNames = "spots", allEntries = true)
    public void delete(UUID guid) {
        Spot spot = respository.findByGuidAndDeletedFalse(guid)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Спот не найден"
            ));

        spot.delete();
    }

    private Map<UUID, List<SpotImageResponse>> getImagesBySpotGuid(List<UUID> spotGuids) {
        if (spotGuids.isEmpty()) {
            return Map.of();
        }

        return imageRepository
            .findAllBySpot_GuidInAndSpot_DeletedFalseOrderByCreatedAtAsc(spotGuids)
            .stream()
            .map(image -> new SpotImageResponse(
                image.getGuid(),
                image.getSpot().getGuid(),
                image.getContentType(),
                image.getSize(),
                image.getCreatedAt(),
                "/api/v1/spots/" + image.getSpot().getGuid()
                    + "/images/" + image.getGuid()
            ))
            .collect(Collectors.groupingBy(
                SpotImageResponse::spotGuid
            ));
    }
}