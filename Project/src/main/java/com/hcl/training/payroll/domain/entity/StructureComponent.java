package com.hcl.training.payroll.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "structure_components", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"structure_id", "component_id"})
})
public class StructureComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id", nullable = false)
    @JsonIgnore
    private SalaryStructure salaryStructure;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_id", nullable = false)
    private SalaryComponent salaryComponent;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountOrRate;

    public StructureComponent() {
    }

    public StructureComponent(SalaryStructure salaryStructure, SalaryComponent salaryComponent, BigDecimal amountOrRate) {
        this.salaryStructure = salaryStructure;
        this.salaryComponent = salaryComponent;
        this.amountOrRate = amountOrRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SalaryStructure getSalaryStructure() {
        return salaryStructure;
    }

    public void setSalaryStructure(SalaryStructure salaryStructure) {
        this.salaryStructure = salaryStructure;
    }

    public SalaryComponent getSalaryComponent() {
        return salaryComponent;
    }

    public void setSalaryComponent(SalaryComponent salaryComponent) {
        this.salaryComponent = salaryComponent;
    }

    public BigDecimal getAmountOrRate() {
        return amountOrRate;
    }

    public void setAmountOrRate(BigDecimal amountOrRate) {
        this.amountOrRate = amountOrRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructureComponent that = (StructureComponent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
