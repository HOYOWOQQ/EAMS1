package com.eams.Service.course;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Course;
import com.eams.Repository.course.CourseRepository;


@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;

	// 新增或更新（Spring JPA 自動辨識 insert or update）
	@Transactional
	public Course saveCourse(Course course) {
		return courseRepository.save(course);
	}

	// 查單筆
	public Course getCourseById(int id) {
		return courseRepository.findById(id).orElse(null);
	}

	// 查全部
	public List<Course> getAllCourse() {
		return courseRepository.findAll();
	}

	// 刪除
	@Transactional
	public void deleteCourseById(Integer courseId) {
	    Course course = courseRepository.findById(courseId).orElse(null);
	    course.getSubjects().clear();
	    courseRepository.save(course);

		courseRepository.deleteById(courseId);
	}

	// 查課程by學生
	public List<Course> getCourseByStudentId(Integer studentId){
		return courseRepository.findByStudentId(studentId);
	}
	

	// 驗證是否可以排課
//	public boolean isAvailable(int courseId, int classroomId, int teacherId, LocalDate date, int periodStart,
//			int periodEnd) {
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			CourseDao dao = new CourseDao(session);
//			if (dao.isAvailable(courseId, classroomId, teacherId, date, periodStart, periodEnd)) {
//				return true;
//			}
//		} catch (Exception e) {
//
//		}
//		return false;
//	}
//
//	// 新增科目到課程
//	public void addSubjectToCourse(int courseId, int subjectId) {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Transaction tx = null;
//		try {
//			tx = session.beginTransaction();
//
//			Course course = session.find(Course.class, courseId);
//			Subject subject = session.find(Subject.class, subjectId);
//
//			if (!course.getSubjects().contains(subject)) {
//				System.out.println("subject");
//				course.getSubjects().add(subject);
//			}
//
//			tx.commit();
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			e.printStackTrace();
//		} finally {
//			session.close();
//		}
//	}
//
//	// 新增科目到老師
//	public void addSubjectToTeacher(int teacherId, int subjectId) {
//		Session session = HibernateUtil.getSessionFactory().openSession();
//		Transaction tx = null;
//		try {
//			tx = session.beginTransaction();
//
//			Teacher teacher = session.find(Teacher.class, teacherId);
//			Subject subject = session.find(Subject.class, subjectId);
//
//			if (!teacher.getSubjects().contains(subject)) {
//				System.out.println("subject");
//				teacher.getSubjects().add(subject);
//			}
//
//			tx.commit();
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			e.printStackTrace();
//		} finally {
//			session.close();
//		}
//	}
}
