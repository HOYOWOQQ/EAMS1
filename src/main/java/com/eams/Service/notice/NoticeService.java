package com.eams.Service.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Course;
import com.eams.Entity.member.Member;
import com.eams.Entity.notice.Notice;
import com.eams.Entity.notice.NoticeReadStatus;
import com.eams.Entity.notice.DTO.CommentDTO;
import com.eams.Entity.notice.DTO.NoticeCreateDTO;
import com.eams.Entity.notice.DTO.NoticeDTO;
import com.eams.Entity.notice.DTO.NoticeUpdateDTO;
import com.eams.Repository.course.CourseRepository;
import com.eams.Repository.member.MemberRepository;
import com.eams.Repository.notice.CommentRepository;
import com.eams.Repository.notice.NoticeReadStatusRepository;
import com.eams.Repository.notice.NoticeRepository;

@Service
@Transactional
public class NoticeService {
    
    @Autowired
    private NoticeRepository noticeRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private NoticeReadStatusRepository noticeReadStatusRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private CommentService commentService;
    
    // 根據用戶角色獲取通知列表
    public List<NoticeDTO> getNoticesByUserId(Integer userId, String userRole) {
        List<Notice> notices;
        
        if ("teacher".equals(userRole)) {
            notices = noticeRepository.findNoticesByTeacherId(userId);
        } else {
            notices = noticeRepository.findNoticesByStudentId(userId);
        }
        
        List<NoticeDTO> noticeDTOs = new ArrayList<>();
        for (Notice notice : notices) {
            NoticeDTO dto = convertToDTO(notice);
            dto.setIsRead(isNoticeReadByUser(notice.getNoticeId(), userId));
            dto.setCommentCount((int) commentRepository.getCommentCountByNoticeId(notice.getNoticeId()));
            
            // 🔧 如果是教師，設置完整的已讀統計
            if ("teacher".equals(userRole)) {
                try {
                    // 計算已讀人數
                    long readCount = noticeReadStatusRepository.countByNoticeId(notice.getNoticeId());
                    dto.setReadCount(readCount);
                    
                    // 計算總學生數
                    long totalStudents = noticeRepository.countStudentsByNoticeId(notice.getNoticeId());
                    dto.setTotalStudents(totalStudents);
                    
                    // 計算未讀人數
                    long unreadCount = Math.max(0, totalStudents - readCount);
                    dto.setUnreadCount(unreadCount);
                    
                    // 計算已讀率
                    double readRate = totalStudents > 0 ? (double) readCount / totalStudents * 100 : 0;
                    dto.setReadRate(Math.round(readRate * 100.0) / 100.0); // 保留兩位小數
                    
                    // 添加除錯日誌
                    System.out.println("通知ID: " + notice.getNoticeId() + 
                                     ", 標題: " + notice.getTitle() + 
                                     ", 已讀: " + readCount + 
                                     ", 未讀: " + unreadCount + 
                                     ", 總計: " + totalStudents + 
                                     ", 已讀率: " + readRate + "%");
                    
                } catch (Exception e) {
                    System.err.println("統計已讀人數時發生錯誤: " + e.getMessage());
                    e.printStackTrace();
                    dto.setReadCount(0L);
                    dto.setUnreadCount(0L);
                    dto.setTotalStudents(0L);
                    dto.setReadRate(0.0);
                }
            } else {
                // 學生不顯示統計數據
                dto.setReadCount(null);
                dto.setUnreadCount(null);
                dto.setTotalStudents(null);
                dto.setReadRate(null);
            }
            
            noticeDTOs.add(dto);
        }
        
        return noticeDTOs;
    }
    
    // 根據ID獲取通知詳情
    public NoticeDTO getNoticeDetailById(Integer noticeId, Integer userId) {
        Optional<Notice> noticeOpt = noticeRepository.findByNoticeIdAndIsActiveTrue(noticeId);
        if (!noticeOpt.isPresent()) {
            return null;
        }
        
        Notice notice = noticeOpt.get();
        NoticeDTO dto = convertToDTO(notice);
        
        // 獲取留言
        List<CommentDTO> comments = commentService.getCommentsByNoticeId(noticeId);
        dto.setComments(comments);
        dto.setCommentCount(comments.size());
        dto.setIsRead(isNoticeReadByUser(noticeId, userId));
        
        return dto;
    }
    
