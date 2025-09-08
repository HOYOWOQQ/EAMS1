package com.eams.Service.course;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Course;
import com.eams.Entity.course.CourseEnroll;
import com.eams.Entity.course.Registration;
import com.eams.Entity.course.DTO.RegistrationDTO;
import com.eams.Entity.course.DTO.RegistrationRequest;
import com.eams.Entity.course.Enum.RegistrationStatus;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Repository.course.CourseEnrollRepository;
import com.eams.Repository.course.CourseRepository;
import com.eams.Repository.course.RegistrationRepository;
import com.eams.Repository.member.MemberRepository;
import com.eams.Repository.member.StudentRepository;

@Service
@Transactional
public class RegistrationService {
    
	
	@Autowired
	private MemberRepository memberRepository;
	
    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final CourseEnrollRepository courseEnrollRepository;
    
    public RegistrationService(RegistrationRepository registrationRepository,
                             CourseRepository courseRepository,
                             StudentRepository studentRepository,
                             CourseEnrollRepository courseEnrollRepository) {
        this.registrationRepository = registrationRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.courseEnrollRepository = courseEnrollRepository;
    }
    
    // ===== 雙軌報名核心方法 =====
    
    /**
     * 統一報名入口（支援雙軌報名）
     */
    public Registration submitRegistration(RegistrationRequest request) {
        // 驗證請求資料
        if (!request.isValid()) {
            throw new IllegalArgumentException("報名資料不完整: " + request.getValidationError());
        }
        
        // 驗證課程存在
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("課程不存在: " + request.getCourseId()));
        
        // 檢查課程是否開放報名
        if (!isRegistrationOpen(course)) {
            throw new IllegalStateException("課程目前未開放報名");
        }
        
        // 檢查課程容量
        if (isCourseFull(request.getCourseId())) {
            throw new IllegalStateException("課程已額滿");
        }
        
