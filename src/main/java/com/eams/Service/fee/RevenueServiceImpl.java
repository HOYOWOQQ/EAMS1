package com.eams.Service.fee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eams.Entity.fee.PaymentNotice;
import com.eams.Entity.fee.DTO.BreakdownItemDTO;
import com.eams.Entity.fee.DTO.ExpenseRowDTO;
import com.eams.Entity.fee.DTO.IncomeRowDTO;
import com.eams.Entity.fee.DTO.RefundRowDTO;
import com.eams.Entity.fee.DTO.RevenueOverviewDTO;
import com.eams.Entity.fee.DTO.RevenueSeriesPointDTO;
import com.eams.Entity.scholarship.ScholarshipGrant;
import com.eams.Repository.fee.PaymentNoticeRepository;
import com.eams.Repository.scholarshipGrant.ScholarshipRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {
	
	@Autowired
	private PaymentNoticeRepository paymentNoticeRepo;
	
	@Autowired
	private ScholarshipRepository grantRepo;

	@Override
	public RevenueOverviewDTO getOverview(LocalDate start, LocalDate end) {
		Long paid = paymentNoticeRepo.sumPaidInRange(start, end);
		
		
		System.out.println("收益!!!!"+paid+start +end);
		
        // 應收：用 createdAt 篩期間已開立但未繳（避免沒有 payDate 的單據被漏抓）
        LocalDateTime sCreated = start != null ? start.atStartOfDay() : null;
        LocalDateTime eCreated = end   != null ? end.atTime(LocalTime.MAX) : null;
       
        Long receivable = paymentNoticeRepo.sumReceivableInCreatedRange(sCreated, eCreated);
        Long expense = grantRepo.sumExpenseInRange(sCreated, eCreated);
        Long refund = paymentNoticeRepo.sumRefundInRange(start, end); // ✅ 新增查詢
        
        long totalRevenue = paid != null ? paid : 0L;
        long accountsReceivable = receivable != null ? receivable : 0L;
        long scholarshipExpense = expense != null ? expense : 0L;
        long refundExpense = refund != null ? refund : 0L;
        long netIncome = totalRevenue - scholarshipExpense;

        return RevenueOverviewDTO.builder()
                .totalRevenue(totalRevenue)
                .accountsReceivable(accountsReceivable)
                .scholarshipExpense(scholarshipExpense)
//                .refundExpense(refundExpense)  // ✅ 新增
                .netIncome(netIncome)
                // Delta 可留待你做同比計算（例如換上前一期間數據）
                .revenueDelta(null).arDelta(null).scholarDelta(null).netDelta(null)
                .build();
	}

	@Override
	public Page<IncomeRowDTO> getIncomeRows(LocalDate start, LocalDate end, Pageable pageable) {
		// TODO Auto-generated method stub
		return  paymentNoticeRepo.findIncomeRows(start, end, pageable);
	}
	
	@Override
	public Page<RefundRowDTO> getRefundRows(LocalDate start, LocalDate end, Pageable pageable) {
	    return paymentNoticeRepo.findRefundRows(start, end, pageable);
	}

	@Override
	public Page<ExpenseRowDTO> getExpenseRows(LocalDate start, LocalDate end, Pageable pageable) {
		LocalDateTime s = start != null ? start.atStartOfDay() : null;
        LocalDateTime e = end   != null ? end.atTime(LocalTime.MAX) : null;
        return grantRepo.findExpenseRows(s, e, pageable);
	}

	@Override
	public List<RevenueSeriesPointDTO> getSeries(LocalDate start, LocalDate end, String granularity) {
		// 取「已收」與「有效獎學金」資料，Service 聚合成序列（避免資料庫相依）
        List<PaymentNotice> paidList = paymentNoticeRepo.findAllPaidInRange(start, end);
        List<ScholarshipGrant> expList = grantRepo.findAllEffectiveInRange(
                start != null ? start.atStartOfDay() : null,
                end   != null ? end.atTime(LocalTime.MAX) : null
        );
     // 累加退費
        List<PaymentNotice> refundList = paymentNoticeRepo.findAllRefundedInRange(start, end);

        boolean byMonth = !"day".equalsIgnoreCase(granularity); // 預設 month
        DateTimeFormatter fmt = byMonth ? DateTimeFormatter.ofPattern("yyyy-MM")
                                        : DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Long> incomeMap = new LinkedHashMap<>();
        Map<String, Long> expenseMap = new LinkedHashMap<>();
        Map<String, Long> refundMap = new LinkedHashMap<>();

        // 先把時間軸打底（避免缺月/缺日斷裂）
        if (start != null && end != null) {
            LocalDate cursor = byMonth ? start.withDayOfMonth(1) : start;
            LocalDate last = end;
            while (!cursor.isAfter(last)) {
                String label = byMonth ? cursor.format(fmt) :
                        cursor.format(fmt);
                incomeMap.putIfAbsent(label, 0L);
                expenseMap.putIfAbsent(label, 0L);
                cursor = byMonth ? cursor.plusMonths(1).withDayOfMonth(1)
                        : cursor.plusDays(1);
            }
        }

        // 累加學費收入
        for (PaymentNotice p : paidList) {
            LocalDate d = p.getPayDate();
            if (d == null) continue;
            String label = (byMonth ? d.withDayOfMonth(1) : d).format(fmt);
            incomeMap.merge(label, (long)Integer.parseInt(p.getNetAmount()) , Long::sum);
        }

        // 累加獎學金支出
        for (ScholarshipGrant g : expList) {
            LocalDate d = g.getGrantTime().toLocalDate();
            String label = (byMonth ? d.withDayOfMonth(1) : d).format(fmt);
            expenseMap.merge(label, (long) Integer.parseInt(g.getAmount()), Long::sum);
        }
     // 累加退費
        for (PaymentNotice r : refundList) {
            LocalDateTime d = r.getCreatedAt();
            if (d == null) continue;
            String label = (byMonth ? d.withDayOfMonth(1) : d).format(fmt);
            expenseMap.merge(label, (long) Integer.parseInt(r.getNetAmount()), Long::sum);
        }

        // 組裝序列
        Set<String> labels = new LinkedHashSet<>();
        labels.addAll(incomeMap.keySet());
        labels.addAll(expenseMap.keySet());
        labels.addAll(refundMap.keySet());
        List<String> sorted = labels.stream().sorted().collect(Collectors.toList());

        List<RevenueSeriesPointDTO> out = new ArrayList<>();
        for (String label : sorted) {
            long inc = incomeMap.getOrDefault(label, 0L);
            long exp = expenseMap.getOrDefault(label, 0L);
            long ref = refundMap.getOrDefault(label, 0L);
            out.add(RevenueSeriesPointDTO.builder()
                    .label(label)
                    .tuitionPaid(inc)
                    .scholarshipExpense(exp)
                    .refundExpense(ref)
                    .netIncome(inc - exp  - ref)
                    .build());
        }
        return out;
	}

	@Override
	 public List<BreakdownItemDTO> getBreakdown(LocalDate start, LocalDate end, String dimension) {
        // 目前提供課程類型（courseType）作為收入分佈，
        // 你可再延伸為 courseName、grade、subject 等。
        if (dimension == null || "courseType".equalsIgnoreCase(dimension)) {
        	List<Object[]> rows = paymentNoticeRepo.sumPaidByCourseType(start, end);

        	return rows.stream()
                    .map(r -> BreakdownItemDTO.builder()
                        .label(r[0] != null ? (String) r[0] : "未分類")
                        .amount(((Number) r[1]).longValue())
                        .build()
                    )
                    .collect(java.util.stream.Collectors.toList());
            }
            return java.util.Collections.emptyList();
	}
}
