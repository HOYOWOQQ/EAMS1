package com.eams.Repository.attendance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.attendance.Attendance;
import com.eams.Entity.course.Course;
import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

@Repository
@Transactional
public class AttendanceCustomRepositoryImpl implements AttendanceCustomRepository{
	@PersistenceContext
    private EntityManager em;

    @Override
    public List<Attendance> searchAttendance(String keyword, String status, Integer courseId, LocalDate date) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Attendance> cq = cb.createQuery(Attendance.class);
        Root<Attendance> a = cq.from(Attendance.class);

        Join<Attendance, Student> student = a.join("student");
        Join<Student, Member> member = student.join("member");
        Join<Attendance, CourseSchedule> cs = a.join("courseSchedule");
        Join<CourseSchedule, Course> course = cs.join("course");

        List<Predicate> conditions = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.add(cb.like(member.get("name"), "%" + keyword.trim() + "%"));
        }
        if (status != null && !status.trim().isEmpty()) {
            conditions.add(cb.equal(a.get("status"), status));
        }
        if (courseId != null) {
            conditions.add(cb.equal(course.get("id"), courseId));
        }
        if (date != null) {
            conditions.add(cb.equal(cs.get("lessonDate"), date));
        }

        cq.where(conditions.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(cs.get("lessonDate")), cb.asc(cs.get("periodStart")));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public void rollCallUpdate(int courseScheduleId, List<Integer> attendStudentIds) {
        // 全部先標記為未出席
        String jpql1 = "UPDATE Attendance a SET a.status = 'ABSENT' WHERE a.courseSchedule.id = :csid";
        em.createQuery(jpql1).setParameter("csid", courseScheduleId).executeUpdate();

        // 把有出席的人改為 ATTEND
        if (attendStudentIds != null && !attendStudentIds.isEmpty()) {
            String jpql2 = "UPDATE Attendance a SET a.status = 'ATTEND' WHERE a.courseSchedule.id = :csid AND a.student.id IN :ids";
            em.createQuery(jpql2)
              .setParameter("csid", courseScheduleId)
              .setParameter("ids", attendStudentIds)
              .executeUpdate();
        }
    }

    @Override
    public void updateStatusToLeaveByLeaveRequestId(int leaveRequestId) {
        String jpql = "UPDATE Attendance a SET a.status = 'LEAVE' WHERE a.leaveRequest.id = :lid";
        em.createQuery(jpql).setParameter("lid", leaveRequestId).executeUpdate();
    }
}
