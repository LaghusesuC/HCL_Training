package com.hcl.training.payroll.init;

import com.hcl.training.payroll.domain.entity.*;
import com.hcl.training.payroll.repository.*;
import com.hcl.training.payroll.service.PayrollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final DepartmentRepository departmentRepository;
    private final SalaryComponentRepository componentRepository;
    private final SalaryStructureRepository structureRepository;
    private final EmployeeRepository employeeRepository;
    private final AppUserRepository appUserRepository;
    private final AttendanceSummaryRepository attendanceRepository;
    private final PayrollService payrollService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(DepartmentRepository departmentRepository,
                           SalaryComponentRepository componentRepository,
                           SalaryStructureRepository structureRepository,
                           EmployeeRepository employeeRepository,
                           AppUserRepository appUserRepository,
                           AttendanceSummaryRepository attendanceRepository,
                           PayrollService payrollService,
                           PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.componentRepository = componentRepository;
        this.structureRepository = structureRepository;
        this.employeeRepository = employeeRepository;
        this.appUserRepository = appUserRepository;
        this.attendanceRepository = attendanceRepository;
        this.payrollService = payrollService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (appUserRepository.count() > 0) {
            logger.info("Database already seeded with demo data.");
            return;
        }

        logger.info("Initializing enterprise demo data for Payroll Management System...");

        // 1. Departments
        Department deptEng = departmentRepository.save(new Department("Engineering & Technology", "ENG"));
        Department deptHr = departmentRepository.save(new Department("Human Resources & Talent", "HR"));
        Department deptFin = departmentRepository.save(new Department("Finance & Accounts", "FIN"));
        Department deptOps = departmentRepository.save(new Department("Cloud Operations & DevOps", "OPS"));
        Department deptQa = departmentRepository.save(new Department("Quality Assurance", "QA"));

        // 2. Salary Components
        SalaryComponent compBasic = componentRepository.save(new SalaryComponent(
                "BASIC", "Basic Salary", SalaryComponent.Type.EARNING,
                SalaryComponent.CalculationMode.FIXED_AMOUNT, BigDecimal.valueOf(50000.00)
        ));

        SalaryComponent compHra = componentRepository.save(new SalaryComponent(
                "HRA", "House Rent Allowance", SalaryComponent.Type.EARNING,
                SalaryComponent.CalculationMode.PERCENTAGE_OF_BASIC, BigDecimal.valueOf(40.00)
        ));

        SalaryComponent compSpl = componentRepository.save(new SalaryComponent(
                "SPECIAL_ALLOWANCE", "Special Allowance", SalaryComponent.Type.EARNING,
                SalaryComponent.CalculationMode.FIXED_AMOUNT, BigDecimal.valueOf(12000.00)
        ));

        SalaryComponent compConv = componentRepository.save(new SalaryComponent(
                "CONVEYANCE", "Conveyance Allowance", SalaryComponent.Type.EARNING,
                SalaryComponent.CalculationMode.FIXED_AMOUNT, BigDecimal.valueOf(3000.00)
        ));

        SalaryComponent compBonus = componentRepository.save(new SalaryComponent(
                "PERFORMANCE_BONUS", "Performance Bonus", SalaryComponent.Type.EARNING,
                SalaryComponent.CalculationMode.FIXED_AMOUNT, BigDecimal.valueOf(5000.00)
        ));

        SalaryComponent compInsurance = componentRepository.save(new SalaryComponent(
                "HEALTH_INSURANCE", "Corporate Health Insurance", SalaryComponent.Type.DEDUCTION,
                SalaryComponent.CalculationMode.FIXED_AMOUNT, BigDecimal.valueOf(1500.00)
        ));

        // 3. Salary Structures
        // Structure 1: Senior Engineering Band
        SalaryStructure structSeniorEng = new SalaryStructure("Senior Engineering Band", "Compensation package for Senior Engineers & Architects");
        structSeniorEng.addComponent(compBasic, BigDecimal.valueOf(65000.00));
        structSeniorEng.addComponent(compHra, BigDecimal.valueOf(40.00));
        structSeniorEng.addComponent(compSpl, BigDecimal.valueOf(15000.00));
        structSeniorEng.addComponent(compConv, BigDecimal.valueOf(3500.00));
        structSeniorEng.addComponent(compBonus, BigDecimal.valueOf(5000.00));
        structSeniorEng.addComponent(compInsurance, BigDecimal.valueOf(1500.00));
        structSeniorEng = structureRepository.save(structSeniorEng);

        // Structure 2: Technical Professional Band
        SalaryStructure structCoreEng = new SalaryStructure("Technical Professional Band", "Compensation package for Software Engineers & SREs");
        structCoreEng.addComponent(compBasic, BigDecimal.valueOf(45000.00));
        structCoreEng.addComponent(compHra, BigDecimal.valueOf(40.00));
        structCoreEng.addComponent(compSpl, BigDecimal.valueOf(9000.00));
        structCoreEng.addComponent(compConv, BigDecimal.valueOf(2500.00));
        structCoreEng.addComponent(compInsurance, BigDecimal.valueOf(1200.00));
        structCoreEng = structureRepository.save(structCoreEng);

        // Structure 3: Operations & Corporate Band
        SalaryStructure structCorp = new SalaryStructure("Operations & Corporate Band", "Compensation package for HR, Finance, and Operations");
        structCorp.addComponent(compBasic, BigDecimal.valueOf(40000.00));
        structCorp.addComponent(compHra, BigDecimal.valueOf(35.00));
        structCorp.addComponent(compSpl, BigDecimal.valueOf(7000.00));
        structCorp.addComponent(compConv, BigDecimal.valueOf(2000.00));
        structCorp.addComponent(compInsurance, BigDecimal.valueOf(1000.00));
        structCorp = structureRepository.save(structCorp);

        // 4. Employees
        Employee emp1 = new Employee("EMP001", "Rajesh Sharma", "rajesh.sharma@hcl.com", "Principal Architect", deptEng, structSeniorEng);
        emp1.setDateOfJoining(LocalDate.of(2021, 3, 15));
        emp1.setPanNumber("ABCPS1234F");
        emp1.setBankAccountNumber("987654321011");
        emp1 = employeeRepository.save(emp1);

        Employee emp2 = new Employee("EMP002", "Priya Nair", "priya.nair@hcl.com", "Senior Full-Stack Engineer", deptEng, structCoreEng);
        emp2.setDateOfJoining(LocalDate.of(2022, 7, 1));
        emp2.setPanNumber("BTRPN5678K");
        emp2.setBankAccountNumber("456789123455");
        emp2 = employeeRepository.save(emp2);

        Employee emp3 = new Employee("EMP003", "Amit Verma", "amit.verma@hcl.com", "Lead Talent Partner", deptHr, structCorp);
        emp3.setDateOfJoining(LocalDate.of(2023, 1, 10));
        emp3.setPanNumber("CPWAV9012L");
        emp3.setBankAccountNumber("112233445566");
        emp3 = employeeRepository.save(emp3);

        Employee emp4 = new Employee("EMP004", "Sneha Patel", "sneha.patel@hcl.com", "Senior Financial Analyst", deptFin, structCorp);
        emp4.setDateOfJoining(LocalDate.of(2023, 5, 20));
        emp4.setPanNumber("DKTSP3456M");
        emp4.setBankAccountNumber("998877665544");
        emp4 = employeeRepository.save(emp4);

        Employee emp5 = new Employee("EMP005", "Vikramaditya Singh", "vikram.singh@hcl.com", "DevOps & Cloud Engineer", deptOps, structCoreEng);
        emp5.setDateOfJoining(LocalDate.of(2022, 11, 15));
        emp5.setPanNumber("EPSVS7890N");
        emp5.setBankAccountNumber("334455667788");
        emp5 = employeeRepository.save(emp5);

        // 5. App Users
        // Admin
        AppUser adminUser = new AppUser("admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN, null);
        appUserRepository.save(adminUser);

        // Payroll Officer
        AppUser officerUser = new AppUser("officer", passwordEncoder.encode("officer123"), Role.ROLE_PAYROLL_OFFICER, null);
        appUserRepository.save(officerUser);

        // Employees
        AppUser emp1User = new AppUser("emp001", passwordEncoder.encode("emp123"), Role.ROLE_EMPLOYEE, emp1);
        appUserRepository.save(emp1User);

        AppUser emp2User = new AppUser("emp002", passwordEncoder.encode("emp123"), Role.ROLE_EMPLOYEE, emp2);
        appUserRepository.save(emp2User);

        // 6. Attendance for current month (2026-10)
        attendanceRepository.save(new AttendanceSummary(emp1, "2026-10", 30, 30, 0));
        attendanceRepository.save(new AttendanceSummary(emp2, "2026-10", 30, 28, 2));
        attendanceRepository.save(new AttendanceSummary(emp3, "2026-10", 30, 29, 1));
        attendanceRepository.save(new AttendanceSummary(emp4, "2026-10", 30, 30, 0));
        attendanceRepository.save(new AttendanceSummary(emp5, "2026-10", 30, 27, 3));

        // 7. Calculate initial payroll run for 2026-10
        try {
            payrollService.executePayrollRun("2026-10");
            logger.info("Successfully executed initial payroll run for 2026-10 with OpenPDF payslips.");
        } catch (Exception ex) {
            logger.warn("Initial payroll run calculation deferred: {}", ex.getMessage());
        }

        logger.info("Enterprise demo data initialized successfully!");
    }
}
