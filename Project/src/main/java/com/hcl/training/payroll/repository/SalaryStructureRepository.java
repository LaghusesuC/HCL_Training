package com.hcl.training.payroll.repository;

import com.hcl.training.payroll.domain.entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {
    Optional<SalaryStructure> findByStructureName(String structureName);
    boolean existsByStructureName(String structureName);
}
