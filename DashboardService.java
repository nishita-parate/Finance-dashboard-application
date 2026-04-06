package com.finance.dashboard.service;

import com.finance.dashboard.dto.DashboardSummaryResponse;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.repository.FinancialRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private FinancialRecordRepository recordRepository;

    public DashboardSummaryResponse getSummary() {

        BigDecimal totalIncome   = recordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        List<Object[]> raw = recordRepository.sumGroupByCategory();
        for (Object[] row : raw) {
            categoryTotals.put((String) row[0], (BigDecimal) row[1]);
        }

        return new DashboardSummaryResponse(
                totalIncome,
                totalExpenses,
                netBalance,
                categoryTotals,
                recordRepository.findTop5ByOrderByDateDesc()
        );
    }
}
