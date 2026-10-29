package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.Department;
import com.hcl.training.payroll.domain.entity.Employee;
import com.hcl.training.payroll.domain.entity.SalaryStructure;
import com.hcl.training.payroll.dto.EmployeeRequest;
import com.hcl.training.payroll.dto.EmployeeResponse;
import com.hcl.training.payroll.exception.BadRequestException;
import com.hcl.training.payroll.exception.ResourceNotFoundException;
import com.hcl.training.payroll.repository.DepartmentRepository;
import com.hcl.training.payroll.repository.EmployeeRepository;
import com.hcl.training.payroll.repository.SalaryStructureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final SalaryStructureRepository salaryStructureRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           SalaryStructureRepository salaryStructureRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.salaryStructureRepository = salaryStructureRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees(Long departmentId, Boolean activeOnly) {
        List<Employee> employees;
        if (departmentId != null) {
            employees = employeeRepository.findByDepartmentDepartmentId(departmentId);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            employees = employeeRepository.findByActiveTrue();
        } else {
            employees = employeeRepository.findAll();
        }
        return employees.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return mapToResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByNumber(String employeeNumber) {
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with number: " + employeeNumber));
        return mapToResponse(employee);
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmployeeNumber(request.employeeNumber())) {
            throw new BadRequestException("Employee number already exists: " + request.employeeNumber());
        }
        if (employeeRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Employee email already exists: " + request.email());
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.departmentId()));

        SalaryStructure structure = null;
        if (request.salaryStructureId() != null) {
            structure = salaryStructureRepository.findById(request.salaryStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found with ID: " + request.salaryStructureId()));
        }

        Employee employee = new Employee(
                request.employeeNumber(),
                request.fullName(),
                request.email(),
                request.designation(),
                department,
                structure
        );

        if (request.dateOfJoining() != null) {
            employee.setDateOfJoining(request.dateOfJoining());
        }
        employee.setBankAccountNumber(request.bankAccountNumber());
        employee.setPanNumber(request.panNumber());
        if (request.active() != null) {
            employee.setActive(request.active());
        }

        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.departmentId()));

        SalaryStructure structure = null;
        if (request.salaryStructureId() != null) {
            structure = salaryStructureRepository.findById(request.salaryStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Salary Structure not found with ID: " + request.salaryStructureId()));
        }

        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setDesignation(request.designation());
        employee.setDepartment(department);
        employee.setSalaryStructure(structure);
        employee.setBankAccountNumber(request.bankAccountNumber());
        employee.setPanNumber(request.panNumber());
        if (request.dateOfJoining() != null) {
            employee.setDateOfJoining(request.dateOfJoining());
        }
        if (request.active() != null) {
            employee.setActive(request.active());
        }

        return mapToResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse assignSalaryStructure(Long employeeId, Long structureId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        SalaryStructure structure = salaryStructureRepository.findById(structureId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary structure not found with ID: " + structureId));

        employee.setSalaryStructure(structure);
        return mapToResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse mapToResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getEmployeeId(),
                employee.getEmployeeNumber(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getDesignation(),
                employee.getDateOfJoining(),
                employee.getBankAccountNumber(),
                employee.getPanNumber(),
                employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null,
                employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null,
                employee.getSalaryStructure() != null ? employee.getSalaryStructure().getStructureId() : null,
                employee.getSalaryStructure() != null ? employee.getSalaryStructure().getStructureName() : "None Assigned",
                employee.isActive()
        );
    }
}
