package com.hcl.training.payroll.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payslips")
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payslipId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_id", nullable = false, unique = true)
    private PayrollEntry payrollEntry;

    @Column(nullable = false, unique = true, length = 100)
    private String documentReference;

    @Column(nullable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    @Column(name = "pdf_content")
    private byte[] pdfContent;

    public Payslip() {
    }

    public Payslip(PayrollEntry payrollEntry, String documentReference, byte[] pdfContent) {
        this.payrollEntry = payrollEntry;
        this.documentReference = documentReference;
        this.pdfContent = pdfContent;
        this.generatedAt = LocalDateTime.now();
    }

    public Long getPayslipId() {
        return payslipId;
    }

    public void setPayslipId(Long payslipId) {
        this.payslipId = payslipId;
    }

    public PayrollEntry getPayrollEntry() {
        return payrollEntry;
    }

    public void setPayrollEntry(PayrollEntry payrollEntry) {
        this.payrollEntry = payrollEntry;
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public byte[] getPdfContent() {
        return pdfContent;
    }

    public void setPdfContent(byte[] pdfContent) {
        this.pdfContent = pdfContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payslip payslip = (Payslip) o;
        return Objects.equals(documentReference, payslip.documentReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentReference);
    }
}
