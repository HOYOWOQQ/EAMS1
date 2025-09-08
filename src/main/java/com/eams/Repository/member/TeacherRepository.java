package com.eams.Repository.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.member.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

	// 查老師by課程
	@Query("SELECT t FROM Teacher t JOIN  t.subjects s JOIN s.courses c WHERE c.id = :courseId")
	List<Teacher> findByCourseId(@Param("courseId") Integer courseId);
	
	//查老師by科目
	@Query("SELECT t FROM Teacher t JOIN t.subjects s WHERE s.id = :subjectId")
	List<Teacher> findBySubjectId(@Param("subjectId") Integer subjectId);

	/*
	 * //根據 ID 列表查詢教師 public List<Teacher> findByIds(List<Integer> ids) { try { if
	 * (ids == null || ids.isEmpty()) { return new ArrayList<>(); }
	 * 
	 * String hql = "FROM Teacher WHERE id IN :ids"; Query<Teacher> query =
	 * session.createQuery(hql, Teacher.class); query.setParameter("ids", ids);
	 * List<Teacher> result = query.list(); return result; } catch (Exception e) {
	 * e.printStackTrace(); return new ArrayList<>(); } }
	 * 
	 * 
	 * 
	 * 
	 * //關鍵字搜尋教師 public List<Teacher> search(String keyword) { try { String hql =
	 * "FROM Teacher WHERE specialty LIKE :keyword OR address LIKE :keyword OR remark LIKE :keyword"
	 * ; Query<Teacher> query = session.createQuery(hql, Teacher.class);
	 * query.setParameter("keyword", "%" + keyword + "%"); List<Teacher> result =
	 * query.list(); return result; } catch (Exception e) { e.printStackTrace();
	 * return new ArrayList<>(); } }
	 * 
	 * //課程的
	 * 
	 * 
	 * public List<TeacherDTO> findTeacherByCourseId(Integer courseId) {
	 * ArrayList<TeacherDTO> teacherList = new ArrayList<>(); String sql =
	 * "SELECT DISTINCT t.*, m.name \r\n" + "FROM teacher t\r\n" +
	 * "JOIN member m ON t.id = m.id\r\n" +
	 * "JOIN course_teacher ct ON t.id = ct.teacher_id\r\n" +
	 * "JOIN course_subject cs ON ct.subject_id = cs.subject_id\r\n" +
	 * "WHERE cs.course_id = ?"; try (Connection conn =
	 * SQLconnection.getConnection(); PreparedStatement ps =
	 * conn.prepareStatement(sql)) { ps.setInt(1, courseId); ResultSet rs =
	 * ps.executeQuery(); while (rs.next()) { int id = rs.getInt("id"); String name
	 * = rs.getString("name"); teacherList.add(new TeacherDTO(id, name)); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return teacherList; }
	 * 
	 * public List<TeacherDTO> findTeacherBySubjectId(Integer subjectId) {
	 * ArrayList<TeacherDTO> teacherList = new ArrayList<>(); String sql =
	 * "SELECT t.id , m.name FROM  teacher t JOIN member m ON t.id=m.id JOIN course_teacher ct ON t.id = ct.teacher_id WHERE ct.subject_id=?"
	 * ; try (Connection conn = SQLconnection.getConnection(); PreparedStatement ps
	 * = conn.prepareStatement(sql)) { ps.setInt(1, subjectId); ResultSet rs =
	 * ps.executeQuery(); while (rs.next()) { int id = rs.getInt("id"); String name
	 * = rs.getString("name"); teacherList.add(new TeacherDTO(id, name)); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return teacherList; }
	 * 
	 * public List<TeacherDTO> findAllTeacher() { ArrayList<TeacherDTO> list = new
	 * ArrayList<>(); String sql =
	 * "SELECT t.* , m.name FROM teacher t JOIN member m ON t.id = m.id"; try
	 * (Connection conn = SQLconnection.getConnection(); PreparedStatement ps =
	 * conn.prepareStatement(sql)) { ResultSet rs = ps.executeQuery(); while
	 * (rs.next()) { int id = rs.getInt("id"); String name = rs.getString("name");
	 * list.add(new TeacherDTO(id, name)); } } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * return list; }
	 */
}
