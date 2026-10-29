package com.hcl.training.payroll.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "payroll_runs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"payroll_month"})
})
public class PayrollRun {

    public enum Status {
        DRAFT,
        LOCKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long runId;

    @Column(name = "payroll_month", nullable = false, length = 7)
    private String payrollMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.DRAFT;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalGross = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalNet = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lockedAt;

    @Column(length = 50)
    private String lockedBy;

    @OneToMany(mappedBy = "payrollRun", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PayrollEntry> entries = new ArrayList<>();

    public PayrollRun() {
    }

    public PayrollRun(String payrollMonth) {
        this.payrollMonth = payrollMonth;
        this.status = Status.DRAFT;
        this.createdAt = LocalDateTime.now();
    }

    public void lock(String username) {
        if (this.status == Status.LOCKED) {
            throw new IllegalStateException("Payroll run for month " + payrollMonth + " is already locked!");
        }
        this.status = Status.LOCKED;
        this.lockedAt = LocalDateTime.now();
        this.lockedBy = username;
    }

    public boolean isLocked() {
        return this.status == Status.LOCKED;
    }

    public void addEntry(PayrollEntry entry) {
        entries.add(entry);
        entry.setPayrollRun(this);
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getPayrollMonth() {
        return payrollMonth;
    }

    public void setPayrollMonth(String payrollMonth) {
        this.payrollMonth = payrollMonth;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getTotalGross() {
        return totalGross;
    }

    public void setTotalGross(BigDecimal totalGross) {
        this.totalGross = totalGross;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(BigDecimal totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public BigDecimal getTotalNet() {
        return totalNet;
    }

    public void setTotalNet(BigDecimal totalNet) {
        this.totalNet = totalNet;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public List<PayrollEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<PayrollEntry> entries) {
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayrollRun that = (PayrollRun) o;
        return Objects.equals(payrollMonth, that.payrollMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payrollMonth);
    }
}