    // 創建通知
    public NoticeDTO createNotice(NoticeCreateDTO createDTO, Integer teacherId) {
        Notice notice = new Notice(createDTO.getCourseId(), teacherId, 
                                 createDTO.getTitle(), createDTO.getContent());
        Notice savedNotice = noticeRepository.save(notice);
        return convertToDTO(savedNotice);
    }
    
    // 更新通知
    public NoticeDTO updateNotice(Integer noticeId, NoticeUpdateDTO updateDTO, Integer teacherId) {
        Optional<Notice> noticeOpt = noticeRepository.findByNoticeIdAndIsActiveTrue(noticeId);
        if (!noticeOpt.isPresent()) {
            return null;
        }
        
        Notice notice = noticeOpt.get();
        
        // 檢查權限
        if (!notice.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("您沒有權限編輯此通知");
        }
        
        notice.setTitle(updateDTO.getTitle());
        notice.setContent(updateDTO.getContent());
        notice.setCourseId(updateDTO.getCourseId());
        Notice savedNotice = noticeRepository.save(notice);
        
        return convertToDTO(savedNotice);
    }
    
    // 刪除通知
    public boolean deleteNotice(Integer noticeId, Integer teacherId) {
        Optional<Notice> noticeOpt = noticeRepository.findByNoticeIdAndIsActiveTrue(noticeId);
        if (!noticeOpt.isPresent()) {
            return false;
        }
        
        Notice notice = noticeOpt.get();
        
        // 檢查權限
        if (!notice.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("您沒有權限刪除此通知");
        }
        
        // 軟刪除相關留言
        commentRepository.softDeleteCommentsByNoticeId(noticeId);
        
        // 軟刪除通知
        int result = noticeRepository.softDeleteNotice(noticeId);
        return result > 0;
    }
    
    // 標記通知為已讀
    public void markNoticeAsRead(Integer noticeId, Integer userId) {
        // 檢查是否已存在讀取記錄
        boolean exists = noticeReadStatusRepository.existsByNoticeIdAndUserId(noticeId, userId);
        if (!exists) {
            NoticeReadStatus readStatus = new NoticeReadStatus();
            readStatus.setNoticeId(noticeId);
            readStatus.setUserId(userId);
            noticeReadStatusRepository.save(readStatus);
        }
    }
    
    // 檢查用戶是否有權限訪問通知
    public boolean canUserAccessNotice(Integer userId, String userRole, Integer noticeId) {
        if ("teacher".equals(userRole)) {
            return noticeRepository.canTeacherAccessNotice(noticeId, userId);
        } else {
            return noticeRepository.canStudentAccessNotice(noticeId, userId);
        }
    }
    
    // 獲取未讀通知數量
    public long getUnreadNoticeCount(Integer userId, String userRole) {
        if ("teacher".equals(userRole)) {
            return noticeRepository.getUnreadNoticeCountForTeacher(userId);
        } else {
            return noticeRepository.getUnreadNoticeCountForStudent(userId);
        }
    }
    
    // 檢查通知是否被用戶讀取
    private boolean isNoticeReadByUser(Integer noticeId, Integer userId) {
        return noticeReadStatusRepository.existsByNoticeIdAndUserId(noticeId, userId);
    }
    
