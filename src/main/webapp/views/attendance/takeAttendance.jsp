<%@ page contentType="text/html; charset=UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<% String contextPath=request.getContextPath(); %>
			<!DOCTYPE html>
			<html lang="zh-TW">

			<head>
				<meta charset="UTF-8">
				<title>老師點名系統</title>
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
						padding: 20px;
					}

					.container {
						max-width: 1000px;
						margin: 0 auto;
					}

					/* Header Section */
					.header-section {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(10px);
						border-radius: 16px;
						padding: 1.5rem;
						margin-bottom: 1.5rem;
						box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
						text-align: center;
					}

					.page-title {
						font-size: 2rem;
						font-weight: 700;
						color: #333;
						margin-bottom: 0.5rem;
					}

					.page-subtitle {
						color: #666;
						font-size: 1rem;
					}

					/* Course Selection Section */
					.course-selection-section {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(10px);
						border-radius: 16px;
						padding: 2rem;
						box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
					}

					.selection-title {
						font-size: 1.3rem;
						font-weight: 600;
						color: #333;
						margin-bottom: 1.5rem;
						display: flex;
						align-items: center;
						gap: 10px;
					}

					.course-form {
						display: flex;
						flex-direction: column;
						gap: 1.5rem;
						max-width: 600px;
						margin: 0 auto;
					}

					.form-group {
						display: flex;
						flex-direction: column;
					}

					.form-label {
						font-weight: 600;
						color: #333;
						margin-bottom: 8px;
						font-size: 16px;
					}

					.course-select {
						padding: 16px 20px;
						border: 2px solid #e1e5e9;
						border-radius: 12px;
						font-size: 16px;
						background: white;
						transition: all 0.3s ease;
						cursor: pointer;
					}

					.course-select:focus {
						outline: none;
						border-color: #667eea;
						box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
					}

					.course-select option {
						padding: 12px;
						font-size: 16px;
					}

					.submit-btn {
						background: linear-gradient(135deg, #667eea, #764ba2);
						color: white;
						border: none;
						padding: 16px 32px;
						border-radius: 12px;
						font-size: 18px;
						font-weight: 600;
						cursor: pointer;
						transition: all 0.3s ease;
						display: flex;
						align-items: center;
						justify-content: center;
						gap: 10px;
						margin-top: 1rem;
					}

					.submit-btn:hover:not(:disabled) {
						transform: translateY(-2px);
						box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
					}

					.submit-btn:disabled {
						background: #ccc;
						cursor: not-allowed;
						transform: none;
						box-shadow: none;
					}

					/* No Courses Section */
					.no-courses-section {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(10px);
						border-radius: 16px;
						padding: 3rem;
						box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
						text-align: center;
					}

					.no-courses-icon {
						font-size: 64px;
						margin-bottom: 1rem;
					}

					.no-courses-title {
						font-size: 1.5rem;
						font-weight: 600;
						color: #333;
						margin-bottom: 1rem;
					}

					.no-courses-message {
						color: #666;
						font-size: 1.1rem;
						line-height: 1.6;
					}

					/* Loading Section */
					.loading-section {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(10px);
						border-radius: 16px;
						padding: 3rem;
						box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
						text-align: center;
					}

					/* Error Section */
					.error-section {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(10px);
						border-radius: 16px;
						padding: 3rem;
						box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
						text-align: center;
					}

					.error-icon {
						font-size: 64px;
						margin-bottom: 1rem;
					}

					.error-title {
						font-size: 1.5rem;
						font-weight: 600;
						color: #dc3545;
						margin-bottom: 1rem;
					}

					.error-message {
						color: #666;
						font-size: 1.1rem;
						line-height: 1.6;
						margin-bottom: 1.5rem;
					}

					.retry-btn {
						background: #dc3545;
						color: white;
						border: none;
						padding: 12px 24px;
						border-radius: 8px;
						font-size: 16px;
						font-weight: 600;
						cursor: pointer;
						transition: all 0.3s ease;
					}

					.retry-btn:hover {
						background: #c82333;
						transform: translateY(-1px);
					}

					/* Course Grid (Alternative Display) */
					.course-grid {
						display: grid;
						grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
						gap: 1.5rem;
						margin-top: 2rem;
					}

					.course-card {
						background: rgba(255, 255, 255, 0.95);
						backdrop-filter: blur(10px);
						border-radius: 16px;
						padding: 1.5rem;
						box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
						transition: all 0.3s ease;
						cursor: pointer;
						border: 2px solid transparent;
					}

					.course-card:hover {
						transform: translateY(-2px);
						border-color: #667eea;
						box-shadow: 0 6px 25px rgba(0, 0, 0, 0.15);
					}

					.course-card.selected {
						border-color: #667eea;
						background: rgba(102, 126, 234, 0.05);
					}

					.course-name {
						font-size: 1.2rem;
						font-weight: 600;
						color: #333;
						margin-bottom: 0.5rem;
					}

					.course-info {
						color: #666;
						font-size: 0.9rem;
						margin-bottom: 0.5rem;
					}

					.course-time {
						background: rgba(102, 126, 234, 0.1);
						color: #667eea;
						padding: 4px 8px;
						border-radius: 6px;
						font-size: 0.8rem;
						font-weight: 600;
						display: inline-block;
					}

					/* Time Display */
					.current-time {
						background: rgba(102, 126, 234, 0.1);
						color: #667eea;
						padding: 8px 16px;
						border-radius: 8px;
						font-size: 14px;
						margin-bottom: 1rem;
						text-align: center;
					}

					/* Responsive */
					@media (max-width: 768px) {
						.container {
							padding: 0 10px;
						}

						.page-title {
							font-size: 1.5rem;
						}

						.course-selection-section {
							padding: 1.5rem;
						}

						.course-form {
							max-width: none;
						}

						.course-grid {
							grid-template-columns: 1fr;
						}
					}

					/* Loading Animation */
					.loading {
						display: inline-block;
						width: 40px;
						height: 40px;
						border: 4px solid rgba(102, 126, 234, 0.3);
						border-radius: 50%;
						border-top-color: #667eea;
						animation: spin 1s ease-in-out infinite;
						margin-bottom: 1rem;
					}

					.btn-loading {
						width: 20px;
						height: 20px;
						border: 3px solid rgba(255, 255, 255, 0.3);
						border-top-color: white;
					}

					@keyframes spin {
						to {
							transform: rotate(360deg);
						}
					}

					.hidden {
						display: none !important;
					}
				</style>
			</head>

			<body>
				<div class="container">
					<!-- Header -->
					<div class="header-section">
						<h1 class="page-title">📋 老師點名系統</h1>
						<p class="page-subtitle">選擇今日授課課程進行點名</p>
						<div class="current-time" id="currentTime">
							🕐 載入時間中...
						</div>
					</div>

					<!-- Loading Section -->
					<div class="loading-section" id="loadingSection">
						<div class="loading"></div>
						<h2 class="no-courses-title">載入中...</h2>
						<p class="no-courses-message">正在獲取今日課程安排</p>
					</div>

					<!-- Error Section -->
					<div class="error-section hidden" id="errorSection">
						<div class="error-icon">⚠️</div>
						<h2 class="error-title">載入失敗</h2>
						<p class="error-message" id="errorMessage">
							無法獲取課程資料，請檢查網絡連接
						</p>
						<button class="retry-btn" onclick="loadCourses()">🔄 重新載入</button>
					</div>

					<!-- No Courses Section -->
					<div class="no-courses-section hidden" id="noCoursesSection">
						<div class="no-courses-icon">📅</div>
						<h2 class="no-courses-title">今日無授課安排</h2>
						<p class="no-courses-message">
							您今天沒有排定的授課課程<br>
							請確認課表安排或聯繫教務處
						</p>
					</div>

					<!-- Course Selection Section -->
					<div class="course-selection-section hidden" id="courseSelectionSection">
						<h2 class="selection-title">
							📚 請選擇要點名的課程
						</h2>

						<form class="course-form" id="courseForm">
							<div class="form-group">
								<label for="courseScheduleId" class="form-label">今日授課課程清單</label>
								<select name="courseScheduleId" id="courseScheduleId" class="course-select" required>
									<option value="">-- 請選擇要點名的課程 --</option>
								</select>
							</div>

							<button type="submit" class="submit-btn" id="submitBtn" disabled>
								📝 請先選擇課程
							</button>
						</form>

						<!-- Alternative Card View (Hidden by default) -->
						<div class="course-grid hidden" id="courseGrid">
							<!-- 動態生成的課程卡片 -->
						</div>
					</div>
				</div>

				<script>
					let courseData = [];
					let selectedCourseId = null;

					// 頁面載入完成後初始化
					document.addEventListener('DOMContentLoaded', function () {
						updateCurrentTime();
						setInterval(updateCurrentTime, 1000);
						loadCourses();

						// 綁定表單事件
						setupEventListeners();
					});

					// 載入課程資料
				async function loadCourses() {
					showSection('loadingSection');

					try {
						const response = await fetch('${pageContext.request.contextPath}/api/attendances/todaySchedules', {
							method: 'GET',
							headers: {
								'Content-Type': 'application/json'
							}
						});
						console.log('載入課程資料...', response);

						if (!response.ok) {
							throw new Error(`HTTP error! status: ${response.status}`);
						}

						const data = await response.json();
						console.log("回傳資料:", data);

						if (Array.isArray(data)) {
							courseData = data;
							handleCoursesLoaded();
						} else {
							showError('載入課程失敗');
						}
					} catch (error) {
						console.error('載入課程出錯:', error);
						showError('連接服務器失敗，請檢查網絡連接或稍後再試');
					}
				}


					// 處理課程載入完成
					function handleCoursesLoaded() {
						if (courseData && courseData.length > 0) {
							renderCourseOptions();
							renderCourseCards();
							showSection('courseSelectionSection');
						} else {
							showSection('noCoursesSection');
						}
					}

					// 渲染課程選項
					function renderCourseOptions() {
						const select = document.getElementById('courseScheduleId');
						const defaultOption = select.querySelector('option[value=""]');

						// 清空選項，保留預設選項
						select.innerHTML = '';
						select.appendChild(defaultOption);

						courseData.forEach(course => {
							const option = document.createElement('option');
							option.value = course.id;
							option.textContent = `\${course.courseName} ｜ 星期\${course.weekdayText} 第\${course.periodStart}-\${course.periodEnd}節`;
							select.appendChild(option);
						});
					}

					// 渲染課程卡片
					function renderCourseCards() {
						const grid = document.getElementById('courseGrid');
						grid.innerHTML = '';

						courseData.forEach(course => {
							const card = createCourseCard(course);
							grid.appendChild(card);
						});
					}

					// 創建課程卡片
					function createCourseCard(course) {
						const card = document.createElement('div');
						card.className = 'course-card';
						card.onclick = () => selectCourse(course.id, card);

						card.innerHTML = `
						<div class="course-name">\${escapeHtml(course.courseName)}</div>
						<div class="course-info">星期\${escapeHtml(course.weekdayText)}</div>
						<div class="course-time">第 \${course.periodStart}-\${course.periodEnd} 節</div>
					`;

						return card;
					}

					// 設置事件監聽器
					function setupEventListeners() {
						const courseSelect = document.getElementById('courseScheduleId');
						const submitBtn = document.getElementById('submitBtn');
						const courseForm = document.getElementById('courseForm');

						// 課程選擇變更
						courseSelect.addEventListener('change', function () {
							const isSelected = !!this.value;
							submitBtn.disabled = !isSelected;

							if (isSelected) {
								submitBtn.innerHTML = '📝 開始點名';
								selectedCourseId = this.value;
							} else {
								submitBtn.innerHTML = '📝 請先選擇課程';
								selectedCourseId = null;
							}

							// 更新卡片選中狀態
							updateCardSelection(this.value);
						});

						// 表單提交
						courseForm.addEventListener('submit', function (e) {
							e.preventDefault();
							if (selectedCourseId) {
								startAttendance(selectedCourseId);
							}
						});

						// 鍵盤快捷鍵
						document.addEventListener('keydown', function (e) {
							if (e.key === 'Enter' && selectedCourseId && !submitBtn.disabled) {
								e.preventDefault();
								startAttendance(selectedCourseId);
							}
						});
					}

					function startAttendance(courseScheduleId) {
						const submitBtn = document.getElementById('submitBtn');

						submitBtn.innerHTML = '<div class="loading btn-loading"></div> 載入中...';
						submitBtn.disabled = true;

						window.location.href = '<%= request.getContextPath() %>/markAttendance?courseScheduleId=' + courseScheduleId;
					}


					// 選擇課程（卡片模式）
					function selectCourse(courseId, cardElement) {
						// 移除其他卡片的選中狀態
						document.querySelectorAll('.course-card').forEach(card => {
							card.classList.remove('selected');
						});

						// 添加選中狀態
						cardElement.classList.add('selected');
						selectedCourseId = courseId;

						// 更新下拉選單
						const courseSelect = document.getElementById('courseScheduleId');
						const submitBtn = document.getElementById('submitBtn');

						courseSelect.value = courseId;
						submitBtn.disabled = false;
						submitBtn.innerHTML = '📝 開始點名';
					}

					// 更新卡片選中狀態
					function updateCardSelection(courseId) {
						document.querySelectorAll('.course-card').forEach((card, index) => {
							if (courseData[index] && courseData[index].id == courseId) {
								card.classList.add('selected');
							} else {
								card.classList.remove('selected');
							}
						});
					}

					// 切換顯示模式
					function toggleDisplayMode() {
						const form = document.querySelector('.course-form');
						const grid = document.getElementById('courseGrid');

						if (form.classList.contains('hidden')) {
							form.classList.remove('hidden');
							grid.classList.add('hidden');
						} else {
							form.classList.add('hidden');
							grid.classList.remove('hidden');
						}
					}

					// 顯示指定的區塊
					function showSection(sectionId) {
						const sections = ['loadingSection', 'errorSection', 'noCoursesSection', 'courseSelectionSection'];

						sections.forEach(id => {
							const element = document.getElementById(id);
							if (id === sectionId) {
								element.classList.remove('hidden');
							} else {
								element.classList.add('hidden');
							}
						});
					}

					// 顯示錯誤
					function showError(message) {
						document.getElementById('errorMessage').textContent = message;
						showSection('errorSection');
					}

					// 更新當前時間
					function updateCurrentTime() {
						const now = new Date();
						const timeString = now.toLocaleString('zh-TW', {
							year: 'numeric',
							month: '2-digit',
							day: '2-digit',
							hour: '2-digit',
							minute: '2-digit',
							second: '2-digit',
							weekday: 'long'
						});
						document.getElementById('currentTime').textContent = '🕐 ' + timeString;
					}

					// HTML轉義函數
					function escapeHtml(text) {
						if (!text) return '';
						const div = document.createElement('div');
						div.textContent = text;
						return div.innerHTML;
					}
				</script>
			</body>

			</html>