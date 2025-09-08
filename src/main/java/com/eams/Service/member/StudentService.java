package com.eams.Service.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.member.Student;
import com.eams.Entity.member.DTO.StudentDTO;
import com.eams.Repository.member.StudentRepository;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;

	// 新增
	@Transactional
	public void saveStudent(Student student) {
		studentRepository.save(student);
	}

	// 查單筆
	public Student getStudentById(int id) {
		return studentRepository.findById(id).orElse(null);
	}

	// 查全部
	public List<Student> getAllStudent() {
		return studentRepository.findAll();
	}
	
	//修改
    @Transactional
    public void updateStudent(Student student) {
        studentRepository.save(student);
    }

	// 刪除
    @Transactional
    public void deleteStudent(Student student) {
        studentRepository.delete(student);
    }
    
    public StudentDTO findStudentById(int id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setName(student.getMember().getName());
        return studentDTO;
    }

}
