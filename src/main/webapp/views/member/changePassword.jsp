<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <% if (session.getAttribute("id")==null) { response.sendRedirect(request.getContextPath() + "/login" ); return;
            } %>

            <!DOCTYPE html>
            <html lang="zh-TW">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>修改密碼</title>
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
                        max-width: 1200px;
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

                    .last-change-info {
                        background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
                        border: 2px solid #f59e0b;
                        border-radius: 16px;
                        padding: 20px 24px;
                        margin-bottom: 32px;
                        display: flex;
                        align-items: center;
                        gap: 16px;
                        color: #92400e;
                        font-size: 0.95rem;
                        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                    }

                    .last-change-info::before {
                        content: '🕐';
                        width: 32px;
                        height: 32px;
                        background: #f59e0b;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 16px;
                        flex-shrink: 0;
                        color: white;
                    }

                    .last-change-content {
                        flex: 1;
                    }

                    .last-change-label {
                        font-weight: 600;
                        margin-bottom: 4px;
                        color: #92400e;
                        font-size: 1rem;
                    }

                    .last-change-time {
                        font-weight: 500;
                        color: #78350f;
                        font-size: 0.9rem;
                    }

                    .security-tips {
                        background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                        border: 2px solid #10b981;
                        border-radius: 16px;
                        padding: 20px 24px;
                        margin-bottom: 32px;
                        font-size: 0.9rem;
                        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                    }

                    .security-tips h4 {
                        color: #166534;
                        font-size: 1rem;
                        font-weight: 600;
                        margin: 0 0 12px 0;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    }

                    .security-tips h4::before {
                        content: '🛡️';
                        font-size: 16px;
                    }

                    .security-tips ul {
                        margin: 0;
                        padding-left: 20px;
                        color: #22543d;
                    }

                    .security-tips li {
                        margin-bottom: 6px;
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
                        grid-template-columns: repeat(3, 1fr);
                        gap: 20px 24px;
                        margin-bottom: 24px;
                    }

                    .form-field {
                        display: flex;
                        flex-direction: column;
                        gap: 8px;
                        min-height: 120px;
                    }

                    .form-label {
                        font-weight: 600;
                        color: #374151;
                        font-size: 0.9rem;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    }

                    .password-input-wrapper {
                        position: relative;
                    }

                    .form-input {
                        background: #fff;
                        border: 2px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 10px 14px;
                        font-size: 0.9rem;
                        color: #1e293b;
                        transition: all 0.3s;
                        outline: none;
                        width: 85%;
                        font-family: inherit;
                        height: 38px;
                    }

                    .form-input.has-toggle {
                        padding-right: 44px;
                    }

                    .form-input:focus {
                        border-color: #4e8cff;
                        box-shadow: 0 0 0 3px rgba(78, 140, 255, 0.1);
                    }

                    .form-input:hover {
                        border-color: #cbd5e1;
                    }

                    .form-input.valid {
                        border-color: #10b981;
                        box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
                    }

                    .form-input.invalid {
                        border-color: #ef4444;
                        box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
                    }

                    .toggle-password {
                        position: absolute;
                        right: 10px;
                        top: 50%;
                        transform: translateY(-50%);
                        background: none;
                        border: none;
                        cursor: pointer;
                        color: #64748b;
                        font-size: 16px;
                        transition: all 0.3s;
                        padding: 6px;
                        border-radius: 6px;
                        width: 28px;
                        height: 28px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }

                    .toggle-password:hover {
                        color: #4e8cff;
                        background: rgba(78, 140, 255, 0.1);
                    }

                    .password-strength {
                        margin-top: 8px;
                        display: none;
                    }

                    .password-strength.show {
                        display: block;
                    }

                    .strength-meter {
                        height: 6px;
                        background: #f1f5f9;
                        border-radius: 3px;
                        overflow: hidden;
                        margin-bottom: 8px;
                    }

                    .strength-fill {
                        height: 100%;
                        transition: all 0.3s;
                        border-radius: 3px;
                    }

                    .strength-weak .strength-fill {
                        width: 33%;
                        background: #ef4444;
                    }

                    .strength-medium .strength-fill {
                        width: 66%;
                        background: #f59e0b;
                    }

                    .strength-strong .strength-fill {
                        width: 100%;
                        background: #10b981;
                    }

                    .strength-text {
                        font-size: 0.8rem;
                        font-weight: 500;
                    }

                    .strength-weak .strength-text {
                        color: #ef4444;
                    }

                    .strength-medium .strength-text {
                        color: #f59e0b;
                    }

                    .strength-strong .strength-text {
                        color: #10b981;
                    }

                    .password-requirements {
                        background: #f8fafc;
                        border: 2px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 12px;
                        margin-top: 8px;
                        font-size: 0.8rem;
                    }

                    .requirement {
                        display: flex;
                        align-items: center;
                        gap: 6px;
                        margin-bottom: 4px;
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
                        font-size: 0.8rem;
                        margin-top: 6px;
                        display: none;
                        padding: 6px 10px;
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

                    .btn-primary:hover:not(:disabled) {
                        background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
                        transform: translateY(-1px);
                        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
                    }

                    .btn-primary:disabled {
                        background: #cbd5e1;
                        color: #94a3b8;
                        cursor: not-allowed;
                        transform: none;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
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

                    .loading-state {
                        text-align: center;
                        padding: 40px 20px;
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
                        0% {
                            transform: rotate(0deg);
                        }

                        100% {
                            transform: rotate(360deg);
                        }
                    }

                    @media (max-width: 900px) {
                        .form-grid {
                            grid-template-columns: 1fr;
                            gap: 20px;
                        }

                        .form-field {
                            min-height: 100px;
                        }
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

                        .last-change-info {
                            flex-direction: column;
                            align-items: flex-start;
                            gap: 12px;
                            text-align: left;
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
                    <!-- 載入中狀態 -->
                    <div id="loadingState" class="loading-state">
                        <div class="loading-spinner"></div>
                        <p>正在載入修改密碼頁面...</p>
                    </div>

                    <!-- 主要內容 -->
                    <div id="mainContent" style="display: none;">
                        <!-- 頁面標題 -->
                        <div class="page-header">
                            <div class="header-content">
                                <div class="header-info">
                                    <div class="page-icon">🔒</div>
                                    <div>
                                        <h1 class="page-title">修改密碼</h1>
                                        <p class="page-subtitle">定期更新密碼，確保您的帳號安全</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 主要內容 -->
                        <div class="form-container">
                            <!-- 訊息顯示區域 -->
                            <div id="alertContainer"></div>

                            <!-- 上次修改時間 -->
                            <div class="last-change-info">
                                <div class="last-change-content">
                                    <div class="last-change-label">上次修改密碼時間</div>
                                    <div class="last-change-time" id="lastChangeTime">載入中...</div>
                                </div>
                            </div>

                            <!-- 安全提示 -->
                            <div class="security-tips">
                                <h4>密碼安全建議</h4>
                                <ul>
                                    <li>建議每3個月更換一次密碼</li>
                                    <li>使用至少8個字元，包含英文字母和數字</li>
                                    <li>避免使用生日、姓名或常見密碼</li>
                                    <li>不要在多個網站使用相同密碼</li>
                                </ul>
                            </div>

                            <form id="passwordForm">
                                <!-- 密碼設定 -->
                                <div class="section-header">
                                    <h3>🔐 密碼設定</h3>
                                </div>
                                <div class="form-grid">
                                    <!-- 舊密碼 -->
                                    <div class="form-field">
                                        <label class="form-label">🔑 目前密碼</label>
                                        <div class="password-input-wrapper">
                                            <input type="password" name="oldPassword" id="oldPassword"
                                                class="form-input has-toggle" required placeholder="請輸入目前使用的密碼"
                                                autocomplete="current-password">
                                            <button type="button" class="toggle-password"
                                                onclick="togglePassword('oldPassword')" tabindex="-1">
                                                👁️
                                            </button>
                                        </div>
                                    </div>

                                    <!-- 新密碼 -->
                                    <div class="form-field">
                                        <label class="form-label">🆕 新密碼</label>
                                        <div class="password-input-wrapper">
                                            <input type="password" name="newPassword" id="newPassword"
                                                class="form-input has-toggle" required minlength="8"
                                                placeholder="請輸入新密碼（至少8個字元）" autocomplete="new-password">
                                            <button type="button" class="toggle-password"
                                                onclick="togglePassword('newPassword')" tabindex="-1">
                                                👁️
                                            </button>
                                        </div>

                                        <!-- 密碼強度指示器 -->
                                        <div class="password-strength" id="passwordStrength">
                                            <div class="strength-meter">
                                                <div class="strength-fill"></div>
                                            </div>
                                            <div class="strength-text">密碼強度：弱</div>
                                        </div>

                                        <!-- 密碼要求 -->
                                        <div class="password-requirements">
                                            <div class="requirement" id="req-length">至少8個字元</div>
                                            <div class="requirement" id="req-letter">包含英文字母</div>
                                            <div class="requirement" id="req-number">包含數字</div>
                                        </div>
                                    </div>

                                    <!-- 確認新密碼 -->
                                    <div class="form-field">
                                        <label class="form-label">✅ 確認新密碼</label>
                                        <div class="password-input-wrapper">
                                            <input type="password" name="confirmPassword" id="confirmPassword"
                                                class="form-input has-toggle" required minlength="8"
                                                placeholder="請再次輸入新密碼" autocomplete="new-password">
                                            <button type="button" class="toggle-password"
                                                onclick="togglePassword('confirmPassword')" tabindex="-1">
                                                👁️
                                            </button>
                                        </div>
                                        <div id="confirmPasswordError" class="error-message">
                                            密碼確認不符
                                        </div>
                                    </div>
                                </div>

                                <!-- 操作按鈕 -->
                                <div class="action-bar">
                                    <button type="submit" class="btn btn-primary" id="submitBtn" disabled>
                                        ✓ 更新密碼
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- JavaScript -->
                <script>
                    // 設定 contextPath 變數
                    const contextPath = '${pageContext.request.contextPath}';
                    const sessionId = '${sessionScope.id}';

                    console.log('=== ChangePassword頁面載入完成 ===');
                    console.log('contextPath:', contextPath);
                    console.log('sessionId:', sessionId);

                    // 檢查登入狀態
                    if (!sessionId) {
                        window.location.href = contextPath + '/login';
                    }

                    // 密碼顯示/隱藏切換
                    function togglePassword(inputId) {
                        const input = document.getElementById(inputId);
                        const button = input.nextElementSibling;

                        if (input.type === 'password') {
                            input.type = 'text';
                            button.textContent = '🙈';
                        } else {
                            input.type = 'password';
                            button.textContent = '👁️';
                        }
                    }

                    // 密碼強度檢測
                    function checkPasswordStrength(password) {
                        let score = 0;
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
                            if (requirements[req]) score++;
                        });

                        return { score: score, requirements: requirements };
                    }

                    // 更新密碼強度顯示
                    function updatePasswordStrength(password) {
                        const strengthDiv = document.getElementById('passwordStrength');
                        if (!strengthDiv) return;

                        const result = checkPasswordStrength(password);
                        const score = result.score;

                        if (password.length === 0) {
                            strengthDiv.classList.remove('show');
                            return;
                        }

                        strengthDiv.classList.add('show');
                        strengthDiv.className = 'password-strength show';

                        if (score < 2) {
                            strengthDiv.classList.add('strength-weak');
                            strengthDiv.querySelector('.strength-text').textContent = '密碼強度：弱';
                        } else if (score < 3) {
                            strengthDiv.classList.add('strength-medium');
                            strengthDiv.querySelector('.strength-text').textContent = '密碼強度：中等';
                        } else {
                            strengthDiv.classList.add('strength-strong');
                            strengthDiv.querySelector('.strength-text').textContent = '密碼強度：強';
                        }
                    }

                    // 檢查密碼是否符合規則並顯示提示
                    function checkPasswordAndShowAlert(password) {
                        const result = checkPasswordStrength(password);
                        const requirements = result.requirements;

                        const failedRequirements = [];
                        if (!requirements.length) failedRequirements.push('• 至少8個字元');
                        if (!requirements.letter) failedRequirements.push('• 包含英文字母');
                        if (!requirements.number) failedRequirements.push('• 包含數字');

                        if (failedRequirements.length > 0) {
                            const message = '密碼必須符合以下條件：\n\n' + failedRequirements.join('\n');

                            Swal.fire({
                                title: '密碼不符合規則',
                                text: message,
                                icon: 'warning',
                                confirmButtonText: '我知道了',
                                confirmButtonColor: '#4e8cff'
                            });
                            return false;
                        }
                        return true;
                    }

                    // 驗證表單
                    function validateForm() {
                        const newPassword = document.getElementById('newPassword').value;
                        const confirmPassword = document.getElementById('confirmPassword').value;
                        const oldPassword = document.getElementById('oldPassword').value;
                        const submitBtn = document.getElementById('submitBtn');
                        const confirmError = document.getElementById('confirmPasswordError');

                        const result = checkPasswordStrength(newPassword);
                        const score = result.score;
                        const requirements = result.requirements;

                        const passwordsMatch = newPassword === confirmPassword && confirmPassword.length > 0;
                        const hasOldPassword = oldPassword.length > 0;
                        const passwordValid = score === 3 && requirements.length && requirements.letter && requirements.number;

                        // 更新確認密碼狀態
                        const confirmInput = document.getElementById('confirmPassword');
                        if (confirmPassword.length > 0) {
                            if (passwordsMatch) {
                                confirmInput.classList.remove('invalid');
                                confirmInput.classList.add('valid');
                                confirmError.style.display = 'none';
                            } else {
                                confirmInput.classList.remove('valid');
                                confirmInput.classList.add('invalid');
                                confirmError.style.display = 'block';
                            }
                        } else {
                            confirmInput.classList.remove('valid', 'invalid');
                            confirmError.style.display = 'none';
                        }

                        // 更新新密碼狀態
                        const newPasswordInput = document.getElementById('newPassword');
                        if (newPassword.length > 0) {
                            if (passwordValid) {
                                newPasswordInput.classList.remove('invalid');
                                newPasswordInput.classList.add('valid');
                            } else {
                                newPasswordInput.classList.remove('valid');
                                newPasswordInput.classList.add('invalid');
                            }
                        } else {
                            newPasswordInput.classList.remove('valid', 'invalid');
                        }

                        // 啟用/禁用提交按鈕
                        submitBtn.disabled = !(hasOldPassword && passwordValid && passwordsMatch);
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

                    // 載入頁面資料
                    function loadPageData() {
                        console.log('[ChangePassword] 開始載入頁面資料');

                        fetch(contextPath + '/api/member/change-password', {
                            method: 'GET',
                            credentials: 'same-origin'
                        })
                            .then(response => {
                                if (!response.ok) {
                                    if (response.status === 401) {
                                        window.location.href = contextPath + '/login';
                                        return;
                                    }
                                    throw new Error('HTTP ' + response.status);
                                }
                                return response.json();
                            })
                            .then(data => {
                                console.log('[ChangePassword] 頁面資料載入成功:', data);

                                // 更新上次修改時間
                                const lastChangeElement = document.getElementById('lastChangeTime');
                                if (data.lastPwdChange) {
                                    const date = new Date(data.lastPwdChange);
                                    lastChangeElement.textContent = date.toLocaleString('zh-TW');
                                } else {
                                    lastChangeElement.textContent = '（尚未變更）';
                                }

                                // 顯示主要內容，隱藏載入狀態
                                document.getElementById('loadingState').style.display = 'none';
                                document.getElementById('mainContent').style.display = 'block';
                            })
                            .catch(error => {
                                console.error('[ChangePassword] 載入頁面資料失敗:', error);

                                Swal.fire({
                                    title: '載入失敗',
                                    text: '無法載入頁面資料，請重新整理頁面',
                                    icon: 'error',
                                    confirmButtonColor: '#ef4444'
                                }).then(function () {
                                    window.location.reload();
                                });
                            });
                    }

                    // 提交表單
                    function submitForm() {
                        Swal.fire({
                            title: '處理中...',
                            text: '正在修改密碼，請稍候',
                            allowOutsideClick: false,
                            allowEscapeKey: false,
                            showConfirmButton: false,
                            didOpen: function () {
                                Swal.showLoading();
                            }
                        });

                        // 收集表單資料
                        const formData = new FormData(document.getElementById('passwordForm'));
                        const passwordData = {};

                        // 轉換FormData為Object
                        for (let [key, value] of formData.entries()) {
                            passwordData[key] = value;
                        }

                        console.log('[ChangePassword] 提交密碼修改:', passwordData);

                        fetch(contextPath + '/api/member/change-password', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            credentials: 'same-origin',
                            body: JSON.stringify(passwordData)
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
                                console.log('[ChangePassword] 密碼修改成功:', data);

                                Swal.fire({
                                    title: '密碼修改成功！',
                                    text: '為了帳號安全，請使用新密碼重新登入',
                                    icon: 'success',
                                    confirmButtonText: '前往登入',
                                    confirmButtonColor: '#4e8cff',
                                    allowOutsideClick: false,
                                    allowEscapeKey: false,
                                    timer: 5000,
                                    timerProgressBar: true
                                }).then(function () {
                                    // 跳轉到登入頁面
                                    window.location.href = contextPath + '/login';
                                });
                            })
                            .catch(error => {
                                console.error('[ChangePassword] 密碼修改失敗:', error);

                                Swal.fire({
                                    title: '密碼修改失敗！',
                                    text: error.message || '請稍後再試',
                                    icon: 'error',
                                    confirmButtonColor: '#ef4444'
                                });
                            });
                    }

                    // 初始化事件
                    document.addEventListener('DOMContentLoaded', function () {
                        console.log('[ChangePassword] 頁面載入完成，開始初始化');

                        // 載入頁面資料
                        loadPageData();

                        // 事件監聽器
                        document.getElementById('newPassword').addEventListener('input', function () {
                            updatePasswordStrength(this.value);
                            validateForm();
                        });

                        // 新密碼失去焦點時檢查

                        document.getElementById('newPassword').addEventListener('blur', function () {
                            if (this.value.length > 0) {
                                checkPasswordAndShowAlert(this.value);
                            }
                        });

                        document.getElementById('confirmPassword').addEventListener('input', validateForm);
                        document.getElementById('oldPassword').addEventListener('input', validateForm);

                        // 表單提交處理
                        document.getElementById('passwordForm').addEventListener('submit', function (e) {
                            e.preventDefault();

                            const newPassword = document.getElementById('newPassword').value;

                            // 最終檢查密碼規則
                            if (!checkPasswordAndShowAlert(newPassword)) {
                                return;
                            }

                            const result = checkPasswordStrength(newPassword);
                            const score = result.score;

                            if (score < 3) {
                                Swal.fire({
                                    title: '密碼強度不足',
                                    text: '建議使用更強的密碼以保護您的帳號安全',
                                    icon: 'warning',
                                    showCancelButton: true,
                                    confirmButtonText: '仍要使用此密碼',
                                    cancelButtonText: '重新設定',
                                    confirmButtonColor: '#f59e0b',
                                    cancelButtonColor: '#4e8cff',
                                    reverseButtons: true
                                }).then(function (result) {
                                    if (result.isConfirmed) {
                                        submitConfirmation();
                                    }
                                });
                            } else {
                                submitConfirmation();
                            }
                        });

                        function submitConfirmation() {
                            Swal.fire({
                                title: '確定要修改密碼嗎？',
                                text: '修改後請使用新密碼登入',
                                icon: 'question',
                                showCancelButton: true,
                                confirmButtonText: '確定修改',
                                cancelButtonText: '取消',
                                confirmButtonColor: '#4e8cff',
                                cancelButtonColor: '#64748b',
                                reverseButtons: true
                            }).then(function (result) {
                                if (result.isConfirmed) {
                                    submitForm();
                                }
                            });
                        }
                    });

                    // 防止表單重複提交
                    let isSubmitting = false;
                    document.addEventListener('submit', function (e) {
                        if (isSubmitting) {
                            e.preventDefault();
                            return false;
                        }
                        isSubmitting = true;

                        // 3秒後重置提交狀態
                        setTimeout(function () {
                            isSubmitting = false;
                        }, 3000);
                    });

                    // 鍵盤快捷鍵支持
                    document.addEventListener('keydown', function (e) {
                        // Ctrl+Enter 提交表單
                        if (e.key === 'Enter' && e.ctrlKey) {
                            e.preventDefault();
                            const submitBtn = document.querySelector('.btn-primary:not(:disabled)');
                            if (submitBtn) {
                                submitBtn.click();
                            }
                        }

                        // Escape 返回首頁
                        if (e.key === 'Escape') {
                            window.location.href = contextPath + '/';
                        }
                    });
                </script>
            </body>

            </html>