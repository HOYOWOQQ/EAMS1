package com.eams.Service.attendance;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.eams.Repository.attendance.AttendanceRepository;
import com.eams.Repository.attendance.CourseOptionView;
import com.eams.Entity.attendance.DTO.TrendPointDTO;

@Service
public class AttendanceAnalyticsService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    private TrendPointDTO toMonthDTO(Object[] r) {
        int monthNo = ((Number) r[0]).intValue();
        int present = ((Number) r[1]).intValue();
        int total = ((Number) r[2]).intValue();
        return new TrendPointDTO(monthNo + "月", present, total);
    }

    private TrendPointDTO toWeekDTO(Object[] r) {
        int weekNo = ((Number) r[0]).intValue();
        int present = ((Number) r[1]).intValue();
        int total = ((Number) r[2]).intValue();
        return new TrendPointDTO("第" + weekNo + "週", present, total);
    }

    private TrendPointDTO toDayDTO(Object[] r) {
        int dayNo = ((Number) r[0]).intValue();
        int present = ((Number) r[1]).intValue();
        int total = ((Number) r[2]).intValue();
        return new TrendPointDTO(String.valueOf(dayNo), present, total);
    }

    public List<TrendPointDTO> getTrend(String groupBy, int year, Integer month, Integer courseId) {
        switch (groupBy) {
            case "month":
                return attendanceRepository.aggregateByMonthNative(year, courseId)
                        .stream().map(this::toMonthDTO).toList();

            case "week":
                if (month == null) throw new IllegalArgumentException("month is required when groupBy=week");
                return attendanceRepository.aggregateByWeekNative(year, month, courseId)
                        .stream().map(this::toWeekDTO).toList();

            case "day":
                if (month == null) throw new IllegalArgumentException("month is required when groupBy=day");
                return attendanceRepository.aggregateByDayNative(year, month, courseId)
                        .stream().map(this::toDayDTO).toList();

            default:
                throw new IllegalArgumentException("invalid groupBy");
        }
    }
    public List<CourseOptionView> getCourseOptionsForAnalytics(int year, Integer month) {
        return attendanceRepository.findCoursesForAnalytics(year, month);
    }
}