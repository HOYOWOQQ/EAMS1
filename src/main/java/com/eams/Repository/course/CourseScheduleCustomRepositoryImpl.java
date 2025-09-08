package com.eams.Repository.course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Classroom;
import com.eams.Entity.course.Course;
import com.eams.Entity.course.CourseEnroll;
import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class CourseScheduleCustomRepositoryImpl implements CourseScheduleCustomRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<CourseSchedule> searchCourseSchedule(String userQuery, Integer roomId, Integer courseId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CourseSchedule> cq = cb.createQuery(CourseSchedule.class);
		Root<CourseSchedule> cs = cq.from(CourseSchedule.class);

		Join<CourseSchedule, Course> course = cs.join("course");
		Join<CourseSchedule, Teacher> teacher = cs.join("teacher");
		Join<CourseSchedule, Classroom> classroom = cs.join("classroom");

		List<Predicate> predicates = new ArrayList<>();

		if (userQuery != null && !userQuery.trim().isEmpty()) {
			Predicate teacherLike = cb.like(teacher.join("member").get("name"), "%" + userQuery.trim() + "%");

			// 學生姓名 like（存在於課程註冊名單裡）
			Subquery<Long> sq = cq.subquery(Long.class);
			Root<CourseEnroll> ce = sq.from(CourseEnroll.class);
			Join<CourseEnroll, Student> student = ce.join("student");
			Join<Student, Member> studentMember = student.join("member");
			sq.select(cb.literal(1L)).where(cb.and(cb.equal(ce.get("course"), cs.get("course")),
					cb.like(studentMember.get("name"), "%" + userQuery.trim() + "%")));

			predicates.add(cb.or(teacherLike, cb.exists(sq)));
		}
		if (roomId != null) {
			predicates.add(cb.equal(classroom.get("id"), roomId));
		}
		if (courseId != null) {
			predicates.add(cb.equal(course.get("id"), courseId));
		}

		cq.where(predicates.toArray(new Predicate[0]));
		cq.orderBy(cb.asc(cs.get("lessonDate")), cb.asc(cs.get("periodStart")));

		return entityManager.createQuery(cq).getResultList();
	}
}
