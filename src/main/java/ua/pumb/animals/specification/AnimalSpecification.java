package ua.pumb.animals.specification;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ua.pumb.animals.entity.AnimalEntity;

public class AnimalSpecification {

    public static Specification<AnimalEntity> filterAnimal(String type, String sex, String category) {
        return (root, query, criteriaBuilder) -> {
            Predicate typePredicate =
                    criteriaBuilder.like(root.get("type"), StringUtils.isBlank(type)
                            ? likePattern("") : type);
            Predicate sexPredicate =
                    criteriaBuilder.like(root.get("sex"), StringUtils.isBlank(sex)
                            ? likePattern("") : sex);
            Predicate categoryPredicate =
                    criteriaBuilder.like(root.get("category"), StringUtils.isBlank(category)
                            ? likePattern("") : category);

            return criteriaBuilder.and(typePredicate, sexPredicate, categoryPredicate);
        };
    }

    private static String likePattern(String value) {
        return "%" + value + "%";
    }
}
