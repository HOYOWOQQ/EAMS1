package com.eams.Entity.attendance.DTO;

import java.util.Map;

import lombok.Data;

@Data
public class RollCallRequest {
    private Integer courseScheduleId;
    private Map<Integer, String> rollCallMap;
}