<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html lang="zh-TW">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>忘記密碼</title>
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
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .main-container {
                    display: flex;
                    max-width: 1200px;
                    width: 100%;
                    background: #fff;
                    border-radius: 20px;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                    overflow: hidden;
                    position: relative;
                }

                .main-container::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    height: 4px;
                    background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                    z-index: 1;
                }

                .left-panel {
                    flex: 1;
                    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
                    padding: 60px 40px;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    border-right: 2px solid #e2e8f0;
                }

                .right-panel {
                    flex: 1;
                    padding: 60px 40px;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                }

                .brand {
                    text-align: center;
                    margin-bottom: 40px;
                }

                .brand-icon {
                    width: 80px;
                    height: 80px;
                    background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                    border-radius: 20px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 32px;
                    color: white;
                    margin: 0 auto 20px;
                    box-shadow: 0 8px 24px rgba(78, 140, 255, 0.3);
                }

                .brand-title {
                    font-size: 2.2rem;
                    font-weight: 700;
                    color: #1e293b;
                    margin: 0 0 12px 0;
                }

                .brand-subtitle {
                    color: #64748b;
                    font-size: 1.1rem;
                    line-height: 1.6;
                }

                .info-section {
                    background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                    border: 2px solid #10b981;
                    border-radius: 16px;
                    padding: 24px;
                    margin-top: 40px;
                }

                .info-section h4 {
                    margin: 0 0 16px 0;
                    font-size: 1.1rem;
                    font-weight: 600;
                    color: #166534;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .info-section ul {
                    margin: 0;
                    padding-left: 20px;
                    color: #166534;
                }

                .info-section li {
                    margin-bottom: 8px;
                    line-height: 1.5;
                }

                .form-section {
                    text-align: center;
                }

                .form-header {
                    margin-bottom: 40px;
                }

                .form-icon {
                    width: 64px;
                    height: 64px;
                    background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                    border-radius: 16px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 24px;
                    color: white;
                    margin: 0 auto 20px;
                }

                .form-title {
                    font-size: 1.8rem;
                    font-weight: 700;
                    color: #1e293b;
                    margin: 0 0 8px 0;
                }

                .form-subtitle {
                    color: #64748b;
                    font-size: 0.95rem;
                }

                .form-group {
                    margin-bottom: 24px;
                    text-align: left;
                }

                .form-label {
                    display: block;
                    font-weight: 600;
                    color: #374151;
                    margin-bottom: 8px;
                    font-size: 0.9rem;
                }

                .form-input {
                    width: 100%;
                    padding: 14px 18px;
                    border: 2px solid #e2e8f0;
                    border-radius: 10px;
                    font-size: 1rem;
                    color: #1e293b;
                    transition: all 0.3s;
                    outline: none;
                    font-family: inherit;
                    box-sizing: border-box;
                }

                .form-input:focus {
                    border-color: #4e8cff;
                    box-shadow: 0 0 0 3px rgba(78, 140, 255, 0.1);
                }

                .form-input:hover {
                    border-color: #cbd5e1;
                }

                .btn {
                    width: 100%;
                    padding: 14px 24px;
                    border: none;
                    border-radius: 10px;
                    font-size: 1rem;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    gap: 10px;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                    font-family: inherit;
                    margin-bottom: 20px;
                    box-sizing: border-box;
                }

                .btn-primary {
                    background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                    color: white;
                }

                .btn-primary:hover:not(:disabled) {
                    background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
                }

                .btn-primary:disabled {
                    background: #cbd5e1;
                    color: #94a3b8;
                    cursor: not-allowed;
                    transform: none;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
                }

                .link {
                    display: block;
                    text-align: center;
                    color: #4e8cff;
                    text-decoration: none;
                    font-size: 0.95rem;
                    padding: 12px;
                    border-radius: 8px;
                    transition: all 0.3s;
                    font-weight: 500;
                }

                .link:hover {
                    background: rgba(78, 140, 255, 0.1);
                    color: #2563eb;
                }

                @media (max-width: 968px) {
                    .main-container {
                        flex-direction: column;
                        max-width: 500px;
                    }

                    .left-panel {
                        border-right: none;
                        border-bottom: 2px solid #e2e8f0;
                        padding: 40px 30px;
                    }

                    .right-panel {
                        padding: 40px 30px;
                    }

                    .brand-title {
                        font-size: 1.8rem;
                    }

                    .brand-subtitle {
                        font-size: 1rem;
                    }

                    .info-section {
                        margin-top: 24px;
                        padding: 20px;
                    }
                }

                @media (max-width: 768px) {
                    body {
                        padding: 10px;
                    }

                    .main-container {
                        border-radius: 16px;
                    }

                    .left-panel,
                    .right-panel {
                        padding: 30px 20px;
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

                .main-container {
                    animation: fadeIn 0.6s ease-out;
                }

                /* 鍵盤導航優化 */
                .form-input:focus-visible,
                .btn:focus-visible,
                .link:focus-visible {
                    outline: 2px solid #4e8cff;
                    outline-offset: 2px;
                }

                /* 選中狀態 */
                ::selection {
                    background: rgba(78, 140, 255, 0.2);
                    color: #1e293b;
                }
            </style>
        </head>

        <body>
            <div class="main-container">
                <!-- 左側面板 - 品牌介紹 -->
                <div class="left-panel">
                    <div class="brand">
                        <div class="brand-icon">🏢</div>
                        <h1 class="brand-title">密碼重設服務</h1>
                        <p class="brand-subtitle">安全、快速、便捷的密碼找回服務</p>
                    </div>

                    <div class="info-section">
                        <h4>📧 重設密碼說明</h4>
                        <ul>
                            <li>請輸入註冊時使用的帳號</li>
                            <li>系統將發送重設連結到您的信箱</li>
                            <li>重設連結有效期限為10分鐘</li>
                            <li>如未收到信件，請檢查垃圾郵件匣</li>
                        </ul>
                    </div>
                </div>

                <!-- 右側面板 - 表單 -->
                <div class="right-panel">
                    <div class="form-section">
                        <div class="form-header">
                            <div class="form-icon">🔑</div>
                            <h2 class="form-title">忘記密碼</h2>
                            <p class="form-subtitle">請輸入您的帳號，我們將發送重設密碼連結</p>
                        </div>

                        <form id="forgotPasswordForm">
                            <div class="form-group">
                                <label class="form-label" for="account">👤 帳號</label>
                                <input type="text" name="account" id="account" class="form-input" required 
                                       placeholder="請輸入您的帳號" autocomplete="username">
                            </div>

                            <button type="submit" class="btn btn-primary" id="submitBtn">
                                📧 發送驗證信
                            </button>
                        </form>

                        <a href="${pageContext.request.contextPath}/login" class="link">
                            ← 回到登入頁面
                        </a>
                    </div>
                </div>
            </div>

            <!-- JavaScript -->
            <script>
                // 設定 contextPath 變數
                const contextPath = '${pageContext.request.contextPath}';

                console.log('=== ForgotPassword頁面載入完成 ===');
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
                        text: '正在發送重設密碼信件，請稍候',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        showConfirmButton: false,
                        didOpen: function () {
                            Swal.showLoading();
                        }
                    });

                    console.log('[ForgotPassword] 提交忘記密碼請求，帳號:', account);
                    console.log('[ForgotPassword] API URL:', contextPath + '/api/member/forgot-password');

                    // 確保使用正確的 API 端點
                    fetch(contextPath + '/api/member/forgot-password', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        credentials: 'same-origin',
                        body: JSON.stringify({ account: account })
                    })
                        .then(response => {
                            console.log('[ForgotPassword] Response status:', response.status);

                            if (!response.ok) {
                                throw new Error('HTTP ' + response.status);
                            }
                            return response.json();
                        })
                        .then(data => {
                            console.log('[ForgotPassword] 請求完成:', data);

                            if (data.success) {
                                Swal.fire({
                                    title: '發送成功！',
                                    html: data.message + (data.testUrl ? '<br><br><small style="color: #64748b;">測試用連結：<br><a href="' + contextPath + data.testUrl + '" style="color: #4e8cff; text-decoration: underline;" target="_blank">' + data.testUrl + '</a></small>' : ''),
                                    icon: 'success',
                                    confirmButtonText: '確定',
                                    confirmButtonColor: '#4e8cff'
                                }).then(function () {
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
                            console.error('[ForgotPassword] 請求失敗:', error);

                            Swal.fire({
                                title: '系統錯誤',
                                text: '發送失敗，請稍後再試',
                                icon: 'error',
                                confirmButtonColor: '#ef4444'
                            });
                        });
                }

                // 初始化事件
                document.addEventListener('DOMContentLoaded', function () {
                    console.log('[ForgotPassword] 頁面初始化完成');

                    // 表單提交處理
                    document.getElementById('forgotPasswordForm').addEventListener('submit', function (e) {
                        e.preventDefault();
                        submitForm();
                    });

                    // Enter鍵提交
                    document.getElementById('account').addEventListener('keypress', function (e) {
                        if (e.key === 'Enter') {
                            e.preventDefault();
                            submitForm();
                        }
                    });
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
            </script>
        </body>

        </html>