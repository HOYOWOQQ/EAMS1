package com.eams.Entity.course;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="classroom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {
	
	@Id @Column(name="ID")
	private Integer id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="capacity")
	private Integer capacity;
	
	@Column(name="remark")
	private String remark;
	
	@Column(name="location")
	private String location;
	
	@Column(name="status")
	private String status;
	
	@Column(name="equipment")
	private String equipment;
	
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "classroom", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<CourseSchedule> courseSchedule=new LinkedList<CourseSchedule>();
	
	
	





	

	
	
}
