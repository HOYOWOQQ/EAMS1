<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="zh-TW">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>重設密碼</title>
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
                    border-radius: 20px;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                    padding: 32px 40px;
                    margin-bottom: 20px;
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

                .main-content {
                    display: flex;
                    gap: 20px;
                    align-items: flex-start;
                }

                .left-panel {
                    flex: 1;
                    background: #fff;
                    border-radius: 20px;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                    padding: 28px;
                    max-width: 400px;
                }

                .right-panel {
                    flex: 2;
                    background: #fff;
                    border-radius: 20px;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                    padding: 40px;
                }

                .loading-state {
                    text-align: center;
                    padding: 60px 40px;
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

                .step-indicator {
                    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
                    border: 2px solid #e2e8f0;
                    border-radius: 16px;
                    padding: 20px 24px;
                    margin-bottom: 32px;
                    display: flex;
                    align-items: center;
                    gap: 16px;
                    color: #475569;
                    font-size: 0.95rem;
                    font-weight: 500;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                }

                .step-indicator::before {
                    content: attr(data-step);
                    width: 32px;
                    height: 32px;
                    background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-weight: bold;
                    color: white;
                    flex-shrink: 0;
                    font-size: 0.9rem;
                }

                .info-section {
                    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
                    border: 2px solid #f59e0b;
                    border-radius: 16px;
                    padding: 24px;
                    margin-bottom: 32px;
                    color: #92400e;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                }

                .info-section h3 {
                    margin: 0 0 16px 0;
                    font-size: 1.1rem;
                    font-weight: 600;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    color: #92400e;
                }

                .info-section h3::before {
                    content: '📧';
                    font-size: 18px;
                }

                .info-section ol {
                    margin: 0;
                    padding-left: 20px;
                }

                .info-section li {
                    margin-bottom: 8px;
                    font-size: 0.9rem;
                    line-height: 1.6;
                }

                .member-info {
                    background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
                    border: 2px solid #3b82f6;
                    border-radius: 16px;
                    padding: 24px;
                    margin-bottom: 32px;
                    color: #1e40af;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                }

                .member-info h3 {
                    margin: 0 0 20px 0;
                    font-size: 1.1rem;
                    font-weight: 600;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    padding-bottom: 12px;
                    border-bottom: 2px solid rgba(191, 219, 254, 0.6);
                    color: #1e40af;
                }

                .member-info h3::before {
                    content: '👤';
                    font-size: 18px;
                }

                .member-detail {
                    display: flex;
                    flex-direction: column;
                    gap: 12px;
                    font-size: 0.95rem;
                }

                .member-detail-item {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    padding: 12px 16px;
                    background: rgba(255, 255, 255, 0.6);
                    border-radius: 8px;
                    border: 1px solid rgba(191, 219, 254, 0.3);
                }

                .member-detail .label {
                    font-weight: 600;
                    color: #1e40af;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .member-detail .value {
                    font-weight: 600;
                    color: #1e3a8a;
                }

                .section-header {
                    margin: 32px 0 24px 0;
                    padding-bottom: 12px;
                    border-bottom: 2px solid #e2e8f0;
                }

                .section-header h1 {
                    margin: 0;
                    color: #1e293b;
                    font-size: 1.8rem;
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
                    grid-template-columns: 1fr 1fr;
                    gap: 20px 32px;
                    margin-bottom: 24px;
                    align-items: start;
                }

                .form-field {
                    display: flex;
                    flex-direction: column;
                    gap: 8px;
                    min-height: 120px;
                }

                .form-field.full-width {
                    grid-column: 1 / -1;
                }

                .form-label {
                    font-weight: 600;
                    color: #374151;
                    font-size: 1rem;
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
                    font-size: 1rem;
                    color: #1e293b;
                    transition: all 0.3s;
                    outline: none;
                    width: 100%;
                    font-family: inherit;
                    height: 42px;
                    box-sizing: border-box;
                }

                .form-input.smaller-password {
                    height: 40px;
                    padding: 8px 12px;
                    font-size: 1rem;
                    border-radius: 6px;
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

                @media (max-width: 1200px) {
                    .main-content {
                        flex-direction: column;
                    }

                    .left-panel {
                        max-width: none;
                        order: 2;
                    }

                    .right-panel {
                        order: 1;
                    }

                    .form-grid {
                        grid-template-columns: 1fr;
                        gap: 16px;
                    }
                }

                @media (max-width: 768px) {
                    body {
                        padding: 10px;
                    }

                    .page-header {
                        padding: 20px;
                        border-radius: 16px;
                    }

                    .header-content {
                        flex-direction: column;
                        align-items: stretch;
                        gap: 16px;
                    }

                    .left-panel,
                    .right-panel {
                        padding: 20px;
                        border-radius: 16px;
                    }

                    .member-detail-item {
                        flex-direction: column;
                        align-items: flex-start;
                        gap: 8px;
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

                .main-content {
                    animation: fadeIn 0.5s ease-out;
                }

                /* 鍵盤導航優化 */
                .form-input:focus-visible,
                .btn:focus-visible,
                .toggle-password:focus-visible {
                    outline: 2px solid #4e8cff;
                    outline-offset: 2px;
                }

                /* 選中狀態 */
                ::selection {
                    background: rgba(78, 140, 255, 0.2);
                    color: #1e293b;
                }

                /* 滾動條優化 */
                ::-webkit-scrollbar {
                    width: 8px;
                    height: 8px;
                }

                ::-webkit-scrollbar-track {
                    background: #f1f5f9;
                }

                ::-webkit-scrollbar-thumb {
                    background: #cbd5e0;
                    border-radius: 4px;
                }

                ::-webkit-scrollbar-thumb:hover {
                    background: #94a3b8;
                }
            </style>
        </head>

        <body>
            <div class="page-container">
                <!-- 頁面標題 -->
                <div class="page-header">
                    <div class="header-content">
                        <div class="header-info">
                            <div class="page-icon">🔄</div>
                            <div>
                                <h1 class="page-title">重設密碼</h1>
                                <p class="page-subtitle">設定一組安全且容易記住的新密碼</p>
                            </div>
                        </div>
                        <div class="header-actions">
                        </div>
                    </div>
                </div>

                <!-- 載入中狀態 -->
                <div id="loadingState" class="right-panel" style="display: block;">
                    <div class="loading-state">
                        <div class="loading-spinner"></div>
                        <p>正在驗證重設連結...</p>
                    </div>
                </div>

                <!-- 無效連結狀態 -->
                <div id="invalidState" class="right-panel" style="display: none;">
                    <div class="alert alert-error">
                        連結無效或已過期，請重新申請重設密碼
                    </div>
                    <div class="action-bar">
                        <a href="${pageContext.request.contextPath}/forgotPassword" class="btn btn-primary">
                            📧 重新申請
                        </a>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">
                            ← 返回登入
                        </a>
                    </div>
                </div>

                <!-- 主要內容 -->
                <div id="mainContent" class="main-content" style="display: none;">
                    <!-- 左側面板 - 資訊和說明 -->
                    <div class="left-panel">
                        <!-- 會員資訊 -->
                        <div class="member-info" id="memberInfo" style="display: none;">
                            <h3>重設密碼的帳號資訊</h3>
                            <div class="member-detail">
                                <div class="member-detail-item">
                                    <span class="label">👤 姓名</span>
                                    <span class="value" id="memberName"></span>
                                </div>
                                <div class="member-detail-item">
                                    <span class="label">🏷️ 帳號</span>
                                    <span class="value" id="memberAccount"></span>
                                </div>
                            </div>
                        </div>

                        <!-- 說明資訊 -->
                        <div class="info-section">
                            <h3>重設密碼注意事項</h3>
                            <ol>
                                <li>新密碼必須至少8個字元</li>
                                <li>需包含英文字母和數字</li>
                                <li>避免使用過於簡單的密碼組合</li>
                            </ol>
                        </div>
                    </div>

                    <!-- 右側面板 - 表單 -->
                    <div class="right-panel">
                        <form id="resetForm">
                            <input type="hidden" id="memberId" />
                            <input type="hidden" id="resetToken" />

                            <!-- 密碼設定 -->
                            <div class="section-header">
                                <h1>🔐 密碼設定</h1>
                            </div>
                            <div class="form-grid">
                                <!-- 新密碼 -->
                                <div class="form-field">
                                    <label class="form-label">🆕 新密碼</label>
                                    <div class="password-input-wrapper">
                                        <input type="password" name="newPassword" id="newPassword"
                                            class="form-input has-toggle smaller-password" required minlength="8"
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
                                            class="form-input has-toggle smaller-password" required minlength="8"
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
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">
                                    ← 返回登入
                                </a>
                                <button type="submit" class="btn btn-primary" id="resetSubmitBtn" disabled>
                                    🔄 重設密碼
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
                    ['length', 'letter', 'number'].forEach(function (requirement) {
                        const element = document.getElementById('req-' + requirement);
                        if (element) {
                            if (requirements[requirement]) {
                                element.classList.add('met');
                            } else {
                                element.classList.remove('met');
                            }
                        }
                        if (requirements[requirement]) score++;
                    });

                    return { score, requirements };
                }

                // 更新密碼強度顯示
                function updatePasswordStrength(password) {
                    const strengthDiv = document.getElementById('passwordStrength');
                    if (!strengthDiv) return;

                    const { score } = checkPasswordStrength(password);

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
                    const requirements = {
                        length: password.length >= 8,
                        letter: /[a-zA-Z]/.test(password),
                        number: /\d/.test(password)
                    };

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

                // 驗證重設密碼表單
                function validateResetForm() {
                    const newPassword = document.getElementById('newPassword');
                    const confirmPassword = document.getElementById('confirmPassword');
                    const submitBtn = document.getElementById('resetSubmitBtn');
                    const confirmError = document.getElementById('confirmPasswordError');

                    if (!newPassword || !confirmPassword || !submitBtn) return;

                    const newPasswordValue = newPassword.value;
                    const confirmPasswordValue = confirmPassword.value;
                    const { score, requirements } = checkPasswordStrength(newPasswordValue);
                    const passwordsMatch = newPasswordValue === confirmPasswordValue && confirmPasswordValue.length > 0;
                    const passwordValid = score === 3 && requirements.length && requirements.letter && requirements.number;

                    // 更新確認密碼狀態
                    if (confirmPasswordValue.length > 0) {
                        if (passwordsMatch) {
                            confirmPassword.classList.remove('invalid');
                            confirmPassword.classList.add('valid');
                            confirmError.style.display = 'none';
                        } else {
                            confirmPassword.classList.remove('valid');
                            confirmPassword.classList.add('invalid');
                            confirmError.style.display = 'block';
                        }
                    } else {
                        confirmPassword.classList.remove('valid', 'invalid');
                        confirmError.style.display = 'none';
                    }

                    // 更新新密碼狀態
                    if (newPasswordValue.length > 0) {
                        if (passwordValid) {
                            newPassword.classList.remove('invalid');
                            newPassword.classList.add('valid');
                        } else {
                            newPassword.classList.remove('valid');
                            newPassword.classList.add('invalid');
                        }
                    } else {
                        newPassword.classList.remove('valid', 'invalid');
                    }

                    // 啟用/禁用提交按鈕
                    submitBtn.disabled = !(passwordValid && passwordsMatch);
                }

                // 驗證重設連結
                function verifyResetLink() {
                    const urlParams = new URLSearchParams(window.location.search);
                    const token = urlParams.get('token');
                    const id = urlParams.get('id');

                    console.log('[ResetPassword] 驗證參數 - ID:', id, 'Token:', token);

                    if (!token || !id) {
                        console.log('[ResetPassword] 缺少必要參數');
                        showInvalidState('缺少必要的驗證參數');
                        return;
                    }

                    // 調用驗證 API
                    fetch(contextPath + '/api/member/reset-password/verify?id=' + id + '&token=' + token, {
                        method: 'GET',
                        credentials: 'same-origin'
                    })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('HTTP ' + response.status);
                            }
                            return response.json();
                        })
                        .then(data => {
                            console.log('[ResetPassword] 驗證結果:', data);

                            document.getElementById('loadingState').style.display = 'none';

                            if (data.success && data.member) {
                                // 驗證成功，顯示重設表單
                                document.getElementById('mainContent').style.display = 'flex';
                                document.getElementById('memberInfo').style.display = 'block';
                                document.getElementById('memberName').textContent = data.member.name;
                                document.getElementById('memberAccount').textContent = data.member.account;
                                document.getElementById('memberId').value = data.member.id;
                                document.getElementById('resetToken').value = token;
                            } else {
                                // 驗證失敗
                                showInvalidState(data.message || '驗證連結無效或已過期');
                            }
                        })
                        .catch(error => {
                            console.error('[ResetPassword] 驗證失敗:', error);
                            document.getElementById('loadingState').style.display = 'none';
                            showInvalidState('驗證過程中發生錯誤，請稍後再試');
                        });
                }

                // 顯示無效狀態
                function showInvalidState(message) {
                    document.getElementById('loadingState').style.display = 'none';
                    document.getElementById('mainContent').style.display = 'none';
                    document.getElementById('invalidState').style.display = 'block';

                    const errorAlert = document.querySelector('#invalidState .alert-error');
                    if (errorAlert) {
                        errorAlert.textContent = message;
                    }
                }

                // 提交重設密碼
                function submitResetForm() {
                    const newPassword = document.getElementById('newPassword').value;
                    const confirmPassword = document.getElementById('confirmPassword').value;
                    const id = document.getElementById('memberId').value;
                    const token = document.getElementById('resetToken').value;

                    // 最終檢查密碼規則
                    if (!checkPasswordAndShowAlert(newPassword)) {
                        return;
                    }

                    if (newPassword !== confirmPassword) {
                        Swal.fire({
                            title: '密碼確認不符',
                            text: '請確保兩次輸入的密碼相同',
                            icon: 'warning',
                            confirmButtonColor: '#4e8cff'
                        });
                        return;
                    }

                    Swal.fire({
                        title: '處理中...',
                        text: '正在重設密碼，請稍候',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        showConfirmButton: false,
                        didOpen: () => {
                            Swal.showLoading();
                        }
                    });

                    // 調用重設密碼 API
                    fetch(contextPath + '/api/member/reset-password/confirm', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        credentials: 'same-origin',
                        body: JSON.stringify({
                            id: id,
                            token: token,
                            newPassword: newPassword,
                            confirmPassword: confirmPassword
                        })
                    })
                        .then(response => {
                            if (!response.ok) {
                                return response.json().then(data => {
                                    throw new Error(data.message || 'HTTP ' + response.status);
                                });
                            }
                            return response.json();
                        })
                        .then(data => {
                            console.log('[ResetPassword] 重設結果:', data);

                            if (data.success) {
                                Swal.fire({
                                    title: '密碼重設成功！',
                                    text: '請使用新密碼登入您的帳號',
                                    icon: 'success',
                                    confirmButtonColor: '#10b981',
                                    allowOutsideClick: false,
                                    allowEscapeKey: false,
                                    timer: 3000,
                                    timerProgressBar: true,
                                    showConfirmButton: false
                                }).then(() => {
                                    window.location.href = contextPath + '/login';
                                });
                            } else {
                                Swal.fire({
                                    title: '重設失敗',
                                    text: data.message,
                                    icon: 'error',
                                    confirmButtonColor: '#ef4444'
                                });
                            }
                        })
                        .catch(error => {
                            console.error('[ResetPassword] 重設失敗:', error);
                            Swal.fire({
                                title: '系統錯誤',
                                text: error.message || '重設失敗，請稍後再試',
                                icon: 'error',
                                confirmButtonColor: '#ef4444'
                            });
                        });
                }

                // 初始化事件監聽器
                document.addEventListener('DOMContentLoaded', function () {
                    console.log('[ResetPassword] 頁面載入完成');

                    // 首先驗證重設連結
                    verifyResetLink();

                    // 重設密碼表單事件
                    const resetForm = document.getElementById('resetForm');
                    if (resetForm) {
                        const newPasswordInput = document.getElementById('newPassword');
                        const confirmPasswordInput = document.getElementById('confirmPassword');

                        if (newPasswordInput && confirmPasswordInput) {
                            console.log('[ResetPassword] 密碼輸入框初始化');

                            // 新密碼輸入事件
                            newPasswordInput.addEventListener('input', function () {
                                updatePasswordStrength(this.value);
                                validateResetForm();
                            });

                            // 新密碼失去焦點時檢查
                            newPasswordInput.addEventListener('blur', function () {
                                if (this.value.length > 0) {
                                    checkPasswordAndShowAlert(this.value);
                                }
                            });

                            // 確認密碼輸入事件
                            confirmPasswordInput.addEventListener('input', function () {
                                validateResetForm();
                            });

                            // 表單提交處理
                            resetForm.addEventListener('submit', function (e) {
                                e.preventDefault();
                                console.log('[ResetPassword] 表單提交');

                                Swal.fire({
                                    title: '確定要重設密碼嗎？',
                                    text: '重設後請使用新密碼登入',
                                    icon: 'question',
                                    showCancelButton: true,
                                    confirmButtonText: '確定重設',
                                    cancelButtonText: '取消',
                                    confirmButtonColor: '#4e8cff',
                                    cancelButtonColor: '#64748b',
                                    reverseButtons: true
                                }).then((result) => {
                                    if (result.isConfirmed) {
                                        submitResetForm();
                                    }
                                });
                            });

                            // 初始驗證
                            validateResetForm();
                        }
                    }

                    console.log('[ResetPassword] 初始化完成');
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
                    setTimeout(() => {
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

                    // Escape 返回登入頁
                    if (e.key === 'Escape') {
                        window.location.href = contextPath + '/login';
                    }
                });
            </script>
        </body>

        </html>