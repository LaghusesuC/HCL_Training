# Payroll Management System

A Java-based payroll management system that calculates monthly employee
salaries using salary structures, attendance, and deductions. It
supports payroll approval, payslip generation, and department-wise cost
reports.

## 1. Functional Requirements

-   **FR1:** Admin can define salary components and salary structures,
    including basic pay, HRA, and allowances.
-   **FR2:** Payroll Officer can assign salary structures to employees.
-   **FR3:** Payroll Officer can run payroll for a selected month.
-   **FR4:** The system calculates gross salary, deductions, loss of
    pay, and net salary for each employee.
-   **FR5:** Payroll Officer can review and approve a payroll run by
    locking it.
-   **FR6:** Employees can view and download their own payslips and
    access payslip history.
-   **FR7:** The system prevents changes to a locked payroll run.
-   **FR8:** Admin can view department-wise payroll cost reports.

## 2. Non-Functional Requirements

-   **NFR-P1 --- Accuracy:** Represent monetary values using
    `BigDecimal` and apply a defined rounding policy.
-   **NFR-P2 --- Performance:** Process payroll for 1,000 employees in
    less than 30 seconds under the agreed test environment.
-   **NFR-P3 --- Security:** Encrypt sensitive salary data at rest and
    never write salary details or secrets to application logs.
-   **NFR-P4 --- Idempotency:** Re-running payroll for an unlocked month
    must not create duplicate or inconsistent payroll entries.
-   **Security and access control:** Restrict administrative, payroll,
    and employee operations according to roles.
-   **Data integrity:** Validate employee, attendance, salary structure,
    and payroll data before calculations and approval.

## 3. Proposed Methodology

1.  **Authentication and employee management:** Authenticate users,
    apply role-based access, and maintain employee and department
    information.
2.  **Salary structure setup:** Admin defines earning and deduction
    components; Payroll Officer assigns a structure to each employee.
3.  **Attendance preparation:** Load or maintain monthly attendance
    summaries and calculate loss of pay according to configured rules.
4.  **Payroll calculation:** Calculate earnings, gross salary,
    deductions (including simplified PF, professional tax, and TDS), and
    net salary.
5.  **Review and approval:** Payroll Officer reviews entries and locks
    the payroll run after approval.
6.  **Payslip generation:** Generate PDF payslips and make them
    available to the corresponding employees.
7.  **Reporting:** Aggregate payroll costs by department and produce
    payroll/statutory reports.
8.  **Validation and testing:** Test calculations, access permissions,
    idempotency, locking, performance, and error handling.

> Tax and statutory deductions are simplified for this project and
> should not be treated as production tax or legal advice.

## 4. System Architecture

The system uses a modular, service-oriented architecture.

``` text
                  +----------------------+
                  | Web UI / REST Client |
                  +----------+-----------+
                             |
                  +----------v-----------+
                  | API / Authentication |
                  | Role-Based Access    |
                  +----------+-----------+
                             |
             +---------------+----------------+
             |               |                |
   +---------v--------+ +----v------------+ +--v----------------+
   | auth-service     | | payroll-service | | payslip-service   |
   | Users, roles,    | | Salary setup,   | | PDF payslips,     |
   | employees        | | attendance,     | | history, reports  |
   |                  | | payroll runs    | |                  |
   +------------------+ +--------+--------+ +--------+---------+
                                  |                    |
                                  +------ REST / Feign-+
                                  |
                       +----------v-----------+
                       | Relational Database  |
                       | Payroll and Employee |
                       | Records              |
                       +----------------------+
```

### Main modules

-   **M1 --- Authentication and Employee (`auth-service`):** User
    authentication, roles, employee records, and access control.
-   **M2 --- Salary Structure (`payroll-service`):** Salary components,
    structures, and employee assignments.
-   **M3 --- Payroll Run (`payroll-service`):** Attendance-based
    calculations, deductions, payroll review, and locking.
-   **M4 --- Payslips (`payslip-service`):** Payslip generation,
    storage/retrieval, and employee payslip history.
-   **M5 --- Reports (`payslip-service`):** Department-wise cost
    summaries and simplified statutory reports.

## 5. ER Diagram

