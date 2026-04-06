package com.finance.dashboard.controller;

import com.finance.dashboard.dto.CreateRecordRequest;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.exception.AppException;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    @Autowired
    private FinancialRecordService recordService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new AppException(401, "Not authenticated"));
    }

    @PostMapping
    public ResponseEntity<FinancialRecord> create(
            @Valid @RequestBody CreateRecordRequest request,
            Principal principal) {
        return ResponseEntity.status(201)
                .body(recordService.createRecord(request, getCurrentUser(principal)));
    }

    @GetMapping
    public ResponseEntity<List<FinancialRecord>> getAll(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {

        if (type != null)
            return ResponseEntity.ok(recordService.getByType(type));
        if (category != null)
            return ResponseEntity.ok(recordService.getByCategory(category));
        if (from != null && to != null)
            return ResponseEntity.ok(recordService.getByDateRange(from, to));

        return ResponseEntity.ok(recordService.getAllRecords());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecord> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateRecordRequest request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<String> handleAppException(AppException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(400).body(errors);
    }
}
