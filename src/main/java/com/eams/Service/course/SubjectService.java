package com.eams.Service.course;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Subject;
import com.eams.Repository.course.SubjectRepository;


@Service
public class SubjectService {


	@Autowired
	private SubjectRepository subjectRepository;

	//新增+更新
	@Transactional
	public void saveSubject(Subject subject) {
		subjectRepository.save(subject);
		
	}
	
	//查單筆
	public Subject getSubjectById(Integer subjectId) {
		return subjectRepository.findById(subjectId).orElse(null);
	}
	
	//查全
	public List<Subject> getAllSubject(){
		return subjectRepository.findAll();
	}
	
	//刪除
	@Transactional
	public void deleteSubject(Subject subject) {
		subjectRepository.delete(subject);
	}
	
	
	//查by課程
	public List<Subject> findSubjectByCourseId(Integer courseId) {
		return subjectRepository.findByCourses_Id(courseId);
	}
	
	
	

	

	
	

}