    // 轉換為DTO
    private NoticeDTO convertToDTO(Notice notice) {
        NoticeDTO dto = new NoticeDTO();
        dto.setNoticeId(notice.getNoticeId());
        dto.setCourseId(notice.getCourseId());
        dto.setTeacherId(notice.getTeacherId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setIsActive(notice.getIsActive());
        dto.setCreatedAt(notice.getCreatedAt());
        dto.setUpdatedAt(notice.getUpdatedAt());
        
        // 設置課程名稱
        Optional<Course> courseOpt = courseRepository.findById(notice.getCourseId());
        if (courseOpt.isPresent()) {
            dto.setCourseName(courseOpt.get().getName());
        }
        
        // 設置教師名稱
        Optional<Member> teacherOpt = memberRepository.findById(notice.getTeacherId());
        if (teacherOpt.isPresent()) {
            dto.setTeacherName(teacherOpt.get().getName());
        }
        
        return dto;
    }
    
    /**
     * 檢查教師是否可以查看指定通知的已讀狀態
     */
    public boolean canTeacherAccessNotice(Integer teacherId, Integer noticeId) {
        // 檢查通知是否屬於該教師
        Optional<Notice> noticeOpt = noticeRepository.findByNoticeIdAndIsActiveTrue(noticeId);
        return noticeOpt.isPresent() && noticeOpt.get().getTeacherId().equals(teacherId);
    }

    /**
     * 獲取通知的已讀人員列表
     */
    public List<Map<String, Object>> getNoticeReaders(Integer noticeId) {
        List<Map<String, Object>> readers = new ArrayList<>();
        
        try {
            // 查詢已讀此通知的用戶資訊
            List<NoticeReadStatus> readStatuses = noticeReadStatusRepository.findByNoticeIdOrderByReadTimeDesc(noticeId);
            
            for (NoticeReadStatus readStatus : readStatuses) {
                Optional<Member> memberOpt = memberRepository.findById(readStatus.getUserId());
                if (memberOpt.isPresent()) {
                    Member member = memberOpt.get();
                    Map<String, Object> readerInfo = new HashMap<>();
                    readerInfo.put("userId", member.getId());
                    readerInfo.put("userName", member.getName());
                    readerInfo.put("userRole", member.getRole());
                    readerInfo.put("readAt", readStatus.getReadTime());
                    readers.add(readerInfo);
                }
            }
        } catch (Exception e) {
            System.err.println("獲取已讀人員列表時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
        
        return readers;
    }

    /**
     * 獲取通知的已讀統計資訊 - 簡化版本
     */
 // 🔧 更新已讀統計方法
    public Map<String, Object> getNoticeReadStats(Integer noticeId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 統計已讀人數
            long readCount = noticeReadStatusRepository.countByNoticeId(noticeId);
            
            // 統計總學生數
            long totalStudents = noticeRepository.countStudentsByNoticeId(noticeId);
            
            // 計算未讀人數和已讀率
            long unreadCount = Math.max(0, totalStudents - readCount);
            double readRate = totalStudents > 0 ? (double) readCount / totalStudents * 100 : 0;
            
            stats.put("readCount", readCount);
            stats.put("unreadCount", unreadCount);
            stats.put("totalStudents", totalStudents);
            stats.put("readRate", Math.round(readRate * 100.0) / 100.0);
            
        } catch (Exception e) {
            System.err.println("獲取已讀統計時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            stats.put("readCount", 0L);
            stats.put("unreadCount", 0L);
            stats.put("totalStudents", 0L);
            stats.put("readRate", 0.0);
        }
        
        return stats;
    }
 // 🔧 新增：獲取未讀人員列表
    public List<Map<String, Object>> getNoticeUnreadUsers(Integer noticeId) {
        List<Map<String, Object>> unreadUsers = new ArrayList<>();
        
        try {
            // 獲取該課程的所有學生ID
            List<Integer> allStudentIds = noticeRepository.findStudentIdsByNoticeId(noticeId);
            
            // 獲取已讀的學生ID
            List<NoticeReadStatus> readStatuses = noticeReadStatusRepository.findByNoticeIdOrderByReadTimeDesc(noticeId);
            List<Integer> readStudentIds = readStatuses.stream()
                    .map(NoticeReadStatus::getUserId)
                    .collect(java.util.stream.Collectors.toList());
            
            // 找出未讀的學生ID
            List<Integer> unreadStudentIds = allStudentIds.stream()
                    .filter(studentId -> !readStudentIds.contains(studentId))
                    .collect(java.util.stream.Collectors.toList());
            
            // 獲取未讀學生的詳細信息
            for (Integer studentId : unreadStudentIds) {
                Optional<Member> memberOpt = memberRepository.findById(studentId);
                if (memberOpt.isPresent()) {
                    Member member = memberOpt.get();
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", member.getId());
                    userInfo.put("userName", member.getName());
                    userInfo.put("userRole", member.getRole());
                    userInfo.put("status", "未讀");
                    unreadUsers.add(userInfo);
                }
            }
            
        } catch (Exception e) {
            System.err.println("獲取未讀人員列表時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
        
        return unreadUsers;
    }
}
