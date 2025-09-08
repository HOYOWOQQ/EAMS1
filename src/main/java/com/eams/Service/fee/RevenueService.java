package com.eams.Service.fee;

import com.eams.Entity.fee.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface RevenueService {
    RevenueOverviewDTO getOverview(LocalDate start, LocalDate end);
    Page<IncomeRowDTO> getIncomeRows(LocalDate start, LocalDate end, Pageable pageable);
    Page<ExpenseRowDTO> getExpenseRows(LocalDate start, LocalDate end, Pageable pageable);
    java.util.List<RevenueSeriesPointDTO> getSeries(LocalDate start, LocalDate end, String granularity);
    java.util.List<BreakdownItemDTO> getBreakdown(LocalDate start, LocalDate end, String dimension);
	Page<RefundRowDTO> getRefundRows(LocalDate start, LocalDate end, Pageable pageable);
}
