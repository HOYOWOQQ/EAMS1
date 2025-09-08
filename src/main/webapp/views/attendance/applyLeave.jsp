<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
		<!DOCTYPE html>
		<html lang="zh-TW">

		<head>
			<meta charset="UTF-8">
			<meta name="viewport" content="width=device-width, initial-scale=1.0">
			<title>請假申請</title>
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
					display: flex;
					align-items: center;
					justify-content: center;
					padding: 20px;
				}

				.container {
					background: rgba(255, 255, 255, 0.95);
					backdrop-filter: blur(20px);
					border-radius: 24px;
					box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
					padding: 3rem;
					width: 100%;
					max-width: 600px;
					animation: slideUp 0.6s ease-out;
				}

				@keyframes slideUp {
					from {
						opacity: 0;
						transform: translateY(30px);
					}

					to {
						opacity: 1;
						transform: translateY(0);
					}
				}

				.page-title {
					text-align: center;
					font-size: 32px;
					font-weight: 700;
					margin-bottom: 2rem;
					background: linear-gradient(135deg, #667eea, #764ba2);
					-webkit-background-clip: text;
					-webkit-text-fill-color: transparent;
					letter-spacing: 1px;
				}

				.message {
					margin-bottom: 1.5rem;
					padding: 16px 20px;
					border-radius: 12px;
					font-weight: 500;
					animation: fadeIn 0.5s ease-out;
				}

				.error-message {
					background: linear-gradient(135deg, rgba(255, 107, 107, 0.15), rgba(238, 90, 82, 0.15));
					color: #e53e3e;
					border: 1px solid rgba(255, 107, 107, 0.3);
				}

				.success-message {
					background: linear-gradient(135deg, rgba(72, 187, 120, 0.15), rgba(56, 178, 172, 0.15));
					color: #38a169;
					border: 1px solid rgba(72, 187, 120, 0.3);
				}

				.form-container {
					background: rgba(255, 255, 255, 0.8);
					border-radius: 16px;
					padding: 2rem;
					box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
				}

				.form-row {
					margin-bottom: 1.5rem;
					display: flex;
					align-items: flex-start;
					gap: 1rem;
				}

				.form-label {
					min-width: 120px;
					font-weight: 600;
					color: #4a5568;
					padding-top: 12px;
					font-size: 16px;
				}

				.form-input {
					flex: 1;
					position: relative;
				}

				select,
				input[type="date"],
				textarea,
				input[type="file"] {
					width: 100%;
					padding: 14px 18px;
					border: 2px solid rgba(102, 126, 234, 0.2);
					border-radius: 12px;
					font-size: 16px;
					font-family: inherit;
					background: rgba(255, 255, 255, 0.9);
					transition: all 0.3s ease;
					outline: none;
				}

				select:focus,
				input[type="date"]:focus,
				textarea:focus {
					border-color: #667eea;
					box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
					transform: translateY(-1px);
				}

				select:hover,
				input[type="date"]:hover,
				textarea:hover {
					border-color: rgba(102, 126, 234, 0.4);
				}

				textarea {
					resize: vertical;
					min-height: 100px;
					font-family: inherit;
				}

				input[type="file"] {
					padding: 12px 18px;
					cursor: pointer;
					background: rgba(102, 126, 234, 0.05);
					border: 2px dashed rgba(102, 126, 234, 0.3);
				}

				input[type="file"]:hover {
					background: rgba(102, 126, 234, 0.1);
					border-color: #667eea;
				}

				.submit-button {
					width: 100%;
					padding: 16px 32px;
					background: linear-gradient(135deg, #667eea, #764ba2);
					color: white;
					border: none;
					border-radius: 16px;
					font-size: 18px;
					font-weight: 600;
					cursor: pointer;
					transition: all 0.3s ease;
					margin-top: 1rem;
					letter-spacing: 0.5px;
				}

				.submit-button:hover {
					transform: translateY(-2px);
					box-shadow: 0 12px 40px rgba(102, 126, 234, 0.4);
				}

				.submit-button:active {
					transform: translateY(0);
				}

				.form-icon {
					display: inline-block;
					margin-right: 8px;
					font-size: 18px;
				}

				/* 響應式設計 */
				@media (max-width: 768px) {
					.container {
						padding: 2rem 1.5rem;
						margin: 10px;
					}

					.page-title {
						font-size: 28px;
					}

					.form-row {
						flex-direction: column;
						gap: 0.5rem;
					}

					.form-label {
						min-width: auto;
						padding-top: 0;
					}

					select,
					input[type="date"],
					textarea,
					input[type="file"] {
						padding: 12px 16px;
						font-size: 16px;
					}
				}

				/* 載入動畫 */
				@keyframes fadeIn {
					from {
						opacity: 0;
						transform: translateX(-20px);
					}

					to {
						opacity: 1;
						transform: translateX(0);
					}
				}

				.form-row {
					animation: fadeIn 0.6s ease-out;
				}

				.form-row:nth-child(1) {
					animation-delay: 0.1s;
				}

				.form-row:nth-child(2) {
					animation-delay: 0.2s;
				}

				.form-row:nth-child(3) {
					animation-delay: 0.3s;
				}

				.form-row:nth-child(4) {
					animation-delay: 0.4s;
				}

				.form-row:nth-child(5) {
					animation-delay: 0.5s;
				}

				.form-row:nth-child(6) {
					animation-delay: 0.6s;
				}

				/* 美化選項樣式 */
				select {
					appearance: none;
					background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23667eea' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6,9 12,15 18,9'%3e%3c/polyline%3e%3c/svg%3e");
					background-repeat: no-repeat;
					background-position: right 12px center;
					background-size: 20px;
					padding-right: 45px;
				}

				/* 文件上傳區域美化 */
				.file-upload-area {
					position: relative;
					overflow: hidden;
				}

				.file-upload-text {
					position: absolute;
					top: 50%;
					left: 50%;
					transform: translate(-50%, -50%);
					pointer-events: none;
					color: #667eea;
					font-weight: 500;
				}

				/* 必填標記 */
				.required::after {
					content: " *";
					color: #e53e3e;
					font-weight: bold;
				}
			</style>
		</head>

		<body>
			<div class="container">
				<h1 class="page-title">
					<span class="form-icon">📋</span>
					請假申請
				</h1>

				<% if (request.getAttribute("error") !=null) { %>
					<div class="message error-message">
						<strong>❌ 錯誤：</strong>
						<%= request.getAttribute("error") %>
					</div>
					<% } %>

						<% if (request.getAttribute("success") !=null) { %>
							<div class="message success-message">
								<strong>✅ 成功：</strong>
								<%= request.getAttribute("success") %>
							</div>
							<% } %>

								<div class="form-container">
									<form method="post" action="${pageContext.request.contextPath}/SubmitLeave"
										enctype="multipart/form-data">

										<div class="form-row">
											<label class="form-label required">
												<span class="form-icon">📚</span>
												課程名稱：
											</label>
											<div class="form-input">
												<select name="courseId" id="courseSelect" required>
													<option value="">請選擇課程</option>
													<!-- JS 動態載入 -->
												</select>
											</div>
										</div>

										<div class="form-row">
											<label class="form-label required">
												<span class="form-icon">📅</span>
												請假日期：
											</label>
											<div class="form-input">
												<input type="date" name="lessonDate" required>
											</div>
										</div>

										<div class="form-row">
											<label class="form-label required">
												<span class="form-icon">⏰</span>
												第幾節：
											</label>
											<div class="form-input">
												<select name="period" required>
													<% for (int i=1; i <=9; i++) { %>
														<option value="<%= i %>">第 <%= i %> 節</option>
														<% } %>
												</select>
											</div>
										</div>

										<div class="form-row">
											<label class="form-label required">
												<span class="form-icon">📝</span>
												請假類型：
											</label>
											<div class="form-input">
												<select name="leaveType" required>
													<option value="">請選擇請假類型</option>
													<option value="事假">事假</option>
													<option value="病假">病假</option>
													<option value="其他">其他</option>
												</select>
											</div>
										</div>

										<div class="form-row">
											<label class="form-label required">
												<span class="form-icon">💬</span>
												請假原因：
											</label>
											<div class="form-input">
												<textarea name="reason" rows="4" placeholder="請詳細說明請假原因..."
													required></textarea>
											</div>
										</div>

										<div class="form-row">
											<label class="form-label">
												<span class="form-icon">📎</span>
												附件：
											</label>
											<div class="form-input file-upload-area">
												<input type="file" name="attachmentFile" accept="image/*">
												<div class="file-upload-text"></div>
											</div>
										</div>

										<button type="submit" class="submit-button">
											<span class="form-icon">🚀</span>
											送出申請
										</button>
									</form>
								</div>
			</div>

			<script>
				// 添加互動效果
				document.addEventListener('DOMContentLoaded', function () {
					// 表單驗證提示
					const form = document.querySelector('form');
					const inputs = form.querySelectorAll('select[required], input[required], textarea[required]');

					inputs.forEach(input => {
						input.addEventListener('blur', function () {
							if (!this.value.trim()) {
								this.style.borderColor = '#e53e3e';
								this.style.boxShadow = '0 0 0 3px rgba(229, 62, 62, 0.15)';
							} else {
								this.style.borderColor = '#48bb78';
								this.style.boxShadow = '0 0 0 3px rgba(72, 187, 120, 0.15)';
							}
						});

						input.addEventListener('focus', function () {
							this.style.borderColor = '#667eea';
							this.style.boxShadow = '0 0 0 3px rgba(102, 126, 234, 0.15)';
						});
					});

					// 文件上傳美化
					const fileInput = document.querySelector('input[type="file"]');
					const fileText = document.querySelector('.file-upload-text');

					if (fileInput && fileText) {
						fileInput.addEventListener('change', function () {
							if (this.files.length > 0) {
								fileText.textContent = `已選擇：\${this.files[0].name}`;
								fileText.style.color = '#48bb78';
							} else {
								fileText.textContent = '點擊選擇圖片文件（可選）';
								fileText.style.color = '#667eea';
							}
						});
					}

					// 表單提交效果
					form.addEventListener('submit', function () {
						const submitBtn = document.querySelector('.submit-button');
						submitBtn.innerHTML = '<span class="form-icon">⏳</span>處理中...';
						submitBtn.disabled = true;
					});

					// 動態載入課程
					fetch('CourseListApi') // 請依實際API路徑調整
						.then(res => res.json())
						.then(courses => {
							const select = document.getElementById('courseSelect');
							courses.forEach(c => {
								const opt = document.createElement('option');
								opt.value = c.id;
								opt.textContent = c.name;
								select.appendChild(opt);
							});
						})
						.catch(err => {
							console.error("課程載入失敗", err);
						});
				});

				// 設置最小日期為今天
				//document.querySelector('input[type="date"]').min = new Date().toISOString().split('T')[0];
			</script>
		</body>

		</html>