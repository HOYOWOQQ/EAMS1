package com.eams.Entity.course.DTO;


import lombok.Data;

import com.eams.Entity.course.Classroom;

@Data
public class ClassroomDTO {
    private Integer id;
    private String name;
    private Integer capacity;
    private String remark;
    private String location;
    private String status;
    private String equipment;

    // 如果你有想要把對應課程表（課程排程）一併帶出，可以加上這個欄位
    // private List<CourseScheduleDTO> courseSchedule;

    public static ClassroomDTO fromEntity(Classroom entity) {
        if (entity == null) return null;
        ClassroomDTO dto = new ClassroomDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCapacity(entity.getCapacity());
        dto.setRemark(entity.getRemark());
        dto.setLocation(entity.getLocation());
        dto.setStatus(entity.getStatus());
        dto.setEquipment(entity.getEquipment());
        return dto;
    }
}
