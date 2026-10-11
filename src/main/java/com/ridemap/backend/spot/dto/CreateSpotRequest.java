package com.ridemap.backend.spot.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSpotRequest(
    @NotBlank(message = "Название спота обязательно")
    @Size(max = 50, message = "Название спота не должно привышать 50 символов")
    String title,

    @NotBlank(message = "Описание спота обязательно")
    @Size(max = 100, message = "Описание спота не должно привышать 100 символов")
    String description,

    @NotNull 
    Integer type,

    @NotNull(message = "Широта опбязательна")
    @DecimalMin(value = "-90.0", message = "Широта не может быть меньше -90")
    @DecimalMax(value = "90.0", message = "Широта не может быть больше 90")
    BigDecimal lat,

    @NotNull(message = "Долгота опбязательна")
    @DecimalMin(value = "-180.0", message = "Долгота не может быть меньше -180")
    @DecimalMax(value = "180.0", message = "Долгота не может быть больше 180")
    BigDecimal lng
) {} 