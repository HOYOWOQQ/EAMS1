package com.eams.Repository.course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.course.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

	// 查課程by學生
	@Query("SELECT c FROM Course c JOIN  c.courseEnroll ce WHERE ce.student.id = :studentId")
	List<Course> findByStudentId(@Param("studentId") Integer studentId);

	// 查課程最大人數
	@Query("SELECT c.maxCapacity FROM Course c WHERE c.id = :courseId")
	int getMaxCapacity(@Param("courseId") Integer courseId);
	
	 @Query("SELECT c FROM Course c JOIN c.subjects s WHERE s.id = :subjectId")
	    List<Course> findCoursesBySubjectId(@Param("subjectId") Integer subjectId);
	// 新版本綜合
	// 自動排課-轉換成符合方法格式的輸入參數
//	public boolean autoCourseSelectCT(LocalDate start, LocalDate end, List<Integer> selectCourseIds,
//			List<Integer> selectTeacherIds, List<Integer> selectClassroomIds, List<Integer> selectSubjectIds) {
//
//		Set<Integer> selectedTeacherSet = new HashSet<>(selectTeacherIds);
//		Set<Integer> selectSubjectSet = new HashSet<>(selectSubjectIds);
//
//		Map<Integer, List<Integer>> courseSubjectMap = new HashMap<>();
//		Map<Integer, List<Integer>> subjectTeacherMap = new HashMap<>();
//		List<Course> selectedCourses = new ArrayList<>();
//
//		// 建立課程-科目映射
//		for (Integer courseId : selectCourseIds) {
//			Course course = findById(courseId);
//			if (course == null)
//				continue;
//
//			List<Integer> subjectInCourse = getSujectsByCourse(course.getId());
//			List<Integer> filteredSubjects = subjectInCourse.stream().filter(selectSubjectSet::contains)
//					.collect(Collectors.toList());
//
//			if (filteredSubjects.isEmpty())
//				continue;
//
//			selectedCourses.add(course);
//			courseSubjectMap.put(courseId, filteredSubjects);
//
//			// 建立科目-老師映射
//			for (Integer subjectId : filteredSubjects) {
//				if (!subjectTeacherMap.containsKey(subjectId)) {
//					List<Integer> allTeachers = getTeacherIdsBySubject(subjectId);
//					List<Integer> matchedTeachers = allTeachers.stream().filter(selectedTeacherSet::contains)
//							.collect(Collectors.toList());
//					if (!matchedTeachers.isEmpty()) {
//						subjectTeacherMap.put(subjectId, matchedTeachers);
//					}
//				}
//			}
//		}
//
//		// 修正：按課程的科目數量排序（資源少的優先排）
//		selectedCourses.sort(Comparator.comparingInt(c -> courseSubjectMap.getOrDefault(c.getId(), List.of()).size()));
//
//		return autoCourseMain(start, end, selectedCourses, selectClassroomIds, courseSubjectMap, subjectTeacherMap);
//	}
//
//	// 自動排課主要邏輯
//	public boolean autoCourseMain(LocalDate start, LocalDate end, List<Course> selectedCourses,
//			List<Integer> classroomIds, Map<Integer, List<Integer>> courseSubjectMap,
//			Map<Integer, List<Integer>> subjectTeacherMap) {
//
//		boolean hasAnyScheduled = false;
//		Random random = new Random();
//
//		for (Course course : selectedCourses) {
//			List<Integer> subjectIds = courseSubjectMap.get(course.getId());
//			if (subjectIds == null || subjectIds.isEmpty())
//				continue;
//
//			// 根據課程計算目標排課節數
//			int targetSessions = calculateTargetSessions(course);
//			int sessionsPlanned = 0;
//
//			// 為每個科目分配排課節數
//			Map<Integer, Integer> subjectSessionCount = distributeSessionsToSubjects(subjectIds, targetSessions);
//
//			// 遍歷日期進行排課
//			for (LocalDate date : getAvailableDates(start, end)) {
//				if (sessionsPlanned >= targetSessions)
//					break;
//
//				// 隨機化節次長度 (1-3節課)
//				int periodLength = random.nextInt(3) + 1;
//
//				// 嘗試不同的起始節次
//				for (int periodStart = 1; periodStart <= 8 - periodLength + 1; periodStart++) {
//					int periodEnd = periodStart + periodLength - 1;
//
//					// 選擇還需要排課的科目
//					List<Integer> availableSubjects = subjectIds.stream()
//							.filter(subjectId -> subjectSessionCount.get(subjectId) > 0).collect(Collectors.toList());
//
//					if (availableSubjects.isEmpty())
//						break;
//
//					// 隨機選擇科目
//					Collections.shuffle(availableSubjects);
//					int selectedSubjectId = availableSubjects.get(0);
//
//					boolean scheduled = tryScheduleForSubjects(course.getId(), selectedSubjectId, classroomIds,
//							subjectTeacherMap, date, periodStart, periodEnd);
//
//					if (scheduled) {
//						sessionsPlanned++;
//						hasAnyScheduled = true;
//						// 更新對應科目的剩餘節數
//						updateSubjectSessionCount(subjectSessionCount, availableSubjects.get(0));
//					}
//
//					if (sessionsPlanned >= targetSessions)
//						break;
//				}
//			}
//		}
//
//		return hasAnyScheduled;
//	}
//
//	private boolean tryScheduleForSubjects(int courseId, int subjectId, List<Integer> classroomIds,
//			Map<Integer, List<Integer>> subjectTeacherMap, LocalDate date, int periodStart, int periodEnd) {
//
//		List<Integer> teacherIds = subjectTeacherMap.get(subjectId);
//		if (teacherIds == null || teacherIds.isEmpty())
//			return false;
//
//		// 隨機化教室和老師順序
//		List<Integer> shuffledClassrooms = new ArrayList<>(classroomIds);
//		List<Integer> shuffledTeachers = new ArrayList<>(teacherIds);
//		Collections.shuffle(shuffledClassrooms);
//		Collections.shuffle(shuffledTeachers);
//
//		for (int classroomId : shuffledClassrooms) {
//			for (int teacherId : shuffledTeachers) {
//				if (isAvailable(courseId, classroomId, teacherId, date, periodStart, periodEnd)) {
//					int scheduleId = insertSchedule(courseId, classroomId, teacherId, date, periodStart, periodEnd,
//							subjectId);
//					/*
//					 * List<Integer> studentIds =
//					 * courseScheduleDao.findStudentIdsByCourseId(courseId);
//					 * courseScheduleDao.insertAttendances(scheduleId, studentIds);
//					 */
//					return true; // 成功排課，返回
//				}
//			}
//		}
//		return false; // 無法排課
//	}
//
//	// 計算課程目標節數
//	private int calculateTargetSessions(Course course) {
//		// 可以根據課程類型、時長等決定
//		// 這裡簡化為固定值，實際應該從課程設定中取得
//		return 10;
//	}
//
//	// 將總節數分配給各科目
//	private Map<Integer, Integer> distributeSessionsToSubjects(List<Integer> subjectIds, int totalSessions) {
//		Map<Integer, Integer> distribution = new HashMap<>();
//		int sessionsPerSubject = totalSessions / subjectIds.size();
//		int remainder = totalSessions % subjectIds.size();
//
//		for (int i = 0; i < subjectIds.size(); i++) {
//			int sessions = sessionsPerSubject + (i < remainder ? 1 : 0);
//			distribution.put(subjectIds.get(i), sessions);
//		}
//
//		return distribution;
//	}
//
//	// 更新科目剩餘節數
//	private void updateSubjectSessionCount(Map<Integer, Integer> subjectSessionCount, int subjectId) {
//		int remainingSessions = subjectSessionCount.get(subjectId) - 1;
//		subjectSessionCount.put(subjectId, Math.max(0, remainingSessions));
//	}
//
//	// 新增課表
//	public int insertSchedule(int courseId, int classroomId, int teacherId, LocalDate date, int periodStart,
//			int periodEnd, int subjectId) {
//		String sql = "INSERT INTO course_schedule "
//				+ "(course_id, classroom_id, teacher_id, lesson_date, period_start, period_end, subject_id, status) "
//				+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'scheduled')";
//
//		try (Connection conn = SQLconnection.getConnection();
//				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//
//			ps.setInt(1, courseId);
//			ps.setInt(2, classroomId);
//			ps.setInt(3, teacherId);
//			ps.setDate(4, Date.valueOf(date));
//			ps.setInt(5, periodStart);
//			ps.setInt(6, periodEnd);
//			ps.setInt(7, subjectId);
//
//			ps.executeUpdate();
//
//			// 關鍵：取得自動產生的 id
//			ResultSet rs = ps.getGeneratedKeys();
//			if (rs.next()) {
//				int scheduleId = rs.getInt(1);
//				System.out.printf("✔ 成功排課：課程%d 教室%d 科目%d 老師%d 日期%s 節次%d~%d，行事曆id=%d\n", courseId, classroomId,
//						subjectId, teacherId, date, periodStart, periodEnd, scheduleId);
//				return scheduleId;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return 0; // 失敗就回傳 0
//	}
//
//	// 驗證是否可以排課
//	public boolean isAvailable(int courseId, int classroomId, int teacherId, LocalDate date, int periodStart,
//			int periodEnd) {
//		String sql = "SELECT COUNT(*) FROM course_schedule " + "WHERE lesson_date = ? AND "
//				+ "((classroom_id = ?) OR (teacher_id = ?)OR (course_id=?)) AND "
//				+ "NOT (period_end < ? OR period_start > ?)";
//		try (Connection conn = SQLconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//			ps.setDate(1, Date.valueOf(date));
//			ps.setInt(2, classroomId);
//			ps.setInt(3, teacherId);
//			ps.setInt(4, courseId);
//			ps.setInt(5, periodStart);
//			ps.setInt(6, periodEnd);
//
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					return rs.getInt(1) == 0;
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	// 取一段時間中的平日
//	public List<LocalDate> getAvailableDates(LocalDate start, LocalDate end) {
//		ArrayList<LocalDate> dates = new ArrayList<>();
//		LocalDate current = start;
//		while (!current.isAfter(end)) {
//			DayOfWeek day = current.getDayOfWeek();
//			if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
//				dates.add(current);
//			}
//			current = current.plusDays(1);
//		}
//
//		return dates;
//	}
//
//	// 政葦區塊
//	// 獲取教師有權限教授的課程
//	public List<Course> getCoursesByTeacher(int teacherId) {
//		List<Course> courses = new ArrayList<>();
//		String sql = "SELECT c.*, s.name AS subject_name\r\n" + "FROM course c\r\n"
//				+ "JOIN course_subject cs ON c.id = cs.course_id\r\n"
//				+ "JOIN course_teacher ct ON cs.subject_id = ct.subject_id\r\n"
//				+ "JOIN subject s ON cs.subject_id = s.id\r\n" + "WHERE ct.teacher_id = ? AND c.status = 'active'\r\n"
//				+ "ORDER BY c.name;";
//
//		try (Connection conn = SQLconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//			pstmt.setInt(1, teacherId);
//			ResultSet rs = pstmt.executeQuery();
//
//			while (rs.next()) {
//				Course course = new Course();
//				course.setId(rs.getInt("id"));
//				course.setName(rs.getString("name"));
//				course.setType(rs.getString("type"));
//				course.setDescription(rs.getString("description"));
//				// course.setSubjectName(rs.getString("subject_name"));
//				courses.add(course);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return courses;
//	}
//
//	// 檢查教師是否有權限教授某課程
//	public boolean canTeacherAccessCourse(int teacherId, int courseId) {
//		String sql = "SELECT COUNT(*)\r\n" + "FROM course_subject cs\r\n"
//				+ "JOIN course_teacher ct ON cs.subject_id = ct.subject_id\r\n"
//				+ "WHERE cs.course_id = ? AND ct.teacher_id = ? ";
//
//		try (Connection conn = SQLconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//			pstmt.setInt(1, courseId);
//			pstmt.setInt(2, teacherId);
//			ResultSet rs = pstmt.executeQuery();
//
//			if (rs.next()) {
//				return rs.getInt(1) > 0;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//	
//	
//	
//	//
//	public List<Integer> getSujectsByCourse(int courseId) {
//        List<Integer> result = new ArrayList<>();
//        String sql = "SELECT subject_id FROM course_subject WHERE course_id = ?";
//        try (Connection conn = SQLconnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, courseId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                result.add(rs.getInt("subject_id"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//	
//	public List<Integer> getTeacherIdsBySubject(int subjectId) {
//        List<Integer> teacherIds = new ArrayList<>();
//        String sql = "SELECT teacher_id FROM course_teacher WHERE subject_id = ?";
//
//        try (Connection conn = SQLconnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, subjectId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                teacherIds.add(rs.getInt("teacher_id"));
//            }
//
//        } catch (Exception  e) {
//            e.printStackTrace(); 
//        }
//
//        return teacherIds;
//    }

}
