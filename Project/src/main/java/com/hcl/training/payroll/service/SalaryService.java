package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.SalaryComponent;
import com.hcl.training.payroll.domain.entity.SalaryStructure;
import com.hcl.training.payroll.domain.entity.StructureComponent;
import com.hcl.training.payroll.dto.SalaryComponentDto;
import com.hcl.training.payroll.dto.SalaryStructureDto;
import com.hcl.training.payroll.dto.StructureComponentItem;
import com.hcl.training.payroll.exception.BadRequestException;
import com.hcl.training.payroll.exception.ResourceNotFoundException;
import com.hcl.training.payroll.repository.SalaryComponentRepository;
import com.hcl.training.payroll.repository.SalaryStructureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SalaryService {

    private final SalaryComponentRepository componentRepository;
    private final SalaryStructureRepository structureRepository;

    public SalaryService(SalaryComponentRepository componentRepository,
                         SalaryStructureRepository structureRepository) {
        this.componentRepository = componentRepository;
        this.structureRepository = structureRepository;
    }

    @Transactional(readOnly = true)
    public List<SalaryComponentDto> getAllComponents() {
        return componentRepository.findAll().stream()
                .map(this::mapComponentToDto)
                .collect(Collectors.toList());
    }

    public SalaryComponentDto createComponent(SalaryComponentDto dto) {
        if (componentRepository.existsByComponentCode(dto.componentCode())) {
            throw new BadRequestException("Salary component code already exists: " + dto.componentCode());
        }

        SalaryComponent component = new SalaryComponent(
                dto.componentCode().toUpperCase(),
                dto.componentName(),
                dto.componentType(),
                dto.calculationMode(),
                dto.defaultValue()
        );
        return mapComponentToDto(componentRepository.save(component));
    }

    @Transactional(readOnly = true)
    public List<SalaryStructureDto> getAllStructures() {
        return structureRepository.findAll().stream()
                .map(this::mapStructureToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalaryStructureDto getStructureById(Long id) {
        SalaryStructure structure = structureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary structure not found with ID: " + id));
        return mapStructureToDto(structure);
    }

    public SalaryStructureDto createStructure(SalaryStructureDto dto) {
        if (structureRepository.existsByStructureName(dto.structureName())) {
            throw new BadRequestException("Salary structure name already exists: " + dto.structureName());
        }

        SalaryStructure structure = new SalaryStructure(dto.structureName(), dto.description());
        List<StructureComponent> components = new ArrayList<>();

        if (dto.components() != null) {
            for (StructureComponentItem item : dto.components()) {
                SalaryComponent comp = componentRepository.findById(item.componentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Salary component not found with ID: " + item.componentId()));

                StructureComponent sc = new StructureComponent(structure, comp, item.amountOrRate());
                components.add(sc);
            }
        }
        structure.setComponents(components);
        SalaryStructure saved = structureRepository.save(structure);
        return mapStructureToDto(saved);
    }

    private SalaryComponentDto mapComponentToDto(SalaryComponent c) {
        return new SalaryComponentDto(
                c.getComponentId(),
                c.getComponentCode(),
                c.getComponentName(),
                c.getComponentType(),
                c.getCalculationMode(),
                c.getDefaultValue()
        );
    }

    private SalaryStructureDto mapStructureToDto(SalaryStructure s) {
        List<StructureComponentItem> items = s.getComponents().stream()
                .map(sc -> new StructureComponentItem(
                        sc.getSalaryComponent().getComponentId(),
                        sc.getSalaryComponent().getComponentCode(),
                        sc.getSalaryComponent().getComponentName(),
                        sc.getSalaryComponent().getComponentType(),
                        sc.getSalaryComponent().getCalculationMode(),
                        sc.getAmountOrRate()
                ))
                .collect(Collectors.toList());

        return new SalaryStructureDto(
                s.getStructureId(),
                s.getStructureName(),
                s.getDescription(),
                items
        );
    }
}
