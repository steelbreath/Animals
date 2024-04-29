package ua.pumb.animals.dto;

import ua.pumb.animals.enums.Category;
import ua.pumb.animals.enums.Sex;

public record AnimalGetDTO(
        String name,
        String type,
        Sex sex,
        Double weight,
        Double cost,
        Category category
) {
}