        if (request.isExistingStudent()) {
            return submitExistingStudentRegistration(request);
        } else {
            return submitNewStudentApplication(request);
        }
    }
    
    /**
     * 現有學生報名
     */
    private Registration submitExistingStudentRegistration(RegistrationRequest request) {
        // 驗證學生存在
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new IllegalArgumentException("學生不存在: " + request.getStudentId()));
        
        // 檢查是否已經報名過
        if (registrationRepository.existsByCourseIdAndStudentId(request.getCourseId(), request.getStudentId())) {
            throw new IllegalStateException("學生已經報名過此課程");
        }
        autoEnrollStudent(request.getCourseId(), request.getStudentId());
        
        // 創建報名記錄（現有學生直接核准）
        Registration registration = request.toRegistration();
        return registrationRepository.save(registration);
    }
    
    /**
     * 新學生申請
     */
    private Registration submitNewStudentApplication(RegistrationRequest request) {
        // 檢查帳號是否已存在於學生表
        Optional<Member> existingStudent = memberRepository.findByAccount(request.getStudentAccount());
        if (existingStudent.isPresent()) {
            throw new IllegalStateException("此帳號已是現有學生，請使用現有學生報名方式");
        }
        
        // 檢查是否已有相同帳號的待審核申請
        if (registrationRepository.existsPendingApplicationByAccount(request.getStudentAccount())) {
            throw new IllegalStateException("此帳號已有待審核的申請，請勿重複申請");
        }
        
        // 檢查是否已申請過此課程
        if (registrationRepository.existsNewStudentApplicationByCourseAndAccount(
                request.getCourseId(), request.getStudentAccount())) {
            throw new IllegalStateException("此帳號已申請過此課程");
        }
        
        // 創建新學生申請記錄
        Registration registration = request.toRegistration();
        return registrationRepository.save(registration);
    }
    
    // ===== 審核相關方法 =====
    
    /**
     * 審核報名申請（支援雙軌）
     */
    public Registration reviewRegistration(Integer registrationId, RegistrationStatus status, 
                                         String reviewNote, Integer reviewerId) {
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("報名記錄不存在: " + registrationId));
        
        if (!registration.canBeReviewed()) {
            throw new IllegalStateException("只能審核待處理的報名申請");
        }
        
        if (registration.isNewStudentApplication()) {
            return reviewNewStudentApplication(registration, status, reviewNote, reviewerId);
        } else {
            return reviewExistingStudentRegistration(registration, status, reviewNote, reviewerId);
        }
    }
    
    /**
     * 審核新學生申請
     */
    private Registration reviewNewStudentApplication(Registration registration, RegistrationStatus status,
                                                   String reviewNote, Integer reviewerId) {
        Integer approvedStudentId = null;
        
        // 如果核准，需要先建立學生記錄
        if (status == RegistrationStatus.APPROVED) {
            Student newStudent = createStudentFromRegistration(registration);
            approvedStudentId = newStudent.getId();
        }
        
        // 設定審核結果
        registration.setReviewResult(status, reviewNote, reviewerId, approvedStudentId);
        Registration savedRegistration = registrationRepository.save(registration);
        
        // 如果核准，自動加入選課
        if (status == RegistrationStatus.APPROVED && approvedStudentId != null) {
            autoEnrollStudent(registration.getCourseId(), approvedStudentId);
        }
        
        return savedRegistration;
    }
    
    /**
     * 審核現有學生報名
     */
    private Registration reviewExistingStudentRegistration(Registration registration, RegistrationStatus status,
                                                         String reviewNote, Integer reviewerId) {
        // 設定審核結果
        registration.setReviewResult(status, reviewNote, reviewerId, null);
        Registration savedRegistration = registrationRepository.save(registration);
        
        // 如果核准，自動加入選課
        if (status == RegistrationStatus.APPROVED) {
            autoEnrollStudent(registration.getCourseId(), registration.getStudentId());
        }
        
        return savedRegistration;
    }
    
    /**
     * 從報名資料建立學生記錄
     */
    /**
     * 從報名資料建立學生記錄
     */
    private Student createStudentFromRegistration(Registration registration) {
        // 生成預設密碼（生日八碼）
        String defaultPassword = generateDefaultPassword(registration.getStudentBirthday());
        
        // 先建立 Member 記錄
        Member member = Member.builder()
            .account(registration.getStudentAccount())
            .password(defaultPassword)
            .name(registration.getStudentName())
            .role("STUDENT")
            .email(registration.getStudentEmail())
            .phone(registration.getStudentPhone())
            .verified(false)
            .status(true)
            .createTime(LocalDateTime.now())
            .updateTime(LocalDateTime.now())
            .build();
        
        Member savedMember = memberRepository.save(member);
        
        // 再建立 Student 記錄
        Student student = Student.builder()
            .id(savedMember.getId())
            .gender(registration.getStudentGender())
            .birthday(registration.getStudentBirthday())
            .grade(registration.getStudentGrade() != null ? registration.getStudentGrade().byteValue() : null)
            .guardianName(registration.getGuardianName())
            .guardianPhone(registration.getGuardianPhone())
            .address(registration.getStudentAddress())
            .enrollDate(LocalDate.now())
            .remark(registration.getRemark())
            .member(savedMember)
            .build();
        
        return studentRepository.save(student);
    }

    /**
     * 生成預設密碼（生日八碼）
     */
    private String generateDefaultPassword(LocalDate birthday) {
        if (birthday == null) {
            return "12345678";
        }
        return birthday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    
    // ===== 取消相關方法 =====
    
    /**
     * 取消報名申請
     */
    public Registration cancelRegistration(Integer registrationId, Integer requesterId) {
        Registration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new IllegalArgumentException("報名記錄不存在: " + registrationId));
        
        // 驗證權限（現有學生只能取消自己的報名）
        if (registration.isExistingStudentRegistration() && 
            !registration.getStudentId().equals(requesterId)) {
            throw new IllegalArgumentException("無權限取消此報名");
        }
        
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new IllegalStateException("報名已經被取消");
        }
        
        registration.setStatus(RegistrationStatus.CANCELLED);
        return registrationRepository.save(registration);
    }
    
    // ===== 查詢方法 =====
    
    /**
     * 查詢學生的報名記錄
     */
    @Transactional(readOnly = true)
    public List<Registration> getStudentRegistrations(Integer studentId) {
        return registrationRepository.findByStudentIdOrderByRegistrationDateDesc(studentId);
    }
    
    /**
     * 查詢課程的報名記錄
     */
    @Transactional(readOnly = true)
    public List<Registration> getCourseRegistrations(Integer courseId) {
        return registrationRepository.findByCourseIdOrderByRegistrationDateDesc(courseId);
    }
    
    /**
     * 查詢待審核的報名記錄
     */
    @Transactional(readOnly = true)
    public List<Registration> getPendingRegistrations() {
        return registrationRepository.findPendingRegistrations();
    }
    
    /**
     * 查詢待審核的新學生申請
     */
    @Transactional(readOnly = true)
    public List<Registration> getPendingNewStudentApplications() {
        return registrationRepository.findPendingNewStudentApplications();
    }
    
    /**
     * 根據ID查詢報名記錄
     */
    @Transactional(readOnly = true)
    public Optional<Registration> getRegistrationById(Integer id) {
        return registrationRepository.findById(id);
    }
    
    // ===== 統計方法 =====
    
    /**
     * 獲取課程報名統計
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCourseRegistrationStats(Integer courseId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 基本統計
        stats.put("pending", registrationRepository.countByCourseIdAndStatus(courseId, RegistrationStatus.PENDING));
        stats.put("approved", registrationRepository.countByCourseIdAndStatus(courseId, RegistrationStatus.APPROVED));
        stats.put("rejected", registrationRepository.countByCourseIdAndStatus(courseId, RegistrationStatus.REJECTED));
        stats.put("cancelled", registrationRepository.countByCourseIdAndStatus(courseId, RegistrationStatus.CANCELLED));
        
        // 分類統計
        stats.put("existingStudents", registrationRepository.countApprovedExistingStudentsByCourseId(courseId));
        stats.put("newStudents", registrationRepository.countApprovedNewStudentsByCourseId(courseId));
        
        return stats;
    }
    
    /**
     * 獲取整體統計
     */
    @Transactional(readOnly = true)
    public Map<String, Integer> getOverallStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("pendingNewStudents", registrationRepository.countPendingNewStudentApplications());
        stats.put("pendingExistingStudents", registrationRepository.countPendingExistingStudentRegistrations());
        return stats;
    }
    
    // ===== 分頁查詢 =====
    
    /**
     * 分頁查詢報名記錄（支援篩選）
     */
