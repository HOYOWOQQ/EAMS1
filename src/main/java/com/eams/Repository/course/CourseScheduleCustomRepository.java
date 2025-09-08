package com.eams.Repository.course;

import java.util.List;

import com.eams.Entity.course.CourseSchedule;

public interface CourseScheduleCustomRepository {
    List<CourseSchedule> searchCourseSchedule(String userQuery, Integer roomId, Integer courseId);
}
