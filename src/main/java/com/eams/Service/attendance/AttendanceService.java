package com.eams.Service.attendance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.attendance.Attendance;
import com.eams.Entity.attendance.DTO.AttendanceDTO;
import com.eams.Entity.attendance.DTO.RollCallRequest;
import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.member.Student;
import com.eams.Repository.attendance.AttendanceRepository;
import com.eams.Repository.attendance.LeaveRequestRepository;
import com.eams.Repository.course.CourseScheduleRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
public class AttendanceService {
	@Autowired
	private AttendanceRepository attendanceRepository;
	@Autowired
	private CourseScheduleRepository courseScheduleRepository;
	
	@Autowired LeaveRequestRepository leaveRequestRepository;
	

	// 學生查詢自己
	public List<AttendanceDTO> searchAttendancesForStudent(Integer studentId, Integer courseId, LocalDate lessonDate) {
		
		List<Attendance> entityList = attendanceRepository.findAll((root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        if (studentId != null) {
	            predicates.add(cb.equal(root.get("student").get("id"), studentId));
	        }

	        if (courseId != null) {
	            predicates.add(cb.equal(
	                root.get("courseSchedule").get("course").get("id"), courseId
	            ));
	        }

	        if (lessonDate != null) {
	            predicates.add(cb.equal(
	                root.get("courseSchedule").get("lessonDate"), lessonDate
	            ));
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    });

	    return entityList.stream()
	        .map(AttendanceDTO::fromEntity)
	        .collect(Collectors.toList());
	}


	// 管理員查詢
	public List<AttendanceDTO> searchAttendanceForTeacher(String studentName, String courseName, LocalDate lessonDate,
			String status) {

		List<Attendance> entityList = attendanceRepository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			// 學生姓名模糊查（Join 到 student.member.name）
			if (studentName != null && !studentName.isEmpty()) {
				predicates.add(cb.like(root.get("student").get("member").get("name"), "%" + studentName + "%"));
			}
			if (courseName != null && !courseName.isEmpty()) {
				predicates.add(cb.like(root.get("courseSchedule").get("course").get("name"), "%" + courseName + "%"));
			}
			if (lessonDate != null) {
				predicates.add(cb.equal(root.get("courseSchedule").get("lessonDate"), lessonDate));
			}
			if (status != null && !status.isEmpty()) {
				predicates.add(cb.equal(root.get("status"), status));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		});

		return entityList.stream().map(AttendanceDTO::fromEntity).collect(Collectors.toList());
	}
	
	//老師當天有授課的課程
	public List<CourseSchedule> getTeacherSchedulesForDate(Integer teacherId, LocalDate lessonDate) {
	    List<CourseSchedule> allSchedules = courseScheduleRepository.findByTeacher_Id(teacherId);

	    return allSchedules.stream()
	        .filter(cs -> cs.getLessonDate() != null && cs.getLessonDate().equals(lessonDate))
	        .collect(Collectors.toList());
	}

	//判斷該老師是否授課
	/*
	public boolean isTeacherOfCourse(Integer teacherId, Integer courseId) {
	    List<CourseSchedule> allByTeacher = courseScheduleRepository.findByTeacher_Id(teacherId);

	    for (CourseSchedule cs : allByTeacher) {
	        if (cs.getCourse() != null && cs.getCourse().getId().equals(courseId)) {
	            return true;
	        }
	    }
	    return false;
	}
	*/
	
	@PersistenceContext
	private EntityManager entityManager;

	public boolean isTeacherOfSchedule(Integer teacherId, Integer scheduleId) {
	    String jpql = "SELECT COUNT(cs) FROM CourseSchedule cs " +
	                  "WHERE cs.id = :scheduleId AND cs.teacher.id = :teacherId";

	    Long count = entityManager.createQuery(jpql, Long.class)
	            .setParameter("scheduleId", scheduleId)
	            .setParameter("teacherId", teacherId)
	            .getSingleResult();

	    return count != null && count > 0;
	}



	// 老師點名（顯示資料）
	public List<AttendanceDTO> getStudentListForRollCall(Integer courseScheduleId) {
		List<Attendance> entityList = attendanceRepository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (courseScheduleId != null) {
				predicates.add(cb.equal(root.get("courseSchedule").get("id"), courseScheduleId));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		});

