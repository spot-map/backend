package com.ridemap.backend.image;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.checkerframework.checker.units.qual.s;

import com.ridemap.backend.spot.Spot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity 
@Table(name = "spot_images")
public class SpotImage {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "guid", nullable = false, unique = true, updatable = false)
    private UUID guid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false, updatable = false)
    private Spot spot;

    @Column(name = "object_key", nullable = false, unique = true, length = 255)
    private String objectKey;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "size", nullable = false)
    private long size;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected SpotImage(){}

    private SpotImage(
        Spot spot,
        String objectKey,
        String contentType,
        long size
    ){
        this.spot = spot;
        this.objectKey = objectKey;
        this.contentType = contentType;
        this.size = size;
    }

    public static SpotImage create(
        Spot spot,
        String objectKey,
        String contentType,
        long size
    ){
        return new SpotImage(spot,objectKey,contentType,size);
    }

    @PrePersist 
    private void prePersist(){
        if(guid == null){
            guid = UUID.randomUUID();
        }

        if(createdAt == null){
            createdAt = OffsetDateTime.now();
        }
    }

    public Integer getId(){
        return id;
    }

    public UUID getGuid(){
        return guid;
    }

    public Spot getSpot(){
        return spot;
    }

    public String getObjectKey(){
        return objectKey;
    }

    public String getContentType(){
        return contentType;
    }

    public long getSize(){
        return size;
    }

    public OffsetDateTime getCreatedAt(){
        return createdAt;
    }
    
}
