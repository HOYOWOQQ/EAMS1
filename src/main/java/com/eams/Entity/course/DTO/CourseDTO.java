package com.eams.Entity.course.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.eams.Entity.course.Course;

@Data
public class CourseDTO {
    private Integer id;
    private String name;
    private String type;
    private String description;
    private Integer maxCapacity;
    private Integer minCapacity;
    private Integer fee;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private Integer updatedBy;
    private LocalDateTime updatedAt;
    private String status;
    private String statusNote;
    private String remark;
    
    public static CourseDTO fromEntity(Course entity) {
        if (entity == null) return null;
        CourseDTO dto = new CourseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setMaxCapacity(entity.getMaxCapacity());
        dto.setMinCapacity(entity.getMinCapacity());
        dto.setFee(entity.getFee());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setRegistrationStartDate(entity.getRegistrationStartDate());
        dto.setRegistrationEndDate(entity.getRegistrationEndDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setStatus(entity.getStatus());
        dto.setStatusNote(entity.getStatusNote());
        dto.setRemark(entity.getRemark());
        return dto;
    }
}

