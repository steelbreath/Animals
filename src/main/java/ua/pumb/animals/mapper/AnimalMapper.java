package ua.pumb.animals.mapper;

import org.mapstruct.*;
import ua.pumb.animals.configuration.MapperConfig;
import ua.pumb.animals.dto.AnimalGetDTO;
import ua.pumb.animals.dto.AnimalPostDTO;
import ua.pumb.animals.entity.AnimalEntity;
import ua.pumb.animals.enums.Category;
import ua.pumb.animals.enums.Sex;

@Mapper(config = MapperConfig.class)
public interface AnimalMapper {

    @BeforeMapping
    default void setCategory(@MappingTarget AnimalEntity animalEntity, AnimalPostDTO animalPostDTO) {
        Double price = Double.valueOf(animalPostDTO.cost());
        if (price >= 0 && price <= 20) {
            animalEntity.setCategory(Category.LOW_PRICE);
        } else if (price > 20 && price <= 40) {
            animalEntity.setCategory(Category.MEDIUM_PRICE);
        } else if (price > 40 && price <= 60) {
            animalEntity.setCategory(Category.HIGH_PRICE);
        } else if (price > 60) {
            animalEntity.setCategory(Category.PREMIUM_PRICE);
        }
    }

    @EnumMapping(nameTransformationStrategy = MappingConstants.CASE_TRANSFORMATION, configuration = "lower")
    Sex toEnum(String sex);

    AnimalEntity toAnimalEntity(AnimalPostDTO animalPostDTO);

    AnimalGetDTO toAnimalGetDTO(AnimalEntity animalEntity);
}
