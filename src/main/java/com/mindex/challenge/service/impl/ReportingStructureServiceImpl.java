package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public ReportingStructure generateReportingStructure(String eid) {
        ReportingStructure reportingStructure = new ReportingStructure();
        int reportsCount = 0;

        //  Get the Employee from the eid passed into endpoint
        Employee topLevelEmployee = employeeRepository.findByEmployeeId(eid);
        if (topLevelEmployee == null) {
            throw new RuntimeException("No ReportingStructure generated because of invalid employeeId: " + eid);
        }

        // Call calculate() method to add up the total reports for topLevelEmployee and populate all off the employee's info
        reportingStructure = calculate(reportingStructure, topLevelEmployee, reportsCount);

        return reportingStructure;
    }

    private ReportingStructure calculate(ReportingStructure reportingStructure, Employee currentEmployee, int reportsCount) {
        // Get the list of direct reports for passed in Employee
        List<Employee> directReports = currentEmployee.getDirectReports();

        if (directReports != null) {
            // There are one or more reports under this Employee
            reportsCount = reportsCount + currentEmployee.getDirectReports().size();  // Add the count to reportsCount total

            // Loop thru all direct reports
            for (int index = 0; index < directReports.size(); index++) {
                String reportEmployeeID = directReports.get(index).getEmployeeId();
                Employee reportEmployee = employeeRepository.findByEmployeeId(reportEmployeeID);

                if (reportEmployee == null) {
                    throw new RuntimeException("Invalid employeeId: " + reportEmployeeID);
                }
                directReports.set(index, reportEmployee);  // Set reportEmployee info into the directReports array

                if (reportEmployee.getDirectReports() != null) {  // reportEmployee has direct reports
                    reportsCount = reportsCount + currentEmployee.getDirectReports().size();  // Add the count to reportsCount total
                    reportingStructure = calculate(reportingStructure, reportEmployee, reportsCount);  // Recursive call
                }
            }
        }
        reportingStructure.setEmployee(currentEmployee);
        reportingStructure.setNumberOfReports(reportsCount);

        return reportingStructure;
    }
}
