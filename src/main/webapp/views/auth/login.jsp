<%@ page contentType="text/html; charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <% String contextPath=request.getContextPath(); %>

            <div class="feature-card">
                <h2>登入學生管理系統</h2>

                <!-- 顯示錯誤訊息 -->
                <c:if test="${not empty errorMsg}">
                    <div class="error-message">${errorMsg}</div>
                </c:if>

                <form id="loginFormReal">
                    <label for="account">帳號</label>
                    <input type="text" id="account" name="account" value="${inputAccount}" placeholder="請輸入帳號" required>

                    <label for="password">密碼</label>
                    <input type="password" id="password" name="password" placeholder="請輸入密碼" required>

                    <button type="submit" style="margin-top:20px;">登入</button>
                </form>

                <!-- 額外功能連結 -->
                <div class="additional-links">
                    <a href="${pageContext.request.contextPath}/resetPassword">忘記密碼</a>
                    |
                    <a href="${pageContext.request.contextPath}/verifyEmail">驗證帳號</a>
                </div>


            </div>

            <!-- SweetAlert2 CSS -->
            <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css" rel="stylesheet">
            <!-- SweetAlert2 JS -->
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

            <script>
                // 如果你想要使用 AJAX 提交（像第一個版本），取消註解下面的代碼

                document.getElementById('loginFormReal').addEventListener('submit', async function (e) {
                    e.preventDefault(); // 攔截表單提交

                    const account = document.getElementById('account').value;
                    const password = document.getElementById('password').value;

                    const response = await fetch('${pageContext.request.contextPath}/login', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: `account=\${encodeURIComponent(account)}&password=\${encodeURIComponent(password)}`
                    });

                    if (response.ok) {
                        const result = await response.text();
                        Swal.fire({
                            icon: result.includes('登入成功') ? 'success' : 'error',
                            title: result,
                            confirmButtonText: '確定'
                        }).then(() => {
                            if (result.includes('登入成功')) {
                                window.top.location.href = '${pageContext.request.contextPath}/views/common/index.jsp';
                            }
                        });
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: '登入失敗：伺服器錯誤',
                            confirmButtonText: '確定'
                        });
                    }
                });

            </script>

            <style>
                body {
                    min-height: 100vh;
                    margin: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    background: url('${pageContext.request.contextPath}/resources/img/004.jpg') center center / cover no-repeat;
                }

                .feature-card {
                    background: #fff;
                    border-radius: 16px;
                    box-shadow: 0 8px 40px rgba(80, 100, 180, 0.18), 0 1.5px 6px #bbb;
                    padding: 32px 36px 24px 36px;
                    max-width: 480px;
                    width: 100%;
                    margin: 0 auto;
                    position: relative;
                    z-index: 10;
                    animation: popup 0.4s cubic-bezier(.25, .8, .25, 1);
                }

                @keyframes popup {
                    from {
                        transform: scale(0.95) translateY(30px);
                        opacity: 0;
                    }

                    to {
                        transform: scale(1) translateY(0);
                        opacity: 1;
                    }
                }

                .feature-card h2 {
                    text-align: center;
                    margin-bottom: 24px;
                    color: #333;
                }

                .feature-card label {
                    display: block;
                    margin-bottom: 6px;
                    font-weight: bold;
                    color: #555;
                }

                .feature-card input {
                    width: 100%;
                    padding: 12px;
                    margin-bottom: 16px;
                    border: 1px solid #ddd;
                    border-radius: 6px;
                    font-size: 14px;
                    box-sizing: border-box;
                }

                .feature-card button {
                    width: 100%;
                    padding: 12px;
                    background-color: #007bff;
                    color: white;
                    border: none;
                    border-radius: 6px;
                    font-size: 16px;
                    cursor: pointer;
                    transition: background-color 0.3s;
                }

                .feature-card button:hover {
                    background-color: #0056b3;
                }

                .error-message {
                    background-color: #f8d7da;
                    color: #721c24;
                    padding: 10px;
                    border: 1px solid #f5c6cb;
                    border-radius: 4px;
                    margin-bottom: 16px;
                }

                .additional-links {
                    text-align: center;
                    margin-top: 20px;
                    padding-top: 16px;
                    border-top: 1px solid #eee;
                }

                .additional-links a {
                    color: #007bff;
                    text-decoration: none;
                    margin: 0 8px;
                }

                .additional-links a:hover {
                    text-decoration: underline;
                }

                .test-accounts {
                    margin-top: 20px;
                    padding: 16px;
                    background-color: #f8f9fa;
                    border-radius: 6px;
                    font-size: 12px;
                    color: #666;
                }

                .test-accounts p {
                    margin: 4px 0;
                }

                .test-accounts strong {
                    color: #333;
                }
            </style>