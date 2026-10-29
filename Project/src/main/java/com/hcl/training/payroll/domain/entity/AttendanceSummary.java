package com.hcl.training.payroll.domain.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "attendance_summaries", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "payroll_month"})
})
public class AttendanceSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "payroll_month", nullable = false, length = 7) // format: YYYY-MM
    private String payrollMonth;

    @Column(nullable = false)
    private int totalWorkingDays;

    @Column(nullable = false)
    private int payableDays;

    @Column(nullable = false)
    private int lossOfPayDays;

    public AttendanceSummary() {
    }

    public AttendanceSummary(Employee employee, String payrollMonth, int totalWorkingDays, int payableDays, int lossOfPayDays) {
        this.employee = employee;
        this.payrollMonth = payrollMonth;
        this.totalWorkingDays = totalWorkingDays;
        this.payableDays = payableDays;
        this.lossOfPayDays = lossOfPayDays;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getPayrollMonth() {
        return payrollMonth;
    }

    public void setPayrollMonth(String payrollMonth) {
        this.payrollMonth = payrollMonth;
    }

    public int getTotalWorkingDays() {
        return totalWorkingDays;
    }

    public void setTotalWorkingDays(int totalWorkingDays) {
        this.totalWorkingDays = totalWorkingDays;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceSummary that = (AttendanceSummary) o;
        return Objects.equals(employee, that.employee) && Objects.equals(payrollMonth, that.payrollMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, payrollMonth);
    }
}
