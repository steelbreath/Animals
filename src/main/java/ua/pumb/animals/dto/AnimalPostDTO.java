package ua.pumb.animals.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;

@JsonPropertyOrder({"name", "type", "sex", "weight", "cost"})
public record AnimalPostDTO(
        @NotBlank
        String name,
        @NotBlank
        String type,
        @NotBlank
        String sex,
        @NotBlank
        String weight,
        @NotBlank
        String cost
) {
}
