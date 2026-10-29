package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.Employee;
import com.hcl.training.payroll.domain.entity.PayrollDeduction;
import com.hcl.training.payroll.domain.entity.PayrollEntry;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Service to generate professional enterprise PDF payslips using OpenPDF.
 * Implements Builder Pattern concepts for payslip document generation.
 */
@Service
public class PayslipPdfService {

    private static final Color PRIMARY_COLOR = new Color(24, 43, 73);      // Deep Navy
    private static final Color SECONDARY_COLOR = new Color(41, 128, 185);  // Accent Blue
    private static final Color LIGHT_BG = new Color(245, 247, 250);        // Soft Gray
    private static final Color BORDER_COLOR = new Color(218, 225, 231);

    public byte[] generatePayslipPdf(PayrollEntry entry, String documentReference) {
        Employee employee = entry.getEmployee();
        String month = entry.getPayrollRun() != null ? entry.getPayrollRun().getPayrollMonth() : "N/A";

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. Company Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, PRIMARY_COLOR);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            Font netPayFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(16, 124, 65));

            Paragraph companyName = new Paragraph("HCL GLOBAL TECHNOLOGIES ENTERPRISE", titleFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);

            Paragraph address = new Paragraph("Corporate Office: IT Park, Tower 4, Technology Corridor | CIN: L72200DL1991PLC046369", subtitleFont);
            address.setAlignment(Element.ALIGN_CENTER);
            address.setSpacingAfter(6f);
            document.add(address);

            Paragraph payslipTitle = new Paragraph("PAYSLIP FOR THE MONTH OF " + month.toUpperCase(), sectionFont);
            payslipTitle.setAlignment(Element.ALIGN_CENTER);
            payslipTitle.setSpacingAfter(15f);
            document.add(payslipTitle);

            // 2. Employee Details Table
            PdfPTable empTable = new PdfPTable(4);
            empTable.setWidthPercentage(100);
            empTable.setWidths(new float[]{22f, 28f, 22f, 28f});
            empTable.setSpacingAfter(12f);

            addCell(empTable, "Employee ID:", boldFont, LIGHT_BG);
            addCell(empTable, employee.getEmployeeNumber(), normalFont, LIGHT_BG);
            addCell(empTable, "Employee Name:", boldFont, LIGHT_BG);
            addCell(empTable, employee.getFullName(), normalFont, LIGHT_BG);

            addCell(empTable, "Designation:", boldFont, Color.WHITE);
            addCell(empTable, employee.getDesignation() != null ? employee.getDesignation() : "N/A", normalFont, Color.WHITE);
            addCell(empTable, "Department:", boldFont, Color.WHITE);
            addCell(empTable, employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : "N/A", normalFont, Color.WHITE);

            addCell(empTable, "PAN Number:", boldFont, LIGHT_BG);
            addCell(empTable, employee.getPanNumber() != null ? employee.getPanNumber() : "N/A", normalFont, LIGHT_BG);
            addCell(empTable, "Bank Account:", boldFont, LIGHT_BG);
            addCell(empTable, employee.getBankAccountNumber() != null ? maskAccount(employee.getBankAccountNumber()) : "N/A", normalFont, LIGHT_BG);

            addCell(empTable, "Document Ref:", boldFont, Color.WHITE);
            addCell(empTable, documentReference, normalFont, Color.WHITE);
            addCell(empTable, "Date of Joining:", boldFont, Color.WHITE);
            addCell(empTable, employee.getDateOfJoining() != null ? employee.getDateOfJoining().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A", normalFont, Color.WHITE);

            document.add(empTable);

            // 3. Attendance Summary Box
            PdfPTable attTable = new PdfPTable(3);
            attTable.setWidthPercentage(100);
            attTable.setSpacingAfter(15f);

            addHeaderCell(attTable, "Total Working Days", sectionFont, SECONDARY_COLOR);
            addHeaderCell(attTable, "Payable Days", sectionFont, SECONDARY_COLOR);
            addHeaderCell(attTable, "Loss of Pay (LOP) Days", sectionFont, SECONDARY_COLOR);

            int workingDays = entry.getPayableDays() + entry.getLossOfPayDays();
            addCellCenter(attTable, String.valueOf(workingDays), boldFont, Color.WHITE);
            addCellCenter(attTable, String.valueOf(entry.getPayableDays()), boldFont, Color.WHITE);
            addCellCenter(attTable, String.valueOf(entry.getLossOfPayDays()), boldFont, Color.WHITE);

            document.add(attTable);

            // 4. Earnings and Deductions 2-Column Table
            PdfPTable salaryTable = new PdfPTable(4);
            salaryTable.setWidthPercentage(100);
            salaryTable.setWidths(new float[]{32f, 18f, 32f, 18f});
            salaryTable.setSpacingAfter(15f);

            addHeaderCell(salaryTable, "Earnings", sectionFont, PRIMARY_COLOR);
            addHeaderCell(salaryTable, "Amount (INR)", sectionFont, PRIMARY_COLOR);
            addHeaderCell(salaryTable, "Deductions", sectionFont, PRIMARY_COLOR);
            addHeaderCell(salaryTable, "Amount (INR)", sectionFont, PRIMARY_COLOR);

            // Add standard gross components
            addCell(salaryTable, "Gross Remuneration", normalFont, Color.WHITE);
            addCellRight(salaryTable, entry.getGrossSalary().toPlainString(), normalFont, Color.WHITE);

            // Loss of pay if any
            if (entry.getLossOfPayAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                addCell(salaryTable, "Loss of Pay (LOP)", normalFont, LIGHT_BG);
                addCellRight(salaryTable, entry.getLossOfPayAmount().toPlainString(), normalFont, LIGHT_BG);
            } else {
                addCell(salaryTable, "-", normalFont, LIGHT_BG);
                addCellRight(salaryTable, "-", normalFont, LIGHT_BG);
            }

            // Deductions list
            for (PayrollDeduction d : entry.getDeductions()) {
                addCell(salaryTable, "-", normalFont, Color.WHITE);
                addCellRight(salaryTable, "-", normalFont, Color.WHITE);

                addCell(salaryTable, d.getDeductionType(), normalFont, Color.WHITE);
                addCellRight(salaryTable, d.getAmount().toPlainString(), normalFont, Color.WHITE);
            }

            // Totals Row
            addCell(salaryTable, "Total Gross Earnings", boldFont, LIGHT_BG);
            addCellRight(salaryTable, "₹ " + entry.getGrossSalary().toPlainString(), boldFont, LIGHT_BG);

            java.math.BigDecimal totalDed = entry.getTotalDeductions().add(entry.getLossOfPayAmount());
            addCell(salaryTable, "Total Deductions (inc. LOP)", boldFont, LIGHT_BG);
            addCellRight(salaryTable, "₹ " + totalDed.toPlainString(), boldFont, LIGHT_BG);

            document.add(salaryTable);

            // 5. Net Salary Highlight Banner
            PdfPTable netTable = new PdfPTable(2);
            netTable.setWidthPercentage(100);
            netTable.setWidths(new float[]{60f, 40f});
            netTable.setSpacingAfter(20f);

            PdfPCell netLabelCell = new PdfPCell(new Phrase("NET TAKE-HOME PAYABLE:", sectionFont));
            netLabelCell.setBackgroundColor(new Color(235, 247, 238));
            netLabelCell.setPadding(10f);
            netLabelCell.setBorderColor(new Color(16, 124, 65));
            netTable.addCell(netLabelCell);

            PdfPCell netValCell = new PdfPCell(new Phrase("₹ " + entry.getNetSalary().toPlainString(), netPayFont));
            netValCell.setBackgroundColor(new Color(235, 247, 238));
            netValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            netValCell.setPadding(10f);
            netValCell.setBorderColor(new Color(16, 124, 65));
            netTable.addCell(netValCell);

            document.add(netTable);

            // 6. Sign-off and Disclaimer
            Paragraph disclaimer = new Paragraph(
                    "Note: This is a system-generated payroll document and does not require a physical signature. " +
                            "For any discrepancies, please reach out to the HR & Payroll Department within 5 working days.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY)
            );
            disclaimer.setAlignment(Element.ALIGN_CENTER);
            disclaimer.setSpacingBefore(15f);
            document.add(disclaimer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF payslip: " + e.getMessage(), e);
        }
    }

    private String maskAccount(String acc) {
        if (acc == null || acc.length() < 4) return "XXXX";
        return "XXXX-XXXX-" + acc.substring(acc.length() - 4);
    }

    private void addCell(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }

    private void addCellCenter(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }

    private void addCellRight(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        cell.setPadding(7f);
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }
}
