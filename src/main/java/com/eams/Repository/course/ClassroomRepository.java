package com.eams.Repository.course;


import org.springframework.stereotype.Repository;

import com.eams.Entity.course.Classroom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



@Repository
public interface  ClassroomRepository  extends JpaRepository<Classroom, Integer>{
	
	
	@Query("SELECT c.id FROM Classroom c")
    List<Integer> findAllClassroomIds();
	
	

    
	
	
}
