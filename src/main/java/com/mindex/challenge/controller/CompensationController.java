package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    @PostMapping("/Compensation/{id},{salary},{effectiveDate}")
    public Compensation create_update(@PathVariable String id, @PathVariable String salary, @PathVariable String effectiveDate) {
        LOG.debug("Received compensation create/update request for employee id [{}]", id);

        return compensationService.create_update(id, salary, effectiveDate);
    }

    @GetMapping("/Compensation/{id}")
    public Compensation read(@PathVariable String id) {
        LOG.debug("Received compensation read request for employee id [{}]", id);

        return compensationService.read(id);
    }
}
