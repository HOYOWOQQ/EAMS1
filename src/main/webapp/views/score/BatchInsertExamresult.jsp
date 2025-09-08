<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<% String path=request.getContextPath(); %>
			<!DOCTYPE html>
			<html lang="zh-Hant">

			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
				<title>批次新增成績</title>
				<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
				<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
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
						max-width: 900px;
						margin: 0 auto;
						animation: fadeInUp 0.6s ease-out;
					}

					.card {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(20px);
						border-radius: 24px;
						box-shadow: 0 16px 48px rgba(0, 0, 0, 0.1);
						border: 1px solid rgba(255, 255, 255, 0.2);
						overflow: hidden;
					}

					.card-header {
						background: linear-gradient(135deg, #667eea, #764ba2);
						color: white;
						padding: 2rem;
						text-align: center;
						position: relative;
					}

					.card-header::before {
						content: "📊";
						font-size: 3rem;
						display: block;
						margin-bottom: 1rem;
					}

					.card-header h4 {
						font-size: 2rem;
						font-weight: 700;
						letter-spacing: 1px;
						margin: 0;
					}

					.card-body {
						padding: 3rem;
					}

					.form-group {
						margin-bottom: 2rem;
						position: relative;
					}

					.form-label {
						display: block;
						font-weight: 600;
						color: #4a5568;
						margin-bottom: 0.5rem;
						font-size: 1rem;
						letter-spacing: 0.5px;
					}

					.form-select,
					.form-control {
						width: 100%;
						padding: 1rem 1.5rem;
						border: 2px solid #e2e8f0;
						border-radius: 12px;
						font-size: 1rem;
						transition: all 0.3s ease;
						background: white;
						color: #333;
					}

					.form-select:focus,
					.form-control:focus {
						outline: none;
						border-color: #667eea;
						box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
						transform: translateY(-2px);
					}

					.form-select:hover,
					.form-control:hover {
						border-color: #cbd5e0;
					}

					/* Student List Styling */
					.student-list {
						border: 2px solid #e2e8f0;
						border-radius: 16px;
						padding: 1.5rem;
						background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
						min-height: 200px;
						max-height: 400px;
						overflow-y: auto;
						transition: all 0.3s ease;
					}

					.student-list:hover {
						border-color: #cbd5e0;
					}

					.student-list.loading {
						display: flex;
						align-items: center;
						justify-content: center;
						background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
					}

					.student-checkbox {
						display: flex;
						align-items: center;
						margin-bottom: 1rem;
						padding: 0.75rem 1rem;
						background: white;
						border-radius: 10px;
						box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
						transition: all 0.3s ease;
						cursor: pointer;
					}

					.student-checkbox:hover {
						transform: translateX(4px);
						box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
					}

					.student-checkbox input[type="checkbox"] {
						width: 18px;
						height: 18px;
						margin-right: 0.75rem;
						accent-color: #667eea;
						cursor: pointer;
					}

					.student-checkbox label {
						font-weight: 500;
						color: #4a5568;
						cursor: pointer;
						margin: 0;
					}

					.student-checkbox.checked {
						background: linear-gradient(135deg, rgba(102, 126, 234, 0.1),
								rgba(118, 75, 162, 0.1));
						border: 2px solid rgba(102, 126, 234, 0.3);
					}

					.empty-state {
						text-align: center;
						color: #a0aec0;
						font-style: italic;
						font-size: 1.1rem;
						padding: 3rem 1rem;
					}

					.empty-state::before {
						content: "🎓";
						font-size: 3rem;
						display: block;
						margin-bottom: 1rem;
					}

					/* Loading spinner */
					.loading-spinner {
						width: 40px;
						height: 40px;
						border: 4px solid rgba(102, 126, 234, 0.1);
						border-left: 4px solid #667eea;
						border-radius: 50%;
						animation: spin 1s linear infinite;
						margin: 0 auto;
					}

					@keyframes spin {
						0% {
							transform: rotate(0deg);
						}

						100% {
							transform:
								rotate(360deg);
						}
					}

					.btn {
						padding: 1rem 2.5rem;
						border: none;
						border-radius: 12px;
						font-size: 1.1rem;
						font-weight: 600;
						cursor: pointer;
						transition: all 0.3s ease;
						text-transform: uppercase;
						letter-spacing: 1px;
						position: relative;
						overflow: hidden;
					}

					.btn-success {
						background: linear-gradient(135deg, #48bb78, #38a169);
						color: white;
						box-shadow: 0 4px 16px rgba(72, 187, 120, 0.3);
					}

					.btn-success:hover {
						transform: translateY(-3px);
						box-shadow: 0 8px 24px rgba(72, 187, 120, 0.4);
					}

					.btn-success:active {
						transform: translateY(-1px);
					}

					.btn::before {
						content: '';
						position: absolute;
						top: 0;
						left: -100%;
						width: 100%;
						height: 100%;
						background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2),
								transparent);
						transition: left 0.5s;
					}

					.btn:hover::before {
						left: 100%;
					}

					.submit-section {
						text-align: center;
						margin-top: 3rem;
						padding-top: 2rem;
						border-top: 1px solid #e2e8f0;
					}

					/* Form Icons */
					.form-group.has-icon {
						position: relative;
					}

					.form-group.has-icon::before {
						content: attr(data-icon);
						position: absolute;
						top: 3rem;
						left: 1.5rem;
						font-size: 1.2rem;
						color: #a0aec0;
						z-index: 1;
					}

					.form-group.has-icon .form-select {
						padding-left: 3.5rem;
					}

					/* Animations */
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

					.form-group {
						animation: fadeInUp 0.6s ease-out;
					}

					.form-group:nth-child(1) {
						animation-delay: 0.1s;
					}

					.form-group:nth-child(2) {
						animation-delay: 0.2s;
					}

					.form-group:nth-child(3) {
						animation-delay: 0.3s;
					}

					.form-group:nth-child(4) {
						animation-delay: 0.4s;
					}

					/* Selected count badge */
					.selected-count {
						position: absolute;
						top: -10px;
						right: -10px;
						background: linear-gradient(135deg, #667eea, #764ba2);
						color: white;
						border-radius: 20px;
						padding: 4px 12px;
						font-size: 0.8rem;
						font-weight: 600;
						min-width: 24px;
						text-align: center;
					}

					.student-list-container {
						position: relative;
					}

					/* Responsive Design */
					@media (max-width : 768px) {
						body {
							padding: 1rem;
						}

						.card-body {
							padding: 2rem;
						}

						.card-header h4 {
							font-size: 1.5rem;
						}

						.student-list {
							min-height: 150px;
							max-height: 300px;
						}
					}

					/* Custom scrollbar */
					.student-list::-webkit-scrollbar {
						width: 8px;
					}

					.student-list::-webkit-scrollbar-track {
						background: #f1f1f1;
						border-radius: 4px;
					}

					.student-list::-webkit-scrollbar-thumb {
						background: linear-gradient(135deg, #667eea, #764ba2);
						border-radius: 4px;
					}

					.student-list::-webkit-scrollbar-thumb:hover {
						background: linear-gradient(135deg, #5a67d8, #6b46c1);
					}
				</style>
			</head>

			<body>
				<div class="container">
					<div class="card">
						<div class="card-header">
							<h4>批次新增考試成績</h4>
						</div>
						<div class="card-body">
							<!-- 課程下拉 -->
							<div class="form-group has-icon" data-icon="📘">
								<label class="form-label">選擇課程</label>
								<select class="form-select" id="courseSelect" name="courseId" required>
									<option value="">-- 請選擇課程 --</option>
								</select>
							</div>

							<!-- 老師下拉 -->
							<div class="form-group has-icon" data-icon="👨‍🏫">
								<label class="form-label">老師姓名</label>
								<select class="form-select" id="teacherSelect" name="teacherId" required>
									<option value="">-- 請先選擇課程 --</option>
								</select>
							</div>
							<!-- 年級下拉選單 -->
							<div class="form-group">
								<label for="gradeSelect">選擇年級：</label>
								<select id="gradeSelect" class="form-select">
									<option value="">-- 請選擇年級 --</option>
									<option value="1">國一</option>
									<option value="2">國二</option>
									<option value="3">國三</option>
								</select>
							</div>

							<!-- 全選按鈕 -->
							<div class="student-list-header" style="margin-bottom: 1rem;">
								<button type="button" id="selectAllBtn"
									class="btn btn-sm btn-outline-primary">全選</button>
							</div>
							<!-- 學生清單容器 -->
							<div id="studentList" class="student-list">
								<div class="empty-state">請先選擇年級</div>
							</div>

							<!-- 考卷下拉 -->
							<div class="form-group has-icon" data-icon="📝">
								<label class="form-label">選擇考試</label>
								<select id="examPaperSelect" class="form-select">
									<option value="">-- 請選擇考試 --</option>
								</select>
							</div>

							<!-- 提交按鈕（用 AJAX 綁定） -->
							<div class="submit-section">
								<button type="button" id="submitBatchBtn" class="btn btn-success">新增成績紀錄</button>
							</div>
						</div>
					</div>
				</div>

				<script>
					$(document).ready(function () {


						const contextPath = "${pageContext.request.contextPath}";
						//console.log(contextPath);
						loadExamPapers(); // 載入所有考卷
						loadAllStudents();//載入所有學生
						// 1. 載入所有課程
						$.ajax({
							url: contextPath + "/api/course/all",
							type: "GET",
							success: function (res) {



								const courseSelect = $("#courseSelect");
								res.data.forEach(course => {
									const option = '<option value="' + course.id + '">' + course.name + '</option>';
									courseSelect.append(option);
								});

							},
							error: function () {
								alert("載入課程失敗");
							}
						});

						// 2. 當選擇課程時，根據課程 ID 查詢老師
						$("#courseSelect").on("change", function () {
							const selectedCourseId = $(this).val();

							// 清空老師選單
							const teacherSelect = $("#teacherSelect");
							teacherSelect.empty();
							teacherSelect.append(`<option value="">-- 請選擇老師 --</option>`);

							if (selectedCourseId) {
								// 發出 AJAX 請求查詢老師
								$.ajax({
									url: contextPath + "/api/course/teacherByCourse",
									type: "GET",
									data: { courseId: selectedCourseId },
									success: function (res) {

										res.data.forEach(teacher => {
											teacherSelect.append('<option value="' + teacher.id + '">' + teacher.name + '</option>');
										});

									},
									error: function () {
										alert("查詢老師失敗");
									}
								});
							} else {
								teacherSelect.html('<option value="">-- 請先選擇課程 --</option>');
							}
						});
						//載入考卷方法
						function loadExamPapers() {
							const select = $("#examPaperSelect");
							select.empty(); // 清空
							select.append('<option value="">-- 請選擇考試 --</option>');

							$.ajax({
								url: contextPath + "/api/exam-papers",
								method: "GET",
								success: function (papers) {
									papers.forEach(paper => {
										// 用字串串接方式建立 option
										const option = '<option value="' + paper.id + '">' + paper.name + '</option>';
										select.append(option);
									});
								},
								error: function () {
									console.error("載入考卷失敗");
								}
							});
						}


						let allStudents = []; // 儲存全部學生資料

						// 取得全部學生資料
						function loadAllStudents() {
							$.ajax({
								url: contextPath + "/api/member/student/all",
								method: "GET",
								success: function (res) {
									allStudents = res.data;
								},
								error: function () {
									alert("學生資料載入失敗");
								}
							});
						}

						// 根據年級篩選並顯示學生
						function displayStudentsByGrade(grade) {
							const listContainer = $("#studentList");
							listContainer.empty();

							const filtered = allStudents.filter(s => s.grade == grade);

							if (filtered.length === 0) {
								listContainer.html('<div class="empty-state">查無學生資料</div>');
								return;
							}

							filtered.forEach(student => {
								const studentHtml =
									'<div class="student-checkbox">' +
									'<input type="checkbox" name="studentIds" value="' + student.id + '">' +
									'<label>' + student.name + '</label>' +
									'</div>';
								listContainer.append(studentHtml);
							});

							// Optional: 點擊整個區塊會切換 checkbox 狀態 & 加上 .checked 樣式
							$(".student-checkbox").on("click", function (e) {
								if (!$(e.target).is("input")) {
									const checkbox = $(this).find('input[type="checkbox"]');
									checkbox.prop("checked", !checkbox.prop("checked"));
								}
								$(this).toggleClass("checked", $(this).find('input[type="checkbox"]').prop("checked"));
							});
						}

						let allSelected = false;

						$("#selectAllBtn").on("click", function () {
							allSelected = !allSelected;

							// 切換所有 checkbox 狀態
							$("#studentList input[type='checkbox']").each(function () {
								$(this).prop("checked", allSelected);
								$(this).closest(".student-checkbox").toggleClass("checked", allSelected);
							});

							// 更新按鈕文字
							$(this).text(allSelected ? "取消全選" : "全選");
						});
						// 綁定年級下拉變更事件
						$("#gradeSelect").on("change", function () {
							const selectedGrade = $(this).val();
							if (selectedGrade) {
								displayStudentsByGrade(selectedGrade);
							} else {
								$("#studentList").html('<div class="empty-state">請先選擇年級</div>');
							}
						});



						$("#submitBatchBtn").on("click", function () {
							const teacherId = $("#teacherSelect").val();
							const examPaperId = $("#examPaperSelect").val();
							const selectedStudents = [];

							// 收集所有選取的學生 ID
							$("#studentList input[name='studentIds']:checked").each(function () {
								selectedStudents.push($(this).val());
							});

							// 驗證資料
							if (!teacherId || !examPaperId || selectedStudents.length === 0) {
								Swal.fire("錯誤", "請選擇老師、考卷與至少一位學生", "error");
								return;
							}

							// 發出 AJAX POST 請求
							$.ajax({
								url: contextPath + "/api/exam-results/batchInsert",
								type: "POST",
								traditional: true, // 讓陣列正確序列化
								data: {
									teacherId: teacherId,
									examPaperId: examPaperId,
									studentIds: selectedStudents
								},
								success: function (msg) {
									Swal.fire({
										title: "成功",
										text: msg,
										icon: "success",
										showCancelButton: true,
										confirmButtonText: "查看成績",
										cancelButtonText: "留在本頁"
									}).then((result) => {
										if (result.isConfirmed) {
											window.location.href = contextPath + "/SelectExamResult";
										}
									});
								},
								error: function () {
									Swal.fire("錯誤", "資料儲存失敗，請稍後再試", "error");
								}
							});
						});
					});


				</script>
			</body>

			</html>