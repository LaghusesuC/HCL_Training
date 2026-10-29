package com.hcl.training.payroll.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "payroll_entries", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"run_id", "employee_id"})
})
public class PayrollEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    @JsonIgnore
    private PayrollRun payrollRun;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private int payableDays;

    @Column(nullable = false)
    private int lossOfPayDays;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lossOfPayAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossSalary = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netSalary = BigDecimal.ZERO;

    @OneToMany(mappedBy = "payrollEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PayrollDeduction> deductions = new ArrayList<>();

    public PayrollEntry() {
    }

    public PayrollEntry(PayrollRun payrollRun, Employee employee, int payableDays, int lossOfPayDays,
                        BigDecimal lossOfPayAmount, BigDecimal grossSalary,
                        BigDecimal totalDeductions, BigDecimal netSalary) {
        this.payrollRun = payrollRun;
        this.employee = employee;
        this.payableDays = payableDays;
        this.lossOfPayDays = lossOfPayDays;
        this.lossOfPayAmount = lossOfPayAmount;
        this.grossSalary = grossSalary;
        this.totalDeductions = totalDeductions;
        this.netSalary = netSalary;
    }

    public void addDeduction(String deductionType, BigDecimal amount) {
        PayrollDeduction d = new PayrollDeduction(this, deductionType, amount);
        deductions.add(d);
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public PayrollRun getPayrollRun() {
        return payrollRun;
    }

    public void setPayrollRun(PayrollRun payrollRun) {
        this.payrollRun = payrollRun;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getPayableDays() {
        return payableDays;
    }

    public void setPayableDays(int payableDays) {
        this.payableDays = payableDays;
    }

    public int getLossOfPayDays() {
        return lossOfPayDays;
    }

    public void setLossOfPayDays(int lossOfPayDays) {
        this.lossOfPayDays = lossOfPayDays;
    }

    public BigDecimal getLossOfPayAmount() {
        return lossOfPayAmount;
    }

    public void setLossOfPayAmount(BigDecimal lossOfPayAmount) {
        this.lossOfPayAmount = lossOfPayAmount;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(BigDecimal totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public List<PayrollDeduction> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<PayrollDeduction> deductions) {
        this.deductions = deductions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayrollEntry that = (PayrollEntry) o;
        return Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId);
    }
}
