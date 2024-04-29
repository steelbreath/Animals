package ua.pumb.animals.service;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ua.pumb.animals.dto.AnimalPostDTO;
import ua.pumb.animals.exception.SuchFormatNotSupportedException;
import ua.pumb.animals.mapper.AnimalMapper;
import ua.pumb.animals.repository.AnimalsRepository;
import jakarta.validation.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AnimalsServiceTest {

    @Mock
    private AnimalsRepository animalsRepository;
    @Mock
    private AnimalMapper animalMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @InjectMocks
    private AnimalsService animalsService;

    @BeforeEach
    void setUp() {
        animalsService = new AnimalsService(animalsRepository, animalMapper, validator);
    }

    @Test
    void testFileUploadMethod() throws IOException {
        MultipartFile csvMultipartFile = new MockMultipartFile(
                "file",
                "animals.csv",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(Path.of("animals.csv"))
        );
        MultipartFile xmlMultipartFile = new MockMultipartFile(
                "file",
                "animals.xml",
                MediaType.APPLICATION_XML_VALUE,
                Files.readAllBytes(Path.of("animals.xml"))
        );
        MultipartFile imlMultipartFile = new MockMultipartFile(
                "file",
                "animals.iml",
                MediaType.APPLICATION_XML_VALUE,
                Files.readAllBytes(Path.of("Animals.iml"))
        );
        MultipartFile csv2MultipartFile = new MockMultipartFile(
                "file",
                "empty.csv",
                MediaType.APPLICATION_XML_VALUE,
                Files.readAllBytes(Path.of("empty.csv"))
        );

        Assertions.assertEquals(ResponseEntity.ok().build(), animalsService.upload(csvMultipartFile));
        Assertions.assertEquals(ResponseEntity.ok().build(), animalsService.upload(xmlMultipartFile));
        Assertions.assertThrows(SuchFormatNotSupportedException.class, () -> animalsService.upload(imlMultipartFile));
        Assertions.assertEquals(ResponseEntity.badRequest().build(), animalsService.upload(csv2MultipartFile));
    }

    @ParameterizedTest
    @MethodSource("argumentsForIsValidTest")
    void testIsValidMethodNegative(AnimalPostDTO animalPostDTO) {
        Assertions.assertFalse(animalsService.isValid(animalPostDTO));
    }

    @Test
    void testIsValidMethodPositive() {
        Assertions.assertTrue(animalsService.isValid(
                new AnimalPostDTO("name", "type", "sex", "weight", "cost")
        ));
    }

    @Test
    void testGetAnimalsMethod() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name").descending());
        Page animalEntityPage = Mockito.mock(Page.class);
        Page animalDTOPage = Mockito.mock(Page.class);
        Mockito.when(animalsRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(animalEntityPage)
                .thenReturn(animalEntityPage);
        Mockito.when(animalEntityPage.isEmpty())
                .thenReturn(true)
                .thenReturn(false);
        Mockito.when(animalEntityPage.map(any()))
                .thenReturn(animalDTOPage);
        Assertions.assertEquals(
                ResponseEntity.noContent().build(),
                animalsService.getAnimals(pageable, "cat", null, null)
        );
        Assertions.assertEquals(
                ResponseEntity.ok(animalDTOPage),
                animalsService.getAnimals(pageable, "cat", null, null)
        );
        Mockito.verify(animalsRepository, Mockito.times(2))
                .findAll(any(Specification.class), any(Pageable.class));
        Mockito.verify(animalEntityPage, Mockito.times(1)).map(any());

    }

    private static Stream<Arguments> argumentsForIsValidTest() {
        return Stream.of(
                Arguments.of(new AnimalPostDTO("", "type", "sex", "weight", "cost")),
                Arguments.of(new AnimalPostDTO(null, "type", "sex", "weight", "cost")),
                Arguments.of(new AnimalPostDTO("name", "", "sex", "weight", "cost")),
                Arguments.of(new AnimalPostDTO("name", null, "sex", "weight", "cost")),
                Arguments.of(new AnimalPostDTO("name", "type", "", "weight", "cost")),
                Arguments.of(new AnimalPostDTO("name", "type", null, "weight", "cost")),
                Arguments.of(new AnimalPostDTO("name", "type", "sex", "", "cost")),
                Arguments.of(new AnimalPostDTO("name", "type", "sex", null, "cost")),
                Arguments.of(new AnimalPostDTO("name", "type", "sex", "weight", "")),
                Arguments.of(new AnimalPostDTO("name", "type", "sex", "weight", null))
        );
    }

}