``` mermaid
erDiagram
    DEPARTMENT ||--o{ EMPLOYEE : contains
    EMPLOYEE ||--o{ PAYROLL_ENTRY : receives
    EMPLOYEE ||--o{ ATTENDANCE_SUMMARY : has
    EMPLOYEE }o--|| SALARY_STRUCTURE : assigned
    SALARY_STRUCTURE ||--o{ STRUCTURE_COMPONENT : includes
    SALARY_COMPONENT ||--o{ STRUCTURE_COMPONENT : defines
    PAYROLL_RUN ||--o{ PAYROLL_ENTRY : contains
    PAYROLL_ENTRY ||--o{ DEDUCTION : records
    PAYROLL_ENTRY ||--o| PAYSLIP : generates
    EMPLOYEE ||--o| APP_USER : may_have

    DEPARTMENT {
        bigint department_id PK
        string department_name
    }
    EMPLOYEE {
        bigint employee_id PK
        bigint department_id FK
        bigint salary_structure_id FK
        string employee_number
        string full_name
    }
    APP_USER {
        bigint user_id PK
        bigint employee_id FK
        string username
        string role
    }
    SALARY_COMPONENT {
        bigint component_id PK
        string component_name
        string component_type
    }
    SALARY_STRUCTURE {
        bigint structure_id PK
        string structure_name
    }
    STRUCTURE_COMPONENT {
        bigint structure_id FK
        bigint component_id FK
        decimal amount_or_rate
    }
    ATTENDANCE_SUMMARY {
        bigint attendance_id PK
        bigint employee_id FK
        string payroll_month
        int payable_days
        int loss_of_pay_days
    }
    PAYROLL_RUN {
        bigint run_id PK
        string payroll_month
        string status
    }
    PAYROLL_ENTRY {
        bigint entry_id PK
        bigint run_id FK
        bigint employee_id FK
        decimal gross_salary
        decimal total_deductions
        decimal net_salary
    }
    DEDUCTION {
        bigint deduction_id PK
        bigint entry_id FK
        string deduction_type
        decimal amount
    }
    PAYSLIP {
        bigint payslip_id PK
        bigint entry_id FK
        string document_reference
    }
```

*Note: This is a logical ER diagram. Adjust columns, constraints, and
relationships to match the final database implementation.*

## 6. Tools and Technologies

  -----------------------------------------------------------------------
  Area                                Technology
  ----------------------------------- -----------------------------------
  Language                            Java

  Backend framework                   Spring Boot

  Inter-service communication         REST APIs, Spring Cloud OpenFeign

  Database                            Relational database (e.g., MySQL or
                                      PostgreSQL)

  Data access                         Spring Data JPA / Hibernate

  API testing and documentation       Postman, Swagger / OpenAPI

  Build tool                          Maven

  Version control                     Git and GitHub

  PDF generation                      Java PDF library (choose during
                                      implementation)

  Testing                             JUnit, Mockito

  Concurrency                         `CompletableFuture` for parallel
                                      employee calculations where
                                      appropriate
  -----------------------------------------------------------------------

## 7. Design Concepts

-   **Strategy Pattern:** Select the configured simplified tax regime
    calculation.
-   **Factory Pattern:** Create the appropriate salary component
    calculator.
-   **Builder Pattern:** Construct payslip objects.
-   **Java `record Money`:** Encapsulate monetary values and rounding
    rules.
-   **Sealed component types:** Model earning and deduction components.
-   **Streams:** Aggregate department-wise payroll totals.
-   **CompletableFuture:** Support parallel calculations with controlled
    concurrency.
-   **OpenFeign:** Call payroll APIs from the payslip service.

## 8. Main API Endpoints

  -------------------------------------------------------------------------------------
  Method                  Endpoint                              Purpose
  ----------------------- ------------------------------------- -----------------------
  `POST`                  `/api/v1/payroll-runs`                Create/run payroll for
                                                                a month

  `GET`                   `/api/v1/payroll-runs/{id}/entries`   View payroll entries

  `POST`                  `/api/v1/payroll-runs/{id}/lock`      Approve and lock a
                                                                payroll run

  `GET`                   `/api/v1/payslips/me`                 View the authenticated
                                                                employee's payslips
  -------------------------------------------------------------------------------------

## 9. Core Data Entities

`employee`, `salary_component`, `salary_structure`,
`structure_component`, `payroll_run`, `payroll_entry`, `deduction`,
`payslip`, `attendance_summary`, and `app_user`.

## 10. Getting Started

Prerequisites:

-   JDK version compatible with the project (Java 17 or later
    recommended)
-   Maven
-   MySQL or PostgreSQL
-   Git

After cloning the repository, configure database connection details and
encryption settings using environment variables or an external
configuration file. Do not commit passwords, API keys, or encryption
keys.

Typical Maven commands:

``` bash
mvn clean test
mvn spring-boot:run
```

Run each service from its own module if the repository is organized as a
multi-module project. Update these commands to match the final project
structure.

## 11. Future Enhancements

-   Integrate validated attendance and leave systems.
-   Add audit trails for payroll changes and approvals.
-   Support configurable statutory rules and effective dates.
-   Add automated reconciliation, notifications, and downloadable
    reports.
-   Add integration, load, and security tests.
