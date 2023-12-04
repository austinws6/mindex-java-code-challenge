Mindex Java Code Challenge
==========================
NOTES:
    - If an employee does not have any reports under them, the value is null for the directReports field in Employee.
    - I also left DEBUG log statements in so you could see how things were flowing while running endpoints.
    - compensation_database.json is empty to start with, only "[]" before start of running endpoints.


TASK 1:  ReportingStructure
    Endpoint:
        localhost:8080/ReportingStructure/{employee id}
            - GET
            - Example:  localhost:8080/ReportingStructure/16a596ae-edd3-4847-99fe-c4518e82c86f
            - Returns:  ReportingStructure that contains a fully populated Employee and numberOfReports for the specified employeeId.


TASK 2:  Compensation
    Endpoints:
        localhost:8080/Compensation/{id}
            - GET
            - Example:  localhost:8080/compensation/c0c2293d-16bd-4603-8e08-638a9d18b22c
            - Returns:  Compensation that contains a fully populated employee, salary, and effectiveDate

        localhost:8080/Compensation/{id},{salary},{effectiveDate}
            - POST
            - Creates a new Compensation, or if the employee id passed in already has a Compensation, it updates that Compensation
            - Example:  localhost:8080/Compensation/c0c2293d-16bd-4603-8e08-638a9d18b22c,120000.00 Annual,2023-12-1
            - Returns:  Compensation that contains a fully populated employee, salary, and effectiveDate

    Persist:  If the webserver is shut down and restarted, the changes from a POST will be loaded into memory and be able to be
              pulled again via the GET.
