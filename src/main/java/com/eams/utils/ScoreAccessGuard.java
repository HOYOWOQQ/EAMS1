package com.eams.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.eams.Entity.score.DTO.ExamPaperDTO;
import com.eams.Entity.score.DTO.ExamResultDTO;
import com.eams.Repository.score.ExamPaperRepository;
import com.eams.common.log.util.UserContextUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreAccessGuard {

    @Autowired private UserContextUtil userCtx;
    @Autowired private ExamPaperRepository examPaperRepo;
    @Autowired private JdbcTemplate jdbc;

    /* ===================== 共用小工具 ===================== */

    public Integer currentUserIdOr403() {
        Long id = userCtx.getCurrentUserId();
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登入");
        return id.intValue();
    }

    /** 主要 role（小寫；可能為 null） */
    private String mainRole() {
        String r = userCtx.getCurrentUserRole();
        r = (r == null) ? null : r.trim().toLowerCase();
        log.debug("[Guard] mainRole from userCtx = {}", r);
        return r;
    }

    /** 權限字串保險判斷；只要包含關鍵字即視為有該類身分 */
    private boolean permsContains(String keyword) {
        var perms = userCtx.getCurrentUserPermissions();
        if (perms == null) return false;
        String kw = keyword.toLowerCase();
        for (String p : perms) {
            if (p != null && p.toLowerCase().contains(kw)) return true;
        }
        return false;
    }

    /** ★ 寫死主任判斷（暫時方案）：只有 id=45 視為主任以上 */
    private boolean isHardcodedDirector() {
        try {
            Integer me = currentUserIdOr403();
            return me != null && me == 45;
        } catch (ResponseStatusException e) {
            return false;
        }
    }

    /* ===================== 角色判斷（簡化 + 寫死主任） ===================== */

    /** 學生（僅認 student；mainRole 空再用權限字串保險判斷） */
    public boolean isStudent() {
        String r = mainRole();
        if ("student".equals(r)) return true;
        return (r == null) && permsContains("student");
    }

    /** 老師（老師 + 主任以上都視為老師等級） */
    public boolean isTeacher() {
        // 寫死的主任也屬於「老師等級」
        if (isDirectorOrAbove()) return true;

        String r = mainRole();
        if ("teacher".equals(r)) return true;

        // 保險判斷
        if (r == null) {
            return permsContains("teacher") || permsContains("director")
                    || permsContains("admin") || permsContains("super_admin");
        }
        return false;
    }

    /** 主任以上（包含 admin/super_admin）；加入寫死 id=45 */
    public boolean isDirectorOrAbove() {
        if (isHardcodedDirector()) return true;

        String r = mainRole();
        if ("academic_director".equals(r) || "director".equals(r)
                || "admin".equals(r) || "super_admin".equals(r)) {
            return true;
        }
        return (r == null) && (permsContains("director")
                || permsContains("admin") || permsContains("super_admin"));
    }

    /** 老師或主任（主任以上皆可） */
    public void assertTeacherOrDirector() {
        if (isTeacher()) return;
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要老師或主任以上身分");
    }

    /** 只有主任以上 */
    public void assertDirectorOrAbove() {
        if (isDirectorOrAbove()) return;
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要主任以上身分");
    }

    /** 學生或家長（保留將來擴充） */
    public boolean isStudentOrParent() {
        String r = mainRole();
        if ("student".equals(r) || "parent".equals(r)) return true;
        return (r == null) && (permsContains("student") || permsContains("parent"));
    }
    public void assertStudentOrParent() {
        if (isStudentOrParent()) return;
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要學生或家長身分");
    }

    /* ===================== 授權用到的關聯檢查（維持既有介面） ===================== */

    /** 老師是否負責某課程（依 course_schedule） */
    public boolean teacherOwnsCourse(int teacherId, int courseId) {
        final String SQL_BY_SCHEDULE =
            "SELECT 1 FROM course_schedule WHERE course_id = ? AND teacher_id = ?";

        try {
            Integer ok = jdbc.query(SQL_BY_SCHEDULE, ps -> {
                ps.setInt(1, courseId);
                ps.setInt(2, teacherId);
            }, rs -> rs.next() ? 1 : null);

            return ok != null;
        } catch (DataAccessException e) {
            log.debug("[Guard] teacherOwnsCourse 查詢 course_schedule 失敗: {}", e.getMessage());
            return false;
        }
    }

    public void assertTeacherOwnsCourse(int teacherId, int courseId) {
        if (!teacherOwnsCourse(teacherId, courseId)) {
            throw new AccessDeniedException("你不負責這門課（courseId=" + courseId + "）");
        }
    }

    public boolean teacherOwnsExamPaper(int teacherId, int examPaperId) {
        Integer courseId = examPaperRepo.findById(examPaperId)
            .map(ep -> ep.getCourse() != null ? ep.getCourse().getId() : null)
            .orElse(null);
        if (courseId == null) return false;
        return teacherOwnsCourse(teacherId, courseId);
    }

    public void assertTeacherOwnsExamPaper(int teacherId, int examPaperId) {
        if (!teacherOwnsExamPaper(teacherId, examPaperId)) {
            throw new AccessDeniedException("你不負責這張考卷（paperId=" + examPaperId + "）");
        }
    }

    public void filterExamResultsByTeacher(List<ExamResultDTO> list, int teacherId) {
        if (list == null || list.isEmpty()) return;
        for (var it = list.iterator(); it.hasNext();) {
            var dto = it.next();
            Integer paperId = dto.getExam_paper_id();
            if (paperId == null || !teacherOwnsExamPaper(teacherId, paperId)) it.remove();
        }
    }

    /** 以可變覆蓋方式過濾（保留既有名稱與行為） */
    public void filterExamPapersByTeacher(List<ExamPaperDTO> list, int teacherId) {
        if (list == null || list.isEmpty()) return;

        var filtered = new ArrayList<ExamPaperDTO>(list.size());
        for (ExamPaperDTO dto : list) {
            Integer courseId = dto.getCourseId();
            boolean keep = false;

            if (courseId == null) {
                Integer paperId = dto.getId();
                if (paperId != null) {
                    Integer cId = examPaperRepo.findById(paperId)
                            .map(ep -> ep.getCourse() != null ? ep.getCourse().getId() : null)
                            .orElse(null);
                    keep = (cId != null && teacherOwnsCourse(teacherId, cId));
                }
            } else {
                keep = teacherOwnsCourse(teacherId, courseId);
            }
            if (keep) filtered.add(dto);
        }
        list.clear();
        list.addAll(filtered);
    }

    /** 回傳新清單版本（保留既有名稱與行為） */
    public List<ExamPaperDTO> filterExamPapersByTeacherCopy(List<ExamPaperDTO> src, int teacherId) {
        if (src == null || src.isEmpty()) return List.of();
        List<ExamPaperDTO> out = new ArrayList<>(src.size());
        for (ExamPaperDTO dto : src) {
            Integer courseId = dto.getCourseId();
            Integer paperId  = dto.getId();
            boolean ok = false;

            if (courseId != null) {
                ok = teacherOwnsCourse(teacherId, courseId);
                log.debug("[Guard] paperId={}, courseId={}, owner? {}", paperId, courseId, ok);
            } else if (paperId != null) {
                ok = teacherOwnsExamPaper(teacherId, paperId);
                log.debug("[Guard] paperId={} (反查 course) owner? {}", paperId, ok);
            } else {
                log.debug("[Guard] paper 既無 courseId 也無 id，略過");
            }

            if (ok) out.add(dto);
        }
        return out;
    }
}
