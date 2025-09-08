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
    <title>發布通知 - 班級通知系統</title>
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

        .create-notice {
            background: rgba(255, 255, 255, 0.98);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            animation: fadeIn 0.5s ease forwards;
        }

        .create-notice h2 {
            font-size: 24px;
            font-weight: 700;
            color: #333;
            margin-bottom: 2rem;
            text-align: center;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
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

        .form-group input::placeholder,
        .form-group textarea::placeholder {
            color: #999;
        }

        .form-actions {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-top: 2rem;
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
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
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
            transition: all 0.3s ease;
            display: inline-block;
            text-align: center;
            box-shadow: 0 4px 15px rgba(164, 164, 164, 0.3);
            cursor: pointer;
        }

        .btn-cancel:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(164, 164, 164, 0.4);
        }

        .loading {
            text-align: center;
            padding: 2rem;
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

        .help-section {
            background: rgba(255, 255, 255, 0.98);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            padding: 2rem;
            margin-top: 2rem;
            border-left: 4px solid #667eea;
        }

        .help-section h3 {
            color: #333;
            margin-bottom: 1rem;
            font-size: 20px;
            font-weight: 600;
        }

        .help-section ul {
            list-style: none;
            padding: 0;
        }

        .help-section li {
            padding: 0.75rem 0;
            color: #666;
            position: relative;
            padding-left: 2rem;
            font-size: 15px;
            line-height: 1.6;
        }

        .help-section li:before {
            content: "💡";
            position: absolute;
            left: 0;
            top: 0.75rem;
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
            body {
                padding: 0.5rem;
            }

            .form-actions {
                flex-direction: column;
                align-items: center;
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
        <div class="create-notice">
            <h2>✏️ 發布新通知</h2>
            
            <div id="alertContainer"></div>

            <div id="loadingDiv" class="loading" style="display: none;">
                <p>正在載入課程列表...</p>
            </div>

            <form id="createNoticeForm" style="display: none;">
                <div class="form-group">
                    <label for="courseId">選擇課程：</label>
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
                    <textarea id="content" name="content" rows="12" required placeholder="請輸入通知內容，可以包含課程重點、作業要求、考試資訊等..."></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary" id="submitBtn">發布通知</button>
                    <button type="button" class="btn-cancel" onclick="goBack()">取消</button>
                </div>
            </form>
        </div>

        <div class="help-section">
            <h3>📋 通知發布指南</h3>
            <ul>
                <li>選擇您要發布通知的課程</li>
                <li>標題應該簡潔明瞭，讓學生一目了然</li>
                <li>內容可以包含課程公告、作業說明、考試資訊等</li>
                <li>發布後學生將能在通知列表中看到您的通知</li>
                <li>您可以隨時編輯或刪除已發布的通知</li>
            </ul>
        </div>
    </div>

    <script>
        let courses = [];

        // 頁面載入時初始化
        document.addEventListener('DOMContentLoaded', function() {
            console.log('頁面已載入，開始初始化...');
            loadCourses();
            setupEventListeners();
        });

        // 設置事件監聽器
        function setupEventListeners() {
            document.getElementById('createNoticeForm').addEventListener('submit', function(e) {
                e.preventDefault();
                createNotice();
            });

            // 自動調整textarea高度
            const textarea = document.getElementById('content');
            textarea.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = this.scrollHeight + 'px';
            });
        }

        // 載入課程列表
        async function loadCourses() {
            try {
                showLoading(true);

                console.log('開始載入課程列表...');
                const response = await fetch('<%=contextPath%>/api/notices/courses', {
                    method: 'GET',
                    credentials: 'include'
                });

                console.log('API 響應狀態:', response.status);
                
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = '<%=contextPath%>/login';
                        return;
                    }
                    throw new Error('獲取課程列表失敗，狀態碼: ' + response.status);
                }

                const result = await response.json();
                console.log('API 響應結果:', result);

                // 檢查響應格式 - 適配實際的 API 響應
                if ((result.success !== false && result.data) || (result.message && result.data)) {
                    courses = result.data;
                    console.log('獲取到課程數據:', courses);
                    
                    populateCourseSelect(courses);
                    document.getElementById('createNoticeForm').style.display = 'block';
                    console.log('表單應該已顯示');
                } else {
                    console.error('API 返回失敗:', result);
                    showAlert(result.message || '獲取課程列表失敗', 'error');
                    
                    // 即使API格式不符，如果有data就使用
                    if (result.data && Array.isArray(result.data)) {
                        courses = result.data;
                        populateCourseSelect(courses);
                        document.getElementById('createNoticeForm').style.display = 'block';
                    }
                }
            } catch (error) {
                console.error('載入課程失敗:', error);
                showAlert('載入課程列表失敗，但您仍可以嘗試發布通知', 'error');
                
                // 如果獲取課程失敗，仍然顯示表單（沒有課程選項）
                populateCourseSelect([]);
                document.getElementById('createNoticeForm').style.display = 'block';
            } finally {
                showLoading(false);
                console.log('載入課程流程結束');
            }
        }

        // 填充課程選項
        function populateCourseSelect(courseList) {
            console.log('開始填充課程選項:', courseList);
            const select = document.getElementById('courseId');
            
            if (!select) {
                console.error('找不到課程選擇框元素');
                return;
            }
            
            // 清除現有選項（保留默認選項）
            select.innerHTML = '<option value="">請選擇課程</option>';

            if (!courseList || courseList.length === 0) {
                console.warn('沒有課程數據');
                const option = document.createElement('option');
                option.value = '';
                option.textContent = '暫無可用課程';
                option.disabled = true;
                select.appendChild(option);
                return;
            }

            courseList.forEach((course, index) => {
                console.log(`處理課程 ${index}:`, course);
                const option = document.createElement('option');
                option.value = course.id;
                option.textContent = course.name || '未知課程名稱';
                select.appendChild(option);
            });
            
            console.log('課程選項填充完成，總共:', courseList.length, '個課程');
        }

        // 創建通知
        async function createNotice() {
            const form = document.getElementById('createNoticeForm');
            const submitBtn = document.getElementById('submitBtn');
            
            // 獲取表單數據
            const formData = {
                courseId: parseInt(form.courseId.value),
                title: form.title.value.trim(),
                content: form.content.value.trim()
            };

            // 前端驗證
            if (!validateForm(formData)) {
                return;
            }

            try {
                // 禁用提交按鈕
                submitBtn.disabled = true;
                submitBtn.textContent = '發布中...';

                const response = await fetch('<%=contextPath%>/api/notices', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                    body: JSON.stringify(formData)
                });

                const result = await response.json();

                if (result.success) {
                    showAlert('通知發布成功！正在跳轉...', 'success');
                    
                    // 立即跳轉到通知列表
                    window.location.href = '<%=contextPath%>/notice/list';
                } else {
                    showAlert(result.message, 'error');
                }
            } catch (error) {
                console.error('發布通知失敗:', error);
                showAlert('發布通知失敗，請稍後再試', 'error');
            } finally {
                // 恢復提交按鈕
                submitBtn.disabled = false;
                submitBtn.textContent = '發布通知';
            }
        }

        // 表單驗證
        function validateForm(data) {
            if (!data.courseId) {
                showAlert('請選擇課程', 'error');
                document.getElementById('courseId').focus();
                return false;
            }

            if (data.title.length < 5) {
                showAlert('通知標題至少需要5個字符', 'error');
                document.getElementById('title').focus();
                return false;
            }

            if (data.title.length > 200) {
                showAlert('通知標題不能超過200個字符', 'error');
                document.getElementById('title').focus();
                return false;
            }

            if (data.content.length < 10) {
                showAlert('通知內容至少需要10個字符', 'error');
                document.getElementById('content').focus();
                return false;
            }

            return true;
        }

        // 返回上一頁
        function goBack() {
            window.location.href = '<%=contextPath%>/notice/list';
        }

        // 顯示載入狀態
        function showLoading(show) {
            const loadingDiv = document.getElementById('loadingDiv');
            console.log('設置載入狀態:', show, '載入元素:', loadingDiv);
            if (loadingDiv) {
                loadingDiv.style.display = show ? 'block' : 'none';
            }
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