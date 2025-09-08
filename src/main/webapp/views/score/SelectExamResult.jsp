<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<% String path=request.getContextPath(); %>
			<!DOCTYPE html>
			<html lang="zh-Hant">

			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>考試成績管理</title>

				<!-- DataTables CSS -->
				<link
					href="https://cdn.datatables.net/v/dt/jq-3.7.0/dt-2.3.2/b-3.2.3/cr-2.1.1/cc-1.0.6/datatables.min.css"
					rel="stylesheet">

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
						max-width: 1400px;
						margin: 0 auto;
						animation: fadeInUp 0.6s ease-out;
					}

					.page-header {
						text-align: center;
						margin-bottom: 2rem;
						color: white;
					}

					.page-header h1 {
						font-size: 2.5rem;
						font-weight: 700;
						letter-spacing: 2px;
						margin-bottom: 0.5rem;
					}

					.page-header::before {
						content: "📊";
						font-size: 4rem;
						display: block;
						margin-bottom: 1rem;
					}

					.filter-section {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(20px);
						border-radius: 16px;
						padding: 1.5rem;
						margin-bottom: 2rem;
						box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
						border: 1px solid rgba(255, 255, 255, 0.2);
					}

					.filter-title {
						font-size: 1.2rem;
						font-weight: 600;
						color: #4a5568;
						margin-bottom: 1rem;
						display: flex;
						align-items: center;
					}

					.filter-title::before {
						content: "🔍";
						margin-right: 0.5rem;
						font-size: 1.5rem;
					}

					.filter-form select {
						width: 100%;
						max-width: 300px;
						padding: 1rem 1.5rem;
						border: 2px solid #e2e8f0;
						border-radius: 12px;
						font-size: 1rem;
						background: white;
						transition: all 0.3s ease;
						cursor: pointer;
					}

					.filter-form select:focus {
						outline: none;
						border-color: #667eea;
						box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
						transform: translateY(-2px);
					}

					.filter-form select:hover {
						border-color: #cbd5e0;
					}

					.card {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(20px);
						border-radius: 24px;
						box-shadow: 0 16px 48px rgba(0, 0, 0, 0.1);
						border: 1px solid rgba(255, 255, 255, 0.2);
						overflow: hidden;
					}

					.card-body {
						padding: 2rem;
					}

					/* DataTables Styling */
					.dataTables_wrapper {
						color: #333;
					}

					.dataTables_length,
					.dataTables_filter,
					.dataTables_info,
					.dataTables_paginate {
						margin: 1rem 0;
					}

					.dataTables_length label,
					.dataTables_filter label {
						font-weight: 600;
						color: #4a5568;
					}

					.dataTables_filter input {
						border: 2px solid #e2e8f0;
						border-radius: 8px;
						padding: 0.5rem 1rem;
						margin-left: 0.5rem;
						transition: all 0.3s ease;
					}

					.dataTables_filter input:focus {
						outline: none;
						border-color: #667eea;
						box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
					}

					#myTable {
						width: 100% !important;
						border-collapse: collapse;
						background: white;
						border-radius: 12px;
						overflow: hidden;
						box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
					}

					#myTable thead th {
						background: linear-gradient(135deg, #667eea, #764ba2);
						color: white;
						font-weight: 600;
						padding: 1rem 0.75rem;
						text-align: center;
						font-size: 0.9rem;
						text-transform: uppercase;
						letter-spacing: 0.5px;
						border: none;
					}

					#myTable tbody td {
						padding: 1rem 0.75rem;
						border-bottom: 1px solid rgba(0, 0, 0, 0.05);
						text-align: center;
						vertical-align: middle;
						font-size: 0.9rem;
					}

					#myTable tbody tr {
						transition: all 0.3s ease;
					}

					#myTable tbody tr:hover {
						background: rgba(102, 126, 234, 0.05);
						transform: scale(1.01);
					}

					/* Score styling based on value */
					.score-excellent {
						background: linear-gradient(135deg, #48bb78, #38a169);
						color: white;
						padding: 0.25rem 0.75rem;
						border-radius: 12px;
						font-weight: 700;
						display: inline-block;
					}

					.score-good {
						background: linear-gradient(135deg, #4dabf7, #339af0);
						color: white;
						padding: 0.25rem 0.75rem;
						border-radius: 12px;
						font-weight: 700;
						display: inline-block;
					}

					.score-average {
						background: linear-gradient(135deg, #fbb036, #f6931e);
						color: white;
						padding: 0.25rem 0.75rem;
						border-radius: 12px;
						font-weight: 700;
						display: inline-block;
					}

					.score-poor {
						background: linear-gradient(135deg, #ff6b6b, #ee5a52);
						color: white;
						padding: 0.25rem 0.75rem;
						border-radius: 12px;
						font-weight: 700;
						display: inline-block;
					}

					/* Rank styling */
					.rank-badge {
						background: linear-gradient(135deg, rgba(102, 126, 234, 0.1),
								rgba(118, 75, 162, 0.1));
						color: #667eea;
						padding: 0.25rem 0.75rem;
						border-radius: 12px;
						font-weight: 600;
						display: inline-block;
					}

					.rank-top3 {
						background: linear-gradient(135deg, #ffd700, #ffed4a);
						color: #744210;
					}

					/* Student name styling */
					.student-name {
						font-weight: 600;
						color: #4a5568;
					}

					/* Exam name styling */
					.exam-name {
						color: #667eea;
						font-weight: 500;
					}

					/* Teacher name styling */
					.teacher-name {
						color: #718096;
						font-style: italic;
					}

					/* Time styling */
					.review-time {
						color: #a0aec0;
						font-size: 0.85rem;
					}

					/* Button Styles */
					.action-btn {
						padding: 0.5rem 1rem;
						border: none;
						border-radius: 8px;
						font-size: 0.85rem;
						font-weight: 600;
						cursor: pointer;
						transition: all 0.3s ease;
						margin: 0 0.25rem;
						text-transform: uppercase;
						letter-spacing: 0.5px;
					}

					.delete-btn {
						background: linear-gradient(135deg, #ff6b6b, #ee5a52);
						color: white;
						box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
					}

					.delete-btn:hover {
						transform: translateY(-2px);
						box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
					}

					.edit-btn {
						background: linear-gradient(135deg, #4dabf7, #339af0);
						color: white;
						box-shadow: 0 2px 8px rgba(77, 171, 247, 0.3);
					}

					.edit-btn:hover {
						transform: translateY(-2px);
						box-shadow: 0 4px 12px rgba(77, 171, 247, 0.4);
					}

					/* DataTables Pagination */
					.dataTables_paginate .paginate_button {
						padding: 0.5rem 1rem !important;
						margin: 0 0.25rem !important;
						border-radius: 8px !important;
						border: 2px solid #e2e8f0 !important;
						background: white !important;
						color: #4a5568 !important;
						transition: all 0.3s ease !important;
					}

					.dataTables_paginate .paginate_button:hover {
						background: #667eea !important;
						color: white !important;
						border-color: #667eea !important;
						transform: translateY(-2px) !important;
					}

					.dataTables_paginate .paginate_button.current {
						background: linear-gradient(135deg, #667eea, #764ba2) !important;
						color: white !important;
						border-color: #667eea !important;
					}

					/* Loading Animation */
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

					/* Responsive Design */
					@media (max-width : 768px) {
						body {
							padding: 1rem;
						}

						.card-body {
							padding: 1rem;
						}

						.page-header h1 {
							font-size: 2rem;
						}

						#myTable {
							font-size: 0.8rem;
						}

						#myTable thead th,
						#myTable tbody td {
							padding: 0.5rem 0.25rem;
						}

						.action-btn {
							padding: 0.4rem 0.8rem;
							font-size: 0.75rem;
							margin: 0.1rem;
						}

						.filter-form select {
							max-width: 100%;
						}
					}

					/* Custom SweetAlert2 styling */
					.swal2-popup {
						border-radius: 20px;
						font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
					}

					.swal2-confirm {
						background: linear-gradient(135deg, #667eea, #764ba2) !important;
						border-radius: 10px !important;
					}

					.swal2-cancel {
						border-radius: 10px !important;
					}

					.swal2-input {
						border: 2px solid #e2e8f0 !important;
						border-radius: 8px !important;
						padding: 0.75rem 1rem !important;
						font-size: 1rem !important;
						transition: all 0.3s ease !important;
						margin: 0.5rem 0 !important;
					}

					.swal2-input:focus {
						border-color: #667eea !important;
						box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
					}

					.edit-form-label {
						display: block;
						text-align: left;
						margin: 1rem 0 0.5rem 0;
						font-weight: 600;
						color: #4a5568;
					}
				</style>
			</head>

			<body>
				<div class="container">
					<div class="page-header">
						<h1>考試成績管理</h1>
						<p>查看和管理所有學生考試成績</p>
					</div>


					<div class="filter-section">
						<div class="filter-title">依考卷篩選</div>
						<select id="examPaperSelect">
							<option value="">-- 全部考卷 --</option>

						</select>
					</div>


					<div class="card">
						<div class="card-body">
							<table id="myTable" class="display">
								<thead>
									<tr>
										<th>學生姓名</th>
										<th>考試分數</th>
										<th>班級排名</th>
										<th>考試名稱</th>
										<th>批改老師</th>
										<th>批改時間</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>

								</tbody>
							</table>
						</div>
					</div>
				</div>

				<!-- Scripts -->
				<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
				<script
					src="https://cdn.datatables.net/v/dt/jq-3.7.0/dt-2.3.2/b-3.2.3/cr-2.1.1/cc-1.0.6/datatables.min.js"></script>
				<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

				<script>
					$(document).ready(function () {
						const contextPath = "${pageContext.request.contextPath}";

						// 初始化 DataTable
						const table = $('#myTable').DataTable({
							language: {
								lengthMenu: "顯示 _MENU_ 筆資料",
								zeroRecords: "沒有找到符合的資料",
								info: "顯示第 _START_ 到 _END_ 筆資料，共 _TOTAL_ 筆",
								infoEmpty: "顯示第 0 到 0 筆資料，共 0 筆",
								infoFiltered: "(從 _MAX_ 筆資料中過濾)",
								search: "搜尋:",
								paginate: {
									first: "第一頁", last: "最後一頁", next: "下一頁", previous: "上一頁"
								}
							},
							pageLength: 10,
							responsive: true,
							order: [[1, 'desc']]
						});

						// 載入考卷名稱選單
						$.ajax({
							url: contextPath + "/api/exam-papers/names",
							method: "GET",
							success: function (data) {
								const select = $("#examPaperSelect");
								data.forEach(paper => {
									select.append('<option value="' + paper + '">' + paper + '</option>');
									console.log(paper);

								});
							},
							error: function () {
								console.error("載入考卷清單失敗");
							}
						});

						// 初次載入所有成績
						loadExamResults("");

						// 下拉變更時查詢成績
						$("#examPaperSelect").on("change", function () {
							const selectedPaper = $(this).val();
							console.log("選擇的考卷：", selectedPaper);

							if (selectedPaper === "") {
								// 如果選擇的是「全部考卷」
								loadExamResults("");  // 使用載入全部的函式
							} else {
								// 使用依考卷查詢的函式
								loadExamResults2(selectedPaper);
							}

							console.log("change ok");
						});

						// AJAX 查詢成績主函式(初次)
						function loadExamResults(examPaperName) {
							$.ajax({
								url: contextPath + "/api/exam-results",
								method: "GET",
								data: { examPaper: examPaperName },
								success: function (results) {
									table.clear();

									results.forEach(er => {
										const scoreClass = getScoreClass(er.score);
										const rankClass = getRankClass(er.rankInClass);
										const reviewedTime = formatDatetimeDisplay(er.reviewedTime);
										//console.log(er);
										table.row.add([
											'<span class="student-name">' + er.studentName + '</span>',
											'<span class="' + scoreClass + '">' + er.score + '</span>',
											'<span class="' + rankClass + '">' + (er.rankInClass ? '第 ' + er.rankInClass + ' 名' : '') + '</span>',
											'<span class="exam-name">' + er.examPaperName + '</span>',
											'<span class="teacher-name">' + er.reviewName + '</span>',
											'<span class="review-time">' + reviewedTime + '</span>',
											'<button class="action-btn edit-btn" ' +
											'data-student="' + er.studentName + '" ' +
											'data-exampaper="' + er.examPaperName + '" ' +
											'data-score="' + er.score + '" ' +
											'data-rank="' + er.rankInClass + '" ' +
											'data-reviewedtime="' + er.reviewedTime + '">' +
											'修改</button> ' +
											'<button class="action-btn delete-btn" ' +
											'data-studentid="' + er.student_id + '" ' +
											'data-exampaperid="' + er.exam_paper_id + '">' +
											'刪除</button>'
										]);
									});

									table.draw();
								},
								error: function () {
									console.error("查詢成績失敗");
								}
							});
						}

						// AJAX 查詢成績主函式(後續)
						function loadExamResults2(examPaperName) {
							$.ajax({
								url: contextPath + "/api/exam-results/paper/" + encodeURIComponent(examPaperName),
								method: "GET",
								data: { examPaper: examPaperName },
								success: function (results) {
									table.clear();

									results.forEach(er => {
										const scoreClass = getScoreClass(er.score);
										const rankClass = getRankClass(er.rankInClass);
										const reviewedTime = formatDatetimeDisplay(er.reviewedTime);


										table.row.add([
											'<span class="student-name">' + er.studentName + '</span>',
											'<span class="' + scoreClass + '">' + er.score + '</span>',
											'<span class="' + rankClass + '">' + (er.rankInClass ? '第 ' + er.rankInClass + ' 名' : '') + '</span>',
											'<span class="exam-name">' + er.examPaperName + '</span>',
											'<span class="teacher-name">' + er.reviewName + '</span>',
											'<span class="review-time">' + reviewedTime + '</span>',
											'<button class="action-btn edit-btn" ' +
											'data-student="' + er.studentName + '" ' +
											'data-exampaper="' + er.examPaperName + '" ' +
											'data-score="' + er.score + '" ' +
											'data-rank="' + er.rankInClass + '" ' +
											'data-reviewedtime="' + er.reviewedTime + '">' +
											'修改</button> ' +
											'<button class="action-btn delete-btn" ' +
											'data-studentid="' + er.student_id + '" ' +
											'data-exampaperid="' + er.exam_paper_id + '">' +
											'刪除</button>'
										]);
									});

									table.draw();
								},
								error: function () {
									console.error("查詢成績失敗");
								}
							});
						}
						function getScoreClass(score) {
							if (score == null) return "";
							if (score >= 90) return "score-excellent";
							if (score >= 80) return "score-good";
							if (score >= 60) return "score-average";
							return "score-poor";
						}

						function getRankClass(rank) {
							if (rank == null) return "";
							return rank <= 3 ? "rank-badge rank-top3" : "rank-badge";
						}

						function formatDatetimeDisplay(datetimeStr) {
							if (!datetimeStr) return '';
							let dt = new Date(datetimeStr);
							return dt.toLocaleString('zh-TW', {
								year: 'numeric', month: '2-digit', day: '2-digit',
								hour: '2-digit', minute: '2-digit'
							});
						}

						$('#myTable tbody').on('click', '.edit-btn', function () {
							const studentName = $(this).data('student');
							const examPaperName = $(this).data('exampaper');
							const currentScore = $(this).data('score');
							const currentRank = $(this).data('rank');

							const editFormHtml =
								'<label class="edit-form-label">學生姓名</label>' +
								'<input type="text" class="swal2-input" value="' + studentName + '" disabled>' +

								'<label class="edit-form-label">考卷名稱</label>' +
								'<input type="text" class="swal2-input" value="' + examPaperName + '" disabled>' +

								'<label class="edit-form-label">成績（0~100）</label>' +
								'<input id="swal-input-score" type="number" class="swal2-input" value="' + currentScore + '" min="0" max="100">' +

								'<label class="edit-form-label">班級排名</label>' +
								'<input id="swal-input-rank" type="number" class="swal2-input" value="' + currentRank + '" min="1">';

							Swal.fire({
								title: '修改成績',
								html: editFormHtml,
								focusConfirm: false,
								showCancelButton: true,
								confirmButtonText: '儲存修改',
								cancelButtonText: '取消',
								preConfirm: () => {
									const score = parseInt(document.getElementById('swal-input-score').value);
									const rank = parseInt(document.getElementById('swal-input-rank').value);

									if (isNaN(score) || score < 0 || score > 100) {
										Swal.showValidationMessage('請輸入 0~100 之間的分數');
										return false;
									}
									if (isNaN(rank) || rank < 1) {
										Swal.showValidationMessage('請輸入正整數作為班級排名');
										return false;
									}
									return { score, rank };
								}
							}).then((result) => {
								if (result.isConfirmed) {
									const { score, rank } = result.value;

									$.ajax({
										url: contextPath + "/api/exam-results/update",
										type: "PUT",
										data: {
											studentName: studentName,
											examPaperName: examPaperName,
											score: score,
											rankInClass: rank
										},
										success: function () {
											Swal.fire({
												icon: 'success',
												title: '修改成功',
												timer: 1500,
												showConfirmButton: false
											});
											const selectpaper = ($('#examPaperSelect').val());
											if (selectpaper === "") {
												loadExamResults("");
											} else {
												loadExamResults2(selectpaper);
											}
										},
										error: function () {
											Swal.fire({
												icon: 'error',
												title: '修改失敗',
												text: '請稍後再試或聯絡管理員'
											});
										}
									});
								}
							});
						});

						// 綁定刪除按鈕事件（事件委派方式）
						$('#myTable tbody').on('click', '.delete-btn', function () {
							const studentId = $(this).data('studentid');
							const examPaperId = $(this).data('exampaperid');
							console.log(studentId);
							console.log(examPaperId);


							Swal.fire({
								title: '確定要刪除嗎？',
								text: '此操作無法復原，請再次確認！',
								icon: 'warning',
								showCancelButton: true,
								confirmButtonColor: '#d33',
								cancelButtonColor: '#3085d6',
								confirmButtonText: '是的，刪除！',
								cancelButtonText: '取消'
							}).then((result) => {
								if (result.isConfirmed) {
									$.ajax({
										url: contextPath + "/api/exam-results/delete?studentId=" + encodeURIComponent(studentId) +
											"&examPaperId=" + encodeURIComponent(examPaperId),
										type: "DELETE",
										success: function () {
											Swal.fire({
												icon: 'success',
												title: '刪除成功',
												timer: 1500,
												showConfirmButton: false
											});
											const selectpaper = ($('#examPaperSelect').val());
											if (selectpaper === "") {

												loadExamResults("");
											} else {

												loadExamResults2(selectpaper);
											}

										},
										error: function () {
											Swal.fire({
												icon: 'error',
												title: '刪除失敗',
												text: '請稍後再試或聯絡管理員'
											});
										}
									});
								}
							});
						});

					});




				</script>

			</body>

			</html>