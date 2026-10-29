package com.hcl.training.payroll.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "salary_structures")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long structureId;

    @Column(nullable = false, unique = true, length = 100)
    private String structureName;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "salaryStructure", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StructureComponent> components = new ArrayList<>();

    public SalaryStructure() {
    }

    public SalaryStructure(String structureName, String description) {
        this.structureName = structureName;
        this.description = description;
    }

    public void addComponent(SalaryComponent component, java.math.BigDecimal amountOrRate) {
        StructureComponent sc = new StructureComponent(this, component, amountOrRate);
        components.add(sc);
    }

    public Long getStructureId() {
        return structureId;
    }

    public void setStructureId(Long structureId) {
        this.structureId = structureId;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StructureComponent> getComponents() {
        return components;
    }

    public void setComponents(List<StructureComponent> components) {
        this.components = components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryStructure that = (SalaryStructure) o;
        return Objects.equals(structureName, that.structureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(structureName);
    }
}
