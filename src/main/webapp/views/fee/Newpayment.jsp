<%@ page contentType="text/html; charset=UTF-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>新增與管理繳費通知單</title>
<link
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"
	rel="stylesheet">
<style>
body {
	margin: 0;
	background: linear-gradient(135deg, #e3f0ff 0%, #f6f8fa 100%);
	font-family: 'Segoe UI', '微軟正黑體', Arial, sans-serif;
}

/* .sidebar {

            position: fixed;

            left: 0; top: 0; bottom: 0;

            width: 64px;

            background: #fff;

            border-right: 1px solid #e5e7eb;

            display: flex;

            flex-direction: column;

            align-items: center;

            padding: 16px 0;

            z-index: 10;

        }

        .sidebar .icon {

            width: 32px; height: 32px;

            margin: 18px 0;

            background: #e0e7ff;

            border-radius: 8px;

            display: flex; align-items: center; justify-content: center;

            font-size: 20px;

            color: #6366f1;

        } */
.header {
	margin-left: 64px;
	background: #fff;
	padding: 18px 32px 12px 32px;
	border-bottom: 1px solid #e5e7eb;
	display: flex;
	align-items: center;
	justify-content: space-between;
	border-radius: 6px;
}

.header .search {
	flex: 1;
	margin: 0 24px;
	max-width: 420px;
}

.header input[type="text"] {
	width: 100%;
	padding: 8px 12px;
	border: 1px solid #d1d5db;
	border-radius: 6px;
	font-size: 1rem;
}

.header .actions>button:not(.btn-close) {
	margin-left: 12px;
	padding: 7px 18px;
	border: none;
	border-radius: 6px;
	background: #22c55e;
	color: #fff;
	font-weight: 500;
	font-size: 1rem;
	cursor: pointer;
	transition: background 0.2s;
}

.header .actions button.secondary {
	background: #6366f1;
}

.main {
	margin-left: 64px;
	padding: 32px 40px 0 40px;
}

.title {
	font-size: 1.6rem;
	font-weight: bold;
	color: #2563eb;
	margin-bottom: 18px;
}

.cards {
	display: flex;
	gap: 18px;
	margin-bottom: 24px;
}

.card {
	flex: 1;
	background: #fff;
	border-radius: 12px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
	padding: 18px 22px;
	min-width: 180px;
}

.card.blue {
	border-left: 5px solid #60a5fa;
}

.card.orange {
	border-left: 5px solid #fbbf24;
}

.card.green {
	border-left: 5px solid #22c55e;
}

.card.red {
	border-left: 5px solid #ef4444;
}

.card .label {
	font-size: 1rem;
	color: #64748b;
	margin-bottom: 6px;
}

.card .amount {
	font-size: 1.4rem;
	font-weight: bold;
	color: #1e293b;
}

.card .desc {
	font-size: 0.95rem;
	color: #94a3b8;
}

.table-section {
	background: #fff;
	border-radius: 12px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
	padding: 18px 22px;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 8px;
}

th, td {
	padding: 10px 8px;
	text-align: center;
}

th {
	background: #f1f5f9;
	color: #475569;
	font-weight: 600;
}

tr:nth-child(even) {
	background: #f9fafb;
}

.status {
	display: inline-block;
	padding: 3px 12px;
	border-radius: 12px;
	background: #fef9c3;
	color: #b45309;
	font-size: 0.95rem;
}

.status.paid {
	background: #bbf7d0;
	color: #15803d;
}

.status.overdue {
	background: #fecaca;
	color: #b91c1c;
}

.sy1 {
	max-width: 80px; /* 限制最大寬度，防止撐開整個表格 */
	overflow: hidden; /* 隱藏超出部分 */
	text-overflow: ellipsis; /* 顯示省略號 */
}

.main {
	text-align: center;
}

.clamp-1-lines {
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	text-overflow: ellipsis;
}

/* 修正 Bootstrap Modal 關閉按鈕位置 */

/* .modal-header .btn-close {

                position: absolute;

                right: 1.2rem;

                top: 1.2rem;

                z-index: 2;

            } */

/* 預留空間給關閉按鈕 */

/* .modal-header {

                position: relative;

                padding-right: 3rem;

            } */
@media ( max-width : 900px) {
	.main {
		padding: 18px 4vw 0 4vw;
	}
	.cards {
		flex-direction: column;
	}
}
/* 您自定義的 CSS 樣式，用於模態框、表格和按鈕 */
.modal-body {
	max-height: 70vh; /* 限制模態框內容高度並啟用滾動條 */
	overflow-y: auto;
}

.payment-item-row {
	display: flex;
	gap: 10px; /* 細項之間的間距 */
	margin-bottom: 10px;
	align-items: center; /* 垂直居中對齊 */
}

.payment-item-row input, .payment-item-row select {
	flex: 1; /* 讓輸入框和選擇框平均分配寬度 */
}
</style>
</head>
<body>

	<div class="container mt-5">
		<h1>繳費通知單管理</h1>

		<div class="card mb-4">
			<div class="card-header">新增繳費通知單</div>
			<div class="card-body">
				<form id="paymentNoticeForm">
					<div class="form-group">
						<label for="studentDropdown">學生 *:</label> <select
							class="form-control" id="student_id" name="student_id" required>
							<option value="">請選擇學生</option>
						</select>
						<!-- TODO: 從資料庫用 script 產生 -->
					</div>
					<div class="form-group">
						<label for="due_date">繳費到期日:</label> <input type="date"
							class="form-control" id="due_date" name="due_date" required>
					</div>
					<div class="form-group">
						<label for="course_id">課程 *:</label> <select class="form-control"
							id="courseDropdown" name="course_id" required>
							<option value="">請選擇課程</option>
						</select>
						<!-- TODO: 從資料庫用 script 產生 -->
					</div>
					<div class="form-group">
						<label for="start_date">課程開始日期:</label> <input type="date"
							class="form-control" id="start_date" name="start_date">
					</div>
					<div class="form-group">
						<label for="end_date">課程結束日期:</label> <input type="date"
							class="form-control" id="end_date" name="end_date">
					</div>
					<div class="form-group">
						<label for="discount_scholar">獎學金折扣:</label> <input type="number"
							class="form-control" id="discount_scholar"
							name="discount_scholar" value="0">
					</div>
					<div class="form-group">
						<label for="discount_sibling">兄弟姐妹折扣:</label> <input type="number"
							class="form-control" id="discount_sibling"
							name="discount_sibling" value="0">
					</div>
					<div class="form-group">
						<label for="net_amount">實收金額:</label> <input type="number"
							class="form-control" id="net_amount" name="net_amount" required>
					</div>
					<div class="form-group">
						<label for="remark">備註:</label>
						<textarea class="form-control" id="remark" name="remark" rows="3"></textarea>
					</div>

					<h5>繳費細項</h5>
					<div id="payment_item_container" class="mb-3">
						<div class="payment-item-row">
							<select class="form-control item-subject" name="item_subject[]"
								required>
								<option value="">選擇科目</option>
							</select> <input type="number" class="form-control item-amount"
								name="item_amount[]" placeholder="金額" required> <input
								type="text" class="form-control item-remark"
								name="item_remark[]" placeholder="備註"> <select
								class="form-control item-status" name="item_status[]">
								<option value="unpaid">未繳</option>
								<option value="paid">已繳</option>
							</select>
							<button type="button" class="btn btn-danger btn-sm remove-item">移除</button>
						</div>
					</div>
					<button type="button" class="btn btn-secondary btn-sm"
						id="add_item">添加細項</button>
					<hr>
					<button type="button" class="btn btn-primary" onclick="insert()">提交新增</button>
				</form>
			</div>
		</div>

		<div class="card">
			<div class="card-header">現有繳費通知單</div>
			<div class="card-body">
				<table class="table table-bordered table-striped">
					<thead>
						<tr>
							<th>ID</th>
							<th>通知單號</th>
							<th>學生姓名</th>
							<th>課程名稱</th>
							<th>實收金額</th>
							<th>繳費到期日</th>
							<th>狀態</th>
							<th>備註</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody id="paymentNoticesTableBody">
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<div class="modal fade" id="editPaymentNoticeModal" tabindex="-1"
		aria-labelledby="editPaymentNoticeModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="editPaymentNoticeModalLabel">編輯繳費通知單</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<form id="editPaymentNoticeForm">
						<input type="hidden" id="edit_id">
						<div class="form-group">
							<label for="edit_notice_no">通知單號:</label> <input type="text"
								class="form-control" id="edit_notice_no" readonly>
						</div>
						<div class="form-group">
							<label for="edit_student_id">學生ID:</label> <input type="number"
								class="form-control" id="edit_student_id" required>
						</div>
						<div class="form-group">
							<label for="edit_due_date">繳費到期日:</label> <input type="date"
								class="form-control" id="edit_due_date" required>
						</div>
						<div class="form-group">
							<label for="edit_course_id">課程ID:</label> <input type="number"
								class="form-control" id="edit_course_id" required>
						</div>
						<div class="form-group">
							<label for="edit_start_date">課程開始日期:</label> <input type="date"
								class="form-control" id="edit_start_date">
						</div>
						<div class="form-group">
							<label for="edit_end_date">課程結束日期:</label> <input type="date"
								class="form-control" id="edit_end_date">
						</div>
						<div class="form-group">
							<label for="edit_discount">折扣:</label> <input type="number"
								class="form-control" id="edit_discount">
						</div>
						<div class="form-group">
							<label for="edit_net_amount">實收金額:</label> <input type="number"
								class="form-control" id="edit_net_amount" required>
						</div>
						<div class="form-group">
							<label for="edit_remark">備註:</label>
							<textarea class="form-control" id="edit_remark" rows="3"></textarea>
						</div>
						<div class="form-group">
							<label for="edit_pay_status">繳費狀態:</label> <select
								class="form-control" id="edit_pay_status" required>
								<option value="unpaid">未繳</option>
								<option value="partially_paid">部分繳納</option>
								<option value="paid">已繳</option>
								<option value="overdue">逾期</option>
							</select>
						</div>

						<h5>繳費細項</h5>
						<div id="edit_payment_item_container" class="mb-3"></div>
						<button type="button" class="btn btn-secondary btn-sm"
							id="edit_add_item">添加細項</button>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal">關閉</button>
					<button type="button" class="btn btn-primary" onclick="saveEdit()">保存修改</button>
				</div>
			</div>
		</div>
	</div>

	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

	<script>
    const API_BASE_URL = '/paymentNotices'; // 您的 API 基本路徑

    // 用於存儲科目數據，以便動態填充下拉選單
   // 用於存儲科目數據，以便動態填充下拉選單
    let subjects = [];
    // 用於存儲學生數據，初始為空，將從模擬數據填充
    let students = [];
    // 用於存儲課程數據，初始為空，將從模擬數據填充
    let courses = [];

    // 頁面加載時執行
    document.addEventListener('DOMContentLoaded', async () => {
        await fetchSubjects(); // 加載科目
        populateSubjectSelects(); // 填充現有科目選單
        
        await fetchStudents(); // 新增：加載學生資料
        populateStudentsSelects(); // 新增：填充學生下拉選單

        await fetchCourses(); // 新增：加載課程資料
        populateCoursesSelects(); // 新增：填充課程下拉選單
        
        fetchPaymentNotices(); // 加載所有繳費通知單
    });

    // --- 輔助函數 ---

    // 獲取所有科目，用於下拉選單
    async function fetchSubjects() {
        try {
            const response = await fetch('/subject/all'); // 假設您有一個獲取所有科目的 API 端點
            if (!response.ok) throw new Error('無法獲取科目列表');
            subjects = await response.json();
        } catch (error) {
        	  console.error('Error fetching subjects:', error);
              // 如果科目 API 也沒完成，可以像下面學生和課程一樣模擬它
              subjects = [
                  { id: 1, name: '數學' },
                  { id: 2, name: '英文' },
                  { id: 3, name: '物理' },
                  { id: 4, name: '化學' }
              ];
              console.warn('載入科目失敗，已使用模擬數據。');
          }
    }

    // 填充科目下拉選單
    // --- ⭐⭐⭐ 修改這裡：模擬獲取所有學生數據的函數 ⭐⭐⭐ ---
    async function fetchStudents() {
        // 直接返回一個 Promise，模擬異步請求的行為
        return new Promise(resolve => {
            setTimeout(() => { // 模擬網路延遲
                students = [
                    { sId: 101, sName: '王小明' },
                    { sId: 102, sName: '陳美玲' },
                    { sId: 103, sName: '林大華' },
                    { sId: 104, sName: '張雅婷' }
                    // 添加更多測試數據
                ];
                console.log("學生模擬資料載入成功:", students);
                resolve(); // 表示數據已準備好
            }, 100); // 100 毫秒延遲
        });
    }
    
    // --- ⭐⭐⭐ 修改這裡：模擬獲取所有課程數據的函數 ⭐⭐⭐ ---
    async function fetchCourses() {
        // 直接返回一個 Promise，模擬異步請求的行為
        return new Promise(resolve => {
            setTimeout(() => { // 模擬網路延遲
                courses = [
                    { id: 201, name: '高中數學入門' },
                    { id: 202, name: '雅思英語強化' },
                    { id: 203, name: '基礎物理概論' },
                    { id: 204, name: '程式設計初階' }
                    // 添加更多測試數據
                ];
                console.log("課程模擬資料載入成功:", courses);
                resolve(); // 表示數據已準備好
            }, 100); // 100 毫秒延遲
        });
    }
    
  
 
 function populateSubjectSelects(selectElement, selectedSubjectId = null) {
        let targetSelects;
        if (selectElement) {
            targetSelects = [selectElement]; // 只填充指定元素
        } else {
            targetSelects = document.querySelectorAll('.item-subject'); // 填充所有現有的
        }

        targetSelects.forEach(select => {
            select.innerHTML = '<option value="">選擇科目</option>'; // 清空並添加預設選項
            subjects.forEach(subject => {
                const option = document.createElement('option');
                option.value = subject.id;
                option.textContent = subject.name;
                select.appendChild(option);
            });
            if (selectedSubjectId) {
                select.value = selectedSubjectId;
            }
        });
    }
 
 
//填充學生下拉選單
 function populateStudentsSelects() {
     const studentSelect = document.getElementById('student_id');
     studentSelect.innerHTML = '<option value="">請選擇學生</option>'; // 清空並添加預設選項
     
     students.forEach(student => {
         const option = document.createElement('option');
         option.value = student.sId;
         option.textContent = student.sName;
         studentSelect.appendChild(option);
     });
 }

 // 填充課程下拉選單
 function populateCoursesSelects() {
     const courseSelect = document.getElementById('courseDropdown');
     courseSelect.innerHTML = '<option value="">請選擇課程</option>'; // 清空並添加預設選項
     
     courses.forEach(course => {
         const option = document.createElement('option');
         option.value = course.id;
         option.textContent = course.name;
         courseSelect.appendChild(option);
     });
 }
 

    // 動態添加繳費細項行（新增表單）
    document.getElementById('add_item').addEventListener('click', () => {
        addPaymentItemRow('payment_item_container');
    });

    // 動態添加繳費細項行（編輯表單）
    document.getElementById('edit_add_item').addEventListener('click', () => {
        addPaymentItemRow('edit_payment_item_container');
    });

    function addPaymentItemRow(containerId, item = null) {
        const container = document.getElementById(containerId);
        const row = document.createElement('div');
        row.className = 'payment-item-row';
        if (item && item.id) { // 如果是編輯模式且有 ID，用於識別
            row.dataset.itemId = item.id;
        }

        const unpaidSelected = (item && item.payStatus === 'unpaid') ? 'selected' : '';
        const paidSelected = (item && item.payStatus === 'paid') ? 'selected' : '';
        
        row.innerHTML = 
            '<input type="hidden" class="item-id" value="' + (item && item.id ? item.id : '') + '">' +
            '<select class="form-control item-subject" required>' +
            '</select>' +
            '<input type="number" class="form-control item-amount" placeholder="金額" required value="' + (item && item.amount ? item.amount : '') + '">' +
            '<input type="text" class="form-control item-remark" placeholder="備註" value="' + (item && item.remark ? item.remark : '') + '">' +
            '<select class="form-control item-status">' +
                '<option value="unpaid"' + (item && item.payStatus === 'unpaid' ? ' selected' : '') + '>未繳</option>' +
                '<option value="paid"' + (item && item.payStatus === 'paid' ? ' selected' : '') + '>已繳</option>' +
            '</select>' +
            '<button type="button" class="btn btn-danger btn-sm remove-item">移除</button>';
        container.appendChild(row);

        // 填充科目下拉選單並選中 (如果是編輯模式)
        const subjectSelect = row.querySelector('.item-subject');
        populateSubjectSelects(subjectSelect, item ? item.subjectId : null); // subjectId 來自 DTO

        // 為新添加的移除按鈕綁定事件
        row.querySelector('.remove-item').addEventListener('click', (e) => {
            e.target.closest('.payment-item-row').remove();
        });
    }


    // --- CRUD 操作 ---

    // 獲取並顯示所有繳費通知單
    async function fetchPaymentNotices() {
        try {
            const response = await fetch('<%=request.getContextPath()%>/apiGetAllPayment');
            if (!response.ok) throw new Error('Network response was not ok');
            const notices = await response.json();

            const tableBody = document.getElementById('paymentNoticesTableBody');
            tableBody.innerHTML = ''; // 清空現有列表

            if (notices.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="9" class="text-center">目前沒有繳費通知單。</td></tr>';
                return;
            }

            notices.forEach(notice => {
                const row = `
                    <tr>
                        <td>${notice.id || ''}</td>
                        <td>${notice.noticeNo || ''}</td>
                        <td>${notice.studentName || 'N/A'}</td>
                        <td>${notice.courseName || 'N/A'}</td>
                        <td>${notice.netAmount || '0'}</td>
                        <td>${notice.payDate || ''}</td>
                        <td>${notice.payStatus || ''}</td>
                        <td>${notice.remark || ''}</td>
                        <td>
                            <button type="button" class="btn btn-info btn-sm" onclick="showEditModal(${notice.id})">編輯</button>
                            <button type="button" class="btn btn-danger btn-sm" onclick="deletePaymentNotice(${notice.id})">作廢</button>
                        </td>
                    </tr>
                `;
                tableBody.insertAdjacentHTML('beforeend', row);
            });
        } catch (error) {
            console.error('Error fetching payment notices:', error);
            alert('載入繳費通知單失敗！');
        }
    }

    // 1. 新增繳費通知單 (POST - application/x-www-form-urlencoded)
    async function insert() {
        const form = document.getElementById('paymentNoticeForm');
        const formData = new FormData(form);
        const params = new URLSearchParams(formData);

        // 處理動態添加的 payment_item 數據
        const paymentItems = [];
        document.querySelectorAll('#payment_item_container .payment-item-row').forEach(row => {
            const subjectId = row.querySelector('.item-subject').value;
            const amount = row.querySelector('.item-amount').value;
            const remark = row.querySelector('.item-remark').value;
            const payStatus = row.querySelector('.item-status').value;
            if (subjectId && amount) { // 確保科目和金額有填寫
                paymentItems.push({
                    subjectId: parseInt(subjectId),
                    amount: String(amount), // 確保是 String 以匹配 Entity
                    remark: remark,
                    payStatus: payStatus
                });
            }
        });

        // 將 paymentItems 陣列轉換為 JSON 字串並添加到 FormData
        params.append('paymentItems', JSON.stringify(paymentItems));

        const operator = prompt("請輸入操作人員名稱：");
        if (!operator) {
            alert("操作人員名稱為必填！");
            return;
        }
        params.append('operator', operator);

        try {
            const response = await fetch('<%=request.getContextPath()%>/apiEditPayment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params.toString()
            });

            const result = await response.json();
            if (response.ok && result.success) {
                alert('新增成功！通知單ID: ' + result.id);
                form.reset(); // 清空表單
                document.getElementById('payment_item_container').innerHTML = ''; // 清空細項
                addPaymentItemRow('payment_item_container'); // 添加一個新的空細項行
                fetchPaymentNotices(); // 重新加載列表
            } else {
                alert('新增失敗: ' + (result.message || '未知錯誤'));
            }
        } catch (error) {
            console.error('新增失敗:', error);
            alert('新增失敗！');
        }
    }


    // 2. 顯示編輯模態框並填充數據 (GET)
    async function showEditModal(id) {
        try {
            const response = await fetch('<%=request.getContextPath()%>/apiGetAllPayment/' + id);
            if (!response.ok) throw new Error('Network response was not ok');
            const notice = await response.json();

            // 填充主表單數據
            document.getElementById('edit_id').value = notice.id;
            document.getElementById('edit_notice_no').value = notice.noticeNo || '';
            document.getElementById('edit_student_id').value = notice.studentId || ''; // 注意這裡的 studentId 是 DTO 裡的
            document.getElementById('edit_due_date').value = notice.payDate || '';
            document.getElementById('edit_course_id').value = notice.courseId || ''; // 注意這裡的 courseId 是 DTO 裡的
            document.getElementById('edit_start_date').value = notice.startDate || '';
            document.getElementById('edit_end_date').value = notice.endDate || '';
            document.getElementById('edit_discount').value = notice.discount || '0'; // DTO 裡是 Integer
            document.getElementById('edit_net_amount').value = notice.netAmount || '0'; // DTO 裡是 Integer
            document.getElementById('edit_remark').value = notice.remark || '';
            document.getElementById('edit_pay_status').value = notice.payStatus || 'unpaid';

            // 清空並重新填充繳費細項
            const editItemContainer = document.getElementById('edit_payment_item_container');
            editItemContainer.innerHTML = '';
            if (notice.paymentItems && notice.paymentItems.length > 0) {
                notice.paymentItems.forEach(item => {
                    addPaymentItemRow('edit_payment_item_container', item); // 傳遞細項數據 (包含 id, subjectId, amount(String), remark, payStatus)
                });
            } else {
                // 如果沒有細項，添加一個空的
                addPaymentItemRow('edit_payment_item_container');
            }

            $('#editPaymentNoticeModal').modal('show'); // 顯示模態框
        } catch (error) {
            console.error('Error fetching notice for edit:', error);
            alert('載入編輯數據失敗！');
        }
    }

    // 3. 保存編輯 (PUT - application/json)
    async function saveEdit() {
        const id = document.getElementById('edit_id').value;
        const operator = prompt("請輸入操作人員名稱：");
        if (!operator) {
            alert("操作人員名稱為必填！");
            return;
        }

        // 構建要發送的 PaymentNotice 物件 (JSON 格式)
        const paymentItems = [];
        document.querySelectorAll('#edit_payment_item_container .payment-item-row').forEach(row => {
            const itemId = row.querySelector('.item-id').value; // 現有細項可能有 ID
            const subjectId = row.querySelector('.item-subject').value;
            const amount = row.querySelector('.item-amount').value;
            const remark = row.querySelector('.item-remark').value;
            const payStatus = row.querySelector('.item-status').value;
            if (subjectId && amount) { // 確保科目和金額有填寫
                paymentItems.push({
                    id: itemId ? parseInt(itemId) : null, // 如果是新細項則為 null
                    subjectId: parseInt(subjectId),
                    amount: String(amount), // 確保是 String 以匹配 Entity
                    remark: remark,
                    payStatus: payStatus
                });
            }
        });

        const updatedNoticeData = {
            id: parseInt(id),
            noticeNo: document.getElementById('edit_notice_no').value,
            // 注意：這裡假設您的 Controller 接收的 PaymentNotice DTO/Entity 可以直接透過巢狀結構識別 ID
            studentAccount: { id: parseInt(document.getElementById('edit_student_id').value) }, // 只傳 Member ID
            payDate: document.getElementById('edit_due_date').value,
            course: { id: parseInt(document.getElementById('edit_course_id').value) }, // 只傳 Course ID
            startDate: document.getElementById('edit_start_date').value,
            endDate: document.getElementById('edit_end_date').value,
            discount: String(document.getElementById('edit_discount').value || '0'), // 確保是 String
            netAmount: String(document.getElementById('edit_net_amount').value), // 確保是 String
            remark: document.getElementById('edit_remark').value,
            payStatus: document.getElementById('edit_pay_status').value,
            paymentItems: paymentItems
            // 如果有作廢細項邏輯，可以在這裡添加一個 voidItemIds 陣列
            // 例如：voidItemIds: [1, 2, 3]
        };

        try {
            // 在發送前記錄原始數據，用於日誌 (如果 Service 需要 oldData)
         //   const oldDataResponse = await fetch('\${pageContext.request.contextPath}/apiGetAllPayment/${id}');
            const oldDataResponse = await fetch('<%=request.getContextPath()%>/apiGetAllPayment/' + id);
            const oldNotice = await oldDataResponse.json();
            const oldDataJsonString = JSON.stringify(oldNotice); // 將原始數據轉換為 JSON 字串

            const baseUrl = '<%=request.getContextPath()%>/apiEditAllPayment/' + id;
            const encodedOldData = encodeURIComponent(oldDataJsonString);
            const encodedOperator = encodeURIComponent(operator);
            const fullUrl = baseUrl + '?oldData=' + encodedOldData + '&operator=' + encodedOperator;
            const response = await fetch(fullUrl, {
            
            
         //   const response = await fetch(`\${pageContext.request.contextPath}/apiEditAllPayment/${id}?oldData=${encodeURIComponent(oldDataJsonString)}&operator=${encodeURIComponent(operator)}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json' // 這裡必須是 application/json
                },
                body: JSON.stringify(updatedNoticeData) // 將物件轉為 JSON 字串
            });

            const result = await response.json();
            if (response.ok && result.success) {
                alert('修改成功！');
                $('#editPaymentNoticeModal').modal('hide'); // 隱藏模態框
                fetchPaymentNotices(); // 重新加載列表
            } else {
                alert('修改失敗: ' + (result.message || '未知錯誤'));
            }
        } catch (error) {
            console.error('修改失敗:', error);
            alert('修改失敗！');
        }
    }

    // 4. 作廢繳費通知單 (DELETE)
    async function deletePaymentNotice(id) {
        if (!confirm('確定要作廢這筆繳費通知單嗎？')) {
            return;
        }

        const reason = prompt("請輸入作廢原因：");
        if (!reason) {
            alert("作廢原因為必填！");
            return;
        }

        const operator = prompt("請輸入操作人員名稱：");
        if (!operator) {
            alert("操作人員名稱為必填！");
            return;
        }

        try {
        	const baseUrl = '<%=request.getContextPath()%>/apiDeletePayment/' + id;
        	const encodedReason = encodeURIComponent(reason);
        	const encodedOperator = encodeURIComponent(operator);
        	const fullUrl = baseUrl + '?reason=' + encodedReason + '&operator=' + encodedOperator;
        	const response = await fetch(fullUrl, {
           
           //const response = await fetch(`\${pageContext.request.contextPath}/apiEditAllPayment/${id}?reason=${encodeURIComponent(reason)}&operator=${encodeURIComponent(operator)}`, {
                method: 'DELETE' // 請求方法改為 DELETE
            });

            const result = await response.json();
            if (response.ok && result.success) {
                alert('作廢成功！');
                fetchPaymentNotices(); // 重新加載列表
            } else {
                alert('作廢失敗: ' + (result.message || '未知錯誤'));
            }
        } catch (error) {
            console.error('作廢失敗:', error);
            alert('作廢失敗！');
        }
    }

</script>
</body>
</html>