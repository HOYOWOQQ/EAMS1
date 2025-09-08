<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>🐦 鳥類管理系統</title>
            <meta charset="UTF-8">
            <style>
                * {
                    box-sizing: border-box;
                }

                body {
                    font-family: Arial, sans-serif;
                    margin: 0;
                    padding: 20px;
                    background-color: #f5f5f5;
                }

                .container {
                    max-width: 1200px;
                    margin: 0 auto;
                }

                .header {
                    text-align: center;
                    margin-bottom: 30px;
                    color: #333;
                }

                /* 導航按鈕 */
                .nav-buttons {
                    text-align: center;
                    margin-bottom: 20px;
                }

                .nav-btn {
                    padding: 12px 24px;
                    margin: 5px;
                    border: none;
                    border-radius: 6px;
                    cursor: pointer;
                    font-size: 16px;
                    transition: all 0.3s;
                    background-color: #007bff;
                    color: white;
                }

                .nav-btn:hover {
                    background-color: #0056b3;
                    transform: translateY(-2px);
                }

                .nav-btn.active {
                    background-color: #28a745;
                }

                /* 功能區塊 */
                .section {
                    background: white;
                    border-radius: 8px;
                    padding: 20px;
                    margin-bottom: 20px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                    display: none;
                }

                .section.active {
                    display: block;
                }

                .section h2 {
                    margin-top: 0;
                    color: #333;
                    border-bottom: 2px solid #007bff;
                    padding-bottom: 10px;
                }

                /* 表格樣式 */
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 15px;
                }

                th,
                td {
                    padding: 12px;
                    text-align: left;
                    border-bottom: 1px solid #ddd;
                }

                th {
                    background-color: #f8f9fa;
                    font-weight: bold;
                }

                tr:hover {
                    background-color: #f5f5f5;
                }

                /* 表單樣式 */
                .form-row {
                    margin-bottom: 15px;
                }

                .form-row label {
                    display: block;
                    margin-bottom: 5px;
                    font-weight: bold;
                    color: #555;
                }

                .form-row input,
                .form-row select {
                    width: 100%;
                    padding: 10px;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    font-size: 14px;
                }

                .form-row input:focus,
                .form-row select:focus {
                    outline: none;
                    border-color: #007bff;
                    box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
                }

                /* 按鈕樣式 */
                .btn {
                    padding: 8px 16px;
                    margin: 2px;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    text-decoration: none;
                    display: inline-block;
                    font-size: 14px;
                    transition: all 0.3s;
                }

                .btn-primary {
                    background-color: #007bff;
                    color: white;
                }

                .btn-primary:hover {
                    background-color: #0056b3;
                }

                .btn-success {
                    background-color: #28a745;
                    color: white;
                }

                .btn-success:hover {
                    background-color: #218838;
                }

                .btn-warning {
                    background-color: #ffc107;
                    color: black;
                }

                .btn-warning:hover {
                    background-color: #e0a800;
                }

                .btn-danger {
                    background-color: #dc3545;
                    color: white;
                }

                .btn-danger:hover {
                    background-color: #c82333;
                }

                .btn-secondary {
                    background-color: #6c757d;
                    color: white;
                }

                .btn-secondary:hover {
                    background-color: #5a6268;
                }

                /* 訊息樣式 */
                .message {
                    padding: 15px;
                    margin: 15px 0;
                    border-radius: 6px;
                    font-weight: bold;
                    display: none;
                    /* 預設隱藏，由 JavaScript 控制 */
                }

                .message.success {
                    background-color: #d4edda;
                    color: #155724;
                    border: 1px solid #c3e6cb;
                }

                .message.error {
                    background-color: #f8d7da;
                    color: #721c24;
                    border: 1px solid #f5c6cb;
                }

                .message.info {
                    background-color: #d1ecf1;
                    color: #0c5460;
                    border: 1px solid #bee5eb;
                }

                .message.warning {
                    background-color: #fff3cd;
                    color: #856404;
                    border: 1px solid #ffeaa7;
                }

                /* 卡片樣式 */
                .bird-card {
                    border: 1px solid #ddd;
                    border-radius: 8px;
                    padding: 20px;
                    margin: 10px 0;
                    background-color: #f9f9f9;
                }

                .bird-info {
                    margin-bottom: 10px;
                }

                .bird-info .label {
                    font-weight: bold;
                    color: #333;
                    display: inline-block;
                    width: 80px;
                }

                .bird-info .value {
                    color: #666;
                }

                /* 搜尋結果 */
                .search-info {
                    background-color: #e7f3ff;
                    padding: 15px;
                    border-radius: 6px;
                    margin: 15px 0;
                    border-left: 4px solid #007bff;
                }

                /* 響應式設計 */
                @media (max-width: 768px) {
                    .nav-btn {
                        display: block;
                        width: 100%;
                        margin: 5px 0;
                    }

                    table {
                        font-size: 12px;
                    }

                    .btn {
                        padding: 6px 12px;
                        font-size: 12px;
                    }
                }
            </style>
        </head>

        <body>
            <div class="container">
                <div class="header">
                    <h1>🐦 鳥類管理系統</h1>
                    <p>單頁面管理所有鳥類資料</p>
                </div>

                <!-- 導航按鈕 -->
                <div class="nav-buttons">
                    <button class="nav-btn active" onclick="showSection('list')">📋 鳥類列表</button>
                    <button class="nav-btn" onclick="showSection('add')">➕ 新增鳥類</button>
                    <button class="nav-btn" onclick="showSection('search')">🔍 搜尋鳥類</button>
                    <button class="nav-btn" onclick="showSection('view')">👁️ 查看詳細</button>
                    <button class="nav-btn" onclick="refreshData()">🔄 重新整理</button>
                </div>

                <!-- 全域訊息顯示 -->
                <div id="globalMessage" class="message">
                    <c:if test="${not empty message}">
                        <script>
                            document.addEventListener('DOMContentLoaded', function () {
                                showMessage('${message}', '${messageType}');
                            });
                        </script>
                    </c:if>
                </div>

                <!-- 鳥類列表區塊 -->
                <div id="listSection" class="section active">
                    <h2>📋 鳥類列表 (總計：<span id="totalCount">${totalCount}</span> 隻)</h2>

                    <c:if test="${not empty isSearchResult}">
                        <div class="search-info">
                            <strong>搜尋結果：</strong>以「${searchType}」搜尋「${searchValue}」，找到 ${totalCount} 筆資料
                            <button class="btn btn-secondary" onclick="clearSearch()">清除搜尋</button>
                        </div>
                    </c:if>

                    <div style="overflow-x: auto;">
                        <table id="birdTable">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>名稱</th>
                                    <th>大小</th>
                                    <th>顏色</th>
                                    <th>年齡</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty birds}">
                                        <tr>
                                            <td colspan="6" style="text-align: center; color: #666; padding: 40px;">
                                                目前沒有鳥類資料，<a href="#" onclick="showSection('add')">點此新增</a>
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="bird" items="${birds}">
                                            <tr data-bird-id="${bird.bid}">
                                                <td>${bird.bid}</td>
                                                <td>${bird.bname}</td>
                                                <td>${bird.size}</td>
                                                <td>${bird.color}</td>
                                                <td>${bird.age}</td>
                                                <td>
                                                    <button class="btn btn-primary"
                                                        onclick="viewBird(${bird.bid})">👁️</button>
                                                    <button class="btn btn-warning"
                                                        onclick="editBird(${bird.bid})">✏️</button>
                                                    <button class="btn btn-danger"
                                                        onclick="deleteBird(${bird.bid}, '${bird.bname}')">🗑️</button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 新增鳥類區塊 -->
                <div id="addSection" class="section">
                    <h2>➕ 新增鳥類</h2>

                    <form action="/birds/form/add" method="post" onsubmit="return validateForm('add')">
                        <div class="form-row">
                            <label for="addBname">鳥類名稱：</label>
                            <input type="text" id="addBname" name="bname" required placeholder="例如：麻雀">
                        </div>

                        <div class="form-row">
                            <label for="addSize">大小：</label>
                            <select id="addSize" name="size" required>
                                <option value="">請選擇大小</option>
                                <option value="小">小</option>
                                <option value="中">中</option>
                                <option value="大">大</option>
                            </select>
                        </div>

                        <div class="form-row">
                            <label for="addColor">顏色：</label>
                            <input type="text" id="addColor" name="color" required placeholder="例如：棕色">
                        </div>

                        <div class="form-row">
                            <label for="addAge">年齡：</label>
                            <input type="number" id="addAge" name="age" required min="0" max="100" placeholder="例如：2">
                        </div>

                        <div style="margin-top: 20px;">
                            <button type="submit" class="btn btn-success">💾 新增鳥類</button>
                            <button type="button" class="btn btn-secondary" onclick="clearAddForm()">🗑️ 清除表單</button>
                            <button type="button" class="btn btn-primary" onclick="addBirdAjax()">⚡ AJAX 新增</button>
                        </div>
                    </form>
                </div>

                <!-- 搜尋鳥類區塊 -->
                <div id="searchSection" class="section">
                    <h2>🔍 搜尋鳥類</h2>

                    <form action="/birds/form/search" method="post">
                        <div class="form-row">
                            <label for="searchType">搜尋類型：</label>
                            <select id="searchType" name="searchType" required>
                                <option value="">請選擇搜尋類型</option>
                                <option value="name">名稱</option>
                                <option value="color">顏色</option>
                                <option value="size">大小</option>
                                <option value="age">年齡</option>
                            </select>
                        </div>

                        <div class="form-row">
                            <label for="searchValue">搜尋內容：</label>
                            <input type="text" id="searchValue" name="searchValue" required placeholder="輸入要搜尋的內容">
                        </div>

                        <div style="margin-top: 20px;">
                            <button type="submit" class="btn btn-primary">🔍 搜尋</button>
                            <button type="button" class="btn btn-secondary" onclick="clearSearchForm()">🗑️ 清除</button>
                            <button type="button" class="btn btn-success" onclick="searchBirdAjax()">⚡ AJAX 搜尋</button>
                        </div>
                    </form>
                </div>

                <!-- 查看/編輯鳥類區塊 -->
                <div id="viewSection" class="section">
                    <h2 id="viewTitle">👁️ 鳥類詳細資訊</h2>

                    <!-- 查看模式 -->
                    <div id="viewMode">
                        <div class="bird-card">
                            <div class="bird-info">
                                <span class="label">ID：</span>
                                <span class="value" id="viewId">-</span>
                            </div>
                            <div class="bird-info">
                                <span class="label">名稱：</span>
                                <span class="value" id="viewName">-</span>
                            </div>
                            <div class="bird-info">
                                <span class="label">大小：</span>
                                <span class="value" id="viewSize">-</span>
                            </div>
                            <div class="bird-info">
                                <span class="label">顏色：</span>
                                <span class="value" id="viewColor">-</span>
                            </div>
                            <div class="bird-info">
                                <span class="label">年齡：</span>
                                <span class="value" id="viewAge">-</span>
                            </div>
                        </div>

                        <div style="margin-top: 20px;">
                            <button class="btn btn-warning" onclick="switchToEditMode()">✏️ 編輯</button>
                            <button class="btn btn-danger" onclick="deleteCurrentBird()">🗑️ 刪除</button>
                        </div>
                    </div>

                    <!-- 編輯模式 -->
                    <div id="editMode" style="display: none;">
                        <form id="editForm">
                            <input type="hidden" id="editId">

                            <div class="form-row">
                                <label for="editBname">鳥類名稱：</label>
                                <input type="text" id="editBname" required>
                            </div>

                            <div class="form-row">
                                <label for="editSize">大小：</label>
                                <select id="editSize" required>
                                    <option value="小">小</option>
                                    <option value="中">中</option>
                                    <option value="大">大</option>
                                </select>
                            </div>

                            <div class="form-row">
                                <label for="editColor">顏色：</label>
                                <input type="text" id="editColor" required>
                            </div>

                            <div class="form-row">
                                <label for="editAge">年齡：</label>
                                <input type="number" id="editAge" required min="0" max="100">
                            </div>

                            <div style="margin-top: 20px;">
                                <button type="button" class="btn btn-success" onclick="saveEditBird()">💾 儲存</button>
                                <button type="button" class="btn btn-secondary" onclick="cancelEdit()">❌ 取消</button>
                            </div>
                        </form>
                    </div>

                    <div style="margin-top: 20px;">
                        <input type="number" id="birdIdInput" placeholder="輸入鳥類 ID"
                            style="padding: 8px; margin-right: 10px;">
                        <button class="btn btn-primary" onclick="loadBirdById()">🔍 載入鳥類</button>
                    </div>
                </div>
            </div>

            <script>
                // 全域變數
                let currentBirdData = null;

                // 顯示指定區塊
                // 修復版本的 showSection 函數
                function showSection(sectionName, element) {
                    // 隱藏所有區塊
                    document.querySelectorAll('.section').forEach(section => {
                        section.classList.remove('active');
                    });

                    // 移除所有按鈕的 active 狀態
                    document.querySelectorAll('.nav-btn').forEach(btn => {
                        btn.classList.remove('active');
                    });

                    // 顯示指定區塊
                    document.getElementById(sectionName + 'Section').classList.add('active');

                    // 設定對應按鈕為 active（安全的方式）
                    if (element) {
                        element.classList.add('active');
                    } else {
                        // 如果沒有傳入 element，就根據 sectionName 找到對應按鈕
                        const buttons = document.querySelectorAll('.nav-btn');
                        buttons.forEach((btn, index) => {
                            const sections = ['list', 'add', 'search', 'view'];
                            if (sections[index] === sectionName) {
                                btn.classList.add('active');
                            }
                        });
                    }
                }

                // 顯示訊息
                function showMessage(message, type) {
                    const messageDiv = document.getElementById('globalMessage');
                    messageDiv.textContent = message;
                    messageDiv.className = 'message ' + type;
                    messageDiv.style.display = 'block';

                    // 3 秒後自動隱藏
                    setTimeout(() => {
                        messageDiv.style.display = 'none';
                    }, 3000);
                }

                // 清除新增表單
                function clearAddForm() {
                    document.getElementById('addBname').value = '';
                    document.getElementById('addSize').value = '';
                    document.getElementById('addColor').value = '';
                    document.getElementById('addAge').value = '';
                }

                // 清除搜尋表單
                function clearSearchForm() {
                    document.getElementById('searchType').value = '';
                    document.getElementById('searchValue').value = '';
                }

                // 驗證表單
                function validateForm(formType) {
                    // 這裡可以加入自定義驗證邏輯
                    return true;
                }

                // 查看鳥類詳細資訊
                function viewBird(id) {
                    fetch('/birds/api/' + id)
                        .then(response => response.json())
                        .then(bird => {
                            if (bird) {
                                currentBirdData = bird;
                                document.getElementById('viewId').textContent = bird.bid;
                                document.getElementById('viewName').textContent = bird.bname;
                                document.getElementById('viewSize').textContent = bird.size;
                                document.getElementById('viewColor').textContent = bird.color;
                                document.getElementById('viewAge').textContent = bird.age + ' 歲';

                                showSection('view');
                                document.getElementById('viewMode').style.display = 'block';
                                document.getElementById('editMode').style.display = 'none';
                                document.getElementById('viewTitle').textContent = '👁️ 鳥類詳細資訊 - ' + bird.bname;
                            } else {
                                showMessage('找不到指定的鳥類', 'error');
                            }
                        })
                        .catch(error => {
                            showMessage('載入鳥類資料失敗：' + error.message, 'error');
                        });
                }

                // 根據 ID 載入鳥類
                function loadBirdById() {
                    const id = document.getElementById('birdIdInput').value;
                    if (id) {
                        viewBird(id);
                    } else {
                        showMessage('請輸入鳥類 ID', 'warning');
                    }
                }

                // 編輯鳥類
                function editBird(id) {
                    viewBird(id);
                    setTimeout(() => switchToEditMode(), 100);
                }

                // 切換到編輯模式
                function switchToEditMode() {
                    if (!currentBirdData) {
                        showMessage('請先選擇要編輯的鳥類', 'warning');
                        return;
                    }

                    document.getElementById('editId').value = currentBirdData.bid;
                    document.getElementById('editBname').value = currentBirdData.bname;
                    document.getElementById('editSize').value = currentBirdData.size;
                    document.getElementById('editColor').value = currentBirdData.color;
                    document.getElementById('editAge').value = currentBirdData.age;

                    document.getElementById('viewMode').style.display = 'none';
                    document.getElementById('editMode').style.display = 'block';
                    document.getElementById('viewTitle').textContent = '✏️ 編輯鳥類 - ' + currentBirdData.bname;
                }

                // 取消編輯
                function cancelEdit() {
                    document.getElementById('viewMode').style.display = 'block';
                    document.getElementById('editMode').style.display = 'none';
                    document.getElementById('viewTitle').textContent = '👁️ 鳥類詳細資訊 - ' + currentBirdData.bname;
                }

                // 儲存編輯
                function saveEditBird() {
                    const id = document.getElementById('editId').value;
                    const birdData = {
                        bname: document.getElementById('editBname').value,
                        size: document.getElementById('editSize').value,
                        color: document.getElementById('editColor').value,
                        age: parseInt(document.getElementById('editAge').value)
                    };

                    fetch('/birds/api/' + id, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(birdData)
                    })
                        .then(response => response.json())
                        .then(bird => {
                            currentBirdData = bird;
                            showMessage('成功更新鳥類：' + bird.bname, 'success');
                            refreshData();
                            viewBird(bird.bid);
                        })
                        .catch(error => {
                            showMessage('更新失敗：' + error.message, 'error');
                        });
                }

                // 刪除鳥類
                function deleteBird(id, name) {
                    if (confirm('確定要刪除「' + name + '」嗎？')) {
                        fetch('/birds/api/' + id, {
                            method: 'DELETE'
                        })
                            .then(response => response.json())
                            .then(success => {
                                if (success) {
                                    showMessage('成功刪除鳥類：' + name, 'success');
                                    refreshData();

                                    // 如果正在查看被刪除的鳥類，回到列表
                                    if (currentBirdData && currentBirdData.bid == id) {
                                        showSection('list');
                                    }
                                } else {
                                    showMessage('刪除失敗：找不到指定的鳥類', 'error');
                                }
                            })
                            .catch(error => {
                                showMessage('刪除失敗：' + error.message, 'error');
                            });
                    }
                }

                // 刪除當前查看的鳥類
                function deleteCurrentBird() {
                    if (currentBirdData) {
                        deleteBird(currentBirdData.bid, currentBirdData.bname);
                    }
                }

                // AJAX 新增鳥類
                function addBirdAjax() {
                    const birdData = {
                        bname: document.getElementById('addBname').value,
                        size: document.getElementById('addSize').value,
                        color: document.getElementById('addColor').value,
                        age: parseInt(document.getElementById('addAge').value)
                    };

                    // 簡單驗證
                    if (!birdData.bname || !birdData.size || !birdData.color || !birdData.age) {
                        showMessage('請填寫所有欄位', 'warning');
                        return;
                    }

                    fetch('/birds/api/add', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(birdData)
                    })
                        .then(response => response.json())
                        .then(bird => {
                            showMessage('成功新增鳥類：' + bird.bname, 'success');
                            clearAddForm();
                            refreshData();
                        })
                        .catch(error => {
                            showMessage('新增失敗：' + error.message, 'error');
                        });
                }

                // AJAX 搜尋鳥類
                function searchBirdAjax() {
                    const searchType = document.getElementById('searchType').value;
                    const searchValue = document.getElementById('searchValue').value;

                    if (!searchType || !searchValue) {
                        showMessage('請選擇搜尋類型並輸入搜尋內容', 'warning');
                        return;
                    }

                    fetch('/birds/api/search?type=' + searchType + '&value=' + encodeURIComponent(searchValue))
                        .then(response => response.json())
                        .then(birds => {
                            updateBirdTable(birds);
                            showMessage('搜尋到 ' + birds.length + ' 筆資料', 'info');
                            showSection('list');
                        })
                        .catch(error => {
                            showMessage('搜尋失敗：' + error.message, 'error');
                        });
                }

                // 更新鳥類表格
                function updateBirdTable(birds) {
                    const tbody = document.querySelector('#birdTable tbody');
                    tbody.innerHTML = '';

                    if (birds.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #666; padding: 40px;">沒有找到符合條件的鳥類資料</td></tr>';
                    } else {
                        birds.forEach(bird => {
                            const row = tbody.insertRow();
                            row.setAttribute('data-bird-id', bird.bid);
                            row.innerHTML = `
                        <td>\${bird.bid}</td>
                        <td>\${bird.bname}</td>
                        <td>\${bird.size}</td>
                        <td>\${bird.color}</td>
                        <td>\${bird.age}</td>
                        <td>
                            <button class="btn btn-primary" onclick="viewBird(\${bird.bid})">👁️</button>
                            <button class="btn btn-warning" onclick="editBird(\${bird.bid})">✏️</button>
                            <button class="btn btn-danger" onclick="deleteBird(\${bird.bid}, '\${bird.bname}')">🗑️</button>
                        </td>
                    `;
                        });
                    }

                    document.getElementById('totalCount').textContent = birds.length;
                }

                // 重新整理資料
                function refreshData() {
                    fetch('/birds/api/all')
                        .then(response => response.json())
                        .then(birds => {
                            updateBirdTable(birds);
                            showMessage('資料已重新整理', 'info');
                        })
                        .catch(error => {
                            showMessage('重新整理失敗：' + error.message, 'error');
                        });
                }

                // 清除搜尋結果
                function clearSearch() {
                    refreshData();
                }
            </script>
        </body>

        </html>