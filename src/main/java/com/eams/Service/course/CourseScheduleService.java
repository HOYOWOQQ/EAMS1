package com.eams.Service.course;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.course.DTO.CourseScheduleDTO;
import com.eams.Repository.course.CourseScheduleRepository;
import com.eams.Service.attendance.AttendanceService;


@Service
public class CourseScheduleService {

	@Autowired
	private CourseScheduleRepository courseScheduleRepository;
	
	@Autowired
	private AttendanceService attendanceService;

	// 新增
	@Transactional
	public CourseSchedule saveCourseSchedule(CourseSchedule courseSchedule) {
		 String validationError = validateCourseSchedule(courseSchedule);
		    if (validationError != null) {
		        throw new RuntimeException(validationError);
		    }
		CourseSchedule saved = courseScheduleRepository.save(courseSchedule);
        // 2. 建立出席資料
        attendanceService.createAttendanceForCourseSchedule(saved);
		return saved;
	}

	// 查單筆
	public CourseSchedule getCourseScheduleById(int id) {
		return courseScheduleRepository.findById(id).orElse(null);
	}

	// 查全部

	public List<CourseSchedule> getAllCourseSchedule() {
		return courseScheduleRepository.findAll();
	}
	
	
	// 刪除
	@Transactional
	public CourseScheduleDTO deleteCourseScheduleById(Integer id) {
	    CourseSchedule cs = courseScheduleRepository.findById(id).orElseThrow();
	    courseScheduleRepository.delete(cs);
	    return CourseScheduleDTO.fromEntity(cs);
	}



	// 查課表by課程
	public List<CourseSchedule> getCourseScheduleByCourseId(int courseId){
		return courseScheduleRepository.findByCourse_Id(courseId);
	}

	// 查課表by教室
	public List<CourseSchedule> getCourseScheduleByClassroomId(int classroomId){
		return courseScheduleRepository.findByClassroom_Id(classroomId);
	}

	// 查課表by學生
	public List<CourseSchedule> getCourseScheduleByStudentId(int studentId){
		return courseScheduleRepository.findByStudentId(studentId);
	}

	// 查課表by老師
	public List<CourseSchedule> getCourseScheduleByTeacherId(int teacherId){
		return courseScheduleRepository.findByTeacher_Id(teacherId);
	}
	
	
	// 查課表by搜尋
	public List<CourseSchedule> searchCourseSchedule(String userQuery, Integer roomId, Integer courseId) {
		return courseScheduleRepository.searchCourseSchedule(userQuery, roomId, courseId);
	}
	
	//查週課表
	public List<CourseSchedule> getCourseScheduleByWeekDay(LocalDate start, LocalDate end){
		return courseScheduleRepository.findByWeeK(start, end);
	}
	
	//總課程數 /週
	public int countScheduledCourses(LocalDate start, LocalDate end) {
		return courseScheduleRepository.totalCourses(start, end);
	}
	
	//總授課教師數 /週
	public int countActiveTeachers(LocalDate start, LocalDate end) {
		return courseScheduleRepository.activeTeachers(start, end);
	}
	
	//教室利用率計算
    public int calculateRoomUtilization(LocalDate start, LocalDate end) {
    	
    	int periodsPerDay = 4;
    	int daysPerWeek = 5;
    	
    	Integer usedPeriods =  courseScheduleRepository.sumUsedPeriods(start, end);
    	if (usedPeriods == null) usedPeriods=0;
    	
    	int roomCount = courseScheduleRepository.activeRooms(start, end);
    	if (roomCount == 0) return 0;
    	
    	int totalAvailablePeriods = roomCount * periodsPerDay * daysPerWeek;
        // 利用率，四捨五入為整數 %
        int rate = Math.round(usedPeriods * 100.0f / totalAvailablePeriods);

        return rate;
    	
    }
    
    //查詢週課表加條件查詢
    public List<CourseSchedule> getCourseScheduleByFilters(LocalDate startDate,LocalDate endDate,Integer memberId,Integer courseId,Integer classroomId){
    	
    	 List<CourseSchedule> result = courseScheduleRepository.findByDateRangeAndFilters(startDate, endDate, memberId, courseId, classroomId);
    	 
    	return result;
    	
    }
    
 // 檢查教師時間衝突
    public boolean hasTeacherTimeConflict(Integer teacherId, LocalDate lessonDate, 
                                         Integer periodStart, Integer periodEnd) {
        List<CourseSchedule> conflicts = courseScheduleRepository.findTeacherTimeConflicts(
            teacherId, lessonDate, periodStart, periodEnd);
        return !conflicts.isEmpty();
    }

    // 檢查教室時間衝突
    public boolean hasClassroomTimeConflict(Integer classroomId, LocalDate lessonDate,
                                           Integer periodStart, Integer periodEnd) {
        List<CourseSchedule> conflicts = courseScheduleRepository.findClassroomTimeConflicts(
            classroomId, lessonDate, periodStart, periodEnd);
        return !conflicts.isEmpty();
    }

    // 檢查課程時間衝突
    public boolean hasCourseTimeConflict(Integer courseId, LocalDate lessonDate,
                                        Integer periodStart, Integer periodEnd) {
        List<CourseSchedule> conflicts = courseScheduleRepository.findCourseTimeConflicts(
            courseId, lessonDate, periodStart, periodEnd);
        return !conflicts.isEmpty();
    }

    // 綜合驗證方法
    public String validateCourseSchedule(CourseSchedule schedule) {
        if (hasTeacherTimeConflict(schedule.getTeacher().getId(), 
                                  schedule.getLessonDate(),
                                  schedule.getPeriodStart(), 
                                  schedule.getPeriodEnd())) {
            return "教師時間衝突";
        }
        
        if (hasClassroomTimeConflict(schedule.getClassroom().getId(),
                                    schedule.getLessonDate(),
                                    schedule.getPeriodStart(),
                                    schedule.getPeriodEnd())) {
            return "教室時間衝突";
        }
        
        if (hasCourseTimeConflict(schedule.getCourse().getId(),
                                 schedule.getLessonDate(),
                                 schedule.getPeriodStart(),
                                 schedule.getPeriodEnd())) {
            return "課程重複排課";
        }
        
        return null; // 無衝突
    }

}
