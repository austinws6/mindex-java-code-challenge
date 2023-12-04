package com.mindex.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Component
public class DataBootstrap {
    private static final String DATASTORE_LOCATION_EMPLOYEE = "/static/employee_database.json";
    private static final String DATASTORE_LOCATION_COMPENSATION = "/static/compensation_database.json";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private ObjectMapper objectMapperEmployee;

    @Autowired
    private ObjectMapper objectMapperCompensation;

    @PostConstruct
    public void init() {
        InputStream inputStreamEmployee = this.getClass().getResourceAsStream(DATASTORE_LOCATION_EMPLOYEE);
        InputStream inputStreamCompensation = this.getClass().getResourceAsStream(DATASTORE_LOCATION_COMPENSATION);

        Employee[] employees = null;
        Compensation[] compensations = null;

        // Load Employees
        try {
            employees = objectMapperEmployee.readValue(inputStreamEmployee, Employee[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Employee employee : employees) {
            employeeRepository.insert(employee);
        }

        // Load Compensations
        try {
            compensations = objectMapperCompensation.readValue(inputStreamCompensation, Compensation[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Compensation compensation : compensations) {
            compensationRepository.insert(compensation);
        }
    }
}
