package com.ridemap.backend.spot;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.ridemap.backend.type.SpotType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "spots")
public class Spot {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;

    @Column(name = "guid", nullable = false, unique = true, updatable = false)
    private  UUID guid;

    @Column(name =  "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id", nullable = false)
    private SpotType type;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column (name = "updated_at")
    private  OffsetDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "lat", nullable = false, precision = 9, scale = 6)
    private  BigDecimal lat;

    @Column(name = "lng", nullable = false, precision = 9, scale = 6)
    private  BigDecimal lng;

    protected Spot(){

    }

    public static Spot create(String title, String description, SpotType type, BigDecimal lat, BigDecimal lng){
        Spot spot = new Spot();

        OffsetDateTime now = OffsetDateTime.now();

        spot.guid = UUID.randomUUID();
        spot.title = title;
        spot.description = description;
        spot.type = type;
        spot.lat = lat;
        spot.lng = lng;
        spot.createdAt = now;
        spot.updatedAt = now;
        spot.deleted = false;

        return spot;
    }

    public void delete(){
        if(deleted) return;

        deleted = true;
        updatedAt = OffsetDateTime.now();
    }

    public Integer getId(){
       return id;
    }
    
    public UUID getGuid(){
        return guid;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public SpotType getSpotType(){
        return type;
    }

    public OffsetDateTime getCreatedAt(){
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt(){
        return updatedAt;
    }

    public boolean isDeleted(){
        return deleted;
    }

    public BigDecimal getLat(){
        return lat;
    }

    public BigDecimal getLng(){
        return lng;
    }
} 
