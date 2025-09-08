package com.eams.Service.course;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Classroom;
import com.eams.Repository.course.ClassroomRepository;


@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepository;

    // 新增或更新（Spring JPA 自動辨識 insert or update）
    @Transactional
    public void saveClassroom(Classroom classroom) {
        classroomRepository.save(classroom);
    }

    // 查單筆
    public Classroom getClassroomById(int id) {
        return classroomRepository.findById(id).orElse(null);
    }

    // 查全部
    public List<Classroom> getAllClassroom() {
        return classroomRepository.findAll();
    }

    // 查全部教室 id（需要自訂方法）
    public List<Integer> getAllClassroomId() {
        return classroomRepository.findAllClassroomIds(); // 此為自定方法，見下方
    }

    // 刪除
    @Transactional
    public void deleteClassroom(Classroom classroom) {
        classroomRepository.delete(classroom);
    }
}
