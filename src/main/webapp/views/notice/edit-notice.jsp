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
    <title>編輯通知 - 班級通知系統</title>
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
            max-width: 800px;
            margin: 0 auto;
            padding: 1rem;
        }

        .edit-notice {
            background: rgba(255, 255, 255, 0.98);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            animation: fadeIn 0.5s ease forwards;
            position: relative;
        }

        .edit-notice::before {
            content: "";
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(135deg, #feca57, #ff9ff3);
            border-radius: 20px 20px 0 0;
        }

        .edit-notice h2 {
            font-size: 24px;
            font-weight: 700;
            color: #333;
            margin-bottom: 1rem;
            text-align: center;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .edit-warning {
            background: linear-gradient(135deg, #feca57, #ff9ff3);
            color: white;
            padding: 1rem 1.5rem;
            border-radius: 15px;
            margin-bottom: 2rem;
            text-align: center;
            font-weight: 600;
            box-shadow: 0 4px 15px rgba(254, 202, 87, 0.3);
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #333;
            font-size: 16px;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 15px 20px;
            border: 2px solid rgba(102, 126, 234, 0.2);
            border-radius: 15px;
            font-size: 16px;
            background: white;
            transition: all 0.3s ease;
            font-family: inherit;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
            transform: translateY(-1px);
        }

        .form-group textarea {
            min-height: 200px;
            resize: vertical;
            line-height: 1.6;
        }

        .form-actions {
            display: flex;
            gap: 1.5rem;
            justify-content: center;
            margin-top: 2.5rem;
            padding-top: 1.5rem;
            border-top: 2px solid rgba(102, 126, 234, 0.1);
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            border: none;
            border-radius: 25px;
            padding: 15px 30px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
            position: relative;
            overflow: hidden;
            min-width: 140px;
            text-align: center;
        }

        .btn-primary::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
            transition: left 0.5s;
        }

        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
            background: linear-gradient(135deg, #5a6fd8, #6b5b95);
        }

        .btn-primary:hover::before {
            left: 100%;
        }

        .btn-primary:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .btn-cancel {
            background: linear-gradient(135deg, #a4a4a4, #757575);
            color: white;
            border: none;
            border-radius: 25px;
            padding: 15px 30px;
            font-size: 16px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            display: inline-block;
            text-align: center;
            box-shadow: 0 4px 15px rgba(164, 164, 164, 0.3);
            position: relative;
            overflow: hidden;
            min-width: 140px;
            cursor: pointer;
        }

        .btn-cancel::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
            transition: left 0.5s;
        }

        .btn-cancel:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(164, 164, 164, 0.4);
            background: linear-gradient(135deg, #909090, #606060);
            color: white;
            text-decoration: none;
        }

        .btn-cancel:hover::before {
            left: 100%;
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
            .form-actions {
                flex-direction: column;
                align-items: center;
                gap: 1rem;
            }

            .btn-primary,
            .btn-cancel {
                width: 100%;
                max-width: 300px;
            }
        }
    </style>
</head>
<body>
    <div class="main-content">
        <div class="edit-notice">
            <h2>✏️ 編輯通知</h2>
            
            <div class="edit-warning">
                <p>⚠️ 編輯通知將會更新所有學生看到的內容，請確認修改後再提交</p>
            </div>
            
            <div id="alertContainer"></div>

            <div id="loadingDiv" class="loading">
                <p>載入中...</p>
            </div>

            <form id="editNoticeForm" style="display: none;">
                <div class="form-group">
                    <label for="courseId">課程：</label>
                    <select id="courseId" name="courseId" required>
                        <option value="">請選擇課程</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="title">通知標題：</label>
                    <input type="text" id="title" name="title" required maxlength="200" placeholder="請輸入通知標題">
                </div>

                <div class="form-group">
                    <label for="content">通知內容：</label>
                    <textarea id="content" name="content" rows="12" required placeholder="請輸入通知內容"></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary" id="submitBtn">更新通知</button>
                    <button type="button" class="btn-cancel" id="cancelBtn">取消</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        let noticeId = null;
        let originalData = {};
        let isFormChanged = false;

        // 頁面載入時初始化
        document.addEventListener('DOMContentLoaded', function() {
            noticeId = getUrlParameter('id');
            if (!noticeId) {
                showAlert('無效的通知ID', 'error');
                setTimeout(() => {
                    window.location.href = '<%=contextPath%>/notice/list';
                }, 2000);
                return;
            }

            loadNoticeData();
            setupEventListeners();
        });

        // 獲取URL參數
        function getUrlParameter(name) {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get(name);
        }

        // 設置事件監聽器
        function setupEventListeners() {
            const form = document.getElementById('editNoticeForm');
            
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                updateNotice();
            });

            // 監聽表單變化
            form.addEventListener('input', function() {
                checkFormChanges();
            });

            // 取消按鈕
            document.getElementById('cancelBtn').addEventListener('click', function() {
                if (isFormChanged) {
                    if (confirm('您有未儲存的修改，確定要離開嗎？')) {
                        goBack();
                    }
                } else {
                    goBack();
                }
            });

            // 防止意外離開頁面
            window.addEventListener('beforeunload', function(e) {
                if (isFormChanged) {
                    e.preventDefault();
                    e.returnValue = '您有未儲存的修改，確定要離開嗎？';
                }
            });

            // 自動調整textarea高度
            const textarea = document.getElementById('content');
            textarea.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = this.scrollHeight + 'px';
            });
        }

        // 載入通知數據
        async function loadNoticeData() {
            try {
                showLoading(true);

                // 同時載入通知詳情和課程列表
                const [noticeResponse, coursesResponse] = await Promise.all([
                    fetch('<%=contextPath%>/api/notices/' + noticeId, {
                        method: 'GET',
                        credentials: 'include'
                    }),
                    fetch('<%=contextPath%>/api/notices/courses', {
                        method: 'GET',
                        credentials: 'include'
                    })
                ]);

                if (!noticeResponse.ok || !coursesResponse.ok) {
                    if (noticeResponse.status === 401 || coursesResponse.status === 401) {
                        window.location.href = '<%=contextPath%>/login';
                        return;
                    }
                    throw new Error('載入數據失敗');
                }

                const [noticeResult, coursesResult] = await Promise.all([
                    noticeResponse.json(),
                    coursesResponse.json()
                ]);

                // 適配實際的 API 響應格式
                const noticeSuccess = (noticeResult.success !== false && noticeResult.data) || 
                                    (noticeResult.message && noticeResult.data);
                const coursesSuccess = (coursesResult.success !== false && coursesResult.data) || 
                                     (coursesResult.message && coursesResult.data);

                if (noticeSuccess && coursesSuccess) {
                    const notice = noticeResult.data;
                    const courses = coursesResult.data;

                    populateForm(notice, courses);
                    document.getElementById('editNoticeForm').style.display = 'block';
                } else {
                    showAlert(noticeResult.message || coursesResult.message || '載入數據失敗', 'error');
                }
            } catch (error) {
                console.error('載入數據失敗:', error);
                showAlert('載入數據失敗，請重新整理頁面', 'error');
            } finally {
                showLoading(false);
            }
        }

        // 填充表單
        function populateForm(notice, courses) {
            // 填充課程選項
            const courseSelect = document.getElementById('courseId');
            courseSelect.innerHTML = '<option value="">請選擇課程</option>';
            
            courses.forEach(course => {
                const option = document.createElement('option');
                option.value = course.id;
                option.textContent = course.name;
                option.selected = course.id === notice.courseId;
                courseSelect.appendChild(option);
            });

            // 填充表單數據
            document.getElementById('title').value = notice.title;
            document.getElementById('content').value = notice.content;

            // 保存原始數據
            originalData = {
                courseId: notice.courseId,
                title: notice.title,
                content: notice.content
            };

            // 初始調整textarea高度
            const textarea = document.getElementById('content');
            textarea.style.height = 'auto';
            textarea.style.height = textarea.scrollHeight + 'px';

            // 重置變更標記
            isFormChanged = false;
        }

        // 檢查表單變化
        function checkFormChanges() {
            const courseIdValue = document.getElementById('courseId').value;
            const currentData = {
                courseId: courseIdValue ? parseInt(courseIdValue) : null,
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            };

            isFormChanged = 
                currentData.courseId !== originalData.courseId ||
                currentData.title !== originalData.title ||
                currentData.content !== originalData.content;
        }

        // 更新通知
        async function updateNotice() {
            const form = document.getElementById('editNoticeForm');
            const submitBtn = document.getElementById('submitBtn');
            
            // 獲取表單數據 - 重要：包含 courseId
            const courseIdValue = form.courseId.value;
            const formData = {
                courseId: courseIdValue ? parseInt(courseIdValue) : null,
                title: form.title.value.trim(),
                content: form.content.value.trim()
            };

            console.log('原始資料:', originalData);
            console.log('提交的表單資料:', formData);
            console.log('課程ID變更:', originalData.courseId, '->', formData.courseId);

            // 前端驗證
            if (!validateForm(formData)) {
                return;
            }

            // 確認更新
            if (!confirm('確定要儲存對此通知的修改嗎？')) {
                return;
            }

            try {
                // 禁用提交按鈕
                submitBtn.disabled = true;
                submitBtn.textContent = '更新中...';

                console.log('發送 PUT 請求到:', '<%=contextPath%>/api/notices/' + noticeId);
                console.log('請求 body:', JSON.stringify(formData));

                const response = await fetch('<%=contextPath%>/api/notices/' + noticeId, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                    body: JSON.stringify(formData)
                });

                console.log('響應狀態:', response.status);
                const result = await response.json();
                console.log('後端響應:', result);

                if (result.success) {
                    showAlert('通知更新成功！正在跳轉...', 'success');
                    isFormChanged = false; // 清除變更標記
                    
                    // 立即跳轉到通知詳情頁面
                    setTimeout(() => {
                        window.location.href = '<%=contextPath%>/notice/detail?id=' + noticeId;
                    }, 1000);
                } else {
                    showAlert(result.message || '更新失敗', 'error');
                    console.error('後端返回錯誤:', result);
                }
            } catch (error) {
                console.error('更新通知失敗:', error);
                showAlert('更新通知失敗，請稍後再試', 'error');
            } finally {
                // 恢復提交按鈕
                submitBtn.disabled = false;
                submitBtn.textContent = '更新通知';
            }
        }

        // 表單驗證
        function validateForm(data) {
            if (!data.courseId) {
                showAlert('請選擇課程', 'error');
                document.getElementById('courseId').focus();
                return false;
            }

            if (!data.title || data.title.length < 5) {
                showAlert('通知標題至少需要5個字符', 'error');
                document.getElementById('title').focus();
                return false;
            }

            if (data.title.length > 200) {
                showAlert('通知標題不能超過200個字符', 'error');
                document.getElementById('title').focus();
                return false;
            }

            if (!data.content || data.content.length < 10) {
                showAlert('通知內容至少需要10個字符', 'error');
                document.getElementById('content').focus();
                return false;
            }

            return true;
        }

        // 返回上一頁
        function goBack() {
            window.location.href = '<%=contextPath%>/notice/detail?id=' + noticeId;
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

            // 滾動到頂部以確保用戶看到訊息
            window.scrollTo(0, 0);

            setTimeout(function() {
                alertDiv.remove();
            }, 5000);
        }
    </script>
</body>
</html>