<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="zh-TW">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>驗證帳號</title>
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
            padding: 20px 0;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .verify-container {
            background: #fff;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
            max-width: 500px;
            width: 90%;
            overflow: hidden;
            position: relative;
        }

        .verify-header {
            background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
            color: white;
            padding: 40px 32px;
            text-align: center;
            position: relative;
        }

        .verify-header::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="verify-pattern" width="50" height="50" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="2" fill="white" opacity="0.1"/><rect x="20" y="30" width="10" height="8" fill="white" opacity="0.05" rx="1"/></pattern></defs><rect width="100" height="100" fill="url(%23verify-pattern)"/></svg>') repeat;
            opacity: 0.3;
        }

        .verify-icon {
            width: 60px;
            height: 60px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 16px auto;
            backdrop-filter: blur(10px);
            position: relative;
            z-index: 1;
        }

        .verify-icon::before {
            content: '📧';
            font-size: 24px;
        }

        .verify-title {
            font-size: 1.8rem;
            font-weight: 700;
            margin: 0 0 8px 0;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
            position: relative;
            z-index: 1;
        }

        .verify-subtitle {
            font-size: 1rem;
            opacity: 0.9;
            margin: 0;
            position: relative;
            z-index: 1;
        }

        .verify-content {
            padding: 32px;
        }

        .info-section {
            background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
            border: 1px solid #bae6fd;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 28px;
            color: #0c4a6e;
        }

        .info-section h3 {
            margin: 0 0 12px 0;
            font-size: 1.1rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .info-section h3::before {
            content: '💡';
            width: 24px;
            height: 24px;
            background: #0284c7;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            color: white;
        }

        .info-section p {
            margin: 0 0 8px 0;
            font-size: 0.95rem;
            line-height: 1.5;
        }

        .info-section p:last-child {
            margin-bottom: 0;
        }

        .form-field {
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-bottom: 24px;
        }

        .form-label {
            font-weight: 600;
            color: #374151;
            font-size: 0.95rem;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .form-label::before {
            content: attr(data-icon);
            width: 20px;
            height: 20px;
            background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            color: white;
            flex-shrink: 0;
        }

        .form-input {
            background: #fff;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            padding: 14px 16px;
            font-size: 1rem;
            color: #1e293b;
            transition: all 0.3s;
            outline: none;
            width: 100%;
            box-sizing: border-box;
        }

        .form-input:focus {
            border-color: #4e8cff;
            box-shadow: 0 0 0 3px rgba(78, 140, 255, 0.1);
            transform: translateY(-1px);
        }

        .form-input.valid {
            border-color: #10b981;
            box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
        }

        .form-input.invalid {
            border-color: #ef4444;
            box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
        }

        .action-bar {
            display: flex;
            gap: 16px;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            margin-top: 32px;
        }

        .btn {
            padding: 14px 28px;
            border: none;
            border-radius: 12px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            min-width: 120px;
            justify-content: center;
        }

        .btn-primary {
            background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
            color: white;
        }

        .btn-primary:hover:not(:disabled) {
            background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(37, 99, 235, 0.3);
        }

        .btn-primary:disabled {
            background: #94a3b8;
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }

        .btn-secondary {
            background: #f8fafc;
            color: #374151;
            border: 2px solid #e2e8f0;
        }

        .btn-secondary:hover {
            background: #e2e8f0;
            border-color: #cbd5e1;
            transform: translateY(-2px);
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            75% { transform: translateX(5px); }
        }

        .shake {
            animation: shake 0.5s ease-in-out;
        }

        @media (max-width: 600px) {
            .verify-container {
                margin: 10px;
                border-radius: 16px;
            }

            .verify-header {
                padding: 32px 20px;
            }

            .verify-title {
                font-size: 1.5rem;
            }

            .verify-content {
                padding: 24px 20px;
            }

            .action-bar {
                flex-direction: column;
                gap: 12px;
            }

            .btn {
                width: 100%;
            }
        }

        /* 無障礙設計 */
        @media (prefers-reduced-motion: reduce) {
            *, *::before, *::after {
                animation-duration: 0.01ms !important;
                animation-iteration-count: 1 !important;
                transition-duration: 0.01ms !important;
            }
        }

        @media (prefers-contrast: high) {
            .btn {
                border: 2px solid currentColor;
            }
            .form-input {
                border-width: 2px;
            }
        }

        /* 焦點狀態樣式 */
        .form-field.focused .form-label {
            color: #4e8cff;
        }

        .form-field.focused .form-label::before {
            transform: scale(1.1);
            box-shadow: 0 2px 8px rgba(78, 140, 255, 0.3);
        }

        /* 載入狀態 */
        .loading {
            opacity: 0.7;
            pointer-events: none;
        }

        /* 觸控優化 */
        @media (hover: none) and (pointer: coarse) {
            .btn {
                min-height: 48px;
                font-size: 1.1rem;
            }
            .form-input {
                min-height: 48px;
                font-size: 1.1rem;
            }
        }
    </style>
</head>

<body>
    <div class="verify-container">
        <!-- 頁面標題 -->
        <div class="verify-header">
            <div class="verify-icon"></div>
            <h1 class="verify-title">驗證帳號</h1>
            <p class="verify-subtitle">發送驗證信件至您的註冊信箱</p>
        </div>

        <div class="verify-content">
            <!-- 說明資訊 -->
            <div class="info-section">
                <h3>驗證帳號說明</h3>
                <p>1. 輸入您註冊時使用的帳號</p>
                <p>2. 系統將發送驗證信件至您的註冊信箱</p>
                <p>3. 點擊信件中的驗證連結完成帳號驗證</p>
                <p>4. 驗證成功後即可正常使用所有功能</p>
            </div>

            <form id="verifyForm">
                <div class="form-field">
                    <label class="form-label" data-icon="👤">帳號</label>
                    <input type="text" name="account" id="account" class="form-input" 
                           required placeholder="請輸入您的帳號">
                </div>

                <!-- 操作按鈕 -->
                <div class="action-bar">
                    <button type="submit" class="btn btn-primary" id="submitBtn">
                        📧 發送驗證信
                    </button>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">
                        ❌ 返回登入
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- JavaScript -->
    <script>
        // 設定 contextPath 變數
        const contextPath = '${pageContext.request.contextPath}';

        console.log('=== VerifyEmail頁面載入完成 ===');
        console.log('contextPath:', contextPath);

        // 提交表單
        function submitForm() {
            const account = document.getElementById('account').value.trim();
            
            if (!account) {
                Swal.fire({
                    title: '請輸入帳號',
                    text: '請填寫您的帳號',
                    icon: 'warning',
                    confirmButtonColor: '#4e8cff'
                });
                return;
            }

            // 顯示處理中
            Swal.fire({
                title: '處理中...',
                text: '正在發送驗證信件，請稍候',
                allowOutsideClick: false,
                allowEscapeKey: false,
                showConfirmButton: false,
                didOpen: function() {
                    Swal.showLoading();
                }
            });

            console.log('[VerifyEmail] 提交驗證信請求，帳號:', account);

            fetch(contextPath + '/api/member/verify-email/request', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'same-origin',
                body: JSON.stringify({ account: account })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('[VerifyEmail] 請求完成:', data);
                
                if (data.success) {
                    Swal.fire({
                        title: '發送成功！',
                        html: data.message + '<br><br><small style="color: #64748b;">測試用連結：<br><a href="' + contextPath + data.testUrl + '" style="color: #4e8cff; text-decoration: underline;" target="_blank">' + data.testUrl + '</a></small>',
                        icon: 'success',
                        confirmButtonText: '確定',
                        confirmButtonColor: '#4e8cff'
                    }).then(function() {
                        // 清空表單
                        document.getElementById('account').value = '';
                    });
                } else {
                    Swal.fire({
                        title: '發送失敗',
                        text: data.message,
                        icon: 'error',
                        confirmButtonColor: '#ef4444'
                    });
                }
            })
            .catch(error => {
                console.error('[VerifyEmail] 請求失敗:', error);
                
                Swal.fire({
                    title: '系統錯誤',
                    text: '發送失敗，請稍後再試',
                    icon: 'error',
                    confirmButtonColor: '#ef4444'
                });
            });
        }

        // 初始化事件監聽器
        document.addEventListener('DOMContentLoaded', function() {
            const verifyForm = document.getElementById('verifyForm');
            const accountInput = document.getElementById('account');
            const submitBtn = document.getElementById('submitBtn');

            console.log('[VerifyEmail] 頁面初始化完成');

            // 表單驗證
            function validateForm() {
                const account = accountInput.value.trim();
                
                if (account.length === 0) {
                    accountInput.classList.remove('valid');
                    accountInput.classList.add('invalid');
                    submitBtn.disabled = true;
                    return false;
                } else {
                    accountInput.classList.remove('invalid');
                    accountInput.classList.add('valid');
                    submitBtn.disabled = false;
                    return true;
                }
            }

            // 帳號輸入事件
            accountInput.addEventListener('input', validateForm);

            // 表單提交處理
            verifyForm.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const account = accountInput.value.trim();
                if (!account) {
                    Swal.fire({
                        title: '請輸入帳號',
                        text: '請輸入您的帳號以繼續',
                        icon: 'warning',
                        confirmButtonColor: '#4e8cff'
                    });
                    accountInput.focus();
                    return;
                }

                Swal.fire({
                    title: '確定要發送驗證信嗎？',
                    text: '將發送驗證信件至帳號「' + account + '」的註冊信箱\n\n📧 請確認帳號正確無誤',
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonText: '確定發送',
                    cancelButtonText: '取消',
                    confirmButtonColor: '#4e8cff',
                    cancelButtonColor: '#64748b'
                }).then((result) => {
                    if (result.isConfirmed) {
                        submitForm();
                    }
                });
            });

            // 輸入框焦點效果
            accountInput.addEventListener('focus', function() {
                this.closest('.form-field').classList.add('focused');
            });

            accountInput.addEventListener('blur', function() {
                this.closest('.form-field').classList.remove('focused');
            });

            // 初始驗證
            validateForm();
        });

        // 防止表單重複提交
        let isSubmitting = false;
        document.addEventListener('submit', function(e) {
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
    </script>
</body>

</html>