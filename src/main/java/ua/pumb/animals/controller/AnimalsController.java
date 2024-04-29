package ua.pumb.animals.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.pumb.animals.dto.AnimalGetDTO;
import ua.pumb.animals.enums.Category;
import ua.pumb.animals.enums.Sex;
import ua.pumb.animals.service.AnimalsService;

@RestController
@RequestMapping(produces = "application/json")
public class AnimalsController {

    private final AnimalsService animalsService;

    @Autowired
    public AnimalsController(AnimalsService animalsService) {
        this.animalsService = animalsService;
    }

    @Operation(
            summary = "Upload csv or xml file.",
            description = "Takes file with animals as an argument, retrieves them, and saves to database only valid."
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {@Content(schema = @Schema())}
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = {@Content(schema = @Schema(implementation = ProblemDetail.class),
                    mediaType = "application/json")}
    )
    @PostMapping(value = "/files/uploads", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file) {
        return animalsService.upload(file);
    }

    @Operation(
            summary = "Get a page with relevant animals.",
            description = "Takes pageable, type, sex and category as arguments, to perform sorting and filtering.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {@Content(schema = @Schema(implementation = Page.class),
                    mediaType = "application/json")}
    )
    @ApiResponse(
            responseCode = "204",
            description = "No Content",
            content = {@Content(schema = @Schema())}
    )
    @PageableAsQueryParam
    @GetMapping("/animals")
    public ResponseEntity<Page<AnimalGetDTO>> getAnimals(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Sex sex,
            @RequestParam(required = false) Category category,
            @Parameter(hidden = true) Pageable pageable) {
        return animalsService.getAnimals(pageable, type, sex, category);
    }

}
