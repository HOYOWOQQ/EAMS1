package com.eams.Repository.attendance;

import java.time.LocalDate;
import java.util.List;

import com.eams.Entity.attendance.Attendance;

public interface AttendanceCustomRepository {
	List<Attendance> searchAttendance(String keyword, String status, Integer courseId, LocalDate date);

	void rollCallUpdate(int courseScheduleId, List<Integer> attendStudentIds);

	void updateStatusToLeaveByLeaveRequestId(int leaveRequestId);
}
