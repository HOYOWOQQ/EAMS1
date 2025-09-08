<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
    <!DOCTYPE html>
    <html lang="zh-TW">

    <head>
        <meta charset="UTF-8">
        <title>OLD學費管理</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
                color: #333;
            }

            .container {
                padding: 2rem;
                max-width: 1400px;
                margin: 0 auto;
            }

            .header {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                border-radius: 20px;
                padding: 2rem;
                margin-bottom: 2rem;
                box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                display: flex;
                align-items: center;
                justify-content: space-between;
                flex-wrap: wrap;
                gap: 1rem;
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

            .header-title {
                font-size: 28px;
                font-weight: 700;
                background: linear-gradient(135deg, #667eea, #764ba2);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .header-title::before {
                content: "💰";
                font-size: 32px;
            }

            .search-container {
                flex: 1;
                max-width: 400px;
                margin: 0 2rem;
            }

            .search-input {
                width: 100%;
                padding: 12px 20px;
                border: 2px solid #e2e8f0;
                border-radius: 25px;
                font-size: 16px;
                transition: all 0.3s ease;
                background: white;
            }

            .search-input:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            .header-actions {
                display: flex;
                gap: 1rem;
                align-items: center;
            }

            .btn {
                border: none;
                border-radius: 12px;
                padding: 12px 24px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .btn-primary {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
            }

            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
            }

            .btn-success {
                background: linear-gradient(135deg, #48bb78, #38a169);
                color: white;
                box-shadow: 0 4px 15px rgba(72, 187, 120, 0.3);
            }

            .btn-success:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(72, 187, 120, 0.4);
            }

            .btn-warning {
                background: linear-gradient(135deg, #ed8936, #dd6b20);
                color: white;
                box-shadow: 0 4px 15px rgba(237, 137, 54, 0.3);
            }

            .btn-warning:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(237, 137, 54, 0.4);
            }

            .btn-danger {
                background: linear-gradient(135deg, #f56565, #e53e3e);
                color: white;
                box-shadow: 0 4px 15px rgba(245, 101, 101, 0.3);
            }

            .btn-danger:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(245, 101, 101, 0.4);
            }

            .btn-secondary {
                background: linear-gradient(135deg, #a0aec0, #718096);
                color: white;
            }

            .btn-sm {
                padding: 6px 12px;
                font-size: 14px;
            }

            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                gap: 1.5rem;
                margin-bottom: 2rem;
            }

            .stat-card {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                border-radius: 20px;
                padding: 2rem;
                box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
            }

            .stat-card::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
                background: var(--card-color);
            }

            .stat-card.blue {
                --card-color: linear-gradient(135deg, #60a5fa, #3b82f6);
            }

            .stat-card.orange {
                --card-color: linear-gradient(135deg, #fbbf24, #f59e0b);
            }

            .stat-card.green {
                --card-color: linear-gradient(135deg, #22c55e, #16a34a);
            }

            .stat-card.red {
                --card-color: linear-gradient(135deg, #ef4444, #dc2626);
            }

            .stat-card::before {
                background: var(--card-color);
            }

            .stat-label {
                font-size: 14px;
                color: #64748b;
                margin-bottom: 0.5rem;
                font-weight: 500;
            }

            .stat-amount {
                font-size: 32px;
                font-weight: 700;
                color: #1e293b;
                margin-bottom: 0.5rem;
            }

            .stat-desc {
                font-size: 14px;
                color: #94a3b8;
            }

            .table-section {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                border-radius: 20px;
                box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                overflow: hidden;
            }

            .table-header {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1.5rem 2rem;
                font-size: 20px;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .table-header::before {
                content: "📊";
                font-size: 24px;
            }

            .table-container {
                padding: 2rem;
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                background: white;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
            }

            th {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1rem;
                font-weight: 600;
                font-size: 14px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                text-align: center;
            }

            td {
                padding: 1rem;
                text-align: center;
                border-bottom: 1px solid rgba(0, 0, 0, 0.05);
                vertical-align: middle;
            }

            tr {
                transition: all 0.3s ease;
            }

            tr:hover {
                background: rgba(102, 126, 234, 0.05);
                transform: scale(1.01);
            }

            tr:last-child td {
                border-bottom: none;
            }

            .status {
                display: inline-block;
                padding: 6px 16px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .status.pending {
                background: rgba(251, 191, 36, 0.1);
                color: #b45309;
                border: 1px solid rgba(251, 191, 36, 0.3);
            }

            .status.paid {
                background: rgba(34, 197, 94, 0.1);
                color: #15803d;
                border: 1px solid rgba(34, 197, 94, 0.3);
            }

            .status.overdue {
                background: rgba(239, 68, 68, 0.1);
                color: #b91c1c;
                border: 1px solid rgba(239, 68, 68, 0.3);
            }

            .amount-unpaid {
                color: #ef4444;
                font-weight: 600;
            }

            .amount-paid {
                color: #22c55e;
                font-weight: 600;
            }

            .remark-cell {
                max-width: 120px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                cursor: help;
            }

            /* Modal 樣式 */
            .modal-content {
                border: none;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            }

            .modal-header {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                border-radius: 20px 20px 0 0;
                padding: 1.5rem 2rem;
            }

            .modal-title {
                font-weight: 600;
                font-size: 20px;
            }

            .btn-close {
                background: rgba(255, 255, 255, 0.2);
                border-radius: 50%;
                opacity: 0.8;
            }

            .btn-close:hover {
                opacity: 1;
                background: rgba(255, 255, 255, 0.3);
            }

            .modal-body {
                padding: 2rem;
            }

            .form-label {
                font-weight: 600;
                color: #374151;
                margin-bottom: 0.5rem;
            }

            .form-control,
            .form-select {
                border: 2px solid #e5e7eb;
                border-radius: 12px;
                padding: 12px 16px;
                transition: all 0.3s ease;
            }

            .form-control:focus,
            .form-select:focus {
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            .fee-item {
                background: rgba(102, 126, 234, 0.05);
                border: 2px solid rgba(102, 126, 234, 0.1);
                border-radius: 12px;
                padding: 1rem;
                margin-bottom: 1rem;
                display: flex;
                justify-content: space-between;
                align-items: center;
                transition: all 0.3s ease;
            }

            .fee-item:hover {
                background: rgba(102, 126, 234, 0.1);
                border-color: rgba(102, 126, 234, 0.2);
            }

            .discount-section {
                background: rgba(251, 191, 36, 0.05);
                border: 2px solid rgba(251, 191, 36, 0.2);
                border-radius: 12px;
                padding: 1.5rem;
                margin: 1.5rem 0;
            }

            .section-title {
                font-size: 18px;
                font-weight: 600;
                color: #374151;
                margin-bottom: 1rem;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            /* 響應式設計 */
            @media (max-width: 768px) {
                .container {
                    padding: 1rem;
                }

                .header {
                    flex-direction: column;
                    text-align: center;
                }

                .search-container {
                    margin: 1rem 0;
                    max-width: 100%;
                }

                .header-actions {
                    flex-wrap: wrap;
                    justify-content: center;
                }

                .stats-grid {
                    grid-template-columns: 1fr;
                }

                .table-container {
                    padding: 1rem;
                }

                table {
                    font-size: 14px;
                }

                th,
                td {
                    padding: 0.5rem;
                }

                .modal-body {
                    padding: 1rem;
                }
            }

            /* 加載動畫 */
            .loading {
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 3rem;
                color: #667eea;
            }

            .loading::before {
                content: "⏳";
                font-size: 32px;
                animation: spin 2s linear infinite;
            }

            @keyframes spin {
                from {
                    transform: rotate(0deg);
                }

                to {
                    transform: rotate(360deg);
                }
            }

            /* 分頁樣式 */
            #pagination {
                margin-top: 2rem;
                text-align: center;
            }

            .pagination-btn {
                background: rgba(102, 126, 234, 0.1);
                border: 1px solid rgba(102, 126, 234, 0.3);
                color: #667eea;
                padding: 8px 16px;
                margin: 0 4px;
                border-radius: 8px;
                cursor: pointer;
                transition: all 0.3s ease;
            }

            .pagination-btn:hover,
            .pagination-btn.active {
                background: #667eea;
                color: white;
            }
        </style>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </head>

    <body>
        <div class="container">
            <!-- Header Section -->
            <div class="header">
                <div class="header-title">學費管理系統</div>
                <div class="search-container">
                    <input type="text" class="search-input" placeholder="🔍 搜尋學生（學號、姓名、卡號）">
                </div>
                <div class="header-actions">
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#tuitionModal">
                        <i class="bi bi-plus-circle"></i>
                        新增學費帳單
                    </button>
                </div>
            </div>

            <!-- Statistics Cards -->
            <div class="stats-grid">
                <div class="stat-card blue">
                    <div class="stat-label">💰 待收金額計算</div>
                    <div class="stat-amount" id="unpaidAmount">NT$ 0</div>
                    <div class="stat-desc">完全未付</div>
                </div>
                <div class="stat-card orange">
                    <div class="stat-label">⏳ 分期未付</div>
                    <div class="stat-amount" id="partialAmount">NT$ 0</div>
                    <div class="stat-desc">部分已付</div>
                </div>
                <div class="stat-card green">
                    <div class="stat-label">✅ 已收總額</div>
                    <div class="stat-amount" id="paidAmount">NT$ 0</div>
                    <div class="stat-desc">學費總存款</div>
                </div>
                <div class="stat-card red">
                    <div class="stat-label">⚠️ 逾期金額</div>
                    <div class="stat-amount" id="overdueAmount">NT$ 0</div>
                    <div class="stat-desc">請立即處理</div>
                </div>
            </div>

            <!-- Table Section -->
            <div class="table-section">
                <div class="table-header">學費記錄列表</div>
                <div class="table-container">
                    <div id="tuitionTable">
                        <table>
                            <thead>
                                <tr>
                                    <th>📋 通知編號</th>
                                    <th>👤 學生姓名</th>
                                    <th>📚 課程名稱</th>
                                    <th>💰 學費總額</th>
                                    <th>✅ 已付金額</th>
                                    <th>❌ 未繳金額</th>
                                    <th>📅 繳費期限</th>
                                    <th>📊 繳費狀態</th>
                                    <th>📝 備註</th>
                                    <th>⚙️ 操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- 資料將透過 JavaScript 動態載入 -->
                            </tbody>
                        </table>
                    </div>
                    <div id="pagination"></div>
                </div>
            </div>

            <!-- 新增學費 Modal -->
            <div class="modal fade" id="tuitionModal" tabindex="-1" aria-labelledby="tuitionModalLabel"
                aria-hidden="true">
                <div class="modal-dialog modal-xl modal-dialog-scrollable">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="tuitionModalLabel">📋 創建學費通知</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="feeForm" onsubmit="insert(event)">
                                <!-- 基本資料 -->
                                <div class="mb-4">
                                    <h6 class="section-title">
                                        <i class="bi bi-person-fill"></i>
                                        基本資料
                                    </h6>
                                    <div class="row">
                                        <div class="col-md-4 mb-3">
                                            <label class="form-label">學生 *</label>
                                            <select id="studentDropdown" class="form-select" name="student_id" required>
                                                <option value="">請選擇學生</option>
                                            </select>
                                        </div>
                                        <div class="col-md-4 mb-3">
                                            <label class="form-label">約定繳費日期 *</label>
                                            <input type="date" class="form-control" name="due_date" required>
                                        </div>
                                        <div class="col-md-4 mb-3">
                                            <label class="form-label">課程 *</label>
                                            <select id="courseDropdown" class="form-select" name="course_id" required>
                                                <option value="">請選擇課程</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">修業開始日 *</label>
                                            <input type="date" class="form-control" name="start_date" required>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">修業結束日 *</label>
                                            <input type="date" class="form-control" name="end_date" required>
                                        </div>
                                    </div>
                                </div>

                                <!-- 科目與費用 -->
                                <div class="mb-4">
                                    <h6 class="section-title">
                                        <i class="bi bi-book-fill"></i>
                                        科目與費用
                                    </h6>
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <select class="form-select" name="subject" id="subject">
                                                <option value="">請選擇科目</option>
                                            </select>
                                        </div>
                                        <div class="col-md-4">
                                            <input type="number" class="form-control" name="amount" id="amount"
                                                placeholder="輸入金額">
                                        </div>
                                        <div class="col-md-2">
                                            <button type="button" class="btn btn-success w-100" id="addFeeBtn">
                                                <i class="bi bi-plus"></i>
                                                新增費用
                                            </button>
                                        </div>
                                    </div>
                                    <div id="feeList"></div>
                                </div>

                                <!-- 折扣設定 -->
                                <div class="discount-section">
                                    <h6 class="section-title">
                                        <i class="bi bi-percent"></i>
                                        折扣設定
                                    </h6>
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">學費優惠</label>
                                            <input type="number" class="form-control" name="discount_scholar"
                                                placeholder="輸入折扣金額">
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">兄弟姊妹折扣</label>
                                            <input type="number" class="form-control" name="discount_sibling"
                                                placeholder="輸入折扣金額">
                                        </div>
                                        
                                        <div class="col-md-6 mb-3">
						<label class="form-label">實收金額</label> <input type="number"
							class="form-control" id="net_amount" name="net_amount" >
					</div>
                                        
                                        
                                    </div>
                                </div>

                                <!-- 備註 -->
                                <div class="mb-3">
                                    <label class="form-label">
                                        <i class="bi bi-chat-text"></i>
                                        備註
                                    </label>
                                    <textarea class="form-control" name="remark" rows="3"
                                        placeholder="請輸入備註 (選填)"></textarea>
                                </div>

                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-check-circle"></i>
                                        確認創建
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 編輯學費 Modal -->
            <div class="modal fade" id="editTuitionModal" tabindex="-1" aria-labelledby="editTuitionModalLabel"
                aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-scrollable">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="editTuitionModalLabel">
                                <i class="bi bi-pencil-square"></i>
                                編輯學費資料
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="關閉"></button>
                        </div>
                        <div class="modal-body">
                            <form id="editForm" onsubmit="updateTuition(event)">
                    		<input type="hidden" name="payment_id">
                               <input type="hidden" name="course_id"> 

                                <div class="mb-3">
                                    <label class="form-label">學生姓名</label>
                                    <input type="text" class="form-control" name="student_name" readonly
                                        style="background-color: #f8f9fa;">
                                </div>

                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label">課程名稱</label>
                                        <input type="text" class="form-control" name="course_name" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">金額</label>
                                        <input type="number" class="form-control" name="amount" required>
                                    </div>
                                </div>

                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label">繳費狀態</label>
                                        <select name="pay_status" class="form-control" required>
  <option value="unpaid">待繳中</option>
  <option value="paid">已付款</option>
  <option value="partial">分期付款</option>
  <option value="refunded">已退費</option>
</select>
                                        
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">繳費期限</label>
                                        <input type="date" class="form-control" name="pay_date" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">備註</label>
                                    <textarea class="form-control" name="remark" rows="3"></textarea>
                                </div>

                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-save"></i>
                                        儲存修改
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script>
            // 頁面一載入就載資料
            document.addEventListener("DOMContentLoaded", function () {
                loadTuitionData();
                loadStudents();
                loadCourses();
                loadSubjects();
            });

            function loadTuitionData() {
                const tableBody = document.querySelector("#tuitionTable tbody");
                tableBody.innerHTML = '<tr><td colspan="10" class="loading">載入中...</td></tr>';

                fetch("${pageContext.request.contextPath}/apiGetAllPayment")
                    .then(res => res.json())
                    .then(data => {
                        console.log(data);
                        tableBody.innerHTML = ""; // 清空載入提示

                        // 計算統計數據
                        updateStatistics(data);

                        if (data.length === 0) {
                            tableBody.innerHTML = '<tr><td colspan="10" style="text-align: center; padding: 3rem; color: #718096;">📭 目前沒有學費記錄</td></tr>';
                            return;
                        }

                        data.forEach((item, index) => {
                            const row = document.createElement("tr");

                            // 翻譯 payStatus
                            let payStatusText = '';
                            let statusClass = '';
                            switch (item.payStatus) {
                                case 'unpaid':
                                    payStatusText = '待繳中';
                                    statusClass = 'pending';
                                    break;
                                case 'paid':
                                    payStatusText = '已付款';
                                    statusClass = 'paid';
                                    break;
                                case 'refunded':
                                    payStatusText = '已退費';
                                    statusClass = 'overdue';
                                    break;
                                case 'partial':
                                    payStatusText = '分期付款';
                                    statusClass = 'overdue';
                                    break;    
                                    
                                default:
                                    payStatusText = item.payStatus;
                                    statusClass = 'pending';
                            }

                            row.innerHTML = `
						<td>\${item.noticeNo}</td>
						<td><strong>\${item.studentName}</strong></td>
						<td>\${item.courseName}</td>
						<td class="amount-paid">NT$ \${parseInt(item.netAmount).toLocaleString()}</td>
						<td class="amount-paid">NT$ 0</td>
						<td class="amount-unpaid">NT$ \${parseInt(item.netAmount).toLocaleString()}</td>
						<td>\${item.payDate}</td>
						<td><span class="status\${statusClass}">\${payStatusText}</span></td>
						<td class="remark-cell" title="\${item.remark || ''}">\${item.remark || "-"}</td>
						<td>
							<button class="btn btn-sm btn-warning editBtn" data-id="\${item.id}" data-subject-id="\${item.courseId}">
								<i class="bi bi-pencil"></i>
							</button>
							<button class="btn btn-sm btn-danger deleteBtn" data-id="\${item.id}">
								<i class="bi bi-trash"></i>
							</button>
						</td>
					`;

                            // 添加動畫延遲
                            row.style.opacity = '0';
                            row.style.transform = 'translateY(20px)';
                            tableBody.appendChild(row);

                            setTimeout(() => {
                                row.style.transition = 'all 0.5s ease';
                                row.style.opacity = '1';
                                row.style.transform = 'translateY(0)';
                            }, index * 100);
                        });

                        // 綁定事件到按鈕上
                        bindTableEvents();
                    })
                    .catch(error => {
                        console.error('載入學費資料失敗:', error);
                        tableBody.innerHTML = '<tr><td colspan="10" style="text-align: center; padding: 3rem; color: #ef4444;">❌ 載入失敗，請重新整理頁面</td></tr>';
                    });
            }

         // 計算並更新統計數據 
          function updateStatistics(data) {
                let unpaidTotal = 0;      // 待收金額（完全未付）
                let partialTotal = 0;     // 分期未付（部分已付）
                let paidTotal = 0;        // 已收總額
                let overdueTotal = 0;     // 逾期金額

                const today = new Date();

                data.forEach(item => {
                    const amount = parseInt(item.netAmount) || 0;
                    const dueDate = new Date(item.payDate);
                    const isOverdue = dueDate < today;

                    switch (item.payStatus) {
                        case 'unpaid':
                            unpaidTotal += amount;
                            if (isOverdue) {
                                overdueTotal += amount;
                            }
                            break;
                        case 'paid':
                            paidTotal += amount;
                            break;
                        case 'partial':
                            partialTotal += amount;
                            if (isOverdue) {
                                overdueTotal += (amount * 0.5); // 假設部分已付剩餘一半
                            }
                            break;
                        case 'refunded':
                            // 已退費不計入統計
                            break;
                        default:
                            unpaidTotal += amount;
                            if (isOverdue) {
                                overdueTotal += amount;
                            }
                    }
                });

                // 更新統計卡片，帶有動畫效果
                animateCounter('unpaidAmount', unpaidTotal);
                animateCounter('partialAmount', partialTotal);
                animateCounter('paidAmount', paidTotal);
                animateCounter('overdueAmount', overdueTotal);
            }
            // 數字動畫效果
               function animateCounter(elementId, targetValue) {
                   const element = document.getElementById(elementId);
                   const startValue = 0;
                   const duration = 1000; // 1秒
                   const startTime = performance.now();

                   function updateCounter(currentTime) {
                       const elapsedTime = currentTime - startTime;
                       const progress = Math.min(elapsedTime / duration, 1);

                       // 使用緩動函數
                       const easeOutQuart = 1 - Math.pow(1 - progress, 4);
                       const currentValue = startValue + (targetValue - startValue) * easeOutQuart;

                       element.textContent =`NT$ \${Math.floor(currentValue).toLocaleString()}`;

                       if (progress < 1) {
                           requestAnimationFrame(updateCounter);
                       } else {
                           element.textContent = `NT$ \${targetValue.toLocaleString()}`;
                       }
                   }

                   requestAnimationFrame(updateCounter);
               }
         
        
              
            
            
            
            
			function updateTuition(event) {
					    event.preventDefault();
					    
					    const form = document.getElementById('editForm'); 
					    const paymentId = form.querySelector("input[name='payment_id']").value;
					    console.log("paymentId:", paymentId);
					    
		    
					    // 構建 JSON 資料
					    const updateData = {
					        id: parseInt(paymentId),
					        payDate: document.querySelector("#editForm input[name='pay_date']").value,
					        payStatus: document.querySelector("#editForm select[name='pay_status']").value,
					        netAmount: document.querySelector("#editForm input[name='amount']").value,
					        remark: document.querySelector("#editForm textarea[name='remark']").value,
					          
					    };
					    
					    // ⚠️ 請仔細檢查您的後端 `EditPaymentNotice` API 預期接收的 JSON 結構
					    // 特別是 paymentItems 是單一還是多個，以及其他字段的名稱。
					
					    const submitBtn = form.querySelector('button[type="submit"]');
					    const originalText = submitBtn.innerHTML;
					    submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 更新中...';
					    submitBtn.disabled = true;
					
					  
					    
					    // 您這裡的 oldData 和 operator 參數，如果後端需要，應確保其正確性
					  //  const encodedOldData = encodeURIComponent(JSON.stringify({})); // 根據實際情況填充 oldData
					    const encodedOldData = "OldDate"; // 根據實際情況填充 oldData
					    const encodedOperator = encodeURIComponent('admin'); // 根據實際操作人員填充
					
					    
					    fetch(`${pageContext.request.contextPath}/apiEditPayment/\${paymentId}?oldData=\${encodedOldData}&operator=\${encodedOperator}`, {
					        method: "PUT",
					        headers: {
					            "Content-Type": "application/json"
					        },
					        body: JSON.stringify(updateData)
					    })
					    .then(res => res.json())
					    .then(data => {
					        console.log("Server 回傳：", data);
					        if (data.success) {
					            showNotification('學費資料更新成功！', 'success');
					            bootstrap.Modal.getInstance(document.getElementById("editTuitionModal")).hide();
					            loadTuitionData();
					        } else {
					            throw new Error(data.message || '更新失敗');
					        }
					    })
					    .catch(error => {
					        console.error('更新失敗:', error);
					        showNotification('更新失敗：' + error.message, 'error');
					    })
					    .finally(() => {
					        submitBtn.innerHTML = originalText;
					        submitBtn.disabled = false;
					    });
			}
			
            function parseChineseDate(str) {
                // 支援全形/半形空格，也容許多餘空白
                const match = str.match(/(\d+)月[\s\u3000]*(\d+),[\s\u3000]*(\d{4})/);
                if (!match) return "";
                const month = match[1].padStart(2, "0");
                const day = match[2].padStart(2, "0");
                const year = match[3];
                return `\${year}-\${month}-\${day}`;
            }

            // 將事件綁定分離成獨立函數
           function bindTableEvents() {
    // 編輯按鈕事件（保持不變）
    document.querySelectorAll(".editBtn").forEach(btn => {
        btn.addEventListener("click", function () {
            const row = this.closest("tr");
            const tid = this.getAttribute("data-id");
            const cid = this.getAttribute("data-subject-id");
            
            const studentName = row.children[1].textContent.trim();
            const courseName = row.children[2].textContent.trim();
            const amount = row.children[3].textContent.replace("NT$ ", "").replace(/,/g, "").trim();
            const payDateText = row.children[6].textContent.trim();
            const payDate = parseChineseDate(payDateText);
            const remark = row.children[8].textContent.trim();
            
            const statusMap = {
            	    "待繳中": "unpaid",
            	    "已繳清": "paid",
            	    "分期付款": "partial",
            	    "已退費": "refunded"
            	};
            
       	   const payStatusText = row.children[7].textContent.trim();
            const payStatusValue = statusMap[payStatusText];

            // 填入資料
            
            document.querySelector("#editForm input[name='payment_id']").value = tid;  //通知單id
            document.querySelector("#editForm input[name='course_id']").value = cid;   //課程ID
            document.querySelector("#editForm input[name='student_name']").value = studentName;
            document.querySelector("#editForm input[name='course_name']").value = courseName;
            document.querySelector("#editForm input[name='amount']").value = amount;
            document.querySelector("#editForm input[name='pay_date']").value = payDate;
            document.querySelector("#editForm select[name='pay_status']").value = payStatusValue;
            document.querySelector("#editForm textarea[name='remark']").value = remark === "-" ? "" : remark;

            // 顯示 modal
            const editModal = new bootstrap.Modal(document.getElementById("editTuitionModal"));
            editModal.show();
        });
    });

 // ✅ 修正刪除按鈕事件
    document.querySelectorAll(".deleteBtn").forEach(btn => {
        btn.addEventListener("click", function () {
            const tid = this.getAttribute("data-id");
            const row = this.closest("tr");
            const studentName = row.children[1].textContent.trim();

            if (confirm(`確定要刪除 ${studentName} 的學費記錄嗎？\n\n此操作無法復原！`)) {
                const reason = prompt("請輸入刪除原因：") || "資料有誤";
                const operator = prompt("請輸入操作人員名稱：") || "admin";

                this.innerHTML = '<i class="bi bi-hourglass-split"></i>';
                this.disabled = true;

                const encodedReason = encodeURIComponent(reason);
                const encodedOperator = encodeURIComponent(operator);
                
                // ✅ 修正：使用正確的路徑格式
                fetch(`${pageContext.request.contextPath}/apiDeletePayment/\${tid}?reason=\${encodedReason}&operator=\${encodedOperator}`, {
                    method: "DELETE"
                })
                .then(res => res.json())
                .then(result => {
                    if (result.success) {
                        showNotification('刪除成功！', 'success');
                        loadTuitionData();
                    } else {
                        throw new Error(result.message || '刪除失敗');
                    }
                })
                .catch(error => {
                    console.error('刪除失敗:', error);
                    showNotification('刪除失敗：' + error.message, 'error');
                    this.innerHTML = '<i class="bi bi-trash"></i>';
                    this.disabled = false;
                });
            }
        });
    });
}

            // 通知提示函數
            function showNotification(message, type = 'info') {
                const notification = document.createElement('div');
                notification.className = `notification ${type}`;
                notification.style.cssText = `
			position: fixed;
			top: 20px;
			right: 20px;
			padding: 1rem 1.5rem;
			border-radius: 12px;
			color: white;
			font-weight: 600;
			z-index: 9999;
			transform: translateX(100%);
			transition: all 0.3s ease;
			box-shadow: 0 4px 20px rgba(0,0,0,0.1);
		`;

                switch (type) {
                    case 'success':
                        notification.style.background = 'linear-gradient(135deg, #48bb78, #38a169)';
                        notification.innerHTML = `<i class="bi bi-check-circle"></i> \${message}`;
                        break;
                    case 'error':
                        notification.style.background = 'linear-gradient(135deg, #f56565, #e53e3e)';
                        notification.innerHTML = `<i class="bi bi-exclamation-circle"></i> \${message}`;
                        break;
                    default:
                        notification.style.background = 'linear-gradient(135deg, #667eea, #764ba2)';
                        notification.innerHTML = `<i class="bi bi-info-circle"></i> \${message}`;
                }

                document.body.appendChild(notification);

                // 顯示動畫
                setTimeout(() => {
                    notification.style.transform = 'translateX(0)';
                }, 100);

                // 自動隱藏
                setTimeout(() => {
                    notification.style.transform = 'translateX(100%)';
                    setTimeout(() => {
                        document.body.removeChild(notification);
                    }, 300);
                }, 3000);
            }

            // 載入學生資料
            function loadStudents() {
                fetch("${pageContext.request.contextPath}/api/member/student/all")
                    .then(response => response.json())
                    .then(data => {
                        console.log("前端收到學生資料：", data);
                        const dropdown = document.getElementById("studentDropdown");
                        data.data.forEach(student => {
                            const option = document.createElement("option");
                            option.value = student.id;
                            option.textContent = student.name;
                            dropdown.appendChild(option);
                        });
                    })
                    .catch(error => {
                        console.error("載入學生資料失敗：", error);
                    });
            }

            // 載入課程資料
            function loadCourses() {
                fetch("${pageContext.request.contextPath}/api/course/all")
                    .then(response => response.json())
                    .then(data => {
                        console.log("前端收到課程資料：", data);
                        const dropdown = document.getElementById('courseDropdown');
                        data.data.forEach(course => {
                            const option = document.createElement('option');
                            option.value = course.id;
                            option.textContent = course.name;
                            dropdown.appendChild(option);
                        });
                    })
                    .catch(error => {
                        console.error('載入課程資料失敗:', error);
                    });
            }

            // 載入科目資料
            function loadSubjects() {
                fetch("${pageContext.request.contextPath}/api/course/subject/all")
                    .then(response => response.json())
                    .then(data => {
                        console.log("前端收到科目資料：", data);
                        const dropdown = document.getElementById('subject');
                        data.data.forEach(subject => {
                            const option = document.createElement('option');
                            option.value = subject.id;
                            option.textContent = subject.name;
                            dropdown.appendChild(option);
                        });
                    })
                    .catch(error => {
                        console.error('載入科目資料失敗:', error);
                    });
            }

            // 新增費用項目
            document.getElementById("addFeeBtn").addEventListener("click", function () {
                const subjectSelect = document.getElementById("subject");
                const amountInput = document.getElementById("amount");
                const subjectText = subjectSelect.options[subjectSelect.selectedIndex].text;
                const amount = amountInput.value;

                // 驗證輸入
                if (subjectSelect.value === "" || subjectSelect.value === "請選擇科目" || !amount) {
                    showNotification("請選擇科目並輸入金額！", "error");
                    return;
                }

                // 建立項目
                const feeItem = document.createElement("div");
                feeItem.className = "fee-item";
                feeItem.innerHTML = `
			<div><i class="bi bi-journal-text me-2"></i>\${subjectText}</div>
			<div class="text-primary fw-bold">NT$ \${parseInt(amount).toLocaleString()}</div>
			<input type="hidden" name="subjectList" value="\${subjectSelect.value}">
			<input type="hidden" name="amountList" value="\${amount}">
			<button type="button" class="btn btn-danger btn-sm ms-2">
				<i class="bi bi-trash"></i>
			</button>
		`;

                // 加入列表
                document.getElementById("feeList").appendChild(feeItem);

                // 清空欄位
                subjectSelect.selectedIndex = 0;
                amountInput.value = "";

                // 刪除功能
                feeItem.querySelector(".btn-danger").addEventListener("click", function () {
                    feeItem.style.transform = 'scale(0)';
                    feeItem.style.opacity = '0';
                    setTimeout(() => {
                        feeItem.remove();
                    }, 300);
                });

                // 添加動畫
                feeItem.style.opacity = '0';
                feeItem.style.transform = 'translateY(-20px)';
                setTimeout(() => {
                    feeItem.style.transition = 'all 0.3s ease';
                    feeItem.style.opacity = '1';
                    feeItem.style.transform = 'translateY(0)';
                }, 100);
            });

            // 新增學費表單提交
           function insert(event) {
    event.preventDefault();

    const form = document.getElementById('feeForm');
    const formData = new FormData(form);
    
    // ✅ 重要：從動態添加的費用列表中收集 paymentItems
    const paymentItems = [];
    const feeItems = document.querySelectorAll('#feeList .fee-item');
    
    console.log('找到費用項目數量:', feeItems.length); // 調試用
    
    feeItems.forEach(item => {
        const subjectId = item.querySelector('input[name="subjectList"]').value;
        const amount = item.querySelector('input[name="amountList"]').value;
        
        if (subjectId && amount) {
            paymentItems.push({
                subjectId: parseInt(subjectId),
                amount: String(amount),
                remark: "",
                payStatus: "unpaid"
            });
        }
    });
    
    console.log('構建的 paymentItems:', paymentItems); // 調試用
    
    // ✅ 檢查是否有費用項目
    if (paymentItems.length === 0) {
        showNotification('請至少添加一個費用項目！', 'error');
        return;
    }
    
    // ✅ 計算 net_amount
    const totalAmount = paymentItems.reduce((sum, item) => sum + parseInt(item.amount), 0);
    const discountScholar = parseInt(formData.get('discount_scholar') || '0');
    const discountSibling = parseInt(formData.get('discount_sibling') || '0');
    const netAmount = totalAmount - discountScholar - discountSibling;
    
    console.log('計算結果:', { totalAmount, discountScholar, discountSibling, netAmount }); // 調試用
    
    // ✅ 構建請求參數
    const params = new URLSearchParams();
    params.append('student_id', formData.get('student_id'));
    params.append('due_date', formData.get('due_date'));
    params.append('course_id', formData.get('course_id'));
    params.append('start_date', formData.get('start_date'));
    params.append('end_date', formData.get('end_date'));
    params.append('discount_scholar', discountScholar.toString());
    params.append('discount_sibling', discountSibling.toString());
    params.append('net_amount', netAmount.toString());
    params.append('remark', formData.get('remark') || '');
    params.append('paymentItems', JSON.stringify(paymentItems)); // ✅ 關鍵：添加 JSON 字串
    params.append('operator', 'admin');
    
    console.log('發送的參數:', params.toString()); // 調試用

    // 添加載入狀態
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 創建中...';
    submitBtn.disabled = true;

    fetch("${pageContext.request.contextPath}/apicreatePayment", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params.toString()
    })
    .then(res => {
        console.log('Response status:', res.status); // 調試用
        return res.json();
    })
    .then(data => {
        console.log("Server 回傳：", data);
        if (data.success) {
            showNotification('學費帳單創建成功！', 'success');
            bootstrap.Modal.getInstance(document.getElementById("tuitionModal")).hide();
            loadTuitionData();

            // 清空表單
            form.reset();
            document.getElementById("feeList").innerHTML = "";
        } else {
            throw new Error(data.message || '創建失敗');
        }
    })
    .catch(error => {
        console.error('創建失敗:', error);
        showNotification('創建失敗：' + error.message, 'error');
    })
    .finally(() => {
        // 恢復按鈕狀態
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    });
}

          
            

            // 搜尋功能
            document.querySelector('.search-input').addEventListener('input', function (e) {
                const searchTerm = e.target.value.toLowerCase().trim();
                const tableRows = document.querySelectorAll('#tuitionTable tbody tr');

                tableRows.forEach(row => {
                    if (row.children.length < 10) return; // 跳過載入中或空白行

                    const studentName = row.children[1].textContent.toLowerCase();
                    const subjectName = row.children[2].textContent.toLowerCase();
                    const notificationId = row.children[0].textContent.toLowerCase();

                    const isMatch = studentName.includes(searchTerm) ||
                        subjectName.includes(searchTerm) ||
                        notificationId.includes(searchTerm);

                    if (isMatch) {
                        row.style.display = '';
                        row.style.opacity = '1';
                    } else {
                        row.style.display = 'none';
                        row.style.opacity = '0';
                    }
                });

                // 如果搜尋結果為空，顯示提示
                const visibleRows = Array.from(tableRows).filter(row =>
                    row.style.display !== 'none' && row.children.length >= 10
                );

                if (searchTerm && visibleRows.length === 0) {
                    const tbody = document.querySelector('#tuitionTable tbody');
                    let noResultRow = tbody.querySelector('.no-result-row');

                    if (!noResultRow) {
                        noResultRow = document.createElement('tr');
                        noResultRow.className = 'no-result-row';
                        noResultRow.innerHTML = '<td colspan="10" style="text-align: center; padding: 3rem; color: #718096;">🔍 找不到符合條件的記錄</td>';
                        tbody.appendChild(noResultRow);
                    }
                    noResultRow.style.display = '';
                } else {
                    const noResultRow = document.querySelector('.no-result-row');
                    if (noResultRow) {
                        noResultRow.style.display = 'none';
                    }
                }
            });

            // Modal 重置功能
            document.getElementById('tuitionModal').addEventListener('hidden.bs.modal', function () {
                const form = document.getElementById('feeForm');
                form.reset();
                document.getElementById('feeList').innerHTML = '';
            });

            document.getElementById('editTuitionModal').addEventListener('hidden.bs.modal', function () {
                const form = document.getElementById('editForm');
                form.reset();
            });
        </script>