package com.hcl.training.payroll.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "salary_components")
public class SalaryComponent {

    public enum Type {
        EARNING,
        DEDUCTION
    }

    public enum CalculationMode {
        FIXED_AMOUNT,
        PERCENTAGE_OF_BASIC
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long componentId;

    @Column(nullable = false, unique = true, length = 50)
    private String componentCode;

    @Column(nullable = false, length = 100)
    private String componentName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Type componentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CalculationMode calculationMode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal defaultValue = BigDecimal.ZERO;

    public SalaryComponent() {
    }

    public SalaryComponent(String componentCode, String componentName, Type componentType,
                           CalculationMode calculationMode, BigDecimal defaultValue) {
        this.componentCode = componentCode;
        this.componentName = componentName;
        this.componentType = componentType;
        this.calculationMode = calculationMode;
        this.defaultValue = defaultValue;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Type getComponentType() {
        return componentType;
    }

    public void setComponentType(Type componentType) {
        this.componentType = componentType;
    }

    public CalculationMode getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(CalculationMode calculationMode) {
        this.calculationMode = calculationMode;
    }

    public BigDecimal getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(BigDecimal defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryComponent that = (SalaryComponent) o;
        return Objects.equals(componentCode, that.componentCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentCode);
    }

    @Override
    public String toString() {
        return "SalaryComponent{" +
                "id=" + componentId +
                ", code='" + componentCode + '\'' +
                ", name='" + componentName + '\'' +
                ", type=" + componentType +
                ", mode=" + calculationMode +
                ", default=" + defaultValue +
                '}';
    }
}
