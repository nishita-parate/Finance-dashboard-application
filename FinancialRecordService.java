package com.finance.dashboard.service;

import com.finance.dashboard.dto.CreateRecordRequest;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.exception.AppException;
import com.finance.dashboard.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialRecordService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    public FinancialRecord createRecord(CreateRecordRequest req, User currentUser) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(req.getAmount());
        record.setType(req.getType());
        record.setCategory(req.getCategory());
        record.setDate(req.getDate());
        record.setNotes(req.getNotes());
        record.setCreatedBy(currentUser);
        return recordRepository.save(record);
    }

    public List<FinancialRecord> getAllRecords() {
        return recordRepository.findAll();
    }

    public List<FinancialRecord> getByType(TransactionType type) {
        return recordRepository.findByType(type);
    }

    public List<FinancialRecord> getByCategory(String category) {
        return recordRepository.findByCategory(category);
    }

    public List<FinancialRecord> getByDateRange(LocalDate from, LocalDate to) {
        return recordRepository.findByDateBetween(from, to);
    }

    public FinancialRecord updateRecord(Long id, CreateRecordRequest req) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Record not found"));
        record.setAmount(req.getAmount());
        record.setType(req.getType());
        record.setCategory(req.getCategory());
        record.setDate(req.getDate());
        record.setNotes(req.getNotes());
        return recordRepository.save(record);
    }

    public void deleteRecord(Long id) {
        if (!recordRepository.existsById(id)) {
            throw new AppException(404, "Record not found");
        }
        recordRepository.deleteById(id);
    }
}
