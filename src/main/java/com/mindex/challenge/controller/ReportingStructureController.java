package com.mindex.challenge.controller;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReportingStructureController {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureController.class);

    @Autowired
    private ReportingStructureService reportingStructureService;

    @GetMapping("/ReportingStructure/{id}")
    public ReportingStructure calculate(@PathVariable String id) {
        LOG.debug("Received calculate reporting structure request for employee id [{}]", id);

        return reportingStructureService.generateReportingStructure(id);
    }
}