		return entityList.stream().map(AttendanceDTO::fromEntity).collect(Collectors.toList());
	}

	//老師點名實際的值
	public Map<Integer, String> getStudentIdStatusMapForRollCall(Integer courseScheduleId) {
	    List<Attendance> attendances = attendanceRepository.findAll((root, query, cb) ->
	        cb.equal(root.get("courseSchedule").get("id"), courseScheduleId)
	    );

	    // 轉成 Map<StudentId, Status>
	    return attendances.stream()
	            .collect(Collectors.toMap(
	                a -> a.getStudent().getId(),
	                Attendance::getStatus
	            ));
	}

	//點名更改狀態
	@Transactional
	public void updateAttendanceStatuses(Integer courseScheduleId, Map<Integer, String> rollCallMap) {
	    for (Map.Entry<Integer, String> entry : rollCallMap.entrySet()) {
	        Integer attendanceId = entry.getKey();
	        String status = entry.getValue();

	        Attendance attendance = attendanceRepository.findById(attendanceId).orElse(null);
	        if (attendance != null) {
	            attendance.setStatus(status);
	            attendanceRepository.save(attendance);
	        }
	    }
	}
	
	//建立出席表by更新課表
	public void createAttendanceForCourseSchedule(CourseSchedule courseSchedule) {
        // 查詢這門課所有學生
        List<Student> students = attendanceRepository.findStudentsByCourseId(courseSchedule.getCourse().getId());
        List<Attendance> attendances = new ArrayList<>();

        for (Student student : students) {
            Attendance att = new Attendance();
            att.setStudent(student);
            att.setCourseSchedule(courseSchedule);
            att.setStatus(Attendance.STATUS_UNMARKED); // 預設"未點名"
            attendances.add(att);
        }
        attendanceRepository.saveAll(attendances); // 一次批量新增
    }
	
	public List<AttendanceDTO> getAbsentAttendances(Integer studentId) {
	    // 查詢該學生所有狀態為 ABSENT 且尚未申請請假的出勤記錄
	    List<Attendance> absentAttendances = attendanceRepository
	        .findByStudent_IdAndStatusAndLeaveRequestIsNull(studentId, Attendance.STATUS_ABSENT);
	    
	    return absentAttendances.stream()
	        .map(AttendanceDTO::fromEntity)
	        .collect(Collectors.toList());
	}
	
	/** 補點名清單（回傳既有 AttendanceDTO） */
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getMakeupList(Integer courseScheduleId, LocalDate lessonDate) {
        List<Attendance> rows = attendanceRepository.findByScheduleAndDate(courseScheduleId, lessonDate);

        // ✅ 若你們的 DTO 已有 fromEntity，直接用
        return rows.stream()
                .map(AttendanceDTO::fromEntity)
                .collect(Collectors.toList());
	}

    /** 管理員補點名（無日期限制；LEAVE 已核准者鎖定） */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listSchedulesByDate(LocalDate date, Integer userId, String role) {
        if (date == null) return List.of();

        StringBuilder jpql = new StringBuilder(
            "SELECT cs.id, cs.course.name, cs.lessonDate, cs.periodStart, cs.periodEnd, cs.teacher.id " +
            "FROM CourseSchedule cs WHERE cs.lessonDate = :date"
        );
        boolean teacherOnly = role != null && role.equalsIgnoreCase("teacher");
        if (teacherOnly) {
            jpql.append(" AND cs.teacher.id = :tid");
        }

        var q = entityManager.createQuery(jpql.toString(), Object[].class)
                .setParameter("date", date);
        if (teacherOnly) q.setParameter("tid", userId);

        List<Object[]> rows = q.getResultList();
        // 轉成前端下拉可用的精簡物件
        return rows.stream().map(r -> {
            Integer id          = (Integer) r[0];
            String courseName   = (String)  r[1];
            LocalDate d         = (LocalDate) r[2];
            Integer pStart      = (Integer) r[3];
            Integer pEnd        = (Integer) r[4];
            Map<String, Object> m = new HashMap<>();
            m.put("id", id);
            m.put("courseName", courseName);
            m.put("lessonDate", d);
            m.put("periodStart", pStart);
            m.put("periodEnd", pEnd);
            m.put("label", String.format("%s（第%d-%d節）", courseName, pStart, pEnd));
            return m;
        }).sorted(Comparator.comparing(m -> (Integer) m.get("periodStart")))
          .collect(Collectors.toList());
    }
    @Transactional
    public Map<String, Object> applyMakeup(LocalDate lessonDate, RollCallRequest req) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> skipped = new ArrayList<>();
        int updated = 0;

        Integer csId = req.getCourseScheduleId();
        Map<Integer, String> rc = req.getRollCallMap();
        if (rc == null || rc.isEmpty()) {
            result.put("updatedCount", 0);
            result.put("skipped", List.of(Map.of("reason", "EMPTY_ROLLCALL")));
            return result;
        }

        Set<Integer> keys = rc.keySet();

        // ① 把 keys 視為 attendanceId 去撈
        List<Attendance> byIds = attendanceRepository.findAllById(keys);
        // ② 把 keys 視為 studentId（同課表+同日期）去撈
        List<Attendance> byStudents =
                attendanceRepository.findByScheduleAndDateAndStudentIn(csId, lessonDate, keys);

        // ③ 合併 & 僅保留指定課表+日期的
        Map<Integer, Attendance> index = new HashMap<>();
        List<Attendance> candidates = new ArrayList<>();
        candidates.addAll(byIds);
        candidates.addAll(byStudents);

        List<Attendance> targets = candidates.stream()
                .filter(a -> a.getCourseSchedule() != null
                          && csId.equals(a.getCourseSchedule().getId())
                          && lessonDate.equals(a.getCourseSchedule().getLessonDate()))
                .distinct()
                .collect(Collectors.toList());

        // 同時支持用 attendanceId 或 studentId 查到同一個物件
        for (Attendance a : targets) {
            index.put(a.getId(), a);                               // attendanceId
            if (a.getStudent() != null) index.put(a.getStudent().getId(), a); // studentId
        }

        for (Map.Entry<Integer, String> e : rc.entrySet()) {
            Integer key = e.getKey();
            String up = (e.getValue() == null ? "" : e.getValue()).toUpperCase(Locale.ROOT);

            Attendance a = index.get(key);
            if (a == null) {
                skipped.add(Map.of("key", key, "reason", "NOT_FOUND"));
                continue;
            }
            if (!"ATTEND".equals(up) && !"ABSENT".equals(up)) {
                skipped.add(Map.of("key", key, "reason", "INVALID_STATUS"));
                continue;
            }
            boolean hasApprovedLeave =
                    leaveRequestRepository.existsByAttendanceIdAndStatus(a.getId(), "APPROVED");
            if (hasApprovedLeave) {
                skipped.add(Map.of("key", key, "reason", "APPROVED_LEAVE_LOCKED"));
                continue;
            }

            a.setStatus(up);
            updated++;
        }

        attendanceRepository.saveAll(targets);

        result.put("updatedCount", updated);
        result.put("skipped", skipped);
        return result;
    }


}
