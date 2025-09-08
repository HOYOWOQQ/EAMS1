<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String contextPath = request.getContextPath();
    // 直接從 session 獲取用戶角色
    String userRole = (String) session.getAttribute("role");
    if (userRole == null) {
        userRole = "student"; // 默認為學生
    }
%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>通知列表 - 班級通知系統</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #333;
            min-height: 100vh;
            padding: 1rem;
        }

        .main-content {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1rem;
        }

        .notice-list {
            background: rgba(255, 255, 255, 0.98);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            animation: fadeIn 0.5s ease forwards;
        }

        .list-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid rgba(102, 126, 234, 0.1);
            flex-wrap: wrap;
            gap: 1rem;
        }

        .list-header h2 {
            font-size: 24px;
            font-weight: 700;
            color: #333;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .search-container {
            margin-bottom: 2rem;
            text-align: center;
        }

        .search-box {
            width: 100%;
            max-width: 500px;
            padding: 16px 20px;
            border: 2px solid rgba(102, 126, 234, 0.2);
            border-radius: 15px;
            background: white;
            font-size: 16px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        .search-box:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
            transform: translateY(-2px);
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            border: none;
            border-radius: 25px;
            padding: 12px 24px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }

        .notice-item {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            border-left: 4px solid transparent;
            cursor: pointer;
        }

        .notice-item:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            border-left-color: #667eea;
        }

        .notice-item.unread {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
            border-left-color: #667eea;
            box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
        }

        .notice-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 1rem;
        }

        .notice-header h3 {
            flex: 1;
            margin-right: 1rem;
            font-size: 18px;
            font-weight: 600;
            color: #333;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .notice-date {
            color: #666;
            font-size: 14px;
            white-space: nowrap;
            font-weight: 500;
        }

        .notice-meta {
            display: flex;
            gap: 1rem;
            margin-bottom: 1rem;
            flex-wrap: wrap;
        }

        .notice-meta span {
            color: #666;
            font-size: 14px;
            padding: 6px 12px;
            background: rgba(102, 126, 234, 0.1);
            border-radius: 20px;
            font-weight: 500;
        }

        .notice-content {
            color: #555;
            line-height: 1.6;
            margin-bottom: 1rem;
            font-size: 15px;
        }

        .new-badge {
            background: linear-gradient(135deg, #ff6b6b, #ee5a52);
            color: white;
            font-size: 12px;
            padding: 4px 8px;
            border-radius: 12px;
            font-weight: 600;
            margin-left: 8px;
        }

        .notice-actions {
            display: flex;
            gap: 0.5rem;
            justify-content: flex-end;
            margin-top: 0.5rem;
        }

        .btn-edit, .btn-delete {
            padding: 8px 16px;
            font-size: 13px;
            border-radius: 20px;
            border: none;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
        }

        .btn-edit {
            background: linear-gradient(135deg, #3742fa, #2f3542);
            color: white;
        }

        .btn-delete {
            background: linear-gradient(135deg, #ff6b6b, #ee5a52);
            color: white;
        }

        .btn-edit:hover, .btn-delete:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        }

        .no-data, .no-results {
            text-align: center;
            padding: 3rem;
            color: #666;
            display: none;
        }

        .loading {
            text-align: center;
            padding: 3rem;
            color: #667eea;
        }

        .alert {
            padding: 1rem;
            border-radius: 10px;
            margin-bottom: 1rem;
            font-weight: 500;
        }

        .alert-success {
            background: rgba(39, 174, 96, 0.1);
            color: #27ae60;
            border: 1px solid rgba(39, 174, 96, 0.2);
        }

        .alert-error {
            background: rgba(231, 76, 60, 0.1);
            color: #e74c3c;
            border: 1px solid rgba(231, 76, 60, 0.2);
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

        @media (max-width: 768px) {
            .list-header {
                flex-direction: column;
                text-align: center;
            }

            .notice-header {
                flex-direction: column;
                gap: 0.5rem;
            }

            .notice-meta {
                flex-direction: column;
                gap: 0.5rem;
            }

            .notice-actions {
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    <div class="main-content">
        <div class="notice-list">
            <div class="list-header">
                <h2>📋 通知列表</h2>
                <button id="createNoticeBtn" class="btn-primary" style="display: none;">✏️ 發布新通知</button>
            </div>

            <div id="alertContainer"></div>

            <div class="search-container">
                <input type="text" id="searchInput" class="search-box" placeholder="🔍 搜索通知標題、內容、課程或教師...">
            </div>

            <div id="loadingDiv" class="loading">
                <p>載入中...</p>
            </div>

            <div id="noticesList"></div>

            <div id="noData" class="no-data" style="display: none;">
                <h3>目前沒有通知</h3>
                <p>暫時沒有任何通知。</p>
                <button id="createFirstNoticeBtn" class="btn-primary" style="display: none;">發布第一個通知</button>
            </div>

            <div id="noResults" class="no-results" style="display: none;">
                <p>找不到符合條件的通知，請嘗試其他關鍵字。</p>
            </div>
        </div>
    </div>

    <script>
        let currentUser = null;
        let notices = [];
        let isTeacher = '<%= userRole %>' === 'teacher'; // 直接從服務器端獲取角色信息

        // 頁面加載時初始化
        document.addEventListener('DOMContentLoaded', function() {
            console.log('用戶角色:', '<%= userRole %>', '是否為教師:', isTeacher);
            
            // 根據角色立即設置按鈕顯示狀態
            if (isTeacher) {
                document.getElementById('createNoticeBtn').style.display = 'inline-block';
                document.getElementById('createFirstNoticeBtn').style.display = 'inline-block';
                console.log('教師用戶，顯示創建按鈕');
            } else {
                document.getElementById('createNoticeBtn').style.display = 'none';
                document.getElementById('createFirstNoticeBtn').style.display = 'none';
                console.log('學生用戶，隱藏創建按鈕');
            }
            
            loadNotices();
            setupEventListeners();
        });

        // 設置事件監聽器
        function setupEventListeners() {
            // 搜索功能
            document.getElementById('searchInput').addEventListener('input', function() {
                filterNotices(this.value.toLowerCase().trim());
            });

            // 創建通知按鈕
            document.getElementById('createNoticeBtn').addEventListener('click', function() {
                window.location.href = '<%=contextPath%>/notice/create';
            });

            document.getElementById('createFirstNoticeBtn').addEventListener('click', function() {
                window.location.href = '<%=contextPath%>/notice/create';
            });
        }

        // 載入通知列表
        async function loadNotices() {
            try {
                showLoading(true);
                
                const response = await fetch('<%=contextPath%>/api/notices', {
                    method: 'GET',
                    credentials: 'include'
                });

                const result = await response.json();

                if (result.success) {
                    notices = result.data;
                    renderNotices(notices);
                } else {
                    showAlert(result.message, 'error');
                    if (response.status === 401) {
                        window.location.href = '<%=contextPath%>/login';
                    }
                }
            } catch (error) {
                console.error('載入通知失敗:', error);
                showAlert('載入通知失敗，請重新整理頁面', 'error');
            } finally {
                showLoading(false);
            }
        }

        // 渲染通知列表
        function renderNotices(noticeList) {
            const container = document.getElementById('noticesList');
            const noDataDiv = document.getElementById('noData');

            if (noticeList.length === 0) {
                container.innerHTML = '';
                noDataDiv.style.display = 'block';
                return;
            }

            noDataDiv.style.display = 'none';
            
            // 修正：使用字符串拼接而不是模板字符串來避免JSP EL衝突
            let html = '';
            noticeList.forEach(notice => {
                html += '<div class="notice-item ' + (!notice.isRead ? 'unread' : '') + '" ' +
                       'onclick="viewNoticeDetail(' + notice.noticeId + ')" ' +
                       'data-searchable="true">' +
                       '<div class="notice-header">' +
                       '<h3>' +
                       '<span data-title>' + notice.title + '</span>' +
                       (!notice.isRead ? '<span class="new-badge">NEW</span>' : '') +
                       '</h3>' +
                       '<span class="notice-date">' + formatDateTime(notice.createdAt) + '</span>' +
                       '</div>' +
                       '<div class="notice-meta">' +
                       '<span data-course>📚 課程：' + (notice.courseName || '未知課程') + '</span>' +
                       '<span data-teacher>👨‍🏫 發布者：' + (notice.teacherName || '未知教師') + '</span>' +
                       '<span>💬 留言：' + (notice.commentCount || 0) + ' 則</span>' +
                       '</div>' +
                       '<div class="notice-content" data-content>' +
                       (notice.content.length > 150 ? notice.content.substring(0, 150) + '...' : notice.content) +
                       '</div>' +
                       renderNoticeActions(notice) +
                       '</div>';
            });

            container.innerHTML = html;
        }

        // 渲染通知操作按鈕
        function renderNoticeActions(notice) {
            // 只有教師才能看到編輯和刪除按鈕
            if (!isTeacher) {
                return ''; // 學生不顯示任何操作按鈕
            }
            
            // 教師可以看到操作按鈕
            return '<div class="notice-actions" onclick="event.stopPropagation()">' +
                   '<button class="btn-edit" onclick="editNotice(' + notice.noticeId + ')">編輯</button>' +
                   '<button class="btn-delete" onclick="deleteNotice(' + notice.noticeId + ')">刪除</button>' +
                   '</div>';
        }

        // 篩選通知
        function filterNotices(searchTerm) {
            const noResults = document.getElementById('noResults');
            
            if (!searchTerm) {
                renderNotices(notices);
                noResults.style.display = 'none';
                return;
            }

            const filtered = notices.filter(notice => {
                const searchableText = (
                    notice.title + ' ' +
                    notice.content + ' ' +
                    (notice.courseName || '') + ' ' +
                    (notice.teacherName || '')
                ).toLowerCase();
                return searchableText.includes(searchTerm);
            });

            renderNotices(filtered);
            noResults.style.display = filtered.length === 0 ? 'block' : 'none';
        }

        // 查看通知詳情
        function viewNoticeDetail(noticeId) {
            window.location.href = '<%=contextPath%>/notice/detail?id=' + noticeId;
        }

        // 編輯通知
        function editNotice(noticeId) {
            // 額外的權限檢查
            if (!isTeacher) {
                showAlert('您沒有權限編輯通知', 'error');
                return;
            }
            window.location.href = '<%=contextPath%>/notice/edit?id=' + noticeId;
        }

        // 刪除通知
        async function deleteNotice(noticeId) {
            // 額外的權限檢查
            if (!isTeacher) {
                showAlert('您沒有權限刪除通知', 'error');
                return;
            }

            if (!confirm('確定要刪除此通知嗎？此操作無法復原，相關留言也會一併刪除。')) {
                return;
            }

            try {
                const response = await fetch('<%=contextPath%>/api/notices/' + noticeId, {
                    method: 'DELETE',
                    credentials: 'include'
                });

                const result = await response.json();

                if (result.success) {
                    showAlert('通知刪除成功', 'success');
                    loadNotices(); // 重新載入列表
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                console.error('刪除通知失敗:', error);
                showAlert('刪除通知失敗，請稍後再試', 'error');
            }
        }

        // 顯示載入狀態
        function showLoading(show) {
            document.getElementById('loadingDiv').style.display = show ? 'block' : 'none';
        }

        // 顯示提示訊息
        function showAlert(message, type) {
            const container = document.getElementById('alertContainer');
            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-' + type;
            alertDiv.textContent = message;
            
            container.innerHTML = '';
            container.appendChild(alertDiv);

            setTimeout(function() {
                alertDiv.remove();
            }, 5000);
        }

        // 格式化日期時間
        function formatDateTime(dateTimeStr) {
            const date = new Date(dateTimeStr);
            return date.toLocaleString('zh-TW', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        }
    </script>
</body>
</html>