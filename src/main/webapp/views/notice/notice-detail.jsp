<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>通知詳情 - 班級通知系統</title>
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
            line-height: 1.6;
        }

        .main-content {
            max-width: 1000px;
            margin: 0 auto;
            padding: 1rem;
        }

        .notice-detail {
            background: rgba(255, 255, 255, 0.98);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            animation: fadeIn 0.5s ease forwards;
        }

        .notice-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem;
        }

        .notice-header h2 {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 1.5rem;
            line-height: 1.3;
            word-wrap: break-word;
        }

        .notice-meta {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
            margin-bottom: 1.5rem;
        }

        .notice-meta span {
            background: rgba(255, 255, 255, 0.2);
            padding: 0.75rem 1rem;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 500;
            backdrop-filter: blur(5px);
        }

        .notice-actions {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
        }

        .btn-edit, .btn-delete, .btn-back {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            border: 2px solid rgba(255, 255, 255, 0.3);
            border-radius: 25px;
            padding: 10px 20px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            backdrop-filter: blur(5px);
        }

        .btn-edit:hover, .btn-back:hover {
            background: rgba(255, 255, 255, 0.3);
            border-color: rgba(255, 255, 255, 0.5);
            transform: translateY(-2px);
        }

        .btn-delete {
            background: rgba(255, 107, 107, 0.3);
            border-color: rgba(255, 107, 107, 0.5);
        }

        .btn-delete:hover {
            background: rgba(255, 107, 107, 0.5);
            border-color: rgba(255, 107, 107, 0.7);
            transform: translateY(-2px);
        }

        .notice-content {
            padding: 2rem;
            background: #ffffff;
            border: 2px solid #e0e7ff;
            border-radius: 15px;
            box-shadow: 0 4px 20px rgba(102, 126, 234, 0.08);
            font-size: 16px;
            line-height: 1.8;
            color: #333;
            white-space: pre-line;
            word-wrap: break-word;
            position: relative;
            margin: 1.5rem;
        }

        .notice-content::before {
            content: '📄 內容';
            position: absolute;
            top: -12px;
            left: 20px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
            box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
        }

        .comments-section {
            padding: 1.5rem;
            background: white;
        }

        .comments-section h3 {
            font-size: 20px;
            font-weight: 700;
            margin-bottom: 1rem;
            color: #333;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .comment, .reply {
            background: rgba(248, 249, 250, 0.8);
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            border-left: 4px solid #667eea;
            transition: all 0.3s ease;
        }

        .comment:hover, .reply:hover {
            transform: translateX(3px);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        .reply {
            margin-left: 2rem;
            margin-top: 1rem;
            border-left-color: #a4a4a4;
            background: rgba(255, 255, 255, 0.8);
        }

        .comment-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
            flex-wrap: wrap;
            gap: 0.5rem;
        }

        .author {
            font-weight: 600;
            color: #333;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .role-badge {
            font-size: 12px;
            padding: 4px 8px;
            border-radius: 12px;
            font-weight: 600;
        }

        .role-badge.teacher {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
        }

        .role-badge.student {
            background: linear-gradient(135deg, #a4a4a4, #757575);
            color: white;
        }

        .date {
            color: #666;
            font-size: 14px;
            white-space: nowrap;
        }

        .comment-content {
            color: #555;
            line-height: 1.6;
            margin-bottom: 1rem;
            word-wrap: break-word;
        }

        .comment-actions {
            display: flex;
            gap: 0.5rem;
            flex-wrap: wrap;
            align-items: center;
        }

        .toggle-replies {
            background: transparent;
            border: 1px solid #a4a4a4;
            color: #a4a4a4;
            border-radius: 20px;
            padding: 6px 12px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 0.3rem;
        }

        .toggle-replies:hover {
            background: #a4a4a4;
            color: white;
            transform: translateY(-1px);
        }

        .toggle-replies .arrow {
            transition: transform 0.3s ease;
        }

        .toggle-replies.collapsed .arrow {
            transform: rotate(-90deg);
        }

        .replies-container {
            overflow: hidden;
            transition: all 0.3s ease;
        }

        .replies-container.collapsed {
            max-height: 0;
            opacity: 0;
            margin: 0;
        }

        .replies-container.expanded {
            max-height: 1000px;
            opacity: 1;
        }

        .btn-reply, .btn-edit-comment, .btn-delete-comment {
            background: transparent;
            border: 1px solid #667eea;
            color: #667eea;
            border-radius: 20px;
            padding: 6px 12px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn-reply:hover, .btn-edit-comment:hover {
            background: #667eea;
            color: white;
            transform: translateY(-1px);
        }

        .btn-delete-comment {
            border-color: #ff6b6b;
            color: #ff6b6b;
        }

        .btn-delete-comment:hover {
            background: #ff6b6b;
            color: white;
            transform: translateY(-1px);
        }

        .form-group {
            margin-bottom: 1rem;
        }

        .form-group textarea {
            width: 100%;
            min-height: 120px;
            max-height: 300px;
            padding: 15px 20px;
            border: 2px solid rgba(102, 126, 234, 0.2);
            border-radius: 15px;
            font-size: 14px;
            background: white;
            transition: all 0.3s ease;
            font-family: inherit;
            resize: vertical;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }

        .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
            transform: translateY(-1px);
        }

        .form-actions {
            display: flex;
            gap: 0.5rem;
            flex-wrap: wrap;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            border: none;
            border-radius: 25px;
            padding: 10px 20px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }

        .btn-cancel {
            background: linear-gradient(135deg, #a4a4a4, #757575);
            color: white;
            border: none;
            border-radius: 25px;
            padding: 10px 20px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(164, 164, 164, 0.3);
        }

        .btn-cancel:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(164, 164, 164, 0.4);
        }

        .edit-form, .reply-form {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            margin-top: 1rem;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            border: 2px solid rgba(102, 126, 234, 0.1);
            display: none;
        }

        .reply-form {
            margin-left: 1rem;
            border-left: 4px solid #667eea;
        }

        .add-comment {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
            border-radius: 20px;
            padding: 2rem;
            margin-top: 2rem;
            border: 2px solid rgba(102, 126, 234, 0.1);
        }

        .no-comments {
            text-align: center;
            padding: 3rem;
            color: #666;
            font-style: italic;
            background: rgba(248, 249, 250, 0.5);
            border-radius: 15px;
            border: 2px dashed rgba(102, 126, 234, 0.2);
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
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @media (max-width: 768px) {
            .notice-header {
                padding: 1.5rem;
            }

            .notice-header h2 {
                font-size: 24px;
            }

            .notice-meta {
                grid-template-columns: 1fr;
                gap: 0.5rem;
            }

            .comment, .reply {
                padding: 1rem;
            }

            .reply {
                margin-left: 1rem;
            }

            .reply-form {
                margin-left: 0.5rem;
            }

            .comment-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 0.5rem;
            }

            .form-actions, .notice-actions, .comment-actions {
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    <div class="main-content">
        <div class="notice-detail">
            <div id="loadingDiv" class="loading">
                <p>載入中...</p>
            </div>

            <div id="noticeDetailContent" style="display: none;">
                <div class="notice-header">
                    <h2 id="noticeTitle"></h2>
                    <div class="notice-meta" id="noticeMeta"></div>
                    <div class="notice-actions">
                        <a href="<%=contextPath%>/notice/list" class="btn-back">← 返回列表</a>
                        <div id="noticeActionButtons"></div>
                    </div>
                </div>

                <div class="notice-content" id="noticeContent"></div>

                <div class="comments-section">
                    <h3 id="commentsTitle">💬 留言區</h3>
                    
                    <div id="alertContainer"></div>

                    <div id="commentsContainer">
                        <div id="commentsList"></div>
                        <div id="noComments" class="no-comments" style="display: none;">
                            <p>目前沒有留言，成為第一個留言的人吧！</p>
                        </div>
                    </div>

                    <div class="add-comment">
                        <form id="addCommentForm">
                            <div class="form-group">
                                <textarea id="commentContent" placeholder="請輸入留言內容..." required maxlength="1000"></textarea>
                            </div>
                            <div class="form-actions">
                                <button type="submit" class="btn-primary">發布留言</button>
                                <a href="<%=contextPath%>/notice/list" class="btn-cancel">返回列表</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        let currentNotice = null;
        let currentUser = null;
        let noticeId = null;

        // 頁面載入時初始化
        document.addEventListener('DOMContentLoaded', function() {
            noticeId = getUrlParameter('id');
            if (!noticeId) {
                window.location.href = '<%=contextPath%>/notice/list';
                return;
            }

            loadNoticeDetail();
            setupEventListeners();
        });

        // 設置事件監聽器
        function setupEventListeners() {
            // 新增留言表單
            document.getElementById('addCommentForm').addEventListener('submit', function(e) {
                e.preventDefault();
                createComment();
            });
        }

        // 獲取URL參數
        function getUrlParameter(name) {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get(name);
        }

        // 載入通知詳情
        async function loadNoticeDetail() {
            try {
                showLoading(true);

                const response = await fetch('<%=contextPath%>/api/notices/' + noticeId, {
                    method: 'GET',
                    credentials: 'include'
                });

                const result = await response.json();

                if (result.success) {
                    currentNotice = result.data;
                    renderNoticeDetail(currentNotice);
                } else {
                    showAlert(result.message, 'error');
                    if (response.status === 401) {
                        window.location.href = '<%=contextPath%>/login';
                    } else if (response.status === 403 || response.status === 404) {
                        setTimeout(() => {
                            window.location.href = '<%=contextPath%>/notice/list';
                        }, 2000);
                    }
                }
            } catch (error) {
                console.error('載入通知詳情失敗:', error);
                showAlert('載入通知詳情失敗，請重新整理頁面', 'error');
            } finally {
                showLoading(false);
            }
        }

        // 渲染通知詳情
        function renderNoticeDetail(notice) {
            document.title = notice.title + ' - 班級通知系統';
            
            document.getElementById('noticeTitle').textContent = '📢 ' + notice.title;
            
            document.getElementById('noticeMeta').innerHTML = 
                '<span>📚 課程：' + (notice.courseName || '未知課程') + '</span>' +
                '<span>👨‍🏫 發布者：' + (notice.teacherName || '未知教師') + '</span>' +
                '<span>🕒 發布時間：' + formatDateTime(notice.createdAt) + '</span>';

            document.getElementById('noticeContent').innerHTML = notice.content.replace(/\n/g, '<br>');

            // 渲染操作按鈕
            renderNoticeActionButtons(notice);

            // 渲染留言
            renderComments(notice.comments || []);

            // 更新留言標題
            const commentCount = notice.comments ? notice.comments.length : 0;
            document.getElementById('commentsTitle').textContent = '💬 留言區 (' + commentCount + ' 則留言)';

            document.getElementById('noticeDetailContent').style.display = 'block';
        }

        // 渲染通知操作按鈕
        function renderNoticeActionButtons(notice) {
            const container = document.getElementById('noticeActionButtons');
            const user = JSON.parse(sessionStorage.getItem('currentUser') || '{}');
            
            // 簡化版本：先顯示操作按鈕，讓後端處理權限
            container.innerHTML = 
                '<button class="btn-edit" onclick="editNotice(' + notice.noticeId + ')">編輯通知</button>' +
                '<button class="btn-delete" onclick="deleteNotice(' + notice.noticeId + ')">刪除通知</button>';
        }

        // 渲染留言
        function renderComments(comments) {
            const container = document.getElementById('commentsList');
            const noComments = document.getElementById('noComments');

            if (comments.length === 0) {
                container.innerHTML = '';
                noComments.style.display = 'block';
                return;
            }

            noComments.style.display = 'none';

            let html = '';
            comments.forEach(comment => {
                const hasReplies = comment.replies && comment.replies.length > 0;
                
                html += '<div class="comment">' +
                    '<div class="comment-header">' +
                    '<span class="author">' +
                    (comment.userName || '未知用戶') +
                    '<span class="role-badge ' + (comment.userRole || 'student') + '">' +
                    (comment.userRole === 'teacher' ? '教師' : '學生') +
                    '</span>' +
                    '</span>' +
                    '<span class="date">' + formatDateTime(comment.createdAt) + '</span>' +
                    '</div>' +
                    '<div class="comment-content" id="comment-content-' + comment.commentId + '">' +
                    comment.content.replace(/\n/g, '<br>') +
                    '</div>' +
                    '<div class="comment-actions">' +
                    '<button onclick="showReplyForm(' + comment.commentId + ')" class="btn-reply">回復</button>';
                
                // 如果有回復，添加展開/收起按鈕
                if (hasReplies) {
                    html += '<button onclick="toggleReplies(' + comment.commentId + ')" class="toggle-replies" id="toggle-' + comment.commentId + '">' +
                        '<span class="arrow">▼</span>' +
                        '<span class="text">' + comment.replies.length + ' 則回復</span>' +
                        '</button>';
                }
                
                html += renderCommentActionButtons(comment) +
                    '</div>' +
                    renderEditForm(comment) +
                    renderReplyForm(comment);
                
                // 如果有回復，添加回復容器
                if (hasReplies) {
                    html += '<div class="replies-container expanded" id="replies-' + comment.commentId + '">' + 
                        renderReplies(comment.replies) + 
                        '</div>';
                }
                
                html += '</div>';
            });

            container.innerHTML = html;
        }

        // 渲染留言操作按鈕
        function renderCommentActionButtons(comment) {
            // 簡化版本：顯示操作按鈕，讓後端處理權限
            return '<button onclick="editComment(' + comment.commentId + ')" class="btn-edit-comment">編輯</button>' +
                   '<button onclick="deleteComment(' + comment.commentId + ')" class="btn-delete-comment">刪除</button>';
        }

        // 渲染編輯表單
        function renderEditForm(comment) {
            return '<div id="edit-form-' + comment.commentId + '" class="edit-form">' +
                   '<form onsubmit="updateComment(event, ' + comment.commentId + ')">' +
                   '<div class="form-group">' +
                   '<textarea id="edit-content-' + comment.commentId + '" required maxlength="1000">' + comment.content + '</textarea>' +
                   '</div>' +
                   '<div class="form-actions">' +
                   '<button type="submit" class="btn-primary">更新留言</button>' +
                   '<button type="button" onclick="cancelEdit(' + comment.commentId + ')" class="btn-cancel">取消</button>' +
                   '</div>' +
                   '</form>' +
                   '</div>';
        }

        // 渲染回復表單
        function renderReplyForm(comment) {
            return '<div id="reply-form-' + comment.commentId + '" class="reply-form">' +
                   '<form onsubmit="createReply(event, ' + comment.commentId + ')">' +
                   '<div class="form-group">' +
                   '<textarea placeholder="請輸入回復內容..." required maxlength="1000"></textarea>' +
                   '</div>' +
                   '<div class="form-actions">' +
                   '<button type="submit" class="btn-primary">發布回復</button>' +
                   '<button type="button" onclick="hideReplyForm(' + comment.commentId + ')" class="btn-cancel">取消</button>' +
                   '</div>' +
                   '</form>' +
                   '</div>';
        }

        // 渲染回復
        function renderReplies(replies) {
            if (replies.length === 0) return '';

            let html = '';
            replies.forEach(reply => {
                html += '<div class="reply">' +
                    '<div class="comment-header">' +
                    '<span class="author">' +
                    (reply.userName || '未知用戶') +
                    '<span class="role-badge ' + (reply.userRole || 'student') + '">' +
                    (reply.userRole === 'teacher' ? '教師' : '學生') +
                    '</span>' +
                    '</span>' +
                    '<span class="date">' + formatDateTime(reply.createdAt) + '</span>' +
                    '</div>' +
                    '<div class="comment-content" id="comment-content-' + reply.commentId + '">' +
                    reply.content.replace(/\n/g, '<br>') +
                    '</div>' +
                    '<div class="comment-actions">' +
                    renderCommentActionButtons(reply) +
                    '</div>' +
                    renderEditForm(reply) +
                    '</div>';
            });

            return html;
        }

        // 創建留言
        async function createComment() {
            const content = document.getElementById('commentContent').value.trim();
            
            if (!content) {
                showAlert('留言內容不能為空', 'error');
                return;
            }

            try {
                const response = await fetch('<%=contextPath%>/api/comments', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                    body: JSON.stringify({
                        noticeId: parseInt(noticeId),
                        content: content
                    })
                });

                const result = await response.json();

                if (result.success) {
                    showAlert('留言發布成功', 'success');
                    document.getElementById('commentContent').value = '';
                    loadNoticeDetail(); // 重新載入通知詳情
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                console.error('創建留言失敗:', error);
                showAlert('創建留言失敗，請稍後再試', 'error');
            }
        }

        // 創建回復
        async function createReply(event, parentCommentId) {
            event.preventDefault();
            
            const form = event.target;
            const content = form.querySelector('textarea').value.trim();
            
            if (!content) {
                showAlert('回復內容不能為空', 'error');
                return;
            }

            try {
                const response = await fetch('<%=contextPath%>/api/comments', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                    body: JSON.stringify({
                        noticeId: parseInt(noticeId),
                        content: content,
                        parentCommentId: parentCommentId
                    })
                });

                const result = await response.json();

                if (result.success) {
                    showAlert('回復發布成功', 'success');
                    hideReplyForm(parentCommentId);
                    loadNoticeDetail(); // 重新載入通知詳情
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                console.error('創建回復失敗:', error);
                showAlert('創建回復失敗，請稍後再試', 'error');
            }
        }

        // 更新留言
        async function updateComment(event, commentId) {
            event.preventDefault();
            
            const content = document.getElementById('edit-content-' + commentId).value.trim();
            
            if (!content) {
                showAlert('留言內容不能為空', 'error');
                return;
            }

            try {
                const response = await fetch('<%=contextPath%>/api/comments/' + commentId, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                    body: JSON.stringify({
                        content: content
                    })
                });

                const result = await response.json();

                if (result.success) {
                    showAlert('留言更新成功', 'success');
                    cancelEdit(commentId);
                    loadNoticeDetail(); // 重新載入通知詳情
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                console.error('更新留言失敗:', error);
                showAlert('更新留言失敗，請稍後再試', 'error');
            }
        }

        // 刪除留言
        async function deleteComment(commentId) {
            if (!confirm('確定要刪除此留言嗎？此操作無法復原。')) {
                return;
            }

            try {
                const response = await fetch('<%=contextPath%>/api/comments/' + commentId, {
                    method: 'DELETE',
                    credentials: 'include'
                });

                const result = await response.json();

                if (result.success) {
                    showAlert('留言刪除成功', 'success');
                    loadNoticeDetail(); // 重新載入通知詳情
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                console.error('刪除留言失敗:', error);
                showAlert('刪除留言失敗，請稍後再試', 'error');
            }
        }

        // 編輯通知
        function editNotice(noticeId) {
            window.location.href = '<%=contextPath%>/notice/edit?id=' + noticeId;
        }

        // 刪除通知
        async function deleteNotice(noticeId) {
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
                    setTimeout(() => {
                        window.location.href = '<%=contextPath%>/notice/list';
                    }, 1000);
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                console.error('刪除通知失敗:', error);
                showAlert('刪除通知失敗，請稍後再試', 'error');
            }
        }

        // 編輯留言
        function editComment(commentId) {
            hideAllForms();
            document.getElementById('edit-form-' + commentId).style.display = 'block';
            document.getElementById('comment-content-' + commentId).style.display = 'none';
        }

        // 取消編輯
        function cancelEdit(commentId) {
            document.getElementById('edit-form-' + commentId).style.display = 'none';
            document.getElementById('comment-content-' + commentId).style.display = 'block';
        }

        // 顯示回復表單
        function showReplyForm(commentId) {
            hideAllForms();
            document.getElementById('reply-form-' + commentId).style.display = 'block';
        }

        // 隱藏回復表單
        function hideReplyForm(commentId) {
            document.getElementById('reply-form-' + commentId).style.display = 'none';
        }

        // 展開/收起回復
        function toggleReplies(commentId) {
            const repliesContainer = document.getElementById('replies-' + commentId);
            const toggleButton = document.getElementById('toggle-' + commentId);
            
            if (!repliesContainer || !toggleButton) {
                console.error('找不到回復容器或切換按鈕，commentId:', commentId);
                return;
            }
            
            const arrow = toggleButton.querySelector('.arrow');
            const text = toggleButton.querySelector('.text');
            
            if (!arrow || !text) {
                console.error('找不到箭頭或文字元素');
                return;
            }
            
            if (repliesContainer.classList.contains('expanded')) {
                // 收起回復
                repliesContainer.classList.remove('expanded');
                repliesContainer.classList.add('collapsed');
                toggleButton.classList.add('collapsed');
                arrow.textContent = '▶';
                text.textContent = '展開回復';
            } else {
                // 展開回復
                repliesContainer.classList.remove('collapsed');
                repliesContainer.classList.add('expanded');
                toggleButton.classList.remove('collapsed');
                arrow.textContent = '▼';
                // 重新計算回復數量
                const replyCount = repliesContainer.querySelectorAll('.reply').length;
                text.textContent = replyCount + ' 則回復';
            }
        }

        // 隱藏所有表單
        function hideAllForms() {
            document.querySelectorAll('.edit-form, .reply-form').forEach(form => {
                form.style.display = 'none';
            });
            
            document.querySelectorAll('.comment-content').forEach(content => {
                content.style.display = 'block';
            });
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