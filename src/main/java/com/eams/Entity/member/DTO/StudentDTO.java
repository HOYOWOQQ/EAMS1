package com.eams.Entity.member.DTO;

import lombok.Data;
import java.time.LocalDate;

import com.eams.Entity.member.Student;

@Data
public class StudentDTO {
    private Integer id;
    private String name;
    private String gender;
    private LocalDate birthday;
    private Byte grade;
    private String guardianName;
    private String guardianPhone;
    private String address;
    private LocalDate enrollDate;
    private String remark;
    // 如需帶出 member 等關聯，這裡可加 MemberDTO member; 等欄位

    public static StudentDTO fromEntity(Student entity) {
        if (entity == null) return null;
        StudentDTO dto = new StudentDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getMember().getName());
        dto.setGender(entity.getGender());
        dto.setBirthday(entity.getBirthday());
        dto.setGrade(entity.getGrade());
        dto.setGuardianName(entity.getGuardianName());
        dto.setGuardianPhone(entity.getGuardianPhone());
        dto.setAddress(entity.getAddress());
        dto.setEnrollDate(entity.getEnrollDate());
        dto.setRemark(entity.getRemark());
        // 如需帶出 member, 請另外處理
        return dto;
    }
}
