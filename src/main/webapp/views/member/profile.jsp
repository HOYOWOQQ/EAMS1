<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% if (session.getAttribute("id") == null) { 
    response.sendRedirect(request.getContextPath() + "/login"); 
    return; 
} %>

<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>個人資料</title>
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

        .role-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 500;
            margin-top: 8px;
        }

        .role-student {
            background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
            color: #6b21a8;
        }

        .role-teacher {
            background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
            color: #14532d;
        }

        .role-other {
            background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
            color: #1e3a8a;
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
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px 24px;
            margin-bottom: 24px;
            align-items: start;
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
            min-height: 80px;
        }

        .form-field.span-2 {
            grid-column: span 2;
        }

        .form-field.span-3 {
            grid-column: span 3;
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
            gap: 8px;
        }

        .form-value {
            background: #f8fafc;
            border: 2px solid #e2e8f0;
            border-radius: 8px;
            padding: 10px 14px;
            font-size: 0.9rem;
            color: #1e293b;
            min-height: 20px;
            display: flex;
            align-items: center;
        }

        .form-input,
        .form-select {
            background: #fff;
            border: 2px solid #e2e8f0;
            border-radius: 8px;
            padding: 6px 8px;
            font-size: 0.9rem;
            color: #1e293b;
            transition: all 0.3s;
            outline: none;
            width: 95%;
            font-family: inherit;
            height: 34px;
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

        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 500;
        }

        .status-active {
            background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
            color: #065f46;
        }

        .status-inactive {
            background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
            color: #991b1b;
        }

        .status-verified {
            background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
            color: #1e3a8a;
        }

        .status-unverified {
            background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
            color: #92400e;
        }

        .grade-display {
            display: inline-flex;
            align-items: center;
            gap: 12px;
        }

        .grade-number {
            background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
            color: white;
            width: 32px;
            height: 32px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 1rem;
        }

        .grade-text {
            font-weight: 500;
            color: #1e293b;
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
            justify-content: space-between;
            align-items: center;
            margin-top: 32px;
            padding-top: 24px;
            border-top: 2px solid #e2e8f0;
            flex-wrap: wrap;
        }

        .btn-group {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }

        .loading-state {
            text-align: center;
            padding: 80px 40px;
            color: #64748b;
        }

        .loading-spinner {
            width: 32px;
            height: 32px;
            border: 3px solid #e2e8f0;
            border-top: 3px solid #4e8cff;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: 0 auto 16px;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        .no-data {
            text-align: center;
            padding: 80px 40px;
            color: #64748b;
            background: #fff;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
        }

        .no-data .icon {
            font-size: 3rem;
            margin-bottom: 16px;
            opacity: 0.5;
        }

        .no-data h3 {
            font-size: 1.2rem;
            color: #374151;
            margin: 0 0 8px 0;
        }

        .no-data p {
            margin: 0;
            font-size: 0.95rem;
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

            .form-field.span-2,
            .form-field.span-3,
            .form-field.full-width {
                grid-column: 1 / -1;
            }

            .action-bar {
                flex-direction: column;
                gap: 16px;
                align-items: stretch;
            }

            .btn-group {
                justify-content: center;
            }

            .btn {
                flex: 1;
                justify-content: center;
                min-width: 120px;
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
        <!-- 載入中 -->
        <div id="loadingState" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在載入個人資料...</p>
        </div>

        <!-- 查無資料 -->
        <div id="noDataState" class="no-data" style="display: none;">
            <div class="icon">👤</div>
            <h3>查無會員資料</h3>
            <p>請檢查網址或聯繫系統管理員</p>
        </div>

        <!-- 主要內容 -->
        <div id="mainContent" style="display: none;">
            <!-- 頁面標題 -->
            <div class="page-header">
                <div class="header-content">
                    <div class="header-info">
                        <div class="page-icon">👤</div>
                        <div>
                            <h1 class="page-title">個人資料管理</h1>
                            <p class="page-subtitle" id="memberSubtitle"></p>
                            <div id="roleBadge" class="role-badge"></div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 表單容器 -->
            <div class="form-container">
                <!-- 成功/錯誤訊息 -->
                <div id="alertContainer"></div>

                <form id="profileForm">
                    <!-- 基本資料 -->
                    <div class="section-header">
                        <h3>🆔 基本資料</h3>
                    </div>
                    <div class="form-grid">
                        <div class="form-field">
                            <label class="form-label">👤 帳號</label>
                            <div class="form-value" id="accountDisplay"></div>
                        </div>

                        <div class="form-field">
                            <label class="form-label">✏️ 姓名</label>
                            <div id="nameField"></div>
                        </div>

                        <div class="form-field">
                            <label class="form-label">⚧ 性別</label>
                            <div id="genderField"></div>
                        </div>

                        <div class="form-field">
                            <label class="form-label">🎂 生日</label>
                            <div id="birthdayField"></div>
                        </div>
                    </div>

                    <!-- 聯絡資訊 -->
                    <div class="section-header">
                        <h3>📞 聯絡資訊</h3>
                    </div>
                    <div class="form-grid">
                        <div class="form-field">
                            <label class="form-label">📧 Email</label>
                            <div id="emailField"></div>
                        </div>

                        <div class="form-field">
                            <label class="form-label">📱 電話</label>
                            <div id="phoneField"></div>
                        </div>

                        <div class="form-field span-2">
                            <label class="form-label">🏠 住址</label>
                            <div id="addressField"></div>
                        </div>
                    </div>

                    <!-- 學生專屬資料 -->
                    <div id="studentSection" style="display: none;">
                        <div class="section-header">
                            <h3>🎓 學生資料</h3>
                        </div>
                        <div class="form-grid">
                            <div class="form-field">
                                <label class="form-label">📚 年級</label>
                                <div id="gradeField"></div>
                            </div>

                            <div class="form-field">
                                <label class="form-label">📅 入學日</label>
                                <div id="enrollDateField"></div>
                            </div>

                            <div class="form-field">
                                <label class="form-label">👨‍👩‍👧 家長姓名</label>
                                <div id="guardianNameField"></div>
                            </div>

                            <div class="form-field">
                                <label class="form-label">☎️ 家長電話</label>
                                <div id="guardianPhoneField"></div>
                            </div>
                        </div>
                    </div>

                    <!-- 老師專屬資料 -->
                    <div id="teacherSection" style="display: none;">
                        <div class="section-header">
                            <h3>👨‍🏫 教師資料</h3>
                        </div>
                        <div class="form-grid">
                            <div class="form-field">
                                <label class="form-label">🎯 專長</label>
                                <div id="specialtyField"></div>
                            </div>

                            <div class="form-field">
                                <label class="form-label">💼 到職日</label>
                                <div id="hireDateField"></div>
                            </div>
                        </div>
                    </div>

                    <!-- 帳戶狀態 -->
                    <div class="section-header">
                        <h3>🔐 帳戶狀態</h3>
                    </div>
                    <div class="form-grid">
                        <div class="form-field">
                            <label class="form-label">⚡ 帳戶狀態</label>
                            <div id="statusField"></div>
                        </div>

                        <div class="form-field">
                            <label class="form-label">✅ Email驗證</label>
                            <div id="verifiedField"></div>
                        </div>

                        <div class="form-field span-2">
                            <label class="form-label">📝 備註</label>
                            <div id="remarkField"></div>
                        </div>
                    </div>

                    <!-- 操作按鈕區域 -->
                    <div class="action-bar">
                        <div class="btn-group" id="actionButtons">
                            <!-- 按鈕會由JavaScript動態生成 -->
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script>
        // 全域變數
        const contextPath = '${pageContext.request.contextPath}';
        const sessionId = '${sessionScope.id}';
        const sessionPosition = '${sessionScope.position}';
        const sessionRole = '${sessionScope.role}';
        
        let currentData = null;
        let editMode = false;
        let canEdit = false;

        // 初始化
        document.addEventListener('DOMContentLoaded', function () {
            console.log('=== Profile頁面載入完成 ===');
            console.log('contextPath:', contextPath);
            console.log('sessionId:', sessionId);
            
            loadProfileData();
        });

        // 載入個人資料資料
        function loadProfileData() {
            const urlParams = new URLSearchParams(window.location.search);
            const targetId = urlParams.get('id');
            const editParam = urlParams.get('edit');
            editMode = editParam === '1';

            console.log('載入資料 - targetId:', targetId, 'editMode:', editMode);

            let apiUrl = contextPath + '/api/member/profile';
            if (targetId) {
                apiUrl += '?id=' + targetId;
            }

            fetch(apiUrl, {
                method: 'GET',
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = contextPath + '/login';
                        return Promise.reject('請先登入');
                    }
                    throw new Error('HTTP ' + response.status + ': ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                console.log('收到資料:', data);
                currentData = data;
                canEdit = data.canEdit;
                renderProfileData();
                
                document.getElementById('loadingState').style.display = 'none';
                document.getElementById('mainContent').style.display = 'block';
            })
            .catch(error => {
                console.error('載入失敗:', error);
                showNoData();
            });
        }

        // 顯示無資料狀態
        function showNoData() {
            document.getElementById('loadingState').style.display = 'none';
            document.getElementById('noDataState').style.display = 'block';
        }

        // 渲染個人資料資料
        function renderProfileData() {
            const member = currentData.member;
            const student = currentData.student;
            const teacher = currentData.teacher;

            // 更新標題
            document.getElementById('memberSubtitle').textContent = member.name + ' 的詳細資料';
            
            // 更新角色標籤
            const roleBadge = document.getElementById('roleBadge');
            if (member.role === 'student') {
                roleBadge.className = 'role-badge role-student';
                roleBadge.innerHTML = '🎓 學生帳戶';
            } else if (member.role === 'teacher') {
                roleBadge.className = 'role-badge role-teacher';
                roleBadge.innerHTML = '👨‍🏫 教師帳戶';
            } else {
                roleBadge.className = 'role-badge role-other';
                roleBadge.innerHTML = '👤 ' + member.role;
            }

            // 基本資料
            document.getElementById('accountDisplay').textContent = member.account;
            
            renderField('nameField', 'name', member.name, 'input', true);
            renderField('emailField', 'email', member.email, 'input', true, 'email');
            renderField('phoneField', 'phone', member.phone, 'input');

            // 根據角色顯示性別和生日
            if (member.role === 'student' && student) {
                renderField('genderField', 'gender', student.gender, 'input');
                renderField('birthdayField', 'birthday', student.birthday, 'input', false, 'date');
                renderField('addressField', 'address', student.address, 'input');
                showStudentFields(student);
            } else if (member.role === 'teacher' && teacher) {
                renderField('genderField', 'gender', teacher.gender, 'input');
                renderField('birthdayField', 'birthday', teacher.birthday, 'input', false, 'date');
                renderField('addressField', 'address', teacher.address, 'input');
                showTeacherFields(teacher);
            } else {
                // 基本會員（如果有的話）
                renderField('genderField', 'gender', member.gender || '', 'input');
                renderField('birthdayField', 'birthday', member.birthday || '', 'input', false, 'date');
                renderField('addressField', 'address', member.address || '', 'input');
            }

            // 帳戶狀態
            renderStatusField(member);
            renderVerifiedField(member);

            // 備註
            if (member.role === 'student' && student) {
                renderField('remarkField', 'remark', student.remark, 'input');
            } else if (member.role === 'teacher' && teacher) {
                renderField('remarkField', 'remark', teacher.remark, 'input');
            } else {
                renderField('remarkField', 'remark', member.remark || '', 'input');
            }

            renderActionButtons();
        }

        // 顯示學生欄位
        function showStudentFields(student) {
            document.getElementById('studentSection').style.display = 'block';
            document.getElementById('teacherSection').style.display = 'none';
            
            // 年級處理
            if (editMode && canEdit) {
                const gradeOptions = [
                    { value: '1', text: '一年級', selected: student.grade == 1 },
                    { value: '2', text: '二年級', selected: student.grade == 2 },
                    { value: '3', text: '三年級', selected: student.grade == 3 }
                ];
                renderSelectField('gradeField', 'grade', gradeOptions);
            } else {
                const gradeText = student.grade == 1 ? '一年級' : 
                                student.grade == 2 ? '二年級' : 
                                student.grade == 3 ? '三年級' : '-';
                const gradeHtml = '<div class="grade-display"><div class="grade-number">' + 
                                (student.grade || '-') + '</div><span class="grade-text">' + 
                                gradeText + '</span></div>';
                document.getElementById('gradeField').innerHTML = '<div class="form-value">' + gradeHtml + '</div>';
            }
            
            renderField('enrollDateField', 'enrollDate', student.enrollDate, 'input', false, 'date');
            renderField('guardianNameField', 'guardianName', student.guardianName, 'input');
            renderField('guardianPhoneField', 'guardianPhone', student.guardianPhone, 'input');
        }

        // 顯示教師欄位
        function showTeacherFields(teacher) {
            document.getElementById('teacherSection').style.display = 'block';
            document.getElementById('studentSection').style.display = 'none';
            
            renderField('specialtyField', 'specialty', teacher.specialty, 'input');
            renderField('hireDateField', 'hireDate', teacher.hireDate, 'input', false, 'date');
        }

        // 渲染狀態欄位
        function renderStatusField(member) {
            const isDirector = sessionPosition === '主任';
            const isNotSelf = member.id != sessionId;
            const canEditStatus = editMode && canEdit && isDirector && isNotSelf;
            
            if (canEditStatus) {
                const statusOptions = [
                    { value: '1', text: '啟用', selected: member.status },
                    { value: '0', text: '停用', selected: !member.status }
                ];
                renderSelectField('statusField', 'status', statusOptions);
            } else {
                const statusClass = member.status ? 'status-active' : 'status-inactive';
                const statusText = member.status ? '✅ 啟用' : '❌ 停用';
                document.getElementById('statusField').innerHTML = 
                    '<div class="form-value"><span class="status-badge ' + statusClass + '">' + statusText + '</span></div>';
            }
        }

        // 渲染驗證欄位
        function renderVerifiedField(member) {
            const verifiedClass = member.verified ? 'status-verified' : 'status-unverified';
            const verifiedText = member.verified ? '✅ 已驗證' : '⏳ 未驗證';
            document.getElementById('verifiedField').innerHTML = 
                '<div class="form-value"><span class="status-badge ' + verifiedClass + '">' + verifiedText + '</span></div>';
        }

        // 通用欄位渲染
        function renderField(fieldId, fieldName, value, type, required = false, inputType = 'text') {
            const field = document.getElementById(fieldId);
            value = value || '';
            
            if (editMode && canEdit) {
                if (type === 'input') {
                    field.innerHTML = '<input name="' + fieldName + '" value="' + escapeHtml(value) + 
                                    '" class="form-input" type="' + inputType + '"' + 
                                    (required ? ' required' : '') + '>';
                }
            } else {
                field.innerHTML = '<div class="form-value">' + escapeHtml(value) + '</div>';
            }
        }

        // 下拉選單渲染
        function renderSelectField(fieldId, fieldName, options) {
            const field = document.getElementById(fieldId);
            let html = '<select name="' + fieldName + '" class="form-select">';
            
            options.forEach(function(option) {
                html += '<option value="' + option.value + '"' + 
                       (option.selected ? ' selected' : '') + '>' + 
                       option.text + '</option>';
            });
            
            html += '</select>';
            field.innerHTML = html;
        }

        // 渲染操作按鈕
        function renderActionButtons() {
            const buttons = document.getElementById('actionButtons');
            let html = '';
            
            if (editMode) {
                html += '<button type="button" class="btn btn-primary" onclick="saveProfile()">💾 儲存變更</button>';
                html += '<button type="button" class="btn btn-secondary" onclick="cancelEdit()">❌ 取消編輯</button>';
            } else {
                if (canEdit) {
                    html += '<button type="button" class="btn btn-primary" onclick="enterEditMode()">✏️ 編輯資料</button>';
                }
            }
            
            buttons.innerHTML = html;
        }

        // 進入編輯模式
        function enterEditMode() {
            const currentUrl = new URL(window.location);
            currentUrl.searchParams.set('edit', '1');
            window.location.href = currentUrl.toString();
        }

        // 取消編輯
        function cancelEdit() {
            const currentUrl = new URL(window.location);
            currentUrl.searchParams.delete('edit');
            window.location.href = currentUrl.toString();
        }

        // 儲存資料
        function saveProfile() {
            Swal.fire({
                title: '確定要儲存變更嗎？',
                text: '系統將更新個人資料',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: '確定儲存',
                cancelButtonText: '取消',
                confirmButtonColor: '#4e8cff',
                cancelButtonColor: '#64748b',
                reverseButtons: true
            }).then(function(result) {
                if (result.isConfirmed) {
                    submitProfile();
                }
            });
        }

        // 提交表單
        function submitProfile() {
            Swal.fire({
                title: '正在儲存...',
                text: '請稍候片刻',
                allowOutsideClick: false,
                allowEscapeKey: false,
                showConfirmButton: false,
                didOpen: function() {
                    Swal.showLoading();
                }
            });

            // 收集表單資料
            const formData = new FormData(document.getElementById('profileForm'));
            const profileData = {
                id: currentData.member.id
            };
            
            // 轉換FormData為Object
            for (let [key, value] of formData.entries()) {
                profileData[key] = value;
            }
            
            // 特殊處理status欄位
            if (profileData.status !== undefined) {
                profileData.status = profileData.status === '1';
            }

            console.log('提交資料:', profileData);

            fetch(contextPath + '/api/member/profile', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'same-origin',
                body: JSON.stringify(profileData)
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
                console.log('儲存成功:', data);
                
                Swal.fire({
                    title: '資料更新成功！',
                    text: data.message || '個人資料已成功更新',
                    icon: 'success',
                    confirmButtonColor: '#10b981',
                    timer: 2000,
                    timerProgressBar: true
                }).then(function() {
                    // 重新載入頁面（非編輯模式）
                    const currentUrl = new URL(window.location);
                    currentUrl.searchParams.delete('edit');
                    window.location.href = currentUrl.toString();
                });
            })
            .catch(error => {
                console.error('儲存失敗:', error);
                
                Swal.fire({
                    title: '資料更新失敗！',
                    text: error.message || '請稍後再試',
                    icon: 'error',
                    confirmButtonColor: '#ef4444'
                });
            });
        }

        // HTML轉義
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        // 顯示訊息
        function showAlert(message, type = 'success') {
            const alertContainer = document.getElementById('alertContainer');
            const alertClass = type === 'success' ? 'alert-success' : 'alert-error';
            
            alertContainer.innerHTML = '<div class="alert ' + alertClass + '">' + message + '</div>';
            
            // 自動隱藏成功訊息
            if (type === 'success') {
                setTimeout(function() {
                    alertContainer.innerHTML = '';
                }, 4000);
            }
        }

        // 鍵盤快捷鍵
        document.addEventListener('keydown', function (e) {
            if (e.ctrlKey && e.key === 's') {
                e.preventDefault();
                if (editMode) {
                    saveProfile();
                }
            }

            if (e.key === 'Escape' && editMode) {
                cancelEdit();
            }

            if (e.ctrlKey && e.key === 'e' && !editMode && canEdit) {
                e.preventDefault();
                enterEditMode();
            }
        });
    </script>
</body>
</html>