<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <% String contextPath=request.getContextPath(); %>
            <!DOCTYPE html>
            <html lang="zh-TW">

            <head>
                <meta charset="UTF-8">
                <title>學生管理系統</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #b699d3 100%);
                        min-height: 100vh;
                        color: #333;
                        overflow: hidden;
                    }

                    /* Header */
                    .header {
                        width: 100%;
                        height: 70px;
                        background: rgba(255, 255, 255, 0.95);
                        backdrop-filter: blur(10px);
                        color: #333;
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        position: fixed;
                        top: 0;
                        left: 0;
                        z-index: 1000;
                        box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
                        padding: 0 2rem;
                    }

                    .header-title {
                        flex: 1;
                        text-align: center;
                        font-size: 28px;
                        font-weight: 700;
                        letter-spacing: 2px;
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                    }

                    .header-user {
                        display: flex;
                        align-items: center;
                        gap: 15px;
                    }

                    /* 歡迎信息樣式 */
                    .welcome-info {
                        display: flex;
                        align-items: center;
                        gap: 15px;
                        padding: 8px 16px;
                        background: rgba(102, 126, 234, 0.1);
                        border-radius: 25px;
                        border: 2px solid rgba(102, 126, 234, 0.2);
                    }

                    .welcome-text {
                        font-size: 16px;
                        font-weight: 600;
                        color: #667eea;
                        white-space: nowrap;
                    }

                    .user-avatar {
                        width: 32px;
                        height: 32px;
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: white;
                        font-size: 14px;
                        font-weight: bold;
                    }

                    .login-btn,
                    .logout-btn {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                        border: none;
                        border-radius: 25px;
                        padding: 12px 24px;
                        font-size: 16px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        white-space: nowrap;
                    }

                    .login-btn:hover,
                    .logout-btn:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
                    }

                    .logout-btn {
                        background: linear-gradient(135deg, #ff6b6b, #ee5a52);
                    }

                    .logout-btn:hover {
                        box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
                    }

                    /* Container */
                    .container {
                        display: flex;
                        margin-top: 70px;
                        height: calc(100vh - 70px);
                    }

                    /* Sidebar */
                    .sidebar {
                        width: 280px;
                        background: rgba(255, 255, 255, 0.95);
                        backdrop-filter: blur(10px);
                        box-shadow: 2px 0 20px rgba(0, 0, 0, 0.1);
                        padding: 2rem 0;
                        position: fixed;
                        left: 0;
                        top: 70px;
                        height: calc(100vh - 70px);
                        overflow-y: auto;
                    }

                    .sidebar ul {
                        list-style: none;
                        padding: 0;
                        margin: 0;
                    }

                    .sidebar li {
                        margin: 0 1rem 8px 1rem;
                    }

                    .sidebar .main-menu>a {
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        padding: 16px 20px;
                        color: #4a5568;
                        text-decoration: none;
                        font-size: 16px;
                        font-weight: 600;
                        border-radius: 12px;
                        transition: all 0.3s ease;
                        cursor: pointer;
                    }

                    .sidebar .main-menu>a:hover,
                    .sidebar .main-menu.active>a {
                        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
                        color: #667eea;
                        transform: translateX(4px);
                    }

                    .sidebar .main-menu>a::after {
                        content: "▼";
                        font-size: 12px;
                        transition: transform 0.3s ease;
                    }

                    .sidebar .main-menu.active>a::after {
                        transform: rotate(180deg);
                    }

                    .sidebar .sub-menu {
                        display: none;
                        background: transparent;
                        margin: 8px 0 0 0;
                        padding-left: 20px;
                        max-height: 0;
                        overflow: hidden;
                        transition: all 0.3s ease;
                    }

                    .sidebar .main-menu.active .sub-menu {
                        display: block;
                        max-height: 300px;
                    }

                    .sidebar .sub-menu li {
                        margin: 4px 0;
                    }

                    .sidebar .sub-menu li a {
                        display: block;
                        padding: 12px 20px;
                        color: #718096;
                        font-size: 14px;
                        text-decoration: none;
                        border-radius: 8px;
                        transition: all 0.3s ease;
                        cursor: pointer;
                    }

                    .sidebar .sub-menu li a:hover,
                    .sidebar .sub-menu li a.active {
                        background: rgba(102, 126, 234, 0.1);
                        color: #667eea;
                        transform: translateX(8px);
                    }

                    /* Content */
                    .content {
                        flex: 1;
                        margin-left: 280px;
                        padding: 0;
                        /* 移除內距 */
                        min-height: 100%;
                        display: flex;
                        align-items: stretch;
                        justify-content: center;
                        position: relative;
                        /* 新增 */
                    }

                    .content iframe {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        border: none;
                        background: rgba(255, 255, 255, 0.95);
                        backdrop-filter: blur(10px);
                        border-radius: 20px;
                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);

                    }

                    /* Enhanced table styles for iframe content */
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 20px;
                        background: white;
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                    }

                    th,
                    td {
                        padding: 16px 20px;
                        border: none;
                        text-align: left;
                        border-bottom: 1px solid rgba(0, 0, 0, 0.05);
                    }

                    th {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                        font-weight: 600;
                        font-size: 14px;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }

                    tr {
                        transition: all 0.3s ease;
                    }

                    tr:hover {
                        background: rgba(102, 126, 234, 0.05);
                        transform: scale(1.01);
                    }

                    /* Menu Icons */
                    .sidebar .main-menu>a::before {
                        margin-right: 12px;
                        font-size: 18px;
                    }

                    .sidebar .main-menu:nth-child(1)>a::before {
                        content: "👤";
                    }

                    .sidebar .main-menu:nth-child(2)>a::before {
                        content: "📊";
                    }

                    .sidebar .main-menu:nth-child(3)>a::before {
                        content: "⏰";
                    }

                    .sidebar .main-menu:nth-child(4)>a::before {
                        content: "💳";
                    }

                    .sidebar .main-menu:nth-child(5)>a::before {
                        content: "📚";
                    }

                    .sidebar .main-menu:nth-child(6)>a::before {
                        content: "📢";
                    }

                    /* Submenu Icons */
                    .sidebar .sub-menu li a::before {
                        margin-right: 8px;
                        font-size: 14px;
                    }

                    .sidebar .sub-menu li:nth-child(1) a::before {
                        content: "📝";
                    }

                    .sidebar .sub-menu li:nth-child(2) a::before {
                        content: "🔐";
                    }

                    .sidebar .sub-menu li:nth-child(3) a::before {
                        content: "👥";
                    }

                    .sidebar .sub-menu li:nth-child(4) a::before {
                        content: "🚪";
                    }

                    /* Responsive Design */
                    @media (max-width: 768px) {
                        .sidebar {
                            transform: translateX(-100%);
                            transition: transform 0.3s ease;
                            z-index: 999;
                        }

                        .sidebar.mobile-open {
                            transform: translateX(0);
                        }

                        .content {
                            margin-left: 0;
                            padding: 1rem;
                        }

                        .header-title {
                            font-size: 20px;
                        }

                        .mobile-menu-btn {
                            display: block;
                            background: none;
                            border: none;
                            font-size: 24px;
                            cursor: pointer;
                            padding: 8px;
                            margin-right: 16px;
                        }

                        .header-user {
                            gap: 8px;
                        }

                        .welcome-info {
                            padding: 6px 12px;
                        }

                        .welcome-text {
                            font-size: 14px;
                        }

                        .user-avatar {
                            width: 28px;
                            height: 28px;
                            font-size: 12px;
                        }

                        .login-btn,
                        .logout-btn {
                            padding: 10px 18px;
                            font-size: 14px;
                        }
                    }

                    .mobile-menu-btn {
                        display: none;
                    }

                    /* Loading Animation */
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

                    .sidebar .main-menu {
                        animation: fadeIn 0.5s ease forwards;
                    }

                    .sidebar .main-menu:nth-child(1) {
                        animation-delay: 0.1s;
                    }

                    .sidebar .main-menu:nth-child(2) {
                        animation-delay: 0.2s;
                    }

                    .sidebar .main-menu:nth-child(3) {
                        animation-delay: 0.3s;
                    }

                    .sidebar .main-menu:nth-child(4) {
                        animation-delay: 0.4s;
                    }

                    .sidebar .main-menu:nth-child(5) {
                        animation-delay: 0.5s;
                    }

                    .sidebar .main-menu:nth-child(6) {
                        animation-delay: 0.6s;
                    }

                    /* Scrollbar Styling */
                    .sidebar::-webkit-scrollbar {
                        width: 6px;
                    }

                    .sidebar::-webkit-scrollbar-track {
                        background: transparent;
                    }

                    .sidebar::-webkit-scrollbar-thumb {
                        background: rgba(102, 126, 234, 0.3);
                        border-radius: 3px;
                    }

                    .sidebar::-webkit-scrollbar-thumb:hover {
                        background: rgba(102, 126, 234, 0.5);
                    }

                    /* 平滑過渡動畫 */
                    .header-user>* {
                        transition: all 0.3s ease;
                    }
                </style>
                <base href="/EAMS/views/common/">
                <!-- SweetAlert2 CSS -->
                <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css" rel="stylesheet">
                <!-- SweetAlert2 JS -->
                <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
            </head>

            <body>
                <div class="header">
                    <button class="mobile-menu-btn" onclick="toggleMobileMenu()">☰</button>
                    <div class="header-title"> 智慧校務平台</div>
                    <div class="header-user" id="headerUser">
                        <!-- 檢查用戶是否已登入 -->
                        <c:choose>
                            <c:when test="${not empty sessionScope.name || not empty sessionScope.member}">
                                <script>

                                </script>
                                <!-- 已登入狀態 -->
                                <div class="welcome-info">
                                    <div class="user-avatar">
                                        <!-- 顯示用戶名的第一個字 -->
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.name}">
                                                ${sessionScope.name.substring(0,1)}
                                            </c:when>
                                            <c:when test="${not empty sessionScope.member.name}">
                                                ${sessionScope.member.name.substring(0,1)}
                                            </c:when>
                                            <c:otherwise>用</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <span class="welcome-text">
                                        歡迎
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.name}">
                                                ${sessionScope.name}
                                            </c:when>
                                            <c:when test="${not empty sessionScope.member.name}">
                                                ${sessionScope.member.name}
                                            </c:when>
                                            <c:otherwise>使用者</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <button class="logout-btn" onclick="performLogout()">登出</button>
                            </c:when>
                            <c:otherwise>
                                <script>
                                    console.log("未登入，sessionScope.name:", "<c:out value='${sessionScope.name}'/>");
                                    console.log("未登入，sessionScope.member:", "<c:out value='${sessionScope.member}'/>");
                                </script>
                                <!-- 未登入狀態 -->
                                <button id="loginBtn" class="login-btn" onclick="showLogin()">登入</button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="container">
                    <nav class="sidebar" id="sidebar">
                        <ul>
                            <li class="main-menu" id="menu-profile">
                                <a onclick="toggleMenu('profile')">學生/老師管理</a>
                                <ul class="sub-menu">
                                    <li><a href="../../profile" target="mainFrame">個人資料</a></li>
                                    <li><a href="../../changePassword" target="mainFrame">修改密碼</a></li>
                                    <!-- <c:if test="${role eq 'teacher' or role eq '主任'}"> -->
                                    <li><a href="../../memberList" target="mainFrame">會員管理</a>
                                    </li>
                                    <!-- </c:if> -->
                                    <!-- <li><a href="../../logout" target="mainFrame">登出</a></li> -->
                                </ul>
                            </li>
                            <li class="main-menu" id="menu-score">
                                <a onclick="toggleMenu('score')">成績管理</a>
                                <ul class="sub-menu">
                                    <c:if test="${role eq 'teacher' or role eq '主任'}">
                                        <li><a href="../../InsertExamPaper" target="mainFrame">考卷新增</a></li>
                                        <li><a href="../../BatchInsertExamresult" target="mainFrame">成績新增</a></li>
                                        <li><a href="../../SelectExamResult" target="mainFrame">成績紀錄/批改</a></li>
                                        <li><a href="../../SelectExamPaper" target="mainFrame">成績查詢/統計</a></li>
                                    </c:if>

                                </ul>
                            </li>
                            <li class="main-menu" id="menu-attendance">
                                <a onclick="toggleMenu('attendance')">出缺勤管理</a>
                                <ul class="sub-menu">
                                    <c:if test="${role eq 'student'}">
                                        <li><a href="../../myAttendance" target="mainFrame">學生打卡記錄</a></li>
                                    </c:if>
                                    <c:if test="${role eq 'teacher' or role eq '主任'}">
                                        <li><a href="../../takeAttendance" target="mainFrame">老師點名</a></li>
                                    </c:if>
                                    <li><a href="../../teacherSearch" target="mainFrame">出缺勤查詢/統計</a></li>

                                </ul>
                            </li>
                            <li class="main-menu" id="menu-fee">
                                <a onclick="toggleMenu('fee')">費用管理</a>
                                <ul class="sub-menu">
                                    <c:if test="${role eq 'teacher' or role eq '主任'}">
                                        <li><a href="../../tuitionManage" target="mainFrame">學費管理</a></li>
                                    </c:if>
                                </ul>
                            </li>
                            <li class="main-menu" id="menu-course">
                                <a onclick="toggleMenu('course')">課程管理</a>
                                <ul class="sub-menu">
                                    <c:if test="${role eq 'teacher' or role eq '主任'}">
                                        <li><a href="../../CourseMain" target="mainFrame">課程管理/排課系統</a></li>
                                    </c:if>
                                    <li><a href="../../CourseEnroll" target="mainFrame">報名系統</a></li>
                                    <li><a href="../../CourseQuery" target="mainFrame">課程查詢</a></li>
                                    <c:if test="${role eq 'teacher' or role eq '主任'}">
                                        <li><a href="../../CourseEdit" target="mainFrame">課表編輯</a></li>
                                    </c:if>
                                </ul>
                            </li>
                            <li class="main-menu" id="menu-notice">
                                <a onclick="toggleMenu('notice')">班級通知/教師日誌</a>
                                <ul class="sub-menu">
                                    <li><a href="../../notice/list" target="mainFrame">公告列表</a></li>
                                </ul>
                            </li>
                        </ul>
                    </nav>

                    <main class="content">
                        <iframe name="mainFrame" src="../common/welcome.jsp"></iframe>
                    </main>
                </div>

                <script>
                    function toggleMenu(menu) {
                        document.querySelectorAll('.main-menu').forEach(function (el) {
                            if (el.id === 'menu-' + menu) {
                                el.classList.toggle('active');
                            } else {
                                el.classList.remove('active');
                            }
                        });
                    }

                    function showLogin() {
                        window.frames['mainFrame'].location.href = '../../login';
                    }

                    function performLogout() {
                        Swal.fire({
                            title: '確定要登出嗎？',
                            icon: 'warning',
                            showCancelButton: true,
                            confirmButtonText: '登出',
                            cancelButtonText: '取消',
                            confirmButtonColor: '#ff6b6b'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                window.location.href = '../../logout';
                            }
                        });
                    }

                    function toggleMobileMenu() {
                        const sidebar = document.getElementById('sidebar');
                        sidebar.classList.toggle('mobile-open');
                    }

                    // Add smooth animations
                    document.addEventListener('DOMContentLoaded', function () {
                        // Add click animations to buttons
                        document.querySelectorAll('button, .sidebar a').forEach(element => {
                            element.addEventListener('click', function () {
                                this.style.transform = 'scale(0.95)';
                                setTimeout(() => {
                                    this.style.transform = '';
                                }, 150);
                            });
                        });

                        // Close mobile menu when clicking outside
                        document.addEventListener('click', function (event) {
                            const sidebar = document.getElementById('sidebar');
                            const mobileMenuBtn = document.querySelector('.mobile-menu-btn');

                            if (window.innerWidth <= 768 &&
                                !sidebar.contains(event.target) &&
                                !mobileMenuBtn.contains(event.target)) {
                                sidebar.classList.remove('mobile-open');
                            }
                        });
                    });
                </script>
            </body>

            </html>