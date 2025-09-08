package com.eams.Service.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eams.Entity.course.Course;
import com.eams.Entity.course.Subject;
import com.eams.Repository.course.CourseRepository;
import com.eams.Repository.course.SubjectRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CourseSubjectService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    // 新增單一科目到課程
    @Transactional
    public boolean addSubjectToCourse(Integer courseId, Integer subjectId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
        
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with id: " + courseId);
        }
        
        if (subjectOpt.isEmpty()) {
            throw new RuntimeException("Subject not found with id: " + subjectId);
        }
        
        Course course = courseOpt.get();
        Subject subject = subjectOpt.get();
        
        // 檢查是否已經關聯
        if (course.getSubjects().contains(subject)) {
            return false; // 已經存在關聯
        }
        
        // 建立雙向關聯
        course.getSubjects().add(subject);
        subject.getCourses().add(course);
        
        // 保存課程（會自動更新關聯表）
        courseRepository.save(course);
        
        return true;
    }
    
    // 批量新增科目到課程
    @Transactional
    public int addSubjectsToCourse(Integer courseId, List<Integer> subjectIds) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with id: " + courseId);
        }
        
        Course course = courseOpt.get();
        int addedCount = 0;
        
        for (Integer subjectId : subjectIds) {
            Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
            
            if (subjectOpt.isPresent()) {
                Subject subject = subjectOpt.get();
                
                // 檢查是否已經關聯
                if (!course.getSubjects().contains(subject)) {
                    course.getSubjects().add(subject);
                    subject.getCourses().add(course);
                    addedCount++;
                }
            }
        }
        
        if (addedCount > 0) {
            courseRepository.save(course);
        }
        
        return addedCount;
    }
    
    // 從課程移除科目
    @Transactional
    public boolean removeSubjectFromCourse(Integer courseId, Integer subjectId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
        
        if (courseOpt.isEmpty() || subjectOpt.isEmpty()) {
            return false;
        }
        
        Course course = courseOpt.get();
        Subject subject = subjectOpt.get();
        
        // 移除雙向關聯
        boolean removedFromCourse = course.getSubjects().remove(subject);
        boolean removedFromSubject = subject.getCourses().remove(course);
        
        if (removedFromCourse || removedFromSubject) {
            courseRepository.save(course);
            return true;
        }
        
        return false;
    }
    
    // 取得課程的所有科目
    public List<Subject> getCourseSubjects(Integer courseId) {
        return subjectRepository.findSubjectsByCourseId(courseId);
    }
    
    // 取得不屬於課程的科目
    public List<Subject> getAvailableSubjectsForCourse(Integer courseId) {
        return subjectRepository.findSubjectsNotInCourse(courseId);
    }
    
    // 取得包含特定科目的課程
    public List<Course> getCoursesContainingSubject(Integer subjectId) {
        return courseRepository.findCoursesBySubjectId(subjectId);
    }
    
    // 檢查課程是否包含科目
    public boolean isCourseContainsSubject(Integer courseId, Integer subjectId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            return course.getSubjects().stream()
                    .anyMatch(subject -> subject.getId().equals(subjectId));
        }
        
        return false;
    }
    
    // 取得課程科目數量
    public int getCourseSubjectCount(Integer courseId) {
        List<Subject> subjects = getCourseSubjects(courseId);
        return subjects.size();
    }
}