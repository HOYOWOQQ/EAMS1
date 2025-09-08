<%@ page contentType="text/html; charset=UTF-8" %>
    <% String path=request.getContextPath(); %>
        <!DOCTYPE html>
        <html lang="zh-TW">

        <head>
            <meta charset="UTF-8">
            <title>課程報名</title>
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

                .search-fields {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                    gap: 1rem;
                    margin-bottom: 1rem;
                }

                .search-actions {
                    display: flex;
                    gap: 12px;
                    justify-content: flex-start;
                }

                .search-result {
                    margin-top: 1rem;
                    padding: 12px 16px;
                    background: rgba(102, 126, 234, 0.05);
                    border-radius: 8px;
                    color: #666;
                    font-size: 14px;
                    font-weight: 500;
                }

                /* Form Controls */
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
                    padding: 10px 20px;
                    border: none;
                    border-radius: 8px;
                    font-size: 14px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    display: flex;
                    align-items: center;
                    gap: 6px;
                }

                .btn:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                }

                .btn-primary {
                    background: #667eea;
                    color: white;
                }

                .btn-secondary {
                    background: #6c757d;
                    color: white;
                }

                .btn-success {
                    background: #28a745;
                    color: white;
                }

                .btn-danger {
                    background: #dc3545;
                    color: white;
                }

                .btn-sm {
                    padding: 6px 12px;
                    font-size: 12px;
                }

                /* Table Section */
                .table-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(10px);
                    border-radius: 16px;
                    overflow: hidden;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
                    margin: 20px auto;
                    max-width: 1400px;
                    border: 1px solid rgba(255, 255, 255, 0.2);
                }

                .course-table {
                    width: 100%;
                    border-collapse: collapse;
                    font-size: 16px;
                }

                .course-table th {
                    background: linear-gradient(135deg, #667eea, #764ba2);
                    color: white;
                    padding: 20px 16px;
                    text-align: left;
                    font-weight: 600;
                    font-size: 15px;
                    position: relative;
                    border: none;
                }

                /* 表頭圖標 */
                .course-table th::before {
                    margin-right: 8px;
                    font-size: 18px;
                }

                .course-table th:nth-child(1)::before {
                    content: "📖";
                }

                .course-table th:nth-child(2)::before {
                    content: "👨‍🏫";
                }

                .course-table th:nth-child(3)::before {
                    content: "👥";
                }

                .course-table th:nth-child(4)::before {
                    content: "💰";
                }

                .course-table th:nth-child(5)::before {
                    content: "📅";
                }

                .course-table th:nth-child(6)::before {
                    content: "🎯";
                }

                /* 表頭分隔線 */
                .course-table th:not(:last-child)::after {
                    content: '';
                    position: absolute;
                    right: 0;
                    top: 25%;
                    height: 50%;
                    width: 1px;
                    background: rgba(255, 255, 255, 0.3);
                }

                .course-table td {
                    padding: 18px 16px;
                    border-bottom: 1px solid #e8ecf0;
                    vertical-align: middle;
                    color: #2c3e50;
                    line-height: 1.6;
                }

                .course-table tbody tr {
                    transition: all 0.3s ease;
                    background: white;
                }

                .course-table tbody tr:hover {
                    background: linear-gradient(90deg, #f8f9ff 0%, #fff 100%);
                    transform: translateY(-2px);
                    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
                }

                .course-table tbody tr:nth-child(even) {
                    background: #fafbfc;
                }

                .course-table tbody tr:nth-child(even):hover {
                    background: linear-gradient(90deg, #f0f2ff 0%, #fafbfc 100%);
                }

                .course-table tbody tr:last-child td {
                    border-bottom: none;
                }

                /* 課程名稱樣式 */
                .course-table td:first-child {
                    font-weight: 600;
                    color: #3498db;
                    font-size: 16px;
                }

                /* 教師名稱樣式 */
                .course-table td:nth-child(2) {
                    color: #8e44ad;
                    font-weight: 500;
                }

                /* 剩餘名額樣式 */
                .course-table td:nth-child(3) {
                    text-align: center;
                    font-weight: 600;
                }

                /* 價格樣式 */
                .course-table td:nth-child(4) {
                    color: #e74c3c;
                    font-weight: 700;
                    font-size: 16px;
                }

                /* 日期樣式 */
                .course-table td:nth-child(5) {
                    font-family: 'Courier New', monospace;
                    font-size: 14px;
                    color: #34495e;
                    white-space: nowrap;
                }

                /* 操作按鈕容器 */
                .course-table td:last-child {
                    text-align: center;
                    padding: 12px 16px;
                }

                /* 按鈕基本樣式 */
                .course-table button {
                    padding: 10px 20px;
                    border: none;
                    border-radius: 8px;
                    font-size: 14px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    min-width: 80px;
                    position: relative;
                    overflow: hidden;
                }

                .course-table button::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: -100%;
                    width: 100%;
                    height: 100%;
                    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
                    transition: left 0.5s;
                }

                .course-table button:hover::before {
                    left: 100%;
                }

                /* 報名按鈕 */
                .enroll-btn {
                    background: linear-gradient(135deg, #28a745, #20c997);
                    color: white;
                }

                .enroll-btn:hover {
                    background: linear-gradient(135deg, #218838, #1ea085);
                    transform: translateY(-2px);
                    box-shadow: 0 6px 20px rgba(40, 167, 69, 0.3);
                }

                /* 已滿按鈕 */
                .full-btn {
                    background: linear-gradient(135deg, #dc3545, #c82333);
                    color: white;
                    cursor: not-allowed;
                }

                .full-btn:disabled {
                    opacity: 0.7;
                }

                /* 已報名按鈕 */
                .enrolled-btn {
                    background: linear-gradient(135deg, #6c757d, #5a6268);
                    color: white;
                    cursor: not-allowed;
                }

                .enrolled-btn:disabled {
                    opacity: 0.8;
                }

                /* 空結果樣式 */
                .no-results {
                    text-align: center;
                    padding: 4rem 2rem;
                    color: #7f8c8d;
                    font-size: 18px;
                    background: white;
                }

                .no-results::before {
                    content: "📚";
                    display: block;
                    font-size: 64px;
                    margin-bottom: 20px;
                    opacity: 0.7;
                }

                /* 名額顯示樣式 */
                .quota-high {
                    color: #28a745;
                    font-weight: 700;
                }

                .quota-medium {
                    color: #ffc107;
                    font-weight: 700;
                }

                .quota-low {
                    color: #dc3545;
                    font-weight: 700;
                }

                .quota-full {
                    color: #6c757d;
                    font-weight: 700;
                }

                /* 響應式設計 */
                @media (max-width: 1024px) {
                    .course-table {
                        font-size: 14px;
                    }

                    .course-table th,
                    .course-table td {
                        padding: 14px 12px;
                    }
                }

                @media (max-width: 768px) {
                    .table-section {
                        margin: 10px;
                        border-radius: 12px;
                    }

                    .course-table {
                        font-size: 13px;
                    }

                    .course-table th,
                    .course-table td {
                        padding: 12px 8px;
                    }

                    .course-table button {
                        padding: 8px 16px;
                        font-size: 12px;
                        min-width: 70px;
                    }

                    .course-table td:nth-child(5) {
                        font-size: 12px;
                    }
                }

                @media (max-width: 480px) {
                    .course-table th::before {
                        display: none;
                    }

                    .course-table th {
                        font-size: 13px;
                        padding: 12px 6px;
                    }

                    .course-table td {
                        padding: 10px 6px;
                        font-size: 12px;
                    }

                    .course-table td:nth-child(5) {
                        white-space: normal;
                        font-size: 11px;
                    }
                }

                /* 載入動畫 */
                .course-table tbody {
                    animation: fadeInUp 0.6s ease-out;
                }

                @keyframes fadeInUp {
                    from {
                        opacity: 0;
                        transform: translateY(30px);
                    }

                    to {
                        opacity: 1;
                        transform: translateY(0);
                    }
                }

                /* 滾動條美化 */
                .table-section::-webkit-scrollbar {
                    height: 8px;
                }

                .table-section::-webkit-scrollbar-track {
                    background: rgba(255, 255, 255, 0.1);
                }

                .table-section::-webkit-scrollbar-thumb {
                    background: rgba(102, 126, 234, 0.3);
                    border-radius: 4px;
                }

                .table-section::-webkit-scrollbar-thumb:hover {
                    background: rgba(102, 126, 234, 0.5);
                }

                /* Modal Styles */
                .modal {
                    display: none;
                    position: fixed;
                    z-index: 1000;
                    left: 0;
                    top: 0;
                    width: 100%;
                    height: 100%;
                    background-color: rgba(0, 0, 0, 0.5);
                    backdrop-filter: blur(5px);
                    justify-content: center;
                    align-items: center;
                    animation: fadeIn 0.3s ease;
                }

                .modal-content {
                    background: white;
                    margin: 5% auto;
                    padding: 2rem;
                    border-radius: 20px;
                    width: 90%;
                    max-width: 600px;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
                    position: relative;
                    animation: slideUp 0.3s ease;
                    max-height: 90vh;
                    /* 新增：限制最大高度 */
                    overflow-y: auto;
                    /* 新增：內容超出時出現垂直捲軸 */
                }

                .modal-content.large {
                    max-width: 1200px;
                }

                @keyframes fadeIn {
                    from {
                        opacity: 0;
                    }

                    to {
                        opacity: 1;
                    }
                }

                @keyframes slideUp {
                    from {
                        transform: translateY(50px);
                        opacity: 0;
                    }

                    to {
                        transform: translateY(0);
                        opacity: 1;
                    }
                }

                .modal-close {
                    position: absolute;
                    right: 20px;
                    top: 15px;
                    font-size: 28px;
                    cursor: pointer;
                    color: #666;
                    transition: color 0.3s ease;
                }

                .modal-close:hover {
                    color: #dc3545;
                }

                .modal-icon {
                    margin-bottom: 1rem;
                    display: flex;
                    justify-content: center;
                }

                .modal-title {
                    text-align: center;
                    width: 100%;
                    font-size: 1.5rem;
                    font-weight: 700;
                    color: #333;
                    margin-bottom: 1.5rem;
                }

                .modal-actions {
                    display: flex;
                    justify-content: center;
                    gap: 12px;
                    margin-top: 1.5rem;
                }

                .modal-table-container {
                    max-height: 400px;
                    overflow-y: auto;
                    margin-bottom: 1rem;
                    border-radius: 12px;
                    border: 1px solid #e1e5e9;
                }

                /* Status Badges */
                .status-badge {
                    display: inline-block;
                    padding: 4px 12px;
                    border-radius: 12px;
                    font-size: 12px;
                    font-weight: 600;
                    text-transform: uppercase;
                }

                .status-active {
                    background: #d4edda;
                    color: #155724;
                }

                .status-pending {
                    background: #fff3cd;
                    color: #856404;
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

                    .search-fields {
                        grid-template-columns: 1fr;
                    }

                    .search-actions {
                        justify-content: center;
                    }

                    .course-table th,
                    .course-table td {
                        padding: 12px 8px;
                        font-size: 14px;
                    }

                    .modal-content {
                        margin: 20px;
                        width: calc(100% - 40px);
                        max-width: none;
                    }

                    .page-title {
                        font-size: 1.5rem;
                    }
                }

                /* Loading */
                .loading {
                    display: inline-block;
                    width: 20px;
                    height: 20px;
                    border: 3px solid rgba(102, 126, 234, 0.3);
                    border-radius: 50%;
                    border-top-color: #667eea;
                    animation: spin 1s ease-in-out infinite;
                }

                @keyframes spin {
                    to {
                        transform: rotate(360deg);
                    }
                }
            </style>
            <!-- SweetAlert2 CSS -->
            <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css" rel="stylesheet">
            <!-- SweetAlert2 JS -->
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        </head>

        <body>
            <div class="container">
                <!-- Header -->
                <div class="header-section">
                    <h1 class="page-title">📚 可報名課程列表</h1>
                    <div class="header-actions">
                        <button class="btn btn-primary" onclick="showMyEnrollModal()">📋 已報名課程</button>
                    </div>
                </div>

                <!-- Search Section -->
                <div class="search-section">
                    <h3 class="search-title">🔍 課程搜尋</h3>
                    <div class="search-fields">
                        <div class="form-group">
                            <label class="form-label">課程名稱</label>
                            <input type="text" id="searchKeyword" class="form-control" placeholder="搜尋課程名稱...">
                        </div>
                        <div class="form-group">
                            <label class="form-label">科目</label>
                            <select id="subjectFilter" class="form-control">
                                <option value="">所有科目</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">價格範圍</label>
                            <select id="priceFilter" class="form-control">
                                <option value="">不限價格</option>
                                <option value="0-5000">5000以下</option>
                                <option value="5000-10000">5000-10000</option>
                                <option value="10000-">10000以上</option>
                            </select>
                        </div>
                    </div>
                    <div class="search-actions">
                        <button class="btn btn-primary" onclick="searchCourses()">🔍 搜尋</button>
                        <button class="btn btn-secondary" onclick="clearFilters()">🔄 清除</button>
                    </div>
                    <div id="searchResult" class="search-result" style="display: none;"></div>
                </div>

                <!-- Course Table -->
                <div class="table-section">
                    <table id="courseTable" class="course-table">
                        <thead>
                            <tr>
                                <th>課程名稱</th>
                                <th>授課老師</th>
                                <th>剩餘名額</th>
                                <th>價格</th>
                                <th>課程日期</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>

            <!-- 報名確認 Modal -->
            <div id="enrollModal" class="modal">
                <div class="modal-content">
                    <span class="modal-close" onclick="hideEnrollModal()">&times;</span>
                    <div class="modal-icon">
                        <svg width="48" height="48" fill="none" viewBox="0 0 48 48">
                            <circle cx="24" cy="24" r="22" fill="#667eea" opacity="0.1" />
                            <path d="M24 14v12" stroke="#667eea" stroke-width="3" stroke-linecap="round" />
                            <circle cx="24" cy="32" r="2" fill="#667eea" />
                        </svg>
                    </div>
                    <h3 class="modal-title">確定要報名這門課程嗎？</h3>
                    <div class="modal-actions">
                        <button class="btn btn-primary" onclick="submitEnroll()">✅ 確定</button>
                        <button class="btn btn-secondary" onclick="hideEnrollModal()">❌ 取消</button>
                    </div>
                </div>
            </div>

            <!-- 我的報名課程 Modal -->
            <div id="myEnrollModal" class="modal">
                <div class="modal-content large">
                    <span class="modal-close" onclick="hideMyEnrollModal()">&times;</span>
                    <div class="modal-icon">
                        <svg width="48" height="48" fill="none" viewBox="0 0 48 48">
                            <circle cx="24" cy="24" r="22" fill="#667eea" opacity="0.1" />
                            <rect x="14" y="18" width="20" height="3" rx="1.5" fill="#667eea" />
                            <rect x="14" y="25" width="20" height="3" rx="1.5" fill="#667eea" />
                        </svg>
                    </div>
                    <h3 class="modal-title">我的報名課程</h3>
                    <div class="modal-table-container">
                        <table id="myEnrollTable" class="course-table">
                            <thead>
                                <tr>
                                    <th>課程名稱</th>
                                    <th>授課老師</th>
                                    <th>課程日期</th>
                                    <th>價格</th>
                                    <th>狀態</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                    <div class="modal-actions">
                        <button class="btn btn-primary" onclick="hideMyEnrollModal()">關閉</button>
                    </div>
                </div>
            </div>

            <!-- 取消報名確認 Modal -->
            <div id="cancelEnrollModal" class="modal">
                <div class="modal-content">
                    <span class="modal-close" onclick="hideCancelEnrollModal()">&times;</span>
                    <div class="modal-icon">
                        <svg width="48" height="48" fill="none" viewBox="0 0 48 48">
                            <circle cx="24" cy="24" r="22" fill="#dc3545" opacity="0.12" />
                            <path d="M24 14v12" stroke="#dc3545" stroke-width="3" stroke-linecap="round" />
                            <circle cx="24" cy="32" r="2" fill="#dc3545" />
                        </svg>
                    </div>
                    <h3 class="modal-title" style="color:#dc3545;">確定要取消報名這門課程嗎？</h3>
                    <div class="modal-actions">
                        <button class="btn btn-danger" id="cancelEnrollConfirmBtn">✅ 確定</button>
                        <button class="btn btn-secondary" onclick="hideCancelEnrollModal()">❌ 取消</button>
                    </div>
                </div>
            </div>

            <script>
                let courseList = [];
                let enrollCourseId = null;
                let cancelCourseId = null;
                let allCourses = [];
                let filteredCourses = [];
                let enrolledCourseIds = [];

                // 頁面載入時，取得課程列表
                async function loadCourseList() {
                    await loadEnrolledCourseIds();
                    const res = await fetch('${pageContext.request.contextPath}/api/course/all').then(r => r.json());
                    const courses = res.data || [];
                    console.log('載入課程列表:', courses);
                    allCourses = courses;
                    filteredCourses = courses;
                    await renderCourseTable(courses);
                    loadSubjectOptions();
                }

                // 取得已報名課程的 ID
                async function loadEnrolledCourseIds() {
                    try {
                        const res = await fetch('${pageContext.request.contextPath}/api/course/ByStudentId').then(res => res.json());
                        const list = res.data || [];
                        console.log('已報名課程列表:', list);
                        enrolledCourseIds = list.map(c => c.id);
                    } catch (e) {
                        enrolledCourseIds = [];
                    }
                }

                // 載入列表
                async function renderCourseTable(courses) {
                    console.log('renderCourseTable 收到的參數:', courses);
                    const tbody = document.querySelector('#courseTable tbody');
                    tbody.innerHTML = '';
                    if (courses.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="6" class="no-results">🔍 沒有找到符合條件的課程</td></tr>';
                        return;
                    }

                    for (const c of courses) {
                        console.log('處理課程:', c);
                        // 取得老師
                        const teachersRes = await fetch('${pageContext.request.contextPath}/api/course/teacherByCourse?courseId=' + c.id).then(r => r.json());
                        const teachers = teachersRes.data || [];
                        console.log('課程ID', c.id, '老師:', teachers);
                        // 取得名額
                        const quotaRes = await fetch('${pageContext.request.contextPath}/api/course/quota?courseId=' + c.id).then(r => r.json());
                        const quota = quotaRes.data || { quota: 0, enrolled: 0 };
                        console.log('課程ID', c.id, '名額:', quota);
                        const isEnrolled = enrolledCourseIds.includes(c.id);

                        const remainingQuota = quota.quota - quota.enrolled;
                        const quotaDisplay = remainingQuota > 0 ? remainingQuota : '已額滿';
                        const buttonDisabled = remainingQuota <= 0 || isEnrolled;
                        const buttonText = isEnrolled ? '已報名' : remainingQuota <= 0 ? '已額滿' : '報名';
                        const buttonClass = isEnrolled ? 'btn btn-secondary btn-sm' : remainingQuota <= 0 ? 'btn btn-secondary btn-sm' : 'btn btn-primary btn-sm';

                        tbody.innerHTML += `
                <tr>
                    <td>\${c.name}</td>
                    <td>\${teachers.length ? teachers.map(t => t.name).join('、') : '無'}</td>
                    <td><span class="quota-low">\${quotaDisplay}</span></td>
                    <td>NT$ \${c.fee || '0'}</td>
                    <td>\${c.startDate || ''} ~ \${c.endDate || ''}</td>
                    <td>
                        <button class="\${buttonClass}" onclick="showEnrollModal(\${c.id})" \${buttonDisabled ? 'disabled' : ''}>\${buttonText}</button>
                    </td>
                </tr>
                `;
                    }
                }

                // 載入科目選項
                async function loadSubjectOptions() {
                    try {
                        const response = await fetch('${pageContext.request.contextPath}/api/course/subject/all').then(res => res.json());
                        const subjects = response.data || [];
                        console.log('載入科目選項:', subjects);
                        const subjectSelect = document.getElementById('subjectFilter');
                        subjectSelect.innerHTML = '<option value="">所有科目</option>';
                        subjects.forEach(data => {
                            subjectSelect.innerHTML += `<option value="\${data.id}">\${data.name}</option>`;
                        });
                    } catch (error) {
                        console.error('載入科目選項失敗:', error);
                    }
                }

                // 搜尋函數
                async function searchCourses() {
                    const keyword = document.getElementById('searchKeyword').value.trim();
                    const subjectId = document.getElementById('subjectFilter').value;
                    const priceRange = document.getElementById('priceFilter').value;

                    console.log('搜尋條件:', { keyword, subjectId, priceRange });

                    let filtered = allCourses.filter(course => {
                        if (keyword && !course.name.toLowerCase().includes(keyword.toLowerCase())) {
                            return false;
                        }
                        if (subjectId && !course.subjectIds.includes(Number(subjectId))) {
                            return false;
                        }
                        if (priceRange) {
                            const fee = parseInt(course.fee) || 0;
                            if (priceRange === '0-5000' && fee > 5000) return false;
                            if (priceRange === '5000-10000' && (fee < 5000 || fee > 10000)) return false;
                            if (priceRange === '10000-' && fee < 10000) return false;
                        }
                        return true;
                    });

                    filteredCourses = filtered;
                    await renderCourseTable(filtered);

                    const resultDiv = document.getElementById('searchResult');
                    if (resultDiv) {
                        resultDiv.style.display = 'block';
                        resultDiv.innerHTML = `📊 找到 \${filtered.length} 門課程（共 \${allCourses.length} 門）`;
                    }
                }

                // 清除搜尋條件
                function clearFilters() {
                    const elements = ['searchKeyword', 'subjectFilter', 'priceFilter'];
                    elements.forEach(id => {
                        const element = document.getElementById(id);
                        if (element) element.value = '';
                    });

                    const resultDiv = document.getElementById('searchResult');
                    if (resultDiv) resultDiv.style.display = 'none';

                    filteredCourses = allCourses;
                    renderCourseTable(allCourses);
                }

                // 顯示報名 Modal
                function showEnrollModal(courseId) {
                    enrollCourseId = courseId;
                    console.log('showEnrollModal called, courseId:', courseId);
                    document.getElementById('enrollModal').style.display = 'flex';
                }

                // 隱藏報名 Modal
                function hideEnrollModal() {
                    document.getElementById('enrollModal').style.display = 'none';
                }


                // 提交報名
                function submitEnroll() {
                    hideEnrollModal();

                    const courseEnroll = {
                        courseId: enrollCourseId
                    };

                    console.log("提交報名資料：", courseEnroll);

                    fetch('${pageContext.request.contextPath}/api/course/enroll/add', {
                        method: "POST",
                        headers: {
                            'Content-Type': 'application/json; charset=UTF-8'
                        },
                        body: JSON.stringify(courseEnroll)
                    })
                        .then(response => {
                            console.log('Response status:', response.status);
                            return response.text(); // 改為 text() 因為後端回傳字串
                        })
                        .then(message => {
                            console.log("報名回傳：", message);
                            // 根據訊息內容判斷成功或失敗
                            const isSuccess = message.includes('成功報名課程');

                            Swal.fire({
                                title: isSuccess ? '報名成功！' : '報名失敗',
                                text: message.message,
                                icon: isSuccess ? 'success' : 'error'
                            });

                            if (isSuccess) {
                                loadCourseList(); // 重新載入課程列表
                            }
                        })
                        .catch(error => {
                            console.error('報名錯誤:', error);
                            Swal.fire({
                                title: '報名失敗',
                                text: error.message || '請稍後再試',
                                icon: 'error'
                            });
                        });
                }


                // 顯示我的報名課程 Modal
                async function showMyEnrollModal() {
                    document.getElementById('myEnrollModal').style.display = 'flex';
                    const response = await fetch('${pageContext.request.contextPath}/api/course/ByStudentId').then(res => res.json());
                    const list = response.data || [];
                    console.log('我的報名課程：', list);
                    const tbody = document.querySelector('#myEnrollTable tbody');
                    tbody.innerHTML = '';
                    if (list.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="6" class="no-results">📝 尚未報名任何課程</td></tr>';
                    } else {
                        for (const c of list) {
                            const res = await fetch('${pageContext.request.contextPath}/api/course/teacherByCourse?courseId=' + c.id).then(r => r.json());
                            const teachers = res.data || [];
                            tbody.innerHTML += `
                    <tr>
                        <td>\${c.name}</td>
                        <td>\${teachers.length ? teachers.map(t => t.name).join('、') : '無'}</td>
                        <td>\${c.startDate || ''} ~ \${c.endDate || ''}</td>
                        <td>NT$ \${c.fee || '0'}</td>
                        <td><span class="status-badge status-active">\${c.status || 'Active'}</span></td>
                        <td><button class="btn btn-danger btn-sm" onclick="showCancelEnrollModal(\${c.id})">取消報名</button></td>
                    </tr>
                    `;
                        }
                    }
                }

                function hideMyEnrollModal() {
                    document.getElementById('myEnrollModal').style.display = 'none';
                }

                // 顯示取消報名 Modal
                function showCancelEnrollModal(courseId) {
                    cancelCourseId = courseId;
                    document.getElementById('cancelEnrollModal').style.display = 'flex';
                }

                // 隱藏取消報名 Modal
                function hideCancelEnrollModal() {
                    document.getElementById('cancelEnrollModal').style.display = 'none';
                    cancelCourseId = null;
                }

                // 綁定確定按鈕事件
                document.getElementById('cancelEnrollConfirmBtn').onclick = function () {
                    if (!cancelCourseId) return;
                    doCancelEnroll(cancelCourseId);
                    hideCancelEnrollModal();
                };


                // 執行取消報名
                function doCancelEnroll(courseId) {
                    console.log('doCancelEnroll called, courseId:', courseId);
                    const bodyData = `courseId=\${encodeURIComponent(courseId)}`;
                    console.log("送出的 body 是：", bodyData);

                    fetch('${pageContext.request.contextPath}/api/course/courseEnroll/delete', {
                        method: 'DELETE',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: bodyData
                    })
                        .then(response => {
                            console.log('Response status:', response.status);
                            return response.text(); // 改為 text() 因為後端回傳字串
                        })
                        .then(message => {
                            console.log('取消報名回傳：', message);
                            // 根據訊息內容判斷成功或失敗
                            const isSuccess = message.includes('刪除成功') || response.status === 200;

                            Swal.fire({
                                title: isSuccess ? '取消成功！' : '取消失敗',
                                text: message.message,
                                icon: isSuccess ? 'success' : 'error'
                            });

                            if (isSuccess) {
                                showMyEnrollModal(); // 重新載入我的報名課程
                                loadCourseList();    // 重新載入課程列表
                            }
                        })
                        .catch(error => {
                            console.error('取消報名錯誤:', error);
                            Swal.fire({
                                title: '取消報名失敗',
                                text: error.message || '請稍後再試',
                                icon: 'error'
                            });
                        });
                }


                // 頁面載入時自動載入課程列表
                document.addEventListener('DOMContentLoaded', () => {
                    loadCourseList();
                });
            </script>
        </body>

        </html>