package ua.pumb.animals.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.pumb.animals.dto.AnimalGetDTO;
import ua.pumb.animals.dto.AnimalPostDTO;
import ua.pumb.animals.entity.AnimalEntity;
import ua.pumb.animals.enums.Category;
import ua.pumb.animals.enums.Sex;
import ua.pumb.animals.exception.SuchFormatNotSupportedException;
import ua.pumb.animals.mapper.AnimalMapper;
import ua.pumb.animals.repository.AnimalsRepository;
import ua.pumb.animals.specification.AnimalSpecification;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class AnimalsService {

    private static final Logger logger = LoggerFactory.getLogger(AnimalsService.class);

    private final AnimalsRepository animalsRepository;
    private final AnimalMapper animalMapper;
    private final Validator validator;

    public AnimalsService(AnimalsRepository animalsRepository, AnimalMapper animalMapper, Validator validator) {
        this.animalsRepository = animalsRepository;
        this.animalMapper = animalMapper;
        this.validator = validator;
    }

    public ResponseEntity<Object> upload(MultipartFile multipartFile) {
        List<AnimalPostDTO> animals;
        try {
            animals = getAnimalPostDTOMappingIterator(multipartFile).readAll();
        } catch (IOException e) {
            logger.error("IOException occurred while reading the file.", e);
            throw new RuntimeException(e);
        }
        logger.info("File was successfully read.");
        List<AnimalEntity> animalEntities = convertToAnimalEntities(animals);
        if (animalEntities.isEmpty()) return ResponseEntity.badRequest().build();
        logger.info("AnimalDTOs were successfully converted to entities.");
        animalsRepository.saveAll(animalEntities);
        logger.info("Entities were successfully saved to database");
        return ResponseEntity.ok().build();
    }

    protected MappingIterator<AnimalPostDTO> getAnimalPostDTOMappingIterator(MultipartFile multipartFile) throws IOException {
        MappingIterator<AnimalPostDTO> personIter;
        String filename = Objects.requireNonNull(multipartFile.getOriginalFilename());
        if (filename.endsWith("csv")) {
            personIter = new CsvMapper()
                    .readerWithTypedSchemaFor(AnimalPostDTO.class)
                    .readValues(multipartFile.getBytes());
        } else if (filename.endsWith("xml")) {
            personIter = new XmlMapper()
                    .readerFor(AnimalPostDTO.class)
                    .readValues(multipartFile.getBytes());

        } else {
            throw new SuchFormatNotSupportedException("This format is not supported. Please use xml or csv");
        }
        return personIter;
    }

    protected List<AnimalEntity> convertToAnimalEntities(List<AnimalPostDTO> animals) {
        List<AnimalEntity> animalEntities = new ArrayList<>();
        for (AnimalPostDTO animalPostDTO : animals) {
            try {
                if (!isValid(animalPostDTO)) continue;
                animalEntities.add(animalMapper.toAnimalEntity(animalPostDTO));
            } catch (IllegalArgumentException e) {
                logger.warn(e.getMessage());
            }
        }
        return animalEntities;
    }

    protected boolean isValid(AnimalPostDTO animalPostDTO) {
        Set<ConstraintViolation<AnimalPostDTO>> violations = validator.validate(animalPostDTO);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<AnimalPostDTO> violation : violations) {
                logger.warn(violation.getMessage());
            }
            return false;
        }
        return true;
    }

    public ResponseEntity<Page<AnimalGetDTO>> getAnimals(Pageable pageable, String type, Sex sex, Category category) {
        Specification<AnimalEntity> specification = AnimalSpecification.filterAnimal(
                type,
                sex == null ? "" : sex.getSex(),
                category == null ? "" : category.getCategory()
        );
        logger.info("AnimalEntity specification was successfully created: {}", specification);
        Page<AnimalEntity> pageOfEntities = animalsRepository.findAll(specification, pageable);
        logger.info("PageOfEntities was successfully retrieved from database: {}", pageOfEntities);
        if (pageOfEntities.isEmpty()) return ResponseEntity.noContent().build();
        Page<AnimalGetDTO> pageOfDTOs = pageOfEntities.map(animalMapper::toAnimalGetDTO);
        logger.info("PageOfEntities was successfully converted to pageOfDTOs: {}", pageOfDTOs);
        return ResponseEntity.ok(pageOfDTOs);
    }
}
