package com.hcl.training.payroll.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "payroll_deductions")
public class PayrollDeduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deductionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", nullable = false)
    @JsonIgnore
    private PayrollEntry payrollEntry;

    @Column(nullable = false, length = 50)
    private String deductionType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    public PayrollDeduction() {
    }

    public PayrollDeduction(PayrollEntry payrollEntry, String deductionType, BigDecimal amount) {
        this.payrollEntry = payrollEntry;
        this.deductionType = deductionType;
        this.amount = amount;
    }

    public Long getDeductionId() {
        return deductionId;
    }

    public void setDeductionId(Long deductionId) {
        this.deductionId = deductionId;
    }

    public PayrollEntry getPayrollEntry() {
        return payrollEntry;
    }

    public void setPayrollEntry(PayrollEntry payrollEntry) {
        this.payrollEntry = payrollEntry;
    }

    public String getDeductionType() {
        return deductionType;
    }

    public void setDeductionType(String deductionType) {
        this.deductionType = deductionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayrollDeduction that = (PayrollDeduction) o;
        return Objects.equals(deductionId, that.deductionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deductionId);
    }
}
