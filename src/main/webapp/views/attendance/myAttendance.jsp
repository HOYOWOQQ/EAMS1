<%@ page contentType="text/html; charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="zh-TW">

    <head>
        <meta charset="UTF-8">
        <title>我的出缺勤紀錄</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                padding: 20px;
            }

            .container {
                max-width: 1200px;
                margin: 0 auto;
            }

            /* Header Section */
            .header-section {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                border-radius: 16px;
                padding: 1.5rem;
                margin-bottom: 1.5rem;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 1rem;
            }

            .page-title {
                font-size: 2rem;
                font-weight: 700;
                color: #333;
            }

            .header-actions {
                display: flex;
                gap: 12px;
            }

            /* Error Message */
            .error-message {
                background: rgba(220, 53, 69, 0.1);
                border: 1px solid rgba(220, 53, 69, 0.3);
                color: #dc3545;
                padding: 12px 16px;
                border-radius: 8px;
                margin-bottom: 1.5rem;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .error-message::before {
                content: "⚠️";
                font-size: 18px;
            }

            /* Search Section */
            .search-section {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                border-radius: 16px;
                padding: 1.5rem;
                margin-bottom: 1.5rem;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            }

            .search-title {
                font-size: 1.2rem;
                font-weight: 600;
                color: #333;
                margin-bottom: 1rem;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .search-form {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 1rem;
                align-items: end;
            }

            .form-group {
                display: flex;
                flex-direction: column;
            }

            .form-label {
                font-weight: 600;
                color: #333;
                margin-bottom: 6px;
                font-size: 14px;
            }

            .form-control {
                padding: 12px 16px;
                border: 2px solid #e1e5e9;
                border-radius: 8px;
                font-size: 16px;
                background: white;
                transition: border-color 0.3s ease;
            }

            .form-control:focus {
                outline: none;
                border-color: #667eea;
            }

            /* Buttons */
            .btn {
                padding: 12px 24px;
                border: none;
                border-radius: 8px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 8px;
                text-decoration: none;
                text-align: center;
                justify-content: center;
            }

            .btn:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }

            .btn-primary {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
            }

            .btn-success {
                background: linear-gradient(135deg, #28a745, #20c997);
                color: white;
            }

            /* Stats Section */
            .stats-section {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 1.5rem;
                margin-bottom: 2rem;
            }

            .stat-card {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                padding: 1.5rem;
                border-radius: 16px;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                text-align: center;
                transition: transform 0.3s ease;
            }

            .stat-card:hover {
                transform: translateY(-2px);
            }

            .stat-number {
                font-size: 2rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
            }

            .stat-number.present {
                color: #28a745;
            }

            .stat-number.absent {
                color: #dc3545;
            }

            .stat-number.late {
                color: #ffc107;
            }

            .stat-number.leave {
                color: #6c757d;
            }

            .stat-label {
                color: #666;
                font-size: 0.9rem;
            }

            /* Table Section */
            .table-section {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                border-radius: 16px;
                overflow: hidden;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                margin-bottom: 1.5rem;
            }

            .attendance-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 16px;
            }

            .attendance-table th {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 16px 12px;
                text-align: left;
                font-weight: 600;
                font-size: 15px;
            }

            .attendance-table td {
                padding: 16px 12px;
                border-bottom: 1px solid #f0f0f0;
                vertical-align: middle;
            }

            .attendance-table tbody tr {
                transition: background-color 0.2s ease;
            }

            .attendance-table tbody tr:hover {
                background: #f8f9ff;
            }

            .attendance-table tbody tr:last-child td {
                border-bottom: none;
            }

            /* Status Badges */
            .status-badge {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 600;
                text-transform: uppercase;
            }

            .status-present {
                background: rgba(40, 167, 69, 0.1);
                color: #28a745;
                border: 1px solid rgba(40, 167, 69, 0.3);
            }

            .status-absent {
                background: rgba(220, 53, 69, 0.1);
                color: #dc3545;
                border: 1px solid rgba(220, 53, 69, 0.3);
            }

            .status-late {
                background: rgba(255, 193, 7, 0.1);
                color: #ffc107;
                border: 1px solid rgba(255, 193, 7, 0.3);
            }

            .status-leave {
                background: rgba(108, 117, 125, 0.1);
                color: #6c757d;
                border: 1px solid rgba(108, 117, 125, 0.3);
            }

            .leave-status-badge {
                display: inline-block;
                padding: 4px 8px;
                border-radius: 8px;
                font-size: 11px;
                font-weight: 600;
            }

            .leave-approved {
                background: rgba(40, 167, 69, 0.1);
                color: #28a745;
            }

            .leave-pending {
                background: rgba(255, 193, 7, 0.1);
                color: #ffc107;
            }

            .leave-rejected {
                background: rgba(220, 53, 69, 0.1);
                color: #dc3545;
            }

            /* Loading & No Data */
            .loading {
                text-align: center;
                padding: 3rem;
                color: #666;
                font-size: 18px;
            }

            .loading::before {
                content: "⏳";
                font-size: 48px;
                display: block;
                margin-bottom: 1rem;
                animation: pulse 1.5s infinite;
            }

            .no-data {
                text-align: center;
                padding: 3rem;
                color: #666;
                font-size: 18px;
            }

            .no-data::before {
                content: "📅";
                font-size: 48px;
                display: block;
                margin-bottom: 1rem;
            }

            @keyframes pulse {

                0%,
                100% {
                    opacity: 1;
                }

                50% {
                    opacity: 0.5;
                }
            }

            /* Responsive */
            @media (max-width: 768px) {
                .container {
                    padding: 0 10px;
                }

                .header-section {
                    flex-direction: column;
                    align-items: stretch;
                    text-align: center;
                }

                .search-form {
                    grid-template-columns: 1fr;
                }

                .stats-section {
                    grid-template-columns: repeat(2, 1fr);
                    gap: 1rem;
                }

                .attendance-table th,
                .attendance-table td {
                    padding: 12px 8px;
                    font-size: 14px;
                }

                .page-title {
                    font-size: 1.5rem;
                }
            }
        </style>
    </head>

    <body>
        <div class="container">
            <!-- Header -->
            <div class="header-section">
                <h1 class="page-title">📊 我的出缺勤紀錄</h1>
                <div class="header-actions">
                    <a href="/api/leave/create" class="btn btn-success">📝 我要請假</a>
                </div>
            </div>

            <!-- Search Section -->
            <div class="search-section">
                <h3 class="search-title">🔍 查詢條件</h3>
                <form id="searchForm" class="search-form">
                    <div class="form-group">
                        <label class="form-label">查詢課程</label>
                        <select id="courseSelect" name="courseId" class="form-control">
                            <option value="">全部課程</option>
                            <!-- JS 動態載入 -->
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label">查詢日期</label>
                        <input type="date" id="lessonDate" name="lessonDate" class="form-control">
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary">🔍 查詢</button>
                    </div>
                </form>
            </div>

            <!-- Stats Section -->
            <div id="statsSection" class="stats-section" style="display: none;">
                <div class="stat-card">
                    <div class="stat-number present" id="presentCount">0</div>
                    <div class="stat-label">出席</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number absent" id="absentCount">0</div>
                    <div class="stat-label">缺席</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number late" id="lateCount">0</div>
                    <div class="stat-label">遲到</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number leave" id="leaveCount">0</div>
                    <div class="stat-label">請假</div>
                </div>
            </div>

            <!-- Table Section -->
            <div class="table-section">
                <table class="attendance-table">
                    <thead>
                        <tr>
                            <th>課程名稱</th>
                            <th>日期</th>
                            <th>節次</th>
                            <th>出缺勤狀態</th>
                            <th>備註</th>
                            <th>假單狀態</th>
                        </tr>
                    </thead>
                    <tbody id="attendanceTableBody">
                        <!-- 動態載入內容 -->
                    </tbody>
                </table>
            </div>
        </div>

        <script>
            // 全域變數
            let currentAttendanceData = [];

            // 頁面載入時初始化
            document.addEventListener("DOMContentLoaded", function () {
                loadCourses();
                loadAttendanceData();
            });

            // 載入課程選項
            function loadCourses() {
                fetch("${pageContext.request.contextPath}/api/course/ByStudentId")
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(courses => {
                    	console.log("從後端回來的課程資料：", courses);
                        const select = document.getElementById('courseSelect');
                        // 清空現有選項（保留第一個）
                        select.innerHTML = '<option value="">全部課程</option>';

                        courses.data.forEach(course => {
                            const option = document.createElement('option');
                            option.value = course.id;
                            option.textContent = course.name;
                            select.appendChild(option);
                        });
                    })
                    .catch(error => {
                        console.error("課程載入失敗:", error);
                        showError("課程載入失敗，請重新整理頁面");
                    });
            }

            // 載入出缺勤資料
            function loadAttendanceData(courseId = "", lessonDate = "") {
                showLoading();

                // 建立查詢參數
                const params = new URLSearchParams();
                if (courseId) params.append('courseId', courseId);
                if (lessonDate) params.append('lessonDate', lessonDate);
                
                console.log("查詢條件：", courseId, lessonDate); 
                console.log("URL 查詢字串：", params.toString()); 

                const url = `/EAMS/api/attendances/studentsearch?${params.toString()}`;
                console.log("組裝後的查詢 URL：", url);
                
                fetch(url)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        console.log("Servlet回傳資料：", data);
                        updateAttendanceTable(data);
                    })
                    .catch(error => {
                        console.error("出缺勤資料載入失敗:", error);
                        showError("出缺勤資料載入失敗，請重新整理頁面");
                    });
             
            }

            // 更新統計區塊
            function updateStatsSection(stats) {
                document.getElementById('presentCount').textContent = stats.present || 0;
                document.getElementById('absentCount').textContent = stats.absent || 0;
                document.getElementById('lateCount').textContent = stats.late || 0;
                document.getElementById('leaveCount').textContent = stats.leave || 0;

                // 顯示統計區塊
                const statsSection = document.getElementById('statsSection');
                if (stats.present > 0 || stats.absent > 0 || stats.late > 0 || stats.leave > 0) {
                    statsSection.style.display = 'grid';
                } else {
                    statsSection.style.display = 'none';
                }
            }

            // 更新出缺勤表格
            function updateAttendanceTable(attendances) {
                const tbody = document.getElementById('attendanceTableBody');

                if (!attendances || attendances.length === 0) {
                    tbody.innerHTML = `
            <tr>
                <td colspan="6" class="no-data">
                    查無出缺勤紀錄
                    <p style="margin-top: 1rem; font-size: 14px; color: #999;">
                        請選擇不同的查詢條件或聯繫老師確認課程安排
                    </p>
                </td>
            </tr>
        `;
                    return;
                }

                tbody.innerHTML = attendances.map(attendance => {
                	console.log("出席狀態：", attendance.statusText);
                    console.log("假單狀態：", attendance.leaveRequestStatusText);
                	console.log("每一筆 attendance：", attendance);
                    const statusClass = getStatusClass(attendance.statusText);
                    const leaveStatusClass = getLeaveStatusClass(attendance.leaveRequestStatusText);

                    return `
            <tr>
                <td>\${attendance.courseName || '-'}</td>
                <td>\${attendance.lessonDate || '-'}</td>
                <td>第 \${attendance.periodStart || '-'}-\${attendance.periodEnd || '-'} 節</td>
                <td>
                    <span class="status-badge \${statusClass}">
                        \${attendance.statusText || '-'}
                    </span>
                </td>
                <td>\${attendance.remark || '-'}</td>
                <td>
                    <span class="leave-badge \${leaveStatusClass}">
                        \${attendance.leaveRequestStatusText || '-'}
                    </span>
                </td>
            </tr>
        `;
                }).join('');
            }


            // 取得狀態樣式類別
            function getStatusClass(status) {
                switch (status) {
                    case '出席': return 'status-present';
                    case '缺席': return 'status-absent';
                    case '請假': return 'status-leave';
                    case '未點名': return 'status-unmarked';
                    default: return '';
                }
            }

            // 取得假單狀態樣式類別
            function getLeaveStatusClass(status) {
            	if (!status) return 'leave-none'; 
                switch (status) {
                    case '已核准': return 'leave-approved';
                    case '待審核': return 'leave-pending';
                    case '未通過': return 'leave-rejected';
                    default: return '';
                }
            }

            // 顯示載入中
            function showLoading() {
                const tbody = document.getElementById('attendanceTableBody');
                tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="loading">
                        載入中...
                    </td>
                </tr>
            `;
                document.getElementById('statsSection').style.display = 'none';
            }

            // 顯示錯誤訊息
            function showError(message) {
                const tbody = document.getElementById('attendanceTableBody');
                tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="error-message">
                        ${message}
                    </td>
                </tr>
            `;
                document.getElementById('statsSection').style.display = 'none';
            }

            // 查詢表單提交事件
            document.getElementById('searchForm').addEventListener('submit', function (e) {
                e.preventDefault();

                const courseId = document.getElementById('courseSelect').value;
                const lessonDate = document.getElementById('lessonDate').value;
               
                console.log("使用者送出查詢：", courseId, lessonDate);
                loadAttendanceData(courseId, lessonDate);
            });
        </script>
    </body>

    </html>