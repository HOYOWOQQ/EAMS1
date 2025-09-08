<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="zh-TW">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>驗證結果</title>
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

        .result-container {
            background: #fff;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
            max-width: 500px;
            width: 90%;
            overflow: hidden;
            position: relative;
            animation: slideIn 0.5s ease-out;
        }

        .loading-state {
            text-align: center;
            padding: 60px 40px;
            color: #64748b;
        }

        .loading-spinner {
            width: 40px;
            height: 40px;
            border: 4px solid #e2e8f0;
            border-top: 4px solid #4e8cff;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }

        .result-header {
            padding: 40px 32px;
            text-align: center;
            position: relative;
        }

        .result-header.success {
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            color: white;
        }

        .result-header.error {
            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
            color: white;
        }

        .result-header::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="result-pattern" width="50" height="50" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="2" fill="white" opacity="0.1"/><rect x="20" y="30" width="10" height="8" fill="white" opacity="0.05" rx="1"/></pattern></defs><rect width="100" height="100" fill="url(%23result-pattern)"/></svg>') repeat;
            opacity: 0.3;
        }

        .result-icon {
            width: 80px;
            height: 80px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 24px auto;
            backdrop-filter: blur(10px);
            position: relative;
            z-index: 1;
            animation: bounce 1s ease-out;
        }

        .result-icon.success::before {
            content: '✅';
            font-size: 36px;
        }

        .result-icon.error::before {
            content: '❌';
            font-size: 36px;
        }

        .result-title {
            font-size: 2rem;
            font-weight: 700;
            margin: 0 0 12px 0;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
            position: relative;
            z-index: 1;
        }

        .result-message {
            font-size: 1.1rem;
            opacity: 0.9;
            margin: 0;
            position: relative;
            z-index: 1;
            line-height: 1.5;
        }

        .result-content {
            padding: 32px;
        }

        .result-details {
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 32px;
            text-align: center;
        }

        .result-details.success {
            background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
            border-color: #86efac;
            color: #14532d;
        }

        .result-details.error {
            background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
            border-color: #fca5a5;
            color: #991b1b;
        }

        .result-details h3 {
            margin: 0 0 12px 0;
            font-size: 1.1rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }

        .result-details.success h3::before {
            content: '🎉';
            font-size: 20px;
        }

        .result-details.error h3::before {
            content: '⚠️';
            font-size: 20px;
        }

        .result-details p {
            margin: 0;
            font-size: 0.95rem;
            line-height: 1.6;
        }

        .next-steps {
            background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
            border: 1px solid #bae6fd;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 32px;
            color: #0c4a6e;
        }

        .next-steps h3 {
            margin: 0 0 16px 0;
            font-size: 1.1rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .next-steps h3::before {
            content: '📋';
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

        .steps-list {
            list-style: none;
            padding: 0;
            margin: 0;
            counter-reset: step-counter;
        }

        .steps-list li {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            margin-bottom: 12px;
            font-size: 0.95rem;
            line-height: 1.5;
        }

        .steps-list li:last-child {
            margin-bottom: 0;
        }

        .steps-list li::before {
            content: counter(step-counter);
            counter-increment: step-counter;
            background: #0284c7;
            color: white;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.8rem;
            font-weight: 600;
            flex-shrink: 0;
            margin-top: 2px;
        }

        .action-bar {
            display: flex;
            gap: 16px;
            justify-content: center;
            align-items: center;
            flex-wrap: wrap;
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
            min-width: 140px;
            justify-content: center;
        }

        .btn-primary {
            background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
            color: white;
        }

        .btn-primary:hover {
            background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(37, 99, 235, 0.3);
        }

        .btn-success {
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            color: white;
        }

        .btn-success:hover {
            background: linear-gradient(135deg, #059669 0%, #047857 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3);
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

        .loading-animation {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 2px solid rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            border-top-color: white;
            animation: spin 0.8s ease-in-out infinite;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(30px) scale(0.95);
            }
            to {
                opacity: 1;
                transform: translateY(0) scale(1);
            }
        }

        @keyframes bounce {
            0%, 20%, 53%, 80%, 100% {
                transform: translate3d(0, 0, 0);
            }
            40%, 43% {
                transform: translate3d(0, -8px, 0);
            }
            70% {
                transform: translate3d(0, -4px, 0);
            }
            90% {
                transform: translate3d(0, -2px, 0);
            }
        }

        @keyframes spin {
            to {
                transform: rotate(360deg);
            }
        }

        @media (max-width: 600px) {
            .result-container {
                margin: 10px;
                border-radius: 16px;
            }

            .result-header {
                padding: 32px 20px;
            }

            .result-title {
                font-size: 1.6rem;
            }

            .result-content {
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
        }

        /* 觸控優化 */
        @media (hover: none) and (pointer: coarse) {
            .btn {
                min-height: 48px;
                font-size: 1.1rem;
            }
        }
    </style>
</head>

<body>
    <div class="result-container">
        <!-- 載入中狀態 -->
        <div id="loadingState" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在驗證您的帳號...</p>
        </div>

        <!-- 主要內容 -->
        <div id="mainContent" style="display: none;">
            <!-- 結果標題 -->
            <div id="resultHeader" class="result-header">
                <div id="resultIcon" class="result-icon"></div>
                <h1 id="resultTitle" class="result-title"></h1>
                <p id="resultMessage" class="result-message"></p>
            </div>

            <div class="result-content">
                <!-- 結果詳情 -->
                <div id="resultDetails" class="result-details">
                    <h3 id="detailsTitle"></h3>
                    <p id="detailsMessage"></p>
                </div>

                <!-- 後續步驟 -->
                <div class="next-steps">
                    <h3 id="stepsTitle"></h3>
                    <ul class="steps-list" id="stepsList">
                    </ul>
                </div>

                <!-- 操作按鈕 -->
                <div class="action-bar" id="actionBar">
                </div>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script>
        // 設定 contextPath 變數
        const contextPath = '${pageContext.request.contextPath}';

        console.log('=== VerifyResult頁面載入完成 ===');
        console.log('contextPath:', contextPath);

        // 檢查URL參數並進行驗證
        function checkVerificationStatus() {
            const urlParams = new URLSearchParams(window.location.search);
            const token = urlParams.get('token');
            const id = urlParams.get('id');

            console.log('[VerifyResult] URL參數 - ID:', id, 'Token:', token);

            if (token && id) {
                // 有token和id，進行驗證
                performVerification(id, token);
            } else {
                // 沒有參數，顯示錯誤
                showError('無效的驗證連結', '缺少必要的驗證參數，請重新申請驗證信件。');
            }
        }

        // 執行驗證
        function performVerification(id, token) {
            console.log('[VerifyResult] 開始執行驗證');

            fetch(contextPath + '/api/member/verify-email/confirm?id=' + id + '&token=' + token, {
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
                console.log('[VerifyResult] 驗證結果:', data);
                
                // 隱藏載入狀態
                document.getElementById('loadingState').style.display = 'none';
                document.getElementById('mainContent').style.display = 'block';

                if (data.success) {
                    showSuccess(data.message, data.member);
                } else {
                    showError('驗證失敗', data.message);
                }
            })
            .catch(error => {
                console.error('[VerifyResult] 驗證請求失敗:', error);
                
                // 隱藏載入狀態
                document.getElementById('loadingState').style.display = 'none';
                document.getElementById('mainContent').style.display = 'block';
                
                showError('系統錯誤', '驗證過程中發生錯誤，請稍後再試。');
            });
        }

        // 顯示成功結果
        function showSuccess(message, member) {
            // 設定標題區域
            const resultHeader = document.getElementById('resultHeader');
            const resultIcon = document.getElementById('resultIcon');
            const resultTitle = document.getElementById('resultTitle');
            const resultMessage = document.getElementById('resultMessage');

            resultHeader.classList.add('success');
            resultIcon.classList.add('success');
            resultTitle.textContent = '驗證成功！';
            resultMessage.textContent = '您的帳號已成功完成驗證';

            // 設定詳情區域
            const resultDetails = document.getElementById('resultDetails');
            const detailsTitle = document.getElementById('detailsTitle');
            const detailsMessage = document.getElementById('detailsMessage');

            resultDetails.classList.add('success');
            detailsTitle.textContent = '驗證完成';
            detailsMessage.textContent = message + (member ? ' - 歡迎 ' + member.name + '！' : '');

            // 設定後續步驟
            const stepsTitle = document.getElementById('stepsTitle');
            const stepsList = document.getElementById('stepsList');

            stepsTitle.textContent = '接下來您可以：';
            stepsList.innerHTML = `
                <li>立即登入您的帳號開始使用所有功能</li>
                <li>更新您的個人資料和偏好設定</li>
                <li>探索系統提供的各項服務</li>
                <li>如有問題，請聯繫客服人員</li>
            `;

            // 設定操作按鈕
            const actionBar = document.getElementById('actionBar');
            actionBar.innerHTML = `
                <a href="${contextPath}/login" class="btn btn-success" id="loginBtn">
                    🚀 立即登入
                </a>
            `;

            // 自動跳轉功能
            setupAutoRedirect();
        }

        // 顯示錯誤結果
        function showError(title, message) {
            // 設定標題區域
            const resultHeader = document.getElementById('resultHeader');
            const resultIcon = document.getElementById('resultIcon');
            const resultTitle = document.getElementById('resultTitle');
            const resultMessage = document.getElementById('resultMessage');

            resultHeader.classList.add('error');
            resultIcon.classList.add('error');
            resultTitle.textContent = title;
            resultMessage.textContent = '帳號驗證過程中發生問題';

            // 設定詳情區域
            const resultDetails = document.getElementById('resultDetails');
            const detailsTitle = document.getElementById('detailsTitle');
            const detailsMessage = document.getElementById('detailsMessage');

            resultDetails.classList.add('error');
            detailsTitle.textContent = '驗證錯誤';
            detailsMessage.textContent = message;

            // 設定解決方案
            const stepsTitle = document.getElementById('stepsTitle');
            const stepsList = document.getElementById('stepsList');

            stepsTitle.textContent = '解決方案：';
            stepsList.innerHTML = `
                <li>檢查驗證連結是否完整且未過期</li>
                <li>嘗試重新申請驗證信件</li>
                <li>確認使用正確的帳號進行驗證</li>
                <li>如持續發生問題，請聯繫技術支援</li>
            `;

            // 設定操作按鈕
            const actionBar = document.getElementById('actionBar');
            actionBar.innerHTML = `
                <a href="${contextPath}/verifyEmail" class="btn btn-primary">
                    📧 重新申請驗證
                </a>
                <a href="${contextPath}/login" class="btn btn-secondary">
                    🔙 返回登入
                </a>
            `;
        }

        // 設定自動跳轉
        function setupAutoRedirect() {
            const loginBtn = document.getElementById('loginBtn');
            if (!loginBtn) return;

            let countdown = 5;
            const originalText = loginBtn.innerHTML;
            
            const updateCountdown = () => {
                if (countdown > 0) {
                    loginBtn.innerHTML = `🚀 立即登入 (${countdown}s)`;
                    countdown--;
                    setTimeout(updateCountdown, 1000);
                } else {
                    // 自動跳轉
                    loginBtn.innerHTML = '<span class="loading-animation"></span> 跳轉中...';
                    setTimeout(() => {
                        window.location.href = contextPath + '/login';
                    }, 500);
                }
            };
            
            // 開始倒計時
            setTimeout(updateCountdown, 2000); // 2秒後開始倒計時
            
            // 點擊按鈕立即跳轉
            loginBtn.addEventListener('click', function(e) {
                e.preventDefault();
                this.innerHTML = '<span class="loading-animation"></span> 跳轉中...';
                setTimeout(() => {
                    window.location.href = contextPath + '/login';
                }, 300);
            });
            
            // 顯示成功提示
            setTimeout(() => {
                Swal.fire({
                    title: '🎉 驗證成功！',
                    text: '歡迎加入我們！即將為您跳轉到登入頁面',
                    icon: 'success',
                    timer: 3000,
                    timerProgressBar: true,
                    showConfirmButton: false,
                    toast: true,
                    position: 'top-end',
                    background: '#d1fae5',
                    color: '#14532d'
                });
            }, 500);
        }

        // 初始化
        document.addEventListener('DOMContentLoaded', function() {
            console.log('[VerifyResult] 頁面初始化完成');
            
            // 檢查驗證狀態
            checkVerificationStatus();

            // 為按鈕添加載入效果
            document.addEventListener('click', function(e) {
                if (e.target.classList.contains('btn') && e.target.id !== 'loginBtn') {
                    if (e.target.href) {
                        e.preventDefault();
                        const originalText = e.target.innerHTML;
                        e.target.innerHTML = '<span class="loading-animation"></span> 載入中...';
                        setTimeout(() => {
                            window.location.href = e.target.href;
                        }, 300);
                    }
                }
            });
        });
    </script>
</body>

</html>