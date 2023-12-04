package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.CompensationService;

import com.mindex.challenge.service.ReportingStructureService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);
    private static final String DATASTORE_LOCATION_COMPENSATION = "src/main/resources/static/compensation_database.json";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private ObjectMapper objectMapperCompensation;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @Autowired
    private MongoClient mongoClient;

    @Override
    public Compensation create_update(String emp_id, String salary, String effectiveDate) {
        LOG.debug("Creating/Updating compensation for employee id [{}]", emp_id);

        Compensation compensation = new Compensation();
        Employee currentEmployee = employeeRepository.findByEmployeeId(emp_id);

        // Get all the Compensations from memory
        List<Compensation> compensationList = compensationRepository.findAll();
        LOG.debug("compensationList START size = " + compensationList.size());
        for(int f = 0; f < compensationList.size(); f++) {
            LOG.debug("   compensationList[" + f + "] = " + compensationList.get(f).getEmployee().getFirstName() +
                                                      " " + compensationList.get(f).getEmployee().getLastName());
        }

        boolean alreadyExists = false;
        // Loop thru all the Compensations, get it's employeeId and compare to passed in emp_id
        for(int z = 0; z < compensationList.size(); z++) {
            LOG.debug("Checking if Compensation with employeeId matching emp_id exists already...");

            LOG.debug("   compensationList[" + z + "] = " + compensationList.get(z).getEmployee().getEmployeeId());
            LOG.debug("                emp_id = " + emp_id);
            if(compensationList.get(z).getEmployee().getEmployeeId().equals(emp_id)) {
                LOG.debug("   Compensation FOUND!");
                alreadyExists = true;
                compensation = compensationList.get(z);

                break;  // Found it, stop looking
            } else {
                LOG.debug("   Compensation NOT FOUND...");
            }
        }

        if(alreadyExists) {  // Remove it
            LOG.debug("Compensation already exists, removing it from memory...");

            MongoDatabase database = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = database.getCollection("compensation");
            LOG.debug("Collection:compensation size = " + collection.countDocuments());
            Bson filter = Filters.eq("compensationId", compensation.getCompensationId());
            collection.deleteOne(filter);  // Remove this Compensation
        }

        // Now create a new Compensation
        LOG.debug("Creating new Compensation...");
        compensation.setCompensationId(UUID.randomUUID().toString());
        currentEmployee = populateAllFields(currentEmployee);  // Replaces missing data for nulls
        compensation.setEmployee(currentEmployee);
        compensation.setSalary(salary);
        compensation.setEffectiveDate(effectiveDate);
        compensationRepository.insert(compensation);

        // Now write to compensation_database.json file for all Compensations in memory
        compensationList = compensationRepository.findAll();
        LOG.debug("compensationList size AFTER delete/insert = " + compensationList.size());
        for(int f = 0; f < compensationList.size(); f++) {
            LOG.debug("   compensationList[" + f + "] = " + compensationList.get(f).getEmployee().getFirstName() +
                                                      " " + compensationList.get(f).getEmployee().getLastName());
        }

        String jsonOutput = "[\n";
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATASTORE_LOCATION_COMPENSATION));
            if(!compensationList.isEmpty()) {
                for (int x = 0; x < compensationList.size(); x++) {
                    jsonOutput = jsonOutput + objectMapperCompensation.writerWithDefaultPrettyPrinter().writeValueAsString(compensationList.get(x));

                    if (x != compensationList.size() - 1) {
                        jsonOutput = jsonOutput + "\n,\n";
                    } else {
                        jsonOutput = jsonOutput + "\n]";
                    }
                }
            } else {
                jsonOutput = jsonOutput + "\n]";
            }
            writer.write(jsonOutput);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return compensation;
    }

    @Override
    public Compensation read(String id) {
        LOG.debug("Reading compensation for employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);
        Compensation compensation = new Compensation();

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        // Get all the Compensations from memory
        List<Compensation> compensationList = compensationRepository.findAll();

        // Loop thru all the Compensations, get it's employeeId and compare to passed in id
        for(int f = 0; f < compensationList.size(); f++) {
            if(compensationList.get(f).getEmployee().getEmployeeId().equals(id)) {
                compensation = compensationList.get(f);
                break;
            }
        }

        if (compensation == null) {
            throw new RuntimeException("No Compensation found for employeeId: " + id);
        }

        return compensation;
    }

    public Employee populateAllFields(Employee employee) {
        ReportingStructure reportingStructure = reportingStructureService.generateReportingStructure(employee.getEmployeeId());
        employee = reportingStructure.getEmployee();

        return employee;
    }
}
