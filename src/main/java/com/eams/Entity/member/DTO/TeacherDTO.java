package com.eams.Entity.member.DTO;

import lombok.Data;
import java.time.LocalDate;
import com.eams.Entity.member.Teacher;

@Data
public class TeacherDTO {
    private Integer id;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String position;
    private String specialty;
    private LocalDate hireDate;
    private String address;
    private String remark;
    private Integer supervisorId;
    // private MemberDTO member;   // 若要帶出 member, 取消註解，建議先設計好 MemberDTO
    // private List<SubjectDTO> subjects;
    // private List<CourseScheduleDTO> courseSchedule;
    // private List<ExamResultDTO> examResult;

    public static TeacherDTO fromEntity(Teacher entity) {
        if (entity == null) return null;
        TeacherDTO dto = new TeacherDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getMember().getName());
        dto.setGender(entity.getGender());
        dto.setBirthday(entity.getBirthday());
        dto.setPosition(entity.getPosition());
        dto.setSpecialty(entity.getSpecialty());
        dto.setHireDate(entity.getHireDate());
        dto.setAddress(entity.getAddress());
        dto.setRemark(entity.getRemark());
        dto.setSupervisorId(entity.getSupervisorId());
        // 若有需要關聯欄位可加對應轉換
        return dto;
    }
}
