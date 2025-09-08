<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <% String contextPath=request.getContextPath(); %>
            <!DOCTYPE html>
            <html lang="zh-TW">

            <head>
                <!-- SweetAlert2 CSS -->
                <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css" rel="stylesheet">
                <!-- SweetAlert2 JS -->
                <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                <meta charset="UTF-8">
                <title>點名畫面</title>
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
                        padding: 2rem;
                        color: #333;
                    }

                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        backdrop-filter: blur(10px);
                        border-radius: 20px;
                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                        animation: fadeIn 0.5s ease;
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

                    .header {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                        padding: 2rem;
                        text-align: center;
                    }

                    .header h2 {
                        font-size: 28px;
                        font-weight: 700;
                        margin-bottom: 0.5rem;
                        letter-spacing: 1px;
                    }

                    .course-info {
                        background: rgba(255, 255, 255, 0.1);
                        border-radius: 12px;
                        padding: 1.5rem;
                        margin-top: 1rem;
                        backdrop-filter: blur(10px);
                    }

                    .course-info p {
                        font-size: 16px;
                        line-height: 1.6;
                        margin: 0;
                    }

                    .course-info strong {
                        color: #fff;
                        font-weight: 600;
                    }

                    .content {
                        padding: 2rem;
                    }

                    /* Loading Section */
                    .loading-section {
                        text-align: center;
                        padding: 4rem 2rem;
                        color: #718096;
                        font-size: 18px;
                    }

                    .loading-icon {
                        display: inline-block;
                        width: 40px;
                        height: 40px;
                        border: 4px solid rgba(102, 126, 234, 0.3);
                        border-radius: 50%;
                        border-top-color: #667eea;
                        animation: spin 1s ease-in-out infinite;
                        margin-bottom: 1rem;
                    }

                    @keyframes spin {
                        to {
                            transform: rotate(360deg);
                        }
                    }

                    /* Error Section */
                    .error-section {
                        text-align: center;
                        padding: 4rem 2rem;
                        color: #e53e3e;
                        font-size: 18px;
                    }

                    .error-section::before {
                        content: "❌";
                        display: block;
                        font-size: 48px;
                        margin-bottom: 1rem;
                    }

                    .retry-btn {
                        background: #e53e3e;
                        color: white;
                        border: none;
                        border-radius: 8px;
                        padding: 12px 24px;
                        font-size: 16px;
                        font-weight: 600;
                        cursor: pointer;
                        margin-top: 1rem;
                        transition: all 0.3s ease;
                    }

                    .retry-btn:hover {
                        background: #c53030;
                        transform: translateY(-1px);
                    }

                    .no-students {
                        text-align: center;
                        padding: 4rem 2rem;
                        color: #718096;
                        font-size: 18px;
                    }

                    .no-students::before {
                        content: "👥";
                        display: block;
                        font-size: 48px;
                        margin-bottom: 1rem;
                    }

                    .controls {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-bottom: 2rem;
                        flex-wrap: wrap;
                        gap: 1rem;
                    }

                    .mark-all-btn {
                        background: linear-gradient(135deg, #48bb78, #38a169);
                        color: white;
                        border: none;
                        border-radius: 12px;
                        padding: 12px 24px;
                        font-size: 16px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        box-shadow: 0 4px 15px rgba(72, 187, 120, 0.3);
                    }

                    .mark-all-btn:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(72, 187, 120, 0.4);
                    }

                    .mark-all-btn:active {
                        transform: translateY(0);
                    }

                    .attendance-table {
                        width: 100%;
                        border-collapse: collapse;
                        background: white;
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                        margin-bottom: 2rem;
                    }

                    .attendance-table th {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                        padding: 1rem;
                        font-weight: 600;
                        font-size: 14px;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                        text-align: left;
                    }

                    .attendance-table td {
                        padding: 1rem;
                        border-bottom: 1px solid rgba(0, 0, 0, 0.05);
                        vertical-align: middle;
                    }

                    .attendance-table tr {
                        transition: all 0.3s ease;
                    }

                    .attendance-table tr:hover {
                        background: rgba(102, 126, 234, 0.05);
                        transform: scale(1.01);
                    }

                    .attendance-table tr:last-child td {
                        border-bottom: none;
                    }

                    .student-name {
                        font-weight: 600;
                        color: #2d3748;
                        font-size: 16px;
                    }

                    .status-select {
                        width: 100%;
                        padding: 10px 15px;
                        border: 2px solid #e2e8f0;
                        border-radius: 8px;
                        font-size: 14px;
                        font-weight: 500;
                        background: white;
                        cursor: pointer;
                        transition: all 0.3s ease;
                    }

                    .status-select:focus {
                        outline: none;
                        border-color: #667eea;
                        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                    }

                    .status-select option {
                        padding: 10px;
                    }

                    /* 狀態顏色標識 */
                    .status-select[data-value="ATTEND"] {
                        border-color: #48bb78;
                        background: rgba(72, 187, 120, 0.05);
                    }

                    .status-select[data-value="LEAVE"] {
                        border-color: #ed8936;
                        background: rgba(237, 137, 54, 0.05);
                    }

                    .status-select[data-value="ABSENT"] {
                        border-color: #f56565;
                        background: rgba(245, 101, 101, 0.05);
                    }

                    .status-select[data-value="UNMARKED"] {
                        border-color: #a0aec0;
                        background: rgba(160, 174, 192, 0.05);
                    }

                    .statistics {
                        display: flex;
                        justify-content: space-around;
                        background: rgba(102, 126, 234, 0.05);
                        border-radius: 12px;
                        padding: 1.5rem;
                        margin-bottom: 2rem;
                        flex-wrap: wrap;
                        gap: 1rem;
                    }

                    .stat-item {
                        text-align: center;
                        flex: 1;
                        min-width: 120px;
                    }

                    .stat-number {
                        font-size: 24px;
                        font-weight: 700;
                        color: #667eea;
                        display: block;
                    }

                    .stat-label {
                        font-size: 14px;
                        color: #718096;
                        margin-top: 0.5rem;
                    }

                    .submit-btn {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                        border: none;
                        border-radius: 12px;
                        padding: 15px 30px;
                        font-size: 18px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                        width: 100%;
                        max-width: 200px;
                        margin: 0 auto;
                        display: block;
                    }

                    .submit-btn:hover:not(:disabled) {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
                    }

                    .submit-btn:active {
                        transform: translateY(0);
                    }

                    .submit-btn:disabled {
                        background: #ccc;
                        cursor: not-allowed;
                        transform: none;
                        box-shadow: none;
                    }

                    /* 響應式設計 */
                    @media (max-width: 768px) {
                        body {
                            padding: 1rem;
                        }

                        .header {
                            padding: 1.5rem;
                        }

                        .header h2 {
                            font-size: 24px;
                        }

                        .content {
                            padding: 1rem;
                        }

                        .controls {
                            flex-direction: column;
                            align-items: stretch;
                        }

                        .attendance-table {
                            font-size: 14px;
                        }

                        .attendance-table th,
                        .attendance-table td {
                            padding: 0.75rem 0.5rem;
                        }

                        .statistics {
                            flex-direction: column;
                            gap: 0.5rem;
                        }

                        .stat-item {
                            min-width: auto;
                        }
                    }

                    /* 動畫效果 */
                    .attendance-table tr {
                        animation: slideIn 0.5s ease forwards;
                        opacity: 0;
                    }

                    @keyframes slideIn {
                        from {
                            opacity: 0;
                            transform: translateX(-20px);
                        }

                        to {
                            opacity: 1;
                            transform: translateX(0);
                        }
                    }

                    .attendance-table tr:nth-child(1) {
                        animation-delay: 0.1s;
                    }

                    .attendance-table tr:nth-child(2) {
                        animation-delay: 0.2s;
                    }

                    .attendance-table tr:nth-child(3) {
                        animation-delay: 0.3s;
                    }

                    .attendance-table tr:nth-child(4) {
                        animation-delay: 0.4s;
                    }

                    .attendance-table tr:nth-child(5) {
                        animation-delay: 0.5s;
                    }

                    .attendance-table tr:nth-child(6) {
                        animation-delay: 0.6s;
                    }

                    .attendance-table tr:nth-child(7) {
                        animation-delay: 0.7s;
                    }

                    .attendance-table tr:nth-child(8) {
                        animation-delay: 0.8s;
                    }

                    .attendance-table tr:nth-child(9) {
                        animation-delay: 0.9s;
                    }

                    .attendance-table tr:nth-child(10) {
                        animation-delay: 1.0s;
                    }

                    .hidden {
                        display: none !important;
                    }

                    .btn-loading {
                        width: 20px;
                        height: 20px;
                        border: 3px solid rgba(255, 255, 255, 0.3);
                        border-radius: 50%;
                        border-top-color: white;
                        animation: spin 1s ease-in-out infinite;
                    }
                </style>
            </head>

            <body>
                <input type="hidden" id="courseScheduleId" value="<%= request.getParameter("courseScheduleId") %>" />

                <div class="container">
                    <div class="header">
                        <h2>📋 點名系統</h2>
                        <div class="course-info" id="courseInfo" style="display: none;">
                            <p>
                                <strong>📚 課程：</strong><span id="courseName"></span><br />
                                <strong>⏰ 節次：</strong><span id="coursePeriod"></span>
                            </p>
                        </div>
                    </div>

                    <div class="content">
                        <!-- Loading Section -->
                        <div class="loading-section" id="loadingSection">
                            <div class="loading-icon"></div>
                            <p>載入點名資料中...</p>
                        </div>

                        <!-- Error Section -->
                        <div class="error-section hidden" id="errorSection">
                            <p id="errorMessage">載入失敗，請稍後再試</p>
                            <button class="retry-btn" onclick="loadAttendanceData()">🔄 重新載入</button>
                        </div>

                        <!-- No Students Section -->
                        <div class="no-students hidden" id="noStudentsSection">
                            <p>目前無學生名單</p>
                        </div>

                        <!-- Main Content Section -->
                        <div class="hidden" id="mainContentSection">
                            <div class="controls">
                                <button type="button" class="mark-all-btn" id="markAllBtn" onclick="markAllAttend()">
                                    ✓ 全部標記為出席
                                </button>
                            </div>

                            <table class="attendance-table">
                                <thead>
                                    <tr>
                                        <th>👤 學生姓名</th>
                                        <th>📊 出勤狀態</th>
                                    </tr>
                                </thead>
                                <tbody id="attendanceTableBody">
                                    <!-- 動態生成的學生列表 -->
                                </tbody>
                            </table>

                            <div class="statistics" id="statisticsSection">
                                <div class="stat-item">
                                    <span class="stat-number" id="totalCount">0</span>
                                    <div class="stat-label">應到人數</div>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-number" id="attendCount">0</span>
                                    <div class="stat-label">出席人數</div>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-number" id="absentCount">0</span>
                                    <div class="stat-label">未到人數</div>
                                </div>
                                <div class="stat-item">
                                    <span class="stat-number" id="attendanceRate">0%</span>
                                    <div class="stat-label">出席率</div>
                                </div>
                            </div>

                            <button type="button" class="submit-btn" id="submitBtn" onclick="submitAttendance()">
                                📤 送出點名結果
                            </button>
                        </div>
                    </div>
                </div>

                <script>
                    let attendanceData = [];
                    let courseScheduleId = null;

                    // 頁面載入完成後初始化
                    document.addEventListener('DOMContentLoaded', function () {
                        // 從URL獲取courseScheduleId
                        const urlParams = new URLSearchParams(window.location.search);
                        const courseScheduleId = urlParams.get('courseScheduleId');
                        console.log("從 URL 拿到 courseScheduleId =", courseScheduleId);

                        if (!courseScheduleId) {
                            showError('缺少課程ID參數');
                            return;
                        }

                        loadAttendanceData();
                    });

                    // 載入點名資料
                    async function loadAttendanceData() {
                        showSection('loadingSection');

                        try {
                            
                            const response = await fetch(`<%= request.getContextPath() %>/api/attendances/rollcall/list?courseScheduleId=${courseScheduleId}`, {
                                method: 'GET',
                                headers: {
                                    'Content-Type': 'application/json'
                                }
                            });
                            console.log(`<%= request.getContextPath() %>/api/attendances/rollcall/list?courseScheduleId=${courseScheduleId}`);


                            if (!response.ok) {
                                throw new Error(`HTTP error! status: \${response.status}`);
                            }

                            const data = await response.json();
                            console.log('載入的點名資料:', data);

                            if (Array.isArray(data)) {
                                attendanceData = data;
                                handleDataLoaded();
                            } else {
                                showError('查詢結果格式錯誤');
                            }

                        } catch (error) {
                            console.error('載入點名資料出錯:', error);
                            showError('連接服務器失敗，請檢查網絡連接或稍後再試');
                        }
                    }

                    // 處理數據載入完成
                    function handleDataLoaded() {
                        if (attendanceData && attendanceData.length > 0) {
                            renderAttendanceTable();
                            updateStatistics();
                            showSection('mainContentSection');
                        } else {
                            showSection('noStudentsSection');
                        }
                    }

                    // 更新課程資訊
                    function updateCourseInfo(data) {
                        if (data.courseInfo) {
                            document.getElementById('courseName').textContent = data.courseInfo.courseName;
                            document.getElementById('coursePeriod').textContent = `第 \${data.courseInfo.periodStart} - \${data.courseInfo.periodEnd} 節`;
                            document.getElementById('courseInfo').style.display = 'block';
                        }
                    }

                    // 渲染出席表格
                    function renderAttendanceTable() {
                        const tbody = document.getElementById('attendanceTableBody');
                        tbody.innerHTML = '';

                        attendanceData.forEach(attendance => {
                            const row = createAttendanceRow(attendance);
                            tbody.appendChild(row);
                        });

                        // 為選擇框添加事件監聽器
                        addSelectEventListeners();
                    }

                    // 創建出席記錄行
                    function createAttendanceRow(attendance) {
                        const row = document.createElement('tr');

                        row.innerHTML = `
                <td class="student-name">\${escapeHtml(attendance.studentName)}</td>
                <td>
                    <select class="status-select" data-student-id="\${attendance.studentId}" data-value="\${attendance.status}">
                        <option value="ATTEND" \${attendance.status === 'ATTEND' ? 'selected' : ''}>✅ 出席</option>
                        <option value="LEAVE" \${attendance.status === 'LEAVE' ? 'selected' : ''}>🏥 請假</option>
                        <option value="ABSENT" \${attendance.status === 'ABSENT' ? 'selected' : ''}>❌ 缺席</option>
                        <option value="UNMARKED" \${attendance.status === 'UNMARKED' ? 'selected' : ''}>⭕ 未標記</option>
                    </select>
                </td>
            `;

                        return row;
                    }

                    // 為選擇框添加事件監聽器
                    function addSelectEventListeners() {
                        const selects = document.querySelectorAll('.status-select');
                        selects.forEach(select => {
                            select.addEventListener('change', function () {
                                // 更新數據值屬性
                                this.setAttribute('data-value', this.value);

                                // 更新統計數據
                                updateStatistics();
                            });
                        });
                    }

                    // 一鍵標記為出席
                    function markAllAttend() {
                        const selects = document.querySelectorAll(".status-select");
                        selects.forEach(sel => {
                            sel.value = 'ATTEND';
                            sel.setAttribute('data-value', 'ATTEND');
                            sel.dispatchEvent(new Event('change'));
                        });

                        // 添加視覺反饋
                        const btn = document.getElementById('markAllBtn');
                        const originalText = btn.textContent;
                        btn.textContent = '✓ 已全部標記';
                        btn.style.background = 'linear-gradient(135deg, #48bb78, #38a169)';

                        setTimeout(() => {
                            btn.textContent = originalText;
                            btn.style.background = '';
                        }, 2000);

                        updateStatistics();
                    }

                    // 更新統計數據
                    function updateStatistics() {
                        const selects = document.querySelectorAll('.status-select');
                        let total = selects.length;
                        let attendCount = 0;
                        let absentCount = 0;

                        selects.forEach(select => {
                            const status = select.value;
                            if (status === 'ATTEND') {
                                attendCount++;
                            } else if (status === 'ABSENT' || status === 'UNMARKED') {
                                absentCount++;
                            }
                        });

                        const attendanceRate = total > 0 ? Math.round((attendCount / total) * 100) : 0;

                        document.getElementById('totalCount').textContent = total;
                        document.getElementById('attendCount').textContent = attendCount;
                        document.getElementById('absentCount').textContent = absentCount;
                        document.getElementById('attendanceRate').textContent = attendanceRate + '%';
                    }

                    // 提交出席記錄
                    async function submitAttendance() {
                        // 顯示確認對話框
                        const result = await Swal.fire({
                            title: '確定要送出點名結果嗎？',
                            icon: 'question',
                            showCancelButton: true,
                            confirmButtonText: '✅ 送出',
                            cancelButtonText: '取消',
                            customClass: {
                                title: 'swal2-title-center'
                            }
                        });

                        if (!result.isConfirmed) {
                            return;
                        }

                        const submitBtn = document.getElementById('submitBtn');
                        const originalText = submitBtn.innerHTML;

                        // 顯示載入狀態
                        submitBtn.innerHTML = '<div class="btn-loading"></div> 送出中...';
                        submitBtn.disabled = true;

                        try {
                            // 收集表單數據
                            const courseScheduleId = parseInt(document.getElementById("courseScheduleId").value);
                            const rollCallMap = {};

                            document.querySelectorAll(".attendance-select").forEach(select => {
                                const attendanceId = parseInt(select.dataset.attendanceId);
                                const status = select.value;
                                rollCallMap[attendanceId] = status;
                            });

                            const payload = {
                                courseScheduleId: courseScheduleId,
                                rollCallMap: rollCallMap
                            };

                                                const response = await fetch('${pageContext.request.contextPath}/api/attendances/rollcall', {
                                                    method: 'POST',
                                                    headers: {
                                    'Content-Type': 'application/json'
                                },
                                body: JSON.stringify(payload)
                            });

                            if (!response.ok) {
                                throw new Error(`HTTP error! status: \${response.status}`);
                            }

                            const data = await response.json();

                            if (data.success) {
                                await Swal.fire({
                                    title: '送出成功！',
                                    text: '點名結果已成功更新',
                                    icon: 'success',
                                    confirmButtonText: '確定'
                                });

                                // 可以選擇跳轉到其他頁面或重新載入
                                // window.location.href = '/teacher-dashboard';
                            } else {
                                throw new Error(data.message || '送出失敗');
                            }
                        } catch (error) {
                            console.error('提交出席記錄出錯:', error);
                            await Swal.fire({
                                title: '送出失敗',
                                text: error.message || '請稍後再試',
                                icon: 'error',
                                confirmButtonText: '確定'
                            });
                        } finally {
                            // 恢復按鈕狀態
                            submitBtn.innerHTML = originalText;
                            submitBtn.disabled = false;
                        }
                    }

                    // 顯示指定的區塊
                    function showSection(sectionId) {
                        const sections = ['loadingSection', 'errorSection', 'noStudentsSection', 'mainContentSection'];

                        sections.forEach(id => {
                            const element = document.getElementById(id);
                            if (id === sectionId) {
                                element.classList.remove('hidden');
                            } else {
                                element.classList.add('hidden');
                            }
                        });
                    }

                    // 顯示錯誤
                    function showError(message) {
                        document.getElementById('errorMessage').textContent = message;
                        showSection('errorSection');
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