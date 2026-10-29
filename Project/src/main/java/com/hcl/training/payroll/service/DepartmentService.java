package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.Department;
import com.hcl.training.payroll.dto.DepartmentDto;
import com.hcl.training.payroll.exception.BadRequestException;
import com.hcl.training.payroll.exception.ResourceNotFoundException;
import com.hcl.training.payroll.repository.DepartmentRepository;
import com.hcl.training.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(d -> new DepartmentDto(
                        d.getDepartmentId(),
                        d.getDepartmentName(),
                        d.getDepartmentCode(),
                        (long) employeeRepository.findByDepartmentDepartmentId(d.getDepartmentId()).size()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }

    public DepartmentDto createDepartment(DepartmentDto dto) {
        if (departmentRepository.existsByDepartmentCode(dto.departmentCode())) {
            throw new BadRequestException("Department code already exists: " + dto.departmentCode());
        }

        Department department = new Department(dto.departmentName(), dto.departmentCode());
        Department saved = departmentRepository.save(department);
        return new DepartmentDto(saved.getDepartmentId(), saved.getDepartmentName(), saved.getDepartmentCode(), 0L);
    }
}