//    @Transactional(readOnly = true)
//    public Page<Registration> getRegistrations(Integer courseId, Integer studentId, 
//                                             RegistrationStatus status, Boolean isNewStudent,
//                                             int page, int size, String sortBy, String sortDir) {
//        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
//                    Sort.by(sortBy).descending() : 
//                    Sort.by(sortBy).ascending();
//        
//        Pageable pageable = PageRequest.of(page, size, sort);
//        
//        // 使用 Specification 進行動態查詢
//        Specification<Registration> spec = Specification.where(null);
//        
//        if (courseId != null) {
//            spec = spec.and((root, query, cb) -> cb.equal(root.get("courseId"), courseId));
//        }
//        
//        if (studentId != null) {
//            spec = spec.and((root, query, cb) -> cb.equal(root.get("studentId"), studentId));
//        }
//        
//        if (status != null) {
//            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
//        }
//        
//        if (isNewStudent != null) {
//            if (isNewStudent) {
//                spec = spec.and((root, query, cb) -> cb.isNull(root.get("studentId")));
//            } else {
//                spec = spec.and((root, query, cb) -> cb.isNotNull(root.get("studentId")));
//            }
//        }
//        
//        return registrationRepository.findAll(spec, pageable);
//    }
//    
    @Transactional(readOnly = true)
    public Page<RegistrationDTO> getRegistrations(Integer courseId, Integer studentId,
                                                RegistrationStatus status, Boolean isNewStudent,
                                                int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // 查詢 Registration 實體（包含關聯資料）
        Page<Registration> registrationPage = registrationRepository.findRegistrationsWithDetails(
                courseId, studentId, status, isNewStudent, pageable);

        // 轉換為 DTO
        return registrationPage.map(RegistrationDTO::fromEntity);
    }
    // ===== 私有輔助方法 =====
    
    /**
     * 檢查課程是否開放報名
     */
    private boolean isRegistrationOpen(Course course) {
        LocalDate now = LocalDate.now();
        
        // 檢查課程狀態
        if (!"active".equals(course.getStatus())) {
            return false;
        }
        
        // 檢查報名時間
        if (course.getRegistrationStartDate() != null && now.isBefore(course.getRegistrationStartDate())) {
            return false;
        }
        
        if (course.getRegistrationEndDate() != null && now.isAfter(course.getRegistrationEndDate())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 檢查課程是否已額滿
     */
    private boolean isCourseFull(Integer courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getMaxCapacity() == null) {
            return false;
        }
        
        Integer approvedCount = registrationRepository.countApprovedRegistrationsByCourseId(courseId);
        return approvedCount >= course.getMaxCapacity();
    }
    
    /**
     * 自動將學生加入選課
     */
    private void autoEnrollStudent(Integer courseId, Integer studentId) {
        // 檢查是否已經選課
        if (!courseEnrollRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            CourseEnroll enrollment = new CourseEnroll();
            enrollment.setCourseId(courseId);
            enrollment.setStudentId(studentId);
            enrollment.setEnrollDate(LocalDate.now());
            enrollment.setStatus("enrolled");
            courseEnrollRepository.save(enrollment);
        }
    }
}