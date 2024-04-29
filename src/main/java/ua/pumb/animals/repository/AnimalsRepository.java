package ua.pumb.animals.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ua.pumb.animals.entity.AnimalEntity;

public interface AnimalsRepository extends JpaRepository<AnimalEntity, Long>,
        JpaSpecificationExecutor<AnimalEntity> {

    Page<AnimalEntity> findAll(Specification<AnimalEntity> specification, Pageable pageable);
}
