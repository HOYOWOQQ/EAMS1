package com.eams.Repository.attendance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.attendance.LeaveRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

@Repository
@Transactional(readOnly = true)
public class LeaveRequestCustomRepositoryImpl implements LeaveRequestCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<LeaveRequest> search(
            Integer studentId,
            Integer courseScheduleId,
            String status,
            LocalDateTime submittedFrom,
            LocalDateTime submittedTo,
            Boolean hasAttachment,
            String keyword,
            Pageable pageable) {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        // 主查詢
        CriteriaQuery<LeaveRequest> cq = cb.createQuery(LeaveRequest.class);
        Root<LeaveRequest> root = cq.from(LeaveRequest.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // 條件篩選
        if (studentId != null) {
            predicates.add(cb.equal(root.get("student").get("id"), studentId));
        }
        if (courseScheduleId != null) {
            predicates.add(cb.equal(root.get("courseSchedule").get("id"), courseScheduleId));
        }
        if (status != null && !status.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (submittedFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("submittedAt"), submittedFrom));
        }
        if (submittedTo != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("submittedAt"), submittedTo));
        }
        if (Boolean.TRUE.equals(hasAttachment)) {
            predicates.add(cb.isNotNull(root.get("attachmentPath")));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("reason")), "%" + keyword.toLowerCase() + "%"));
        }
        
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("submittedAt")));
        
        // 分頁查詢
        var query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<LeaveRequest> content = query.getResultList();
        
        // 計算總數
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<LeaveRequest> countRoot = countQuery.from(LeaveRequest.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();
        
        return new PageImpl<>(content, pageable, total);
    }
}