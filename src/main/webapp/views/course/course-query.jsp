<%@ page contentType="text/html; charset=UTF-8" %>
    <% String path=request.getContextPath(); %>
        <!DOCTYPE html>
        <html lang="zh-TW">

        <head>
            <meta charset="UTF-8">
            <title>課程查詢與週課表</title>
            <!-- FullCalendar CSS -->
            <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.17/index.global.min.css" rel="stylesheet" />
            <!-- FullCalendar JS -->
            <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.17/index.global.min.js"></script>

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
                    max-width: 1200px;
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
                    margin-bottom: 1rem;
                }

                /* Controls Section */
                .controls-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(10px);
                    border-radius: 16px;
                    padding: 1.5rem;
                    margin-bottom: 1.5rem;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                }

                .controls-row {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    gap: 1rem;
                    flex-wrap: wrap;
                }

                .btn {
                    padding: 12px 24px;
                    border: none;
                    border-radius: 8px;
                    font-size: 16px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    display: flex;
                    align-items: center;
                    gap: 6px;
                }

                .btn:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                }

                .btn-primary {
                    background: #667eea;
                    color: white;
                }

                .btn-primary.active,
                .btn-primary:hover {
                    background: #5a6fd8;
                    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                }

                .form-select {
                    padding: 12px 16px;
                    border: 2px solid #e1e5e9;
                    border-radius: 8px;
                    background: white;
                    font-size: 16px;
                    cursor: pointer;
                    min-width: 150px;
                    transition: border-color 0.3s ease;
                }

                .form-select:focus {
                    outline: none;
                    border-color: #667eea;
                }

                .hidden {
                    display: none !important;
                }

                /* Calendar Section */
                .calendar-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(10px);
                    border-radius: 16px;
                    padding: 2rem;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                }

                #calendar {
                    min-height: 600px;
                }

                /* FullCalendar 自定義樣式 */
                .fc {
                    font-family: inherit;
                }

                .fc-toolbar-title {
                    font-size: 1.5rem !important;
                    font-weight: 700 !important;
                    color: #333 !important;
                }

                .fc-button-primary {
                    background: #667eea !important;
                    border-color: #667eea !important;
                }

                .fc-button-primary:hover {
                    background: #5a6fd8 !important;
                    border-color: #5a6fd8 !important;
                }

                .fc-button-primary:disabled {
                    background: #a0a9d8 !important;
                    border-color: #a0a9d8 !important;
                }

                .fc-event {
                    border-radius: 6px !important;
                    border: none !important;
                    padding: 2px 6px !important;
                    font-weight: 600 !important;
                    cursor: pointer !important;
                }

                .fc-event:hover {
                    filter: brightness(1.1) !important;
                    transform: scale(1.02) !important;
                }

                .fc-daygrid-event {
                    margin: 1px 0 !important;
                }

                .fc-timegrid-event {
                    margin: 0 1px !important;
                }

                /* Modal Styles */
                .modal-overlay {
                    position: fixed;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background: rgba(0, 0, 0, 0.7);
                    backdrop-filter: blur(8px);
                    z-index: 1000;
                    display: none;
                    justify-content: center;
                    align-items: center;
                    animation: fadeIn 0.3s ease;
                }

                .modal-overlay.show {
                    display: flex;
                }

                .modal-container {
                    background: white;
                    border-radius: 20px;
                    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
                    max-width: 600px;
                    /* 從 500px 增加到 600px */
                    width: 90%;
                    max-height: 100vh;
                    /* 從 80vh 增加到 85vh */
                    overflow: hidden;
                    position: relative;
                    transform: scale(0.9);
                    transition: transform 0.3s ease;
                }

                .modal-overlay.show .modal-container {
                    transform: scale(1);
                }

                .modal-header {
                    background: linear-gradient(135deg, #667eea, #764ba2);
                    color: white;
                    padding: 20px 24px;
                    text-align: center;
                    position: relative;
                }

                .modal-title {
                    font-size: 1.4rem;
                    font-weight: 700;
                    margin: 0;
                }

                .close-btn {
                    position: absolute;
                    top: 15px;
                    right: 20px;
                    background: none;
                    border: none;
                    color: white;
                    font-size: 1.8rem;
                    cursor: pointer;
                    width: 32px;
                    height: 32px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-radius: 50%;
                    transition: all 0.3s ease;
                }

                .close-btn:hover {
                    background: rgba(255, 255, 255, 0.2);
                    transform: scale(1.1);
                }

                .modal-body {
                    padding: 24px;
                    max-height: none;
                    /* 移除高度限制 */
                    overflow-y: visible;
                    /* 改為 visible，不顯示滾動條 */
                }

                .course-info-grid {
                    display: grid;
                    gap: 16px;
                }

                .info-item {
                    display: flex;
                    align-items: flex-start;
                    padding: 16px;
                    background: rgba(102, 126, 234, 0.05);
                    border-radius: 12px;
                    border: 1px solid rgba(102, 126, 234, 0.1);
                    transition: all 0.3s ease;
                }

                .info-item:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                    background: rgba(102, 126, 234, 0.08);
                }

                .info-icon {
                    width: 40px;
                    height: 40px;
                    background: linear-gradient(135deg, #667eea, #764ba2);
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-right: 16px;
                    flex-shrink: 0;
                    color: white;
                    font-size: 18px;
                }

                .info-content {
                    flex: 1;
                }

                .info-label {
                    font-size: 0.9rem;
                    color: #666;
                    margin-bottom: 4px;
                    font-weight: 500;
                }

                .info-value {
                    font-size: 1.1rem;
                    color: #333;
                    font-weight: 600;
                    word-break: break-word;
                }

                .time-badge {
                    display: inline-block;
                    background: linear-gradient(135deg, #28a745, #20c997);
                    color: white;
                    padding: 6px 12px;
                    border-radius: 20px;
                    font-size: 0.9rem;
                    font-weight: 500;
                    margin: 2px;
                }

                .location-badge {
                    background: linear-gradient(135deg, #fd7e14, #e63946);
                    color: white;
                    padding: 8px 16px;
                    border-radius: 20px;
                    font-size: 1rem;
                    font-weight: 600;
                    display: inline-block;
                    margin-top: 4px;
                }

                /* Animations */
                @keyframes fadeIn {
                    from {
                        opacity: 0;
                    }

                    to {
                        opacity: 1;
                    }
                }

                @keyframes slideUp {
                    from {
                        transform: translateY(20px);
                        opacity: 0;
                    }

                    to {
                        transform: translateY(0);
                        opacity: 1;
                    }
                }

                .info-item {
                    animation: slideUp 0.3s ease;
                }

                .info-item:nth-child(2) {
                    animation-delay: 0.1s;
                }

                .info-item:nth-child(3) {
                    animation-delay: 0.2s;
                }

                .info-item:nth-child(4) {
                    animation-delay: 0.3s;
                }

                /* Responsive */
                @media (max-width: 768px) {
                    .container {
                        padding: 0 10px;
                    }

                    .controls-row {
                        flex-direction: column;
                        align-items: stretch;
                        gap: 12px;
                    }

                    .btn,
                    .form-select {
                        width: 100%;
                        justify-content: center;
                    }

                    .calendar-section {
                        padding: 1rem;
                    }

                    .page-title {
                        font-size: 1.5rem;
                    }

                    .modal-container {
                        width: 95%;
                        margin: 20px;
                    }

                    .modal-body {
                        padding: 16px;
                    }

                    .info-item {
                        flex-direction: column;
                        text-align: center;
                    }

                    .info-icon {
                        margin: 0 auto 12px auto;
                    }

                    #calendar {
                        min-height: 400px;
                    }

                    .fc-toolbar {
                        flex-direction: column !important;
                        gap: 10px !important;
                    }

                    .fc-toolbar-chunk {
                        display: flex !important;
                        justify-content: center !important;
                    }
                }

                /* Loading spinner */
                .loading {
                    display: inline-block;
                    width: 20px;
                    height: 20px;
                    border: 3px solid rgba(102, 126, 234, 0.3);
                    border-radius: 50%;
                    border-top-color: #667eea;
                    animation: spin 1s ease-in-out infinite;
                }

                @keyframes spin {
                    to {
                        transform: rotate(360deg);
                    }
                }
            </style>
        </head>

        <body>
            <div class="container">
                <!-- Header -->
                <div class="header-section">
                    <h1 class="page-title">📅 課程查詢與週課表</h1>
                </div>

                <!-- Controls -->
                <div class="controls-section">
                    <div class="controls-row">
                        <button class="btn btn-primary active" id="btn-my" onclick="switchType('my')">
                            👤 我的課表
                        </button>
                        <button class="btn btn-primary" id="btn-room" onclick="switchType('room')">
                            🏫 教室課表
                        </button>
                        <button class="btn btn-primary" id="btn-course" onclick="switchType('course')">
                            📚 課程課表
                        </button>

                        <select id="roomSelect" class="form-select hidden" onchange="calendar.refetchEvents()">
                            <option value="">選擇教室</option>
                        </select>

                        <select id="courseSelect" class="form-select hidden" onchange="calendar.refetchEvents()">
                            <option value="">選擇課程</option>
                        </select>
                    </div>
                </div>

                <!-- Calendar -->
                <div class="calendar-section">
                    <div id="calendar"></div>
                </div>
            </div>

            <!-- 課程詳細資訊彈窗 -->
            <div id="courseModal" class="modal-overlay" onclick="closeModal(event)">
                <div class="modal-container">
                    <div class="modal-header">
                        <h2 class="modal-title">📋 課程詳細資訊</h2>
                        <button class="close-btn" onclick="closeModal()">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="course-info-grid">
                            <div class="info-item">
                                <div class="info-icon">📚</div>
                                <div class="info-content">
                                    <div class="info-label">課程名稱</div>
                                    <div class="info-value" id="courseName">-</div>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-icon">🎯</div>
                                <div class="info-content">
                                    <div class="info-label">科目</div>
                                    <div class="info-value" id="courseSubject">-</div>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-icon">🕐</div>
                                <div class="info-content">
                                    <div class="info-label">上課時間</div>
                                    <div class="info-value">
                                        <span class="time-badge" id="startTime">-</span>
                                        <span style="margin: 0 8px; color: #666;">至</span>
                                        <span class="time-badge" id="endTime">-</span>
                                    </div>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-icon">📍</div>
                                <div class="info-content">
                                    <div class="info-label">教室位置</div>
                                    <div class="info-value">
                                        <span class="location-badge" id="courseLocation">-</span>
                                    </div>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-icon">👨‍🏫</div>
                                <div class="info-content">
                                    <div class="info-label">授課老師</div>
                                    <div class="info-value" id="courseTeacher">-</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                let currentType = 'my';
                let calendar;

                document.addEventListener('DOMContentLoaded', function () {
                    var calendarEl = document.getElementById('calendar');
                    calendar = new FullCalendar.Calendar(calendarEl, {
                        initialView: 'timeGridWeek',
                        locale: 'zh-tw',
                        height: 'auto',
                        slotMinTime: '08:00:00',
                        slotMaxTime: '18:00:00',
                        slotDuration: '01:00:00',
                        headerToolbar: {
                            left: 'prev,next today',
                            center: 'title',
                            right: 'dayGridMonth,timeGridWeek,timeGridDay'
                        },
                        buttonText: {
                            today: '今天',
                            month: '月',
                            week: '週',
                            day: '日'
                        },
                        events: fetchEvents,
                        eventClick: function (info) {
                            showCourseModal(info.event);
                        },
                        loading: function (isLoading) {
                            // 可以在這裡添加載入指示器
                        }
                    });
                    calendar.render();
                });

                function fetchEvents(fetchInfo, successCallback, failureCallback) {
                    let url = '${pageContext.request.contextPath}/api/schedule?type=' + currentType;
                    if (currentType === 'room') {
                        const roomId = document.getElementById('roomSelect').value;
                        if (!roomId) {
                            successCallback([]);
                            return;
                        }
                        url += '&roomId=' + roomId;
                    } else if (currentType === 'course') {
                        const courseId = document.getElementById('courseSelect').value;
                        if (!courseId) {
                            successCallback([]);
                            return;
                        }
                        url += '&courseId=' + courseId;
                    }

                    fetch(url)
                        .then(res => {
                            if (!res.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return res.json();
                        })
                        .then(data => successCallback(data))
                        .catch(err => {
                            console.error('Failed to fetch events:', err);
                            failureCallback(err);
                        });
                }

                function showCourseModal(event) {
                    const courseTitle = event.title.replace(/<br>/g, ' ');
                    console.log("event.extendedProps:", event.extendedProps);

                    const startTime = event.start.toLocaleTimeString('zh-TW', {
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                    const endTime = event.end.toLocaleTimeString('zh-TW', {
                        hour: '2-digit',
                        minute: '2-digit'
                    });

                    document.getElementById('courseName').textContent = courseTitle;
                    document.getElementById('courseSubject').textContent = event.extendedProps.subjectName || '未指定';
                    document.getElementById('startTime').textContent = startTime;
                    document.getElementById('endTime').textContent = endTime;
                    document.getElementById('courseLocation').textContent = event.extendedProps.classroomId || '未指定';
                    document.getElementById('courseTeacher').textContent = event.extendedProps.teacherName || '未指定';

                    document.getElementById('courseModal').classList.add('show');
                    document.body.style.overflow = 'hidden';
                }

                function closeModal(event) {
                    if (!event || event.target.classList.contains('modal-overlay') || event.target.classList.contains('close-btn')) {
                        document.getElementById('courseModal').classList.remove('show');
                        document.body.style.overflow = 'auto';
                    }
                }

                document.addEventListener('keydown', function (event) {
                    if (event.key === 'Escape') {
                        closeModal();
                    }
                });

                function switchType(type) {
                    currentType = type;

                    // 隱藏所有選擇器
                    document.getElementById('roomSelect').classList.add('hidden');
                    document.getElementById('courseSelect').classList.add('hidden');

                    // 顯示對應的選擇器
                    if (type === 'room') {
                        document.getElementById('roomSelect').classList.remove('hidden');
                        initRoomSelect();
                    } else if (type === 'course') {
                        document.getElementById('courseSelect').classList.remove('hidden');
                        initCourseSelect();
                    }

                    calendar.refetchEvents();

                    // 更新按鈕狀態
                    document.querySelectorAll('.btn').forEach(btn => btn.classList.remove('active'));
                    document.getElementById('btn-' + type).classList.add('active');
                }

                async function initRoomSelect() {
                    try {
                        console.log("開始載入教室選單");
                        const res = await fetch('${pageContext.request.contextPath}/api/course/room/allId').then(r => r.json());
                        const rooms = res.data || [];
                        console.log("教室資料：", rooms);
                        const select = document.getElementById('roomSelect');
                        select.innerHTML = '<option value="">選擇教室</option>';

                        rooms.forEach(roomId => {
                            const opt = new Option(roomId, roomId);
                            select.appendChild(opt);
                        });
                    } catch (err) {
                        console.error('Failed to load rooms:', err);
                    }


                }

                async function initCourseSelect() {
                    try {
                        console.log("開始載入課程選單");
                        const res = await fetch('${pageContext.request.contextPath}/api/course/all').then(r => r.json());
                        const courses = res.data || [];
                        console.log("課程資料：", courses);
                        const courseSel = document.getElementById('courseSelect');
                        courseSel.innerHTML = '<option value="">選擇課程</option>';

                        courses.forEach(c => {
                            const opt = new Option(c.name, c.id);
                            courseSel.appendChild(opt);
                        });
                    } catch (err) {
                        console.error('Failed to load courses:', err);
                    }
                }
            </script>
        </body>

        </html>