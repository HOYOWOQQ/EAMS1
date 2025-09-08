package com.eams.Service.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eams.Entity.member.Teacher;
import com.eams.Repository.member.TeacherRepository;

import jakarta.transaction.Transactional;

@Service
public class TeacherService {

	@Autowired
	private TeacherRepository teacherRepository;

	// 新增
	@Transactional
	public void saveTeacher(Teacher teacher) {
		teacherRepository.save(teacher);
	}

	// 查單筆
	public Teacher getTeacherById(int id) {
		return teacherRepository.findById(id).orElse(null);
	}

	// 查全部
	public List<Teacher> getAllTeacher() {
		return teacherRepository.findAll();
	}

	// 刪除
	public void deleteTeacher(Teacher teacher) {
		teacherRepository.delete(teacher);
	}
	
	//查byCourse
	public List<Teacher> getTeacherByCourseId(Integer courseId){
		return teacherRepository.findByCourseId(courseId);
	}
	
	//查bySubject
	public List<Teacher> getTeacherBySubjectId(Integer subjectId){
		return teacherRepository.findBySubjectId(subjectId);
	}
	
	@Transactional
	public void updateTeacher(Teacher teacher) {
	    teacherRepository.save(teacher);
	}

	/*
	 * // 新增 public void addTeacher(Teacher teacher) { Transaction tx = null; try
	 * (Session session = HibernateUtil.getSessionFactory().openSession()) { tx =
	 * session.beginTransaction();
	 * 
	 * TeacherDao dao = new TeacherDao(session); dao.save(teacher); tx.commit();
	 * 
	 * } catch (Exception e) { if (tx != null) tx.rollback(); throw e; } }
	 * 
	 * // 查單筆 public Teacher getTeacherById(int id) { try (Session session =
	 * HibernateUtil.getSessionFactory().openSession()) { TeacherDao dao = new
	 * TeacherDao(session); return dao.findById(id); } }
	 * 
	 * // 查全部 public List<Teacher> getAllTeacher() { try (Session session =
	 * HibernateUtil.getSessionFactory().openSession()) { TeacherDao dao = new
	 * TeacherDao(session); return dao.findAll(); } }
	 * 
	 * // 更新 public void updateTeacher(Teacher teacher) { Transaction tx = null; try
	 * (Session session = HibernateUtil.getSessionFactory().openSession()) { tx =
	 * session.beginTransaction();
	 * 
	 * TeacherDao dao = new TeacherDao(session); dao.update(teacher); tx.commit(); }
	 * catch (Exception e) { if (tx != null) tx.rollback(); throw e; } }
	 * 
	 * // 刪除 public void deleteTeacher(Teacher teacher) { Transaction tx = null; try
	 * (Session session = HibernateUtil.getSessionFactory().openSession()) { tx =
	 * session.beginTransaction();
	 * 
	 * TeacherDao dao = new TeacherDao(session); dao.delete(teacher); tx.commit();
	 * 
	 * } catch (Exception e) { if (tx != null) tx.rollback(); throw e; } }
	 */

}
