package com.hcl.training.payroll.repository;

import com.hcl.training.payroll.domain.entity.SalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryComponentRepository extends JpaRepository<SalaryComponent, Long> {
    Optional<SalaryComponent> findByComponentCode(String componentCode);
    List<SalaryComponent> findByComponentType(SalaryComponent.Type componentType);
    boolean existsByComponentCode(String componentCode);
}
