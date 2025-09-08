<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <% String contextPath=request.getContextPath(); %>
            <% if (session.getAttribute("id")==null) { response.sendRedirect(request.getContextPath() + "/login" );
                return; } %>

                <!DOCTYPE html>
                <html lang="zh-TW">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>會員管理</title>
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
                            border-radius: 20px 20px 0 0;
                            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                            padding: 32px;
                            margin-bottom: 2px;
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

                        .header-actions {
                            display: flex;
                            gap: 12px;
                            align-items: center;
                        }

                        .search-container {
                            background: #fff;
                            border-radius: 0 0 20px 20px;
                            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                            padding: 24px 32px;
                            margin-bottom: 20px;
                        }

                        .search-form {
                            display: flex;
                            flex-wrap: wrap;
                            gap: 16px;
                            align-items: end;
                        }

                        .search-field {
                            display: flex;
                            flex-direction: column;
                            gap: 6px;
                            min-width: 120px;
                        }

                        .search-label {
                            font-weight: 600;
                            color: #374151;
                            font-size: 0.9rem;
                        }

                        .search-input,
                        .search-select {
                            background: #fff;
                            border: 2px solid #e2e8f0;
                            border-radius: 8px;
                            padding: 10px 12px;
                            font-size: 0.95rem;
                            color: #1e293b;
                            transition: all 0.3s;
                            outline: none;
                        }

                        .search-input:focus,
                        .search-select:focus {
                            border-color: #4e8cff;
                            box-shadow: 0 0 0 3px rgba(78, 140, 255, 0.1);
                        }

                        .btn {
                            padding: 10px 20px;
                            border: none;
                            border-radius: 8px;
                            font-size: 0.95rem;
                            font-weight: 600;
                            cursor: pointer;
                            transition: all 0.3s;
                            text-decoration: none;
                            display: inline-flex;
                            align-items: center;
                            gap: 6px;
                            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                            white-space: nowrap;
                        }

                        .btn-primary {
                            background: linear-gradient(135deg, #4e8cff 0%, #2563eb 100%);
                            color: white;
                        }

                        .btn-primary:hover {
                            background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
                            transform: translateY(-1px);
                            box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
                        }

                        .btn-success {
                            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                            color: white;
                        }

                        .btn-success:hover {
                            background: linear-gradient(135deg, #059669 0%, #047857 100%);
                            transform: translateY(-1px);
                            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
                        }

                        .btn-danger {
                            background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
                            color: white;
                        }

                        .btn-danger:hover {
                            background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
                            transform: translateY(-1px);
                            box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
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

                        /* 新增：獨立的檢視按鈕樣式 */
                        .btn-view {
                            background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
                            color: white;
                            border: none;
                        }

                        .btn-view:hover {
                            background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%);
                            transform: translateY(-1px);
                            box-shadow: 0 4px 12px rgba(139, 92, 246, 0.35);
                        }

                        .btn-view:active {
                            transform: translateY(0);
                            box-shadow: 0 2px 6px rgba(139, 92, 246, 0.25);
                        }

                        /* 檢視按鈕在深色主題下的變化 */
                        .btn-view:focus {
                            outline: none;
                            box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.2);
                        }

                        .btn-sm {
                            padding: 6px 12px;
                            font-size: 0.85rem;
                        }

                        .table-container {
                            background: #fff;
                            border-radius: 20px;
                            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                            overflow: hidden;
                            margin-bottom: 20px;
                        }

                        .table {
                            width: 100%;
                            border-collapse: collapse;
                            margin: 0;
                        }

                        .table th {
                            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
                            color: #374151;
                            font-weight: 600;
                            text-align: left;
                            padding: 16px 20px;
                            border-bottom: 2px solid #e2e8f0;
                            position: sticky;
                            top: 0;
                            z-index: 10;
                        }

                        .table td {
                            padding: 16px 20px;
                            border-bottom: 1px solid #f1f5f9;
                            vertical-align: middle;
                        }

                        .table tr:hover {
                            background: rgba(78, 140, 255, 0.02);
                        }

                        .table tr:last-child td {
                            border-bottom: none;
                        }

                        .status-badge {
                            display: inline-flex;
                            align-items: center;
                            gap: 6px;
                            padding: 4px 12px;
                            border-radius: 20px;
                            font-size: 0.85rem;
                            font-weight: 500;
                        }

                        .status-active {
                            background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
                            color: #065f46;
                        }

                        .status-inactive {
                            background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
                            color: #991b1b;
                        }

                        .status-verified {
                            background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
                            color: #1e3a8a;
                        }

                        .status-unverified {
                            background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
                            color: #92400e;
                        }

                        .role-badge {
                            display: inline-flex;
                            align-items: center;
                            gap: 6px;
                            padding: 4px 12px;
                            border-radius: 20px;
                            font-size: 0.85rem;
                            font-weight: 500;
                        }

                        .role-student {
                            background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
                            color: #6b21a8;
                        }

                        .role-teacher {
                            background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
                            color: #14532d;
                        }

                        .actions {
                            display: flex;
                            gap: 8px;
                            align-items: center;
                        }

                        .empty-state {
                            text-align: center;
                            padding: 60px 20px;
                            color: #64748b;
                        }

                        .empty-state .icon {
                            font-size: 3rem;
                            margin-bottom: 16px;
                            opacity: 0.5;
                        }

                        .empty-state h3 {
                            font-size: 1.2rem;
                            color: #374151;
                            margin: 0 0 8px 0;
                        }

                        .empty-state p {
                            margin: 0;
                            font-size: 0.95rem;
                        }

                        .loading-state {
                            text-align: center;
                            padding: 40px 20px;
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

                        .error-state {
                            text-align: center;
                            padding: 40px 20px;
                            color: #ef4444;
                        }

                        @keyframes spin {
                            0% {
                                transform: rotate(0deg);
                            }

                            100% {
                                transform: rotate(360deg);
                            }
                        }

                        @media (max-width: 768px) {
                            body {
                                padding: 10px;
                            }

                            .page-header {
                                padding: 20px;
                                border-radius: 16px 16px 0 0;
                            }

                            .header-content {
                                flex-direction: column;
                                align-items: stretch;
                                gap: 16px;
                            }

                            .search-container {
                                padding: 20px;
                                border-radius: 0 0 16px 16px;
                            }

                            .search-form {
                                flex-direction: column;
                                align-items: stretch;
                            }

                            .search-field {
                                min-width: auto;
                            }

                            .table-container {
                                border-radius: 16px;
                                overflow-x: auto;
                            }

                            .table {
                                min-width: 800px;
                            }

                            .btn {
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

                        .table-container {
                            animation: fadeIn 0.5s ease-out;
                        }
                    </style>
                </head>

                <body>
                    <div class="page-container">
                        <!-- 頁面標題 -->
                        <div class="page-header">
                            <div class="header-content">
                                <div class="header-info">
                                    <div class="page-icon">👥</div>
                                    <h1 class="page-title">會員管理</h1>
                                </div>
                                <div class="header-actions">
                                    <c:if test="${sessionScope.position eq '主任'}">
                                        <a href="${pageContext.request.contextPath}/addMember" class="btn btn-success">
                                            ➕ 新增會員
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <!-- 搜尋區域 -->
                        <div class="search-container">
                            <div class="search-form">
                                <div class="search-field">
                                    <label class="search-label">身份篩選</label>
                                    <select id="roleFilter" class="search-select" onchange="performSearch()">
                                        <option value="">全部</option>
                                        <option value="student">學生</option>
                                        <option value="teacher">老師</option>
                                    </select>
                                </div>

                                <div class="search-field">
                                    <label class="search-label">年級</label>
                                    <select id="gradeFilter" class="search-select" onchange="performSearch()">
                                        <option value="">全部</option>
                                        <option value="1">一年級</option>
                                        <option value="2">二年級</option>
                                        <option value="3">三年級</option>
                                    </select>
                                </div>

                                <div class="search-field">
                                    <label class="search-label">姓名搜尋</label>
                                    <input type="text" id="nameFilter" placeholder="輸入姓名" class="search-input"
                                        onkeypress="handleEnterKey(event)" />
                                </div>

                                <div class="search-field">
                                    <label class="search-label">&nbsp;</label>
                                    <button type="button" class="btn btn-primary" onclick="performSearch()">
                                        🔍 查詢
                                    </button>
                                </div>

                                <div class="search-field">
                                    <label class="search-label">&nbsp;</label>
                                    <button type="button" class="btn btn-secondary" onclick="clearSearch()">
                                        🔄 清除
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- 表格區域 -->
                        <div class="table-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>帳號</th>
                                        <th>姓名</th>
                                        <th>身份</th>
                                        <th>年級</th>
                                        <th>帳號狀態</th>
                                        <th>驗證狀態</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody id="memberTableBody">
                                    <!-- 載入中狀態 -->
                                    <tr id="loadingRow">
                                        <td colspan="7">
                                            <div class="loading-state">
                                                <div class="loading-spinner"></div>
                                                <p>正在載入會員資料...</p>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- JavaScript -->
                    <script>
                        // 全域變數
                        const contextPath = '${pageContext.request.contextPath}';
                        const sessionPosition = '${sessionScope.position}';

                        console.log('=== 初始化資訊 ===');
                        console.log('contextPath:', contextPath);
                        console.log('sessionPosition:', sessionPosition);

                        // Enter 鍵處理
                        function handleEnterKey(event) {
                            if (event.key === 'Enter') {
                                performSearch();
                            }
                        }

                        // 搜尋功能
                        function performSearch() {
                            console.log('=== 執行搜尋 ===');
                            loadMemberTable();
                        }

                        // 清除搜尋
                        function clearSearch() {
                            console.log('=== 清除搜尋條件 ===');
                            document.getElementById('roleFilter').value = '';
                            document.getElementById('gradeFilter').value = '';
                            document.getElementById('nameFilter').value = '';
                            loadMemberTable();
                        }

                        // 顯示載入狀態
                        function showLoading() {
                            const tableBody = document.getElementById('memberTableBody');
                            tableBody.innerHTML =
                                '<tr id="loadingRow">' +
                                '<td colspan="7">' +
                                '<div class="loading-state">' +
                                '<div class="loading-spinner"></div>' +
                                '<p>正在載入會員資料...</p>' +
                                '</div>' +
                                '</td>' +
                                '</tr>';
                        }

                        // 載入會員表格 - 使用Ajax調用API
                        function loadMemberTable() {
                            console.log('=== 開始載入會員表格 ===');

                            // 顯示載入狀態
                            showLoading();

                            // 取得搜尋條件
                            const role = document.getElementById('roleFilter').value.trim();
                            const grade = document.getElementById('gradeFilter').value.trim();
                            const name = document.getElementById('nameFilter').value.trim();

                            console.log('搜尋條件:', { role, grade, name });

                            // 構建 API URL
                            let apiUrl = contextPath + '/api/member/list';
                            const params = new URLSearchParams();

                            if (role) params.append('role', role);
                            if (grade) params.append('grade', grade);
                            if (name) params.append('name', name);

                            if (params.toString()) {
                                apiUrl += '?' + params.toString();
                            }

                            console.log('API URL:', apiUrl);

                            // 發送Ajax請求
                            fetch(apiUrl, {
                                method: 'GET',
                                headers: {
                                    'Content-Type': 'application/json',
                                    'Accept': 'application/json'
                                },
                                credentials: 'same-origin' // 包含 session
                            })
                                .then(response => {
                                    console.log('=== 收到 Response ===');
                                    console.log('Status:', response.status);

                                    if (!response.ok) {
                                        if (response.status === 401) {
                                            console.error('未登入，重定向到登入頁面');
                                            window.location.href = contextPath + '/login';
                                            return Promise.reject('請先登入');
                                        }
                                        if (response.status === 403) {
                                            return response.json().then(data => {
                                                throw new Error(data.error || '沒有權限');
                                            });
                                        }
                                        throw new Error('HTTP ' + response.status + ': ' + response.statusText);
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    console.log('=== 解析 JSON 數據 ===');
                                    console.log('原始數據:', data);
                                    console.log('數據類型:', Object.prototype.toString.call(data));
                                    console.log('是否為陣列:', Array.isArray(data));

                                    const tableBody = document.getElementById('memberTableBody');

                                    // 檢查數據格式
                                    if (!Array.isArray(data)) {
                                        console.error('數據不是陣列格式');
                                        tableBody.innerHTML =
                                            '<tr>' +
                                            '<td colspan="7">' +
                                            '<div class="error-state">' +
                                            '<p>❌ 數據格式錯誤</p>' +
                                            '<small>期望陣列，實際收到: ' + Object.prototype.toString.call(data) + '</small>' +
                                            '</div>' +
                                            '</td>' +
                                            '</tr>';
                                        return;
                                    }

                                    // 檢查是否有數據
                                    if (data.length === 0) {
                                        console.log('沒有找到會員資料');
                                        tableBody.innerHTML =
                                            '<tr>' +
                                            '<td colspan="7">' +
                                            '<div class="empty-state">' +
                                            '<div class="icon">📭</div>' +
                                            '<h3>沒有找到會員資料</h3>' +
                                            '<p>請嘗試調整搜尋條件</p>' +
                                            '</div>' +
                                            '</td>' +
                                            '</tr>';
                                        return;
                                    }

                                    console.log('=== 開始渲染 ' + data.length + ' 筆數據 ===');

                                    // 清空表格
                                    tableBody.innerHTML = '';

                                    // 渲染每一筆數據
                                    data.forEach(function (member, index) {
                                        console.log('處理第 ' + (index + 1) + ' 筆:', member);

                                        try {
                                            const tr = createMemberRow(member);
                                            tableBody.appendChild(tr);
                                            console.log('第 ' + (index + 1) + ' 筆渲染成功');
                                        } catch (error) {
                                            console.error('第 ' + (index + 1) + ' 筆渲染失敗:', error);
                                            console.error('問題數據:', member);
                                        }
                                    });

                                    console.log('=== 所有數據渲染完成 ===');
                                })
                                .catch(function (error) {
                                    console.error('=== API 調用失敗 ===');
                                    console.error('錯誤詳情:', error);

                                    const tableBody = document.getElementById('memberTableBody');

                                    let errorMessage = '載入資料失敗';
                                    if (error.message) {
                                        if (error.message.indexOf('權限') !== -1) {
                                            errorMessage = error.message;
                                        } else if (error.message.indexOf('網路') !== -1) {
                                            errorMessage = '網路連線錯誤，請稍後再試';
                                        } else {
                                            errorMessage = error.message;
                                        }
                                    }

                                    tableBody.innerHTML =
                                        '<tr>' +
                                        '<td colspan="7">' +
                                        '<div class="error-state">' +
                                        '<p>❌ ' + errorMessage + '</p>' +
                                        '<small>詳細錯誤: ' + error.toString() + '</small><br>' +
                                        '<button onclick="loadMemberTable()" class="btn btn-primary btn-sm" style="margin-top: 10px;">' +
                                        '🔄 重新載入' +
                                        '</button>' +
                                        '</div>' +
                                        '</td>' +
                                        '</tr>';
                                });
                        }

                        // 創建會員行
                        function createMemberRow(member) {
                            // 安全地取得數據
                            const account = member.account || 'N/A';
                            const memberName = member.name || 'N/A';
                            const memberRole = member.role || 'unknown';
                            const memberId = member.id || 0;

                            console.log('創建行，數據:', {
                                id: memberId,
                                account: account,
                                name: memberName,
                                role: memberRole,
                                grade: member.grade,
                                status: member.status,
                                verified: member.verified
                            });

                            const tr = document.createElement('tr');

                            // 處理角色顯示
                            let roleHtml = '';
                            switch (memberRole) {
                                case 'student':
                                    roleHtml = '<span class="role-badge role-student">🎓 學生</span>';
                                    break;
                                case 'teacher':
                                    roleHtml = '<span class="role-badge role-teacher">👨‍🏫 老師</span>';
                                    break;
                                default:
                                    roleHtml = '<span class="role-badge">' + escapeHtml(memberRole) + '</span>';
                            }

                            // 處理年級顯示
                            let gradeDisplay = '-';
                            if (memberRole === 'student' && member.grade != null) {
                                const gradeNum = parseInt(member.grade);
                                if (!isNaN(gradeNum)) {
                                    switch (gradeNum) {
                                        case 1: gradeDisplay = '一年級'; break;
                                        case 2: gradeDisplay = '二年級'; break;
                                        case 3: gradeDisplay = '三年級'; break;
                                        default: gradeDisplay = gradeNum + '年級';
                                    }
                                }
                            }

                            // 處理狀態顯示
                            let statusHtml = '';
                            const status = member.status;
                            console.log('狀態值:', status, '類型:', typeof status);

                            const isActive = status === true || status === 'true' || status === 1 || status === '1' || status === 'active';
                            if (isActive) {
                                statusHtml = '<span class="status-badge status-active">✅ 啟用</span>';
                            } else {
                                statusHtml = '<span class="status-badge status-inactive">❌ 停用</span>';
                            }

                            // 處理驗證狀態
                            let verifiedHtml = '';
                            const verified = member.verified;
                            console.log('驗證值:', verified, '類型:', typeof verified);

                            const isVerified = verified === true || verified === 'true' || verified === 1 || verified === '1' || verified === 'verified';
                            if (isVerified) {
                                verifiedHtml = '<span class="status-badge status-verified">✅ 已驗證</span>';
                            } else {
                                verifiedHtml = '<span class="status-badge status-unverified">⏳ 未驗證</span>';
                            }

                            // 操作按鈕 - 使用新的檢視按鈕樣式
                            const viewButton = '<button class="btn btn-view btn-sm" onclick="viewMember(' + memberId + ')">👁️ 檢視</button>';

                            let deleteButton = '';
                            if (sessionPosition === '主任') {
                                deleteButton =
                                    '<button type="button" class="btn btn-danger btn-sm" ' +
                                    'onclick="confirmDelete(\'' + escapeHtml(memberName) + '\', \'' + escapeHtml(account) + '\', ' + memberId + ')">' +
                                    '🗑️ 刪除' +
                                    '</button>';
                            }

                            // 組裝表格行
                            tr.innerHTML =
                                '<td><strong>' + escapeHtml(account) + '</strong></td>' +
                                '<td>' + escapeHtml(memberName) + '</td>' +
                                '<td>' + roleHtml + '</td>' +
                                '<td>' + gradeDisplay + '</td>' +
                                '<td>' + statusHtml + '</td>' +
                                '<td>' + verifiedHtml + '</td>' +
                                '<td>' +
                                '<div class="actions">' +
                                viewButton +
                                deleteButton +
                                '</div>' +
                                '</td>';

                            return tr;
                        }

                        // HTML 轉義函數
                        function escapeHtml(text) {
                            const div = document.createElement('div');
                            div.textContent = text;
                            return div.innerHTML;
                        }

                        // 檢視會員
                        function viewMember(memberId) {
                            console.log('檢視會員:', memberId);
                            // 跳轉到個人資料頁面
                            window.location.href = contextPath + '/profile?id=' + memberId;
                        }

                        // 確認刪除函數
                        function confirmDelete(name, account, memberId) {
                            console.log('確認刪除:', { name: name, account: account, memberId: memberId });

                            Swal.fire({
                                title: '確定要刪除此會員嗎？',
                                html:
                                    '<div style="text-align: left; margin: 20px 0;">' +
                                    '<p><strong>姓名：</strong>' + name + '</p>' +
                                    '<p><strong>帳號：</strong>' + account + '</p>' +
                                    '<p style="color: #ef4444; margin-top: 16px;">⚠️ 此操作無法復原</p>' +
                                    '</div>',
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonText: '確定刪除',
                                cancelButtonText: '取消',
                                confirmButtonColor: '#ef4444',
                                cancelButtonColor: '#64748b',
                                reverseButtons: true,
                                allowOutsideClick: false,
                                allowEscapeKey: false
                            }).then(function (result) {
                                if (result.isConfirmed) {
                                    deleteMemberAPI(memberId, name);
                                }
                            });
                        }

                        // 執行刪除 - 純 API 調用
                        function deleteMemberAPI(memberId, memberName) {
                            Swal.fire({
                                title: '刪除中...',
                                text: '正在刪除會員資料，請稍候',
                                allowOutsideClick: false,
                                allowEscapeKey: false,
                                showConfirmButton: false,
                                didOpen: function () {
                                    Swal.showLoading();
                                }
                            });

                            // 調用刪除 API
                            fetch(contextPath + '/api/member/delete/' + memberId, {
                                method: 'DELETE',
                                headers: {
                                    'Content-Type': 'application/json',
                                    'Accept': 'application/json'
                                },
                                credentials: 'same-origin'
                            })
                                .then(response => {
                                    console.log('API Response Status:', response.status);

                                    if (!response.ok) {
                                        return response.json().then(data => {
                                            throw new Error(data.message || 'HTTP ' + response.status);
                                        });
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    console.log('刪除成功:', data);

                                    Swal.fire({
                                        title: '刪除成功！',
                                        text: data.message || '會員已成功刪除',
                                        icon: 'success',
                                        confirmButtonColor: '#10b981',
                                        timer: 2000,
                                        timerProgressBar: true,
                                        allowOutsideClick: false,
                                        allowEscapeKey: false,
                                        showConfirmButton: false
                                    }).then(function () {
                                        // 重新載入會員列表，保持在當前頁面
                                        console.log('重新載入會員列表');
                                        loadMemberTable();
                                    });
                                })
                                .catch(error => {
                                    console.error('刪除失敗:', error);

                                    Swal.fire({
                                        title: '刪除失敗！',
                                        text: error.message || '請稍後再試',
                                        icon: 'error',
                                        confirmButtonColor: '#ef4444'
                                    });
                                });
                        }

                        // 保留舊的 deleteMember 函數，但改為調用新的 API 函數
                        function deleteMember(memberId) {
                            // 為了向後兼容，重定向到新的 API 函數
                            deleteMemberAPI(memberId, '');
                        }

                        // 初始化
                        document.addEventListener('DOMContentLoaded', function () {
                            console.log('=== 頁面載入完成 ===');
                            console.log('DOM 已準備好');

                            // 載入會員數據
                            setTimeout(function () {
                                loadMemberTable();
                            }, 100);
                        });

                    </script>
                </body>

                </html>