package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

public interface CompensationService {
    Compensation create_update(String id, String salary, String effectiveDate);
    Compensation read(String id);
}
