package com.ridemap.backend.image;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.ridemap.backend.image.dto.SpotImageResponse;
import com.ridemap.backend.image.storage.StorageService;
import com.ridemap.backend.spot.Spot;
import com.ridemap.backend.spot.SpotRespository;

@Service 
public class ImageService {
    private final SpotImageRepository repository;
    private final SpotRespository spotRespository;
    private final StorageService service;
    private final ImageValidator validator;

    public ImageService(
        SpotImageRepository repository,
        SpotRespository spotRespository,
        StorageService service,
        ImageValidator validator
    ){
        this.repository = repository;
        this.spotRespository = spotRespository;
        this.service = service;
        this.validator = validator;
    }

    @Transactional 
    public SpotImageResponse upload(UUID spotGuid, MultipartFile file){
        Spot spot = findSpot(spotGuid);
        String contentType = validator.validate(file);

        String objectKey = spotGuid + "/" + UUID.randomUUID();

        try(InputStream inputStream = file.getInputStream()){
            service.upload(objectKey, inputStream, file.getSize(), contentType);
        }catch(java.io.IOException ex){
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Не удалось прочитать файл"
            );
        }

        try{
            SpotImage image = SpotImage.create(spot, objectKey, contentType, file.getSize());

            SpotImage saved = repository.saveAndFlush(image);

            return toResponse(saved);
        }catch(RuntimeException ex){
            try{
              service.delete(objectKey);
            }catch(RuntimeException cleanupEx){
                ex.addSuppressed(cleanupEx);
            }

            throw ex;
        }
    }


    @Transactional(readOnly = true)
    public List<SpotImageResponse> getAll(UUID spotGuid){
        findSpot(spotGuid);

        return repository.findAllBySpot_GuidAndSpot_DeletedFalseOrderByCreatedAtAsc(spotGuid)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public DownloadedImage download(UUID spotGuid, UUID imageGuid){
        SpotImage image = findImage(spotGuid, imageGuid);

        InputStream stream = service.download(image.getObjectKey());

        return new DownloadedImage(stream, image.getContentType(), image.getSize());
    }

    @Transactional
    public void delete(UUID spotGuid, UUID imageGuid){
        SpotImage image = findImage(spotGuid, imageGuid);

        service.delete(image.getObjectKey());
        repository.delete(image);
    }

    private Spot findSpot(UUID spotGuid){
        return spotRespository.findByGuidAndDeletedFalse(spotGuid)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Спот не найден"
            ));
    }

    private SpotImage findImage(UUID spotGuid, UUID imageGuid){
        return repository.findByGuidAndSpot_GuidAndSpot_DeletedFalse(imageGuid, spotGuid)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Изображение не найдено"
            ));
    }

    private SpotImageResponse toResponse(SpotImage image){
        return new SpotImageResponse(
            image.getGuid(),
            image.getSpot().getGuid(),
            image.getContentType(),
            image.getSize(),
            image.getCreatedAt(),
            "/api/v1/spots"
                + image.getSpot().getGuid()
                + "/images/"
                + image.getGuid()
        );
    }

    public record DownloadedImage(
        InputStream stream,
        String contentType,
        long size
    ) {}
}
