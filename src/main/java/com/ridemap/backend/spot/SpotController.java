package com.ridemap.backend.spot;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ridemap.backend.spot.dto.CreateSpotRequest;
import com.ridemap.backend.spot.dto.CreateSpotResponse;
import com.ridemap.backend.spot.dto.GetAllSpotsResponse;
import com.ridemap.backend.spot.dto.GetSpotResponse;
import com.ridemap.backend.spot.dto.GetSpotsByBoundsResponse;
import com.ridemap.backend.spot.dto.SpotSearchRequest;
import com.ridemap.backend.spot.dto.SpotSearchResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
@RequestMapping("api/v1/spots")
@Tag(name = "Spots")
@Validated 
public class SpotController {
   private final SpotService service;

public SpotController(SpotService service) {
    this.service = service;
}

@GetMapping
@Operation(
    summary = "Получить список спотов",
    description = """
        Возвращает список неудалённых спотов с пагинацией.
        Результаты отсортированы по дате создания в порядке убывания.
        """
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Список спотов успешно получен",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = GetAllSpotsResponse.class
            )
        )
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные параметры пагинации",
        content = @Content(
            mediaType = "application/json"
        )
    )
})
public GetAllSpotsResponse getAll(
    @Parameter(
        name = "page",
        description = "Номер страницы, начиная с 1",
        in = ParameterIn.QUERY,
        schema = @Schema(
            type = "integer",
            minimum = "1",
            defaultValue = "1"
        )
    )
    @RequestParam(defaultValue = "1")
    @Min(1)
    int page,

    @Parameter(
        name = "pageSize",
        description = "Количество спотов на странице",
        in = ParameterIn.QUERY,
        schema = @Schema(
            type = "integer",
            minimum = "1",
            defaultValue = "10"
        )
    )
    @RequestParam(defaultValue = "10")
    @Min(1)
    int pageSize
) {
    return service.getAll(page, pageSize);
}


@GetMapping("/{guid}")
@Operation(
    summary = "Получить спот по UUID",
    description = "Возвращает информацию об одном неудаленном споте"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Спот найден"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Спот не найден"
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Некорректный UUID"
    )
})
public GetSpotResponse getMethodName(
    @Parameter(description = "UUID спота")
    @PathVariable UUID guid
) {
    return service.getByGuid(guid);
}


@GetMapping("/bounds")
@Operation(
    summary = "Получить споты в пределах карты",
    description = "Возвращает неудалённые споты, попадающие в заданные границы области карты."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Споты успешно получены"
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные координаты"
    )
})
public GetSpotsByBoundsResponse getByBounds(
    @Parameter(description = "Минимальная широта", example = "55.60")
    @RequestParam
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    BigDecimal latMin,

    @Parameter(description = "Максимальная широта", example = "55.90")
    @RequestParam
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    BigDecimal latMax,

    @Parameter(description = "Минимальная долгота", example = "37.30")
    @RequestParam
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    BigDecimal lngMin,

    @Parameter(description = "Максимальная долгота", example = "37.80")
    @RequestParam
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    BigDecimal lngMax
) {
    if (latMin.compareTo(latMax) > 0 || lngMin.compareTo(lngMax) > 0) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Минимальные границы не могут превышать максимальные"
        );
    }

    return service.getByBounds(latMin, latMax, lngMin, lngMax);
}

@GetMapping("/search")
@Operation(
    summary = "Поиск спотов",
    description = """
        Выполняет поиск неудалённых спотов по названию и типу.
        Параметры search и type необязательны.
        Результаты отсортированы по дате создания в порядке убывания.
        """
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Результаты поиска успешно получены",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = SpotSearchResponse.class)
        )
    )
})
public SpotSearchResponse search(
    @Parameter(
        description = "Поисковая строка для названия спота",
        example = "park"
    )
    @RequestParam(required = false)
    String search,
    @Parameter(
        description = "Идентификатор типа спота",
        example = "1"
    )
    @RequestParam(required = false)
    Integer type
) {
    return service.search(new SpotSearchRequest(search, type));
}

@PostMapping()
@ResponseStatus(HttpStatus.CREATED)
@Operation(
    summary = "Создать спот",
    description = "Создает новый спот"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Спот успешно создан",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateSpotResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Некореектные данные или тип спота не найден"
    )
})
public CreateSpotResponse create(
    @jakarta.validation.Valid @RequestBody CreateSpotRequest request
) {
    return service.create(request);
}

@DeleteMapping("/{guid}")
@ResponseStatus(HttpStatus.NO_CONTENT)
@Operation(
    summary = "Удалить спот",
    description = "Выполняет мягкое удаление спота по UUID"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "204",
        description = "Спот успешно удален"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Спот не найден"
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Некорректный UUID"
    ),
})
public void delete(
    @Parameter(description = "UUID спота")
    @PathVariable UUID guid
){
    service.delete(guid);
}
}