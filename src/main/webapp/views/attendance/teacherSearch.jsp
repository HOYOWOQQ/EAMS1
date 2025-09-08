<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <% String contextPath=request.getContextPath(); %>
            <!DOCTYPE html>
            <html lang="zh-TW">

            <head>
                <meta charset="UTF-8">
                <title>學生出缺勤紀錄查詢</title>
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
                        max-width: 1400px;
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
                        text-align: center;
                    }

                    .page-title {
                        font-size: 2rem;
                        font-weight: 700;
                        color: #333;
                    }

                    .page-subtitle {
                        color: #666;
                        font-size: 1rem;
                        margin-top: 0.5rem;
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
                        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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
                        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                    }

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
                        justify-content: center;
                    }

                    .btn:hover:not(:disabled) {
                        transform: translateY(-1px);
                        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                    }

                    .btn:disabled {
                        opacity: 0.6;
                        cursor: not-allowed;
                    }

                    .btn-primary {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                    }

                    .btn-secondary {
                        background: #6c757d;
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

                    .stat-number.total {
                        color: #667eea;
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
                    }

                    .attendance-table {
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 15px;
                    }

                    .attendance-table th {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                        padding: 16px 12px;
                        text-align: left;
                        font-weight: 600;
                        font-size: 14px;
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

                    .leave-link {
                        color: #667eea;
                        text-decoration: none;
                        font-weight: 600;
                        padding: 4px 8px;
                        border-radius: 6px;
                        transition: all 0.3s ease;
                    }

                    .leave-link:hover {
                        background: rgba(102, 126, 234, 0.1);
                        text-decoration: underline;
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
                        display: none;
                    }

                    .error-message::before {
                        content: "❌";
                        font-size: 16px;
                    }

                    /* No Data */
                    .no-data {
                        text-align: center;
                        padding: 3rem;
                        color: #666;
                        font-size: 18px;
                    }

                    .no-data::before {
                        content: "📋";
                        font-size: 48px;
                        display: block;
                        margin-bottom: 1rem;
                    }

                    /* Search Results Info */
                    .search-results-info {
                        background: rgba(102, 126, 234, 0.05);
                        border: 1px solid rgba(102, 126, 234, 0.1);
                        color: #333;
                        padding: 12px 16px;
                        border-radius: 8px;
                        margin-bottom: 1.5rem;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        display: none;
                    }

                    .search-results-info::before {
                        content: "📊";
                        font-size: 16px;
                    }

                    /* Responsive */
                    @media (max-width: 768px) {
                        .container {
                            padding: 0 10px;
                        }

                        .search-form {
                            grid-template-columns: 1fr;
                        }

                        .stats-section {
                            grid-template-columns: repeat(2, 1fr);
                            gap: 1rem;
                        }

                        .attendance-table {
                            font-size: 13px;
                        }

                        .attendance-table th,
                        .attendance-table td {
                            padding: 10px 6px;
                        }

                        .page-title {
                            font-size: 1.5rem;
                        }
                    }

                    /* Loading Animation */
                    .loading {
                        display: inline-block;
                        width: 20px;
                        height: 20px;
                        border: 3px solid rgba(255, 255, 255, 0.3);
                        border-radius: 50%;
                        border-top-color: #ffffff;
                        animation: spin 1s ease-in-out infinite;
                    }

                    @keyframes spin {
                        to {
                            transform: rotate(360deg);
                        }
                    }

                    .hidden {
                        display: none !important;
                    }
                </style>
            </head>

            <body>
                <div class="container">
                    <!-- Header -->
                    <div class="header-section">
                        <h1 class="page-title">👨‍🏫 學生出缺勤查詢</h1>
                        <p class="page-subtitle">查詢和管理學生的出缺勤紀錄</p>
                    </div>

                    <!-- Search Section -->
                    <div class="search-section">
                        <h3 class="search-title">🔍 查詢條件</h3>
                        <form class="search-form" id="searchForm">
                            <div class="form-group">
                                <label class="form-label">學生姓名</label>
                                <input type="text" name="studentName" id="studentName" class="form-control"
                                    placeholder="輸入學生姓名...">
                            </div>
                            <div class="form-group">
                                <label class="form-label">上課日期</label>
                                <input type="date" name="lessonDate" id="lessonDate" class="form-control">
                            </div>
                            <div class="form-group">
                                <label class="form-label">課程名稱</label>
                                <input type="text" name="courseName" id="courseName" class="form-control"
                                    placeholder="輸入課程名稱...">
                            </div>
                            <div class="form-group">
                                <button type="submit" class="btn btn-primary" id="searchBtn">
                                    <span class="search-text">🔍 查詢</span>
                                    <div class="loading hidden"></div>
                                </button>
                            </div>
                        </form>
                    </div>

                    <!-- Error Message -->
                    <div class="error-message" id="errorMessage">
                        查詢時發生錯誤，請稍後再試
                    </div>

                    <!-- Search Results Info -->
                    <div class="search-results-info" id="searchResultsInfo">
                        查詢結果：找到 <span id="totalRecords">0</span> 筆出缺勤紀錄
                    </div>

                    <!-- Stats Section -->
                    <div class="stats-section hidden" id="statsSection">
                        <div class="stat-card">
                            <div class="stat-number total" id="totalCount">0</div>
                            <div class="stat-label">總筆數</div>
                        </div>
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
                        <table class="attendance-table hidden" id="attendanceTable">
                            <thead>
                                <tr>
                                    <th>學生姓名</th>
                                    <th>課程名稱</th>
                                    <th>上課日期</th>
                                    <th>節次</th>
                                    <th>出缺勤狀態</th>
                                    <th>備註</th>
                                    <th>假單檔案</th>
                                    <th>審核狀態</th>
                                </tr>
                            </thead>
                            <tbody id="attendanceTableBody">
                            </tbody>
                        </table>

                        <div class="no-data" id="noDataMessage">
                            請輸入查詢條件並點擊查詢按鈕
                            <p style="margin-top: 1rem; font-size: 14px; color: #999;">
                                可以依據學生姓名、日期或課程名稱進行查詢
                            </p>
                        </div>

                        <div class="no-data hidden" id="noResultsMessage">
                            查無符合條件的出缺勤紀錄
                            <p style="margin-top: 1rem; font-size: 14px; color: #999;">
                                請調整查詢條件或確認學生資料是否正確
                            </p>
                        </div>
                    </div>
                </div>

                <script>
                    // 頁面加載完成後初始化
                    document.addEventListener('DOMContentLoaded', function () {
                    	searchAttendance();
                    	// 綁定表單提交事件
                        document.getElementById('searchForm').addEventListener('submit', function (e) {
                            e.preventDefault();
                            searchAttendance();
                        });
                    });

                    // 查詢出缺勤資料
                    async function searchAttendance() {
                        const formData = new FormData();
                        const studentName = document.getElementById('studentName').value.trim();
                        const lessonDate = document.getElementById('lessonDate').value;
                        const courseName = document.getElementById('courseName').value.trim();

                        // 構建請求參數
                        if (studentName) formData.append('studentName', studentName);
                        if (lessonDate) formData.append('lessonDate', lessonDate);
                        if (courseName) formData.append('courseName', courseName);

                        showLoading(true);
                        hideError();

                        try {
                            const response = await fetch("${pageContext.request.contextPath}/api/attendances/teachersearch", {
                                method: 'POST',
                                body: formData
                            });

                            if (!response.ok) {
                                throw new Error(`HTTP error! status: \${response.status}`);
                            }

                            const data = await response.json();
                            console.log('查詢結果:', data);
                            if (Array.isArray(data)) {
                                handleSearchSuccess(data);
                            } else {
                                showError('查詢結果格式錯誤');
                            }
                        } catch (error) {
                            console.error('查詢出錯:', error);
                            showError('連接服務器失敗，請檢查網絡連接或稍後再試');
                        } finally {
                            showLoading(false);
                        }
                    }

                    // 處理查詢成功
                    function handleSearchSuccess(results) {
                        if (results && results.length > 0) {
                            renderAttendanceTable(results);
                            updateStatistics(results);
                            showResults();
                        } else {
                            showNoResults();
                        }
                    }

                    // 渲染出缺勤表格
                    function renderAttendanceTable(data) {
                        const tbody = document.getElementById('attendanceTableBody');
                        tbody.innerHTML = '';

                        data.forEach(attendance => {
                            const row = createAttendanceRow(attendance);
                            tbody.appendChild(row);
                        });
                    }

                    // 創建單個出缺勤記錄行
                    function createAttendanceRow(attendance) {
                        const row = document.createElement('tr');

                        // 確定狀態樣式類別
                        const statusClass = getStatusClass(attendance.statusText);
                        const leaveStatusClass = getLeaveStatusClass(attendance.leaveReviewStatusText);

                        let leaveLinkHtml = '-';
                        if (attendance.leaveRequestId) {
                            leaveLinkHtml = `<a href="ViewLeaveRequestServlet?id=\${attendance.leaveRequestId}" target="_blank" class="leave-link">📄 查看假單</a>`;
                        }

                        // 處理請假審核狀態 HTML
                        let reviewStatusHtml = '-';
                        if (attendance.leaveReviewStatusText && attendance.leaveReviewStatusText !== '-') {
                            reviewStatusHtml = `<span class="leave-status-badge \${leaveStatusClass}">\${escapeHtml(attendance.leaveReviewStatusText)}</span>`;
                        }

                        row.innerHTML = `
                <td><strong>\${escapeHtml(attendance.studentName || '')}</strong></td>
                <td>\${escapeHtml(attendance.courseName || '')}</td>
                <td>\${escapeHtml(attendance.lessonDate || '')}</td>
                <td>第 \${attendance.periodStart || ''} - \${attendance.periodEnd || ''} 節</td>
                <td>
                    <span class="status-badge \${statusClass}">
                        \${escapeHtml(attendance.statusText || '')}
                    </span>
                </td>
                <td>\${attendance.remark ? escapeHtml(attendance.remark) : ''}</td>
                  <td>\${leaveLinkHtml}</td>
                    <td>\${reviewStatusHtml}</td>
            `;

                        return row;
                    }

                    // 獲取狀態樣式類別
                    function getStatusClass(status) {
                        switch (status) {
                            case '已出席':
                            case '出席':
                                return 'status-present';
                            case '未出席':
                            case '缺席':
                                return 'status-absent';
                            case '遲到':
                                return 'status-late';
                            case '請假':
                                return 'status-leave';
                            default:
                                return '';
                        }
                    }

                    // 獲取請假審核狀態樣式類別
                    function getLeaveStatusClass(status) {
                        switch (status) {
                            case '已核准':
                                return 'leave-approved';
                            case '待審核':
                                return 'leave-pending';
                            case '已拒絕':
                                return 'leave-rejected';
                            default:
                                return '';
                        }
                    }

                    // 更新統計數據
                    function updateStatistics(data) {
                        let totalCount = data.length;
                        let presentCount = 0;
                        let absentCount = 0;
                        let lateCount = 0;
                        let leaveCount = 0;

                        data.forEach(attendance => {
                            const status = attendance.statusText;
                            switch (status) {
                                case '已出席':
                                case '出席':
                                    presentCount++;
                                    break;
                                case '未出席':
                                case '缺席':
                                    absentCount++;
                                    break;
                                case '遲到':
                                    lateCount++;
                                    break;
                                case '請假':
                                    leaveCount++;
                                    break;
                            }
                        });

                        // 更新DOM元素
                        document.getElementById('totalCount').textContent = totalCount;
                        document.getElementById('presentCount').textContent = presentCount;
                        document.getElementById('absentCount').textContent = absentCount;
                        document.getElementById('lateCount').textContent = lateCount;
                        document.getElementById('leaveCount').textContent = leaveCount;
                        document.getElementById('totalRecords').textContent = totalCount;
                    }

                    // 顯示查詢結果
                    function showResults() {
                        document.getElementById('searchResultsInfo').style.display = 'flex';
                        document.getElementById('statsSection').classList.remove('hidden');
                        document.getElementById('attendanceTable').classList.remove('hidden');
                        document.getElementById('noDataMessage').classList.add('hidden');
                        document.getElementById('noResultsMessage').classList.add('hidden');
                    }

                    // 顯示無結果消息
                    function showNoResults() {
                        document.getElementById('searchResultsInfo').style.display = 'none';
                        document.getElementById('statsSection').classList.add('hidden');
                        document.getElementById('attendanceTable').classList.add('hidden');
                        document.getElementById('noDataMessage').classList.add('hidden');
                        document.getElementById('noResultsMessage').classList.remove('hidden');
                    }

                    // 顯示/隱藏載入動畫
                    function showLoading(show) {
                        const loadingEl = document.querySelector('.loading');
                        const textEl = document.querySelector('.search-text');
                        const submitBtn = document.getElementById('searchBtn');

                        if (show) {
                            loadingEl.classList.remove('hidden');
                            textEl.classList.add('hidden');
                            submitBtn.disabled = true;
                        } else {
                            loadingEl.classList.add('hidden');
                            textEl.classList.remove('hidden');
                            submitBtn.disabled = false;
                        }
                    }

                    // 顯示錯誤消息
                    function showError(message) {
                        const errorEl = document.getElementById('errorMessage');
                        errorEl.textContent = message;
                        errorEl.style.display = 'flex';

                        // 隱藏其他內容
                        document.getElementById('searchResultsInfo').style.display = 'none';
                        document.getElementById('statsSection').classList.add('hidden');
                        document.getElementById('attendanceTable').classList.add('hidden');
                        document.getElementById('noResultsMessage').classList.add('hidden');
                    }

                    // 隱藏錯誤消息
                    function hideError() {
                        document.getElementById('errorMessage').style.display = 'none';
                    }

                    // HTML轉義函數
                    function escapeHtml(text) {
                        if (!text) return '';
                        const div = document.createElement('div');
                        div.textContent = text;
                        return div.innerHTML;
                    }
                </script>
            </body>

            </html>