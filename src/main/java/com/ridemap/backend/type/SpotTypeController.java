package com.ridemap.backend.type;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ridemap.backend.type.dto.GetSpotTypeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;


@RestController 
@RequestMapping("/api/v1/filters")
@Tag(name = "Filters")
public class SpotTypeController {
    private final SpotTypeService service;

	public SpotTypeController(SpotTypeService service) {
		this.service = service;
	}

    @GetMapping()
    @Operation(
        summary = "Получить фильтры спотов",
        description = "Возвращает спискок типов спотов для фильтров"
    )
    public GetSpotTypeResponse getMethodName() {
        return service.getFilters();
    }
    
}
