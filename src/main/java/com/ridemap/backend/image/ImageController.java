package com.ridemap.backend.image;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ridemap.backend.image.dto.SpotImageResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/spots/{spotGuid}/images")
@Tag(name = "Images")
public class ImageController {

    private final ImageService service;

    public ImageController(ImageService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SpotImageResponse> upload(@PathVariable UUID spotGuid, @RequestParam("file") MultipartFile file) {
        SpotImageResponse response = service.upload(spotGuid, file);

        return ResponseEntity
                .created(java.net.URI.create(response.url()))
                .body(response);
    }

    @GetMapping
    public List<SpotImageResponse> getAll(@PathVariable UUID spotGuid) {
        return service.getAll(spotGuid);
    }

    @GetMapping("/{imageGuid}")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID spotGuid,@PathVariable UUID imageGuid) {
        ImageService.DownloadedImage image =
                service.download(spotGuid, imageGuid);

        InputStreamResource resource =
                new InputStreamResource(image.stream());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .contentLength(image.size())
                .cacheControl(CacheControl.noCache())
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }

    @DeleteMapping("/{imageGuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID spotGuid, @PathVariable UUID imageGuid) {
        service.delete(spotGuid, imageGuid);

        return ResponseEntity.noContent().build();
    }
}