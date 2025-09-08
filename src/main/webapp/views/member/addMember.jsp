<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <% if (session.getAttribute("id")==null) { response.sendRedirect(request.getContextPath() + "/login" ); return;
            } %>

            <!DOCTYPE html>
            <html lang="zh-TW">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>新增會員</title>
                <!-- SweetAlert2 CSS -->
                <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css" rel="stylesheet">
                <!-- SweetAlert2 JS -->
                <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                <style>
                    body {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        font-family: "Noto Sans TC", Arial, sans-serif;
                        margin: 0;
                        padding: 20px;
                    }

                    .page-container {
                        max-width: 1400px;
                        margin: 0 auto;
                    }

                    .page-header {
                        background: #fff;
                        border-radius: 20px 20px 0 0;
                        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                        padding: 32px;
                        margin-bottom: 2px;
                        position: relative;
                        overflow: hidden;
                    }

                    .page-header::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 4px;
                        background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                    }

                    .header-content {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        flex-wrap: wrap;
                        gap: 20px;
                    }

                    .header-info {
                        display: flex;
                        align-items: center;
                        gap: 16px;
                    }

                    .page-icon {
                        width: 48px;
                        height: 48px;
                        background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                        border-radius: 12px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 20px;
                        color: white;
                    }

                    .page-title {
                        font-size: 1.8rem;
                        font-weight: 700;
                        color: #1e293b;
                        margin: 0;
                    }

                    .page-subtitle {
                        font-size: 0.95rem;
                        color: #64748b;
                        margin: 4px 0 0 0;
                    }

                    .header-actions {
                        display: flex;
                        gap: 12px;
                        align-items: center;
                    }

                    .form-container {
                        background: #fff;
                        border-radius: 0 0 20px 20px;
                        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                        padding: 32px;
                        margin-bottom: 20px;
                    }

                    .alert {
                        padding: 16px 20px;
                        border-radius: 12px;
                        margin-bottom: 24px;
                        font-weight: 500;
                        display: flex;
                        align-items: center;
                        gap: 12px;
                        font-size: 0.95rem;
                        border: 2px solid;
                        display: none;
                    }

                    .alert-success {
                        background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
                        color: #065f46;
                        border-color: #10b981;
                    }

                    .alert-error {
                        background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
                        color: #991b1b;
                        border-color: #ef4444;
                    }

                    .alert::before {
                        content: '✓';
                        width: 24px;
                        height: 24px;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-weight: bold;
                        flex-shrink: 0;
                        font-size: 14px;
                    }

                    .alert-success::before {
                        background: #10b981;
                        color: white;
                    }

                    .alert-error::before {
                        content: '⚠';
                        background: #ef4444;
                        color: white;
                    }

                    .role-selector {
                        background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
                        border: 2px solid #e2e8f0;
                        border-radius: 16px;
                        padding: 24px;
                        margin-bottom: 32px;
                        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                    }

                    .role-selector h3 {
                        margin: 0 0 20px 0;
                        color: #1e293b;
                        font-size: 1.1rem;
                        font-weight: 600;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    }

                    .role-selector h3::before {
                        content: '🎭';
                        font-size: 16px;
                    }

                    .role-tabs {
                        display: flex;
                        gap: 16px;
                    }

                    .role-tab {
                        padding: 12px 24px;
                        border: 2px solid #e2e8f0;
                        border-radius: 12px;
                        background: white;
                        color: #64748b;
                        cursor: pointer;
                        transition: all 0.3s;
                        font-weight: 600;
                        font-size: 0.95rem;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
                    }

                    .role-tab.active {
                        background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                        color: white;
                        border-color: #4e8cff;
                        box-shadow: 0 4px 12px rgba(78, 140, 255, 0.3);
                        transform: translateY(-1px);
                    }

                    .role-tab:hover:not(.active) {
                        background: #f1f5f9;
                        border-color: #cbd5e1;
                        transform: translateY(-1px);
                        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                    }

                    .section-header {
                        margin: 32px 0 24px 0;
                        padding-bottom: 12px;
                        border-bottom: 2px solid #e2e8f0;
                    }

                    .section-header h3 {
                        margin: 0;
                        color: #1e293b;
                        font-size: 1.2rem;
                        font-weight: 600;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    }

                    .section-header:first-of-type {
                        margin-top: 0;
                    }

                    .form-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                        gap: 20px 24px;
                        margin-bottom: 24px;
                    }

                    @media (min-width: 1200px) {
                        .form-grid {
                            grid-template-columns: repeat(4, 1fr);
                        }
                    }

                    @media (min-width: 900px) and (max-width: 1199px) {
                        .form-grid {
                            grid-template-columns: repeat(3, 1fr);
                        }
                    }

                    @media (min-width: 600px) and (max-width: 899px) {
                        .form-grid {
                            grid-template-columns: repeat(2, 1fr);
                        }
                    }

                    .form-field {
                        display: flex;
                        flex-direction: column;
                        gap: 8px;
                    }

                    .form-field.span-2 {
                        grid-column: span 2;
                    }

                    .form-field.full-width {
                        grid-column: 1 / -1;
                    }

                    .form-label {
                        font-weight: 600;
                        color: #374151;
                        font-size: 0.9rem;
                        display: flex;
                        align-items: center;
                        gap: 6px;
                    }

                    .form-input,
                    .form-select {
                        background: #fff;
                        border: 2px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 14px 12px;
                        font-size: 0.95rem;
                        color: #1e293b;
                        transition: all 0.3s;
                        outline: none;
                        width: 95%;
                        font-family: inherit;
                    }

                    .form-input:focus,
                    .form-select:focus {
                        border-color: #4e8cff;
                        box-shadow: 0 0 0 3px rgba(78, 140, 255, 0.1);
                    }

                    .form-input:hover,
                    .form-select:hover {
                        border-color: #cbd5e1;
                    }

                    .form-input.valid,
                    .form-select.valid {
                        border-color: #10b981;
                        box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
                    }

                    .form-input.invalid,
                    .form-select.invalid {
                        border-color: #ef4444;
                        box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
                    }

                    .role-fields {
                        transition: all 0.5s ease;
                        overflow: hidden;
                    }

                    .role-fields.hidden {
                        max-height: 0;
                        opacity: 0;
                        margin: 0;
                        padding: 0;
                    }

                    .role-fields.visible {
                        max-height: 2000px;
                        opacity: 1;
                    }

                    .student-fields,
                    .teacher-fields {
                        background: linear-gradient(135deg, #fefefe 0%, #f8fafc 100%);
                        border: 2px solid #e2e8f0;
                        border-radius: 16px;
                        padding: 24px;
                        margin-bottom: 24px;
                        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                    }

                    .student-fields h4,
                    .teacher-fields h4 {
                        margin: 0 0 20px 0;
                        color: #1e293b;
                        font-size: 1.1rem;
                        font-weight: 600;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        padding-bottom: 12px;
                        border-bottom: 2px solid #e2e8f0;
                    }

                    .student-fields h4::before {
                        content: '🎓';
                        font-size: 18px;
                    }

                    .teacher-fields h4::before {
                        content: '👨‍🏫';
                        font-size: 18px;
                    }

                    .password-requirements {
                        background: #f8fafc;
                        border: 2px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 16px;
                        margin-top: 8px;
                        font-size: 0.85rem;
                    }

                    .requirement {
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        margin-bottom: 6px;
                        color: #64748b;
                        transition: color 0.3s;
                    }

                    .requirement:last-child {
                        margin-bottom: 0;
                    }

                    .requirement.met {
                        color: #10b981;
                    }

                    .requirement::before {
                        content: '○';
                        font-weight: bold;
                        transition: all 0.3s;
                        width: 16px;
                        text-align: center;
                        font-size: 12px;
                    }

                    .requirement.met::before {
                        content: '✓';
                        color: #10b981;
                    }

                    .error-message {
                        color: #ef4444;
                        font-size: 0.85rem;
                        margin-top: 6px;
                        display: none;
                        padding: 8px 12px;
                        background: #fee2e2;
                        border-radius: 6px;
                        border: 1px solid #fecaca;
                    }

                    .btn {
                        padding: 12px 24px;
                        border: none;
                        border-radius: 8px;
                        font-size: 0.95rem;
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.3s;
                        text-decoration: none;
                        display: inline-flex;
                        align-items: center;
                        gap: 8px;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                        white-space: nowrap;
                        font-family: inherit;
                    }

                    .btn-primary {
                        background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                        color: white;
                    }

                    .btn-primary:hover {
                        background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
                        transform: translateY(-1px);
                        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
                    }

                    .btn-secondary {
                        background: #f8fafc;
                        color: #374151;
                        border: 2px solid #e2e8f0;
                    }

                    .btn-secondary:hover {
                        background: #e2e8f0;
                        border-color: #cbd5e1;
                        transform: translateY(-1px);
                    }

                    .action-bar {
                        display: flex;
                        gap: 16px;
                        justify-content: flex-end;
                        align-items: center;
                        margin-top: 32px;
                        padding-top: 24px;
                        border-top: 2px solid #e2e8f0;
                    }

                    @media (max-width: 768px) {
                        body {
                            padding: 10px;
                        }

                        .page-header {
                            padding: 20px;
                            border-radius: 16px 16px 0 0;
                        }

                        .header-content {
                            flex-direction: column;
                            align-items: stretch;
                            gap: 16px;
                        }

                        .form-container {
                            padding: 20px;
                            border-radius: 0 0 16px 16px;
                        }

                        .form-grid {
                            grid-template-columns: 1fr;
                            gap: 16px;
                        }

                        .role-tabs {
                            flex-direction: column;
                            gap: 12px;
                        }

                        .role-tab {
                            width: 100%;
                            justify-content: center;
                        }

                        .action-bar {
                            flex-direction: column;
                            gap: 12px;
                        }

                        .btn {
                            width: 100%;
                            justify-content: center;
                        }
                    }

                    @keyframes fadeIn {
                        from {
                            opacity: 0;
                            transform: translateY(20px);
                        }

                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }

                    .form-container {
                        animation: fadeIn 0.5s ease-out;
                    }
                </style>
            </head>

            <body>
                <div class="page-container">
                    <!-- 頁面標題 -->
                    <div class="page-header">
                        <div class="header-content">
                            <div class="header-info">
                                <div class="page-icon">➕</div>
                                <div>
                                    <h1 class="page-title">新增會員</h1>
                                    <p class="page-subtitle">建立新的會員帳號，支援學生和教師身份</p>
                                </div>
                            </div>
                            <div class="header-actions">
                                <a href="${pageContext.request.contextPath}/memberList" class="btn btn-secondary">
                                    ← 返回列表
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- 主要內容 -->
                    <div class="form-container">
                        <!-- 訊息顯示區域 -->
                        <div id="alertContainer"></div>

                        <!-- 身份選擇器 -->
                        <div class="role-selector">
                            <h3>選擇身份類型</h3>
                            <div class="role-tabs">
                                <div class="role-tab active" data-role="student" onclick="selectRole('student')"
                                    tabindex="0">
                                    🎓 學生
                                </div>
                                <div class="role-tab" data-role="teacher" onclick="selectRole('teacher')" tabindex="0">
                                    👨‍🏫 老師
                                </div>
                            </div>
                        </div>

                        <form id="addMemberForm">
                            <input type="hidden" name="role" id="selectedRole" value="student">

                            <!-- 基本資料 -->
                            <div class="section-header">
                                <h3>🆔 基本資料</h3>
                            </div>
                            <div class="form-grid">
                                <div class="form-field">
                                    <label class="form-label">👤 帳號</label>
                                    <input type="text" name="account" class="form-input" required placeholder="請輸入帳號"
                                        autocomplete="username">
                                </div>

                                <div class="form-field">
                                    <label class="form-label">✏️ 姓名</label>
                                    <input type="text" name="name" class="form-input" required placeholder="請輸入姓名"
                                        autocomplete="name">
                                </div>

                                <div class="form-field">
                                    <label class="form-label">⚧ 性別</label>
                                    <select name="gender" class="form-select" required>
                                        <option value="">請選擇性別</option>
                                        <option value="男">男</option>
                                        <option value="女">女</option>
                                    </select>
                                </div>

                                <div class="form-field">
                                    <label class="form-label">🎂 生日</label>
                                    <input type="date" name="birthday" class="form-input" required autocomplete="bday">
                                </div>
                            </div>

                            <!-- 聯絡資訊 -->
                            <div class="section-header">
                                <h3>📞 聯絡資訊</h3>
                            </div>
                            <div class="form-grid">
                                <div class="form-field">
                                    <label class="form-label">📧 Email</label>
                                    <input type="email" name="email" class="form-input" required placeholder="請輸入 Email"
                                        autocomplete="email">
                                </div>

                                <div class="form-field">
                                    <label class="form-label">📱 電話</label>
                                    <input type="text" name="phone" class="form-input" placeholder="請輸入電話號碼"
                                        autocomplete="tel">
                                </div>

                                <div class="form-field span-2">
                                    <label class="form-label">🏠 住址</label>
                                    <input type="text" name="address" class="form-input" placeholder="請輸入住址"
                                        autocomplete="street-address">
                                </div>
                            </div>

                            <!-- 帳戶安全 -->
                            <div class="section-header">
                                <h3>🔒 帳戶安全</h3>
                            </div>
                            <div class="form-grid">
                                <div class="form-field">
                                    <label class="form-label">🔒 密碼</label>
                                    <input type="password" name="password" id="password" class="form-input" required
                                        placeholder="請輸入密碼（至少8個字元）" minlength="8" autocomplete="new-password">
                                    <div class="password-requirements">
                                        <div class="requirement" id="req-length">至少8個字元</div>
                                        <div class="requirement" id="req-letter">包含英文字母</div>
                                        <div class="requirement" id="req-number">包含數字</div>
                                    </div>
                                </div>

                                <div class="form-field">
                                    <label class="form-label">✅ 確認密碼</label>
                                    <input type="password" name="confirmPassword" id="confirmPassword"
                                        class="form-input" required placeholder="請再次輸入密碼" minlength="8"
                                        autocomplete="new-password">
                                    <div class="error-message" id="confirmPasswordError">
                                        密碼確認不符
                                    </div>
                                </div>
                            </div>

                            <!-- 學生專屬欄位 -->
                            <div id="studentFields" class="role-fields visible">
                                <div class="student-fields">
                                    <h4>學生專屬資料</h4>
                                    <div class="form-grid">
                                        <div class="form-field">
                                            <label class="form-label">📚 年級</label>
                                            <select name="grade" class="form-select">
                                                <option value="">請選擇年級</option>
                                                <option value="1">一年級</option>
                                                <option value="2">二年級</option>
                                                <option value="3">三年級</option>
                                            </select>
                                        </div>

                                        <div class="form-field">
                                            <label class="form-label">📅 入學日</label>
                                            <input type="date" name="enrollDate" class="form-input">
                                        </div>

                                        <div class="form-field">
                                            <label class="form-label">👨‍👩‍👧 家長姓名</label>
                                            <input type="text" name="guardianName" class="form-input"
                                                placeholder="請輸入家長姓名">
                                        </div>

                                        <div class="form-field">
                                            <label class="form-label">☎️ 家長電話</label>
                                            <input type="text" name="guardianPhone" class="form-input"
                                                placeholder="請輸入家長電話">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- 老師專屬欄位 -->
                            <div id="teacherFields" class="role-fields hidden">
                                <div class="teacher-fields">
                                    <h4>教師專屬資料</h4>
                                    <div class="form-grid">
                                        <div class="form-field">
                                            <label class="form-label">🎯 專長</label>
                                            <input type="text" name="specialty" class="form-input"
                                                placeholder="請輸入專長領域">
                                        </div>

                                        <div class="form-field">
                                            <label class="form-label">📅 到職日</label>
                                            <input type="date" name="hireDate" class="form-input">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- 操作按鈕 -->
                            <div class="action-bar">
                                <button type="submit" class="btn btn-primary" id="submitBtn">
                                    ✓ 確認新增
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- JavaScript -->
                <script>
                    // 設定 contextPath 變數
                    const contextPath = '${pageContext.request.contextPath}';
                    const sessionPosition = '${sessionScope.position}';

                    console.log('=== AddMember頁面載入完成 ===');
                    console.log('contextPath:', contextPath);
                    console.log('sessionPosition:', sessionPosition);

                    // 權限檢查
                    if (sessionPosition !== '主任') {
                        Swal.fire({
                            title: '權限不足',
                            text: '只有主任可以新增會員',
                            icon: 'error',
                            confirmButtonColor: '#ef4444'
                        }).then(function () {
                            window.location.href = contextPath + '/memberList';
                        });
                    }

                    // 身份切換功能
                    function selectRole(role) {
                        // 更新選中狀態
                        document.querySelectorAll('.role-tab').forEach(function (tab) {
                            tab.classList.remove('active');
                        });
                        document.querySelector('[data-role="' + role + '"]').classList.add('active');

                        // 更新隱藏域
                        document.getElementById('selectedRole').value = role;

                        // 切換顯示欄位
                        const studentFields = document.getElementById('studentFields');
                        const teacherFields = document.getElementById('teacherFields');

                        if (role === 'student') {
                            studentFields.classList.remove('hidden');
                            studentFields.classList.add('visible');
                            teacherFields.classList.remove('visible');
                            teacherFields.classList.add('hidden');
                        } else {
                            teacherFields.classList.remove('hidden');
                            teacherFields.classList.add('visible');
                            studentFields.classList.remove('visible');
                            studentFields.classList.add('hidden');
                        }
                    }

                    // 密碼強度檢測
                    function checkPasswordStrength(password) {
                        const requirements = {
                            length: password.length >= 8,
                            letter: /[a-zA-Z]/.test(password),
                            number: /\d/.test(password)
                        };

                        // 更新要求指示器
                        Object.keys(requirements).forEach(function (req) {
                            const element = document.getElementById('req-' + req);
                            if (element) {
                                element.classList.toggle('met', requirements[req]);
                            }
                        });

                        return requirements;
                    }

                    // 驗證密碼
                    function validatePassword(password) {
                        const requirements = checkPasswordStrength(password);
                        return requirements.length && requirements.letter && requirements.number;
                    }

                    // 驗證密碼確認
                    function validatePasswordConfirm() {
                        const password = document.getElementById('password').value;
                        const confirmPassword = document.getElementById('confirmPassword').value;
                        const confirmError = document.getElementById('confirmPasswordError');
                        const confirmInput = document.getElementById('confirmPassword');

                        if (confirmPassword.length > 0) {
                            if (password === confirmPassword) {
                                confirmInput.classList.remove('invalid');
                                confirmInput.classList.add('valid');
                                confirmError.style.display = 'none';
                                return true;
                            } else {
                                confirmInput.classList.remove('valid');
                                confirmInput.classList.add('invalid');
                                confirmError.style.display = 'block';
                                return false;
                            }
                        } else {
                            confirmInput.classList.remove('valid', 'invalid');
                            confirmError.style.display = 'none';
                            return false;
                        }
                    }

                    // 表單驗證
                    function validateForm() {
                        const form = document.getElementById('addMemberForm');
                        const formData = new FormData(form);
                        const password = formData.get('password');
                        const confirmPassword = formData.get('confirmPassword');

                        // 檢查必填欄位
                        const requiredFields = form.querySelectorAll('[required]');
                        let isValid = true;

                        requiredFields.forEach(function (field) {
                            if (!field.value.trim()) {
                                field.classList.add('invalid');
                                isValid = false;
                            } else {
                                field.classList.remove('invalid');
                                field.classList.add('valid');
                            }
                        });

                        // 檢查密碼強度
                        if (password && !validatePassword(password)) {
                            document.getElementById('password').classList.add('invalid');
                            isValid = false;
                        }

                        // 檢查密碼確認
                        if (password !== confirmPassword) {
                            isValid = false;
                        }

                        return isValid;
                    }

                    // 顯示訊息
                    function showAlert(message, type) {
                        const alertContainer = document.getElementById('alertContainer');
                        const alertClass = type === 'success' ? 'alert-success' : 'alert-error';

                        alertContainer.innerHTML = '<div class="alert ' + alertClass + '" style="display: flex;">' + message + '</div>';

                        // 自動隱藏成功訊息
                        if (type === 'success') {
                            setTimeout(function () {
                                alertContainer.innerHTML = '';
                            }, 4000);
                        }
                    }

                    // 提交表單
                    function submitForm() {
                        Swal.fire({
                            title: '處理中...',
                            text: '正在新增會員資料，請稍候',
                            allowOutsideClick: false,
                            allowEscapeKey: false,
                            showConfirmButton: false,
                            didOpen: function () {
                                Swal.showLoading();
                            }
                        });

                        // 收集表單資料
                        const formData = new FormData(document.getElementById('addMemberForm'));
                        const memberData = {};

                        // 轉換FormData為Object
                        for (let [key, value] of formData.entries()) {
                            memberData[key] = value;
                        }

                        console.log('提交資料:', memberData);

                        fetch(contextPath + '/api/member/add', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            credentials: 'same-origin',
                            body: JSON.stringify(memberData)
                        })
                            .then(response => {
                                if (!response.ok) {
                                    return response.json().then(data => {
                                        throw new Error(data.error || 'HTTP ' + response.status);
                                    });
                                }
                                return response.json();
                            })
                            .then(data => {
                                console.log('新增成功:', data);

                                Swal.fire({
                                    title: '新增成功！',
                                    text: '會員「' + data.memberName + '」已成功建立',
                                    icon: 'success',
                                    confirmButtonColor: '#10b981',
                                    timer: 2000,
                                    timerProgressBar: true
                                }).then(function () {
                                    // 重定向到會員列表
                                    window.location.href = contextPath + '/memberList';
                                });
                            })
                            .catch(error => {
                                console.error('新增失敗:', error);

                                Swal.fire({
                                    title: '新增失敗！',
                                    text: error.message || '請稍後再試',
                                    icon: 'error',
                                    confirmButtonColor: '#ef4444'
                                });
                            });
                    }

                    // 初始化事件
                    document.addEventListener('DOMContentLoaded', function () {
                        console.log('addMember.jsp 載入完成');

                        // 身份選擇器鍵盤支援
                        document.querySelectorAll('.role-tab').forEach(function (tab) {
                            tab.addEventListener('keydown', function (e) {
                                if (e.key === 'Enter' || e.key === ' ') {
                                    e.preventDefault();
                                    selectRole(this.dataset.role);
                                }
                            });
                        });

                        // 密碼輸入事件
                        const passwordInput = document.getElementById('password');
                        const confirmPasswordInput = document.getElementById('confirmPassword');

                        passwordInput.addEventListener('input', function () {
                            checkPasswordStrength(this.value);
                            if (confirmPasswordInput.value) {
                                validatePasswordConfirm();
                            }
                        });

                        confirmPasswordInput.addEventListener('input', function () {
                            validatePasswordConfirm();
                        });

                        // 表單提交事件
                        document.getElementById('addMemberForm').addEventListener('submit', function (e) {
                            e.preventDefault();

                            if (!validateForm()) {
                                Swal.fire({
                                    title: '表單填寫不完整',
                                    text: '請檢查並填寫所有必填欄位，密碼需符合規則且兩次輸入需一致',
                                    icon: 'warning',
                                    confirmButtonColor: '#4e8cff'
                                });
                                return;
                            }

                            const password = document.getElementById('password').value;
                            const confirmPassword = document.getElementById('confirmPassword').value;

                            if (password !== confirmPassword) {
                                Swal.fire({
                                    title: '密碼確認不符',
                                    text: '請確保兩次輸入的密碼相同',
                                    icon: 'warning',
                                    confirmButtonColor: '#4e8cff'
                                });
                                return;
                            }

                            if (!validatePassword(password)) {
                                const failedRequirements = [];
                                const requirements = checkPasswordStrength(password);
                                if (!requirements.length) failedRequirements.push('• 至少8個字元');
                                if (!requirements.letter) failedRequirements.push('• 包含英文字母');
                                if (!requirements.number) failedRequirements.push('• 包含數字');

                                const message = '密碼必須符合以下條件：\n\n' + failedRequirements.join('\n');

                                Swal.fire({
                                    title: '密碼不符合規則',
                                    text: message,
                                    icon: 'warning',
                                    confirmButtonText: '我知道了',
                                    confirmButtonColor: '#4e8cff'
                                });
                                return;
                            }

                            // 確認新增對話框
                            Swal.fire({
                                title: '確定要新增此會員嗎？',
                                text: '請確認填寫的資料正確無誤',
                                icon: 'question',
                                showCancelButton: true,
                                confirmButtonText: '確定新增',
                                cancelButtonText: '取消',
                                confirmButtonColor: '#4e8cff',
                                cancelButtonColor: '#64748b',
                                reverseButtons: true
                            }).then(function (result) {
                                if (result.isConfirmed) {
                                    submitForm();
                                }
                            });
                        });

                        // 初始化密碼檢查
                        checkPasswordStrength('');

                        // Email 格式驗證
                        document.querySelectorAll('input[type="email"]').forEach(function (input) {
                            input.addEventListener('blur', function () {
                                const emailRegex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;
                                if (this.value && !emailRegex.test(this.value)) {
                                    this.classList.add('invalid');
                                    this.title = '請輸入有效的 Email 格式';
                                } else {
                                    this.classList.remove('invalid');
                                    this.title = '';
                                    if (this.value) {
                                        this.classList.add('valid');
                                    }
                                }
                            });
                        });

                        // 通用欄位驗證
                        document.querySelectorAll('.form-input, .form-select').forEach(function (input) {
                            input.addEventListener('blur', function () {
                                if (this.hasAttribute('required')) {
                                    if (this.value.trim()) {
                                        this.classList.remove('invalid');
                                        this.classList.add('valid');
                                    } else {
                                        this.classList.remove('valid');
                                        this.classList.add('invalid');
                                    }
                                }
                            });

                            input.addEventListener('input', function () {
                                if (this.classList.contains('invalid') && this.value.trim()) {
                                    this.classList.remove('invalid');
                                    this.classList.add('valid');
                                }
                            });
                        });
                    });

                    // 鍵盤快捷鍵支持
                    document.addEventListener('keydown', function (e) {
                        // Ctrl+Enter 提交表單
                        if (e.key === 'Enter' && e.ctrlKey) {
                            e.preventDefault();
                            const submitBtn = document.getElementById('submitBtn');
                            if (submitBtn) {
                                submitBtn.click();
                            }
                        }

                        // Escape 返回列表
                        if (e.key === 'Escape') {
                            window.location.href = contextPath + '/memberList';
                        }
                    });
                </script>
            </body>

            </html>