package com.eams.Repository.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.member.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

	@Query("FROM Student WHERE guardianName LIKE %:keyword% OR address LIKE %:keyword% OR remark LIKE %:keyword%")
	List<Student> search(@Param("keyword") String keyword);

	@Query("SELECT s FROM Student s JOIN s.member m WHERE m.name = :memberName")
	Student findByMemberName(@Param("memberName") String memberName);

//	//關鍵字搜尋學生
//		public List<Student> search(String keyword) {
//			try {
//				String hql = "FROM Student WHERE guardianName LIKE :keyword OR address LIKE :keyword OR remark LIKE :keyword";
//				Query<Student> query = session.createQuery(hql, Student.class);
//				query.setParameter("keyword", "%" + keyword + "%");
//				List<Student> result = query.list();
//				return result;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return new ArrayList<>();
//			}
//		}

}
