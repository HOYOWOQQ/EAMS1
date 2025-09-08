<%@ page contentType="text/html; charset=UTF-8" %>
    <% String path=request.getContextPath(); %>
        <!DOCTYPE html>
        <html lang="zh-TW">

        <head>
            <meta charset="UTF-8">
            <title>課表編輯</title>
            <!-- FullCalendar CSS -->
            <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.17/index.global.min.css" rel="stylesheet" />
            <!-- FullCalendar JS -->
            <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.17/index.global.min.js"></script>
            <!-- Bootstrap 5 -->
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <!-- SweetAlert2 CSS -->
            <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css" rel="stylesheet">
            <!-- SweetAlert2 JS -->
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
                    margin: 0;
                }

                /* Search Section */
                .search-section {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(10px);
                    border-radius: 16px;
                    padding: 1.5rem;
                    margin-bottom: 1.5rem;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                }

                .search-title {
                    font-size: 1.2rem;
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 1rem;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .search-fields {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                    gap: 1rem;
                    margin-bottom: 1rem;
                }

                .search-actions {
                    display: flex;
                    gap: 12px;
                    justify-content: flex-start;
                }

                .search-result {
                    margin-top: 1rem;
                    padding: 12px 16px;
                    background: rgba(102, 126, 234, 0.05);
                    border-radius: 8px;
                    color: #666;
                    font-size: 14px;
                    font-weight: 500;
                }

                /* Form Controls */
                .form-group {
                    display: flex;
                    flex-direction: column;
                }

                .form-label {
                    font-weight: 600;
                    color: #333;
                    margin-bottom: 6px;
                    font-size: 14px;
                }

                .form-control {
                    padding: 12px 16px;
                    border: 2px solid #e1e5e9;
                    border-radius: 8px;
                    font-size: 16px;
                    background: white;
                    transition: border-color 0.3s ease;
                }

                .form-control:focus {
                    outline: none;
                    border-color: #667eea;
                }

                /* Buttons */
                .btn {
                    padding: 10px 20px;
                    border: none;
                    border-radius: 8px;
                    font-size: 14px;
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

                .btn-secondary {
                    background: #6c757d;
                    color: white;
                }

                .btn-danger {
                    background: #dc3545;
                    color: white;
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

                /* FullCalendar Custom Styles */
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

                /* Bootstrap Modal Customization */
                .modal-content {
                    border-radius: 16px;
                    border: none;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
                }

                .modal-header {
                    background: linear-gradient(135deg, #667eea, #764ba2);
                    color: white;
                    border-radius: 16px 16px 0 0;
                    border-bottom: none;
                }

                .modal-title {
                    font-weight: 700;
                }

                .btn-close {
                    filter: invert(1);
                }

                .modal-body {
                    padding: 2rem;
                }

                .modal-footer {
                    border-top: 1px solid #e1e5e9;
                    padding: 1rem 2rem;
                }

                .mb-3 {
                    margin-bottom: 1.5rem !important;
                }

                /* Loading */
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

                /* Responsive */
                @media (max-width: 768px) {
                    .container {
                        padding: 0 10px;
                    }

                    .header-section,
                    .search-section,
                    .calendar-section {
                        padding: 1rem;
                    }

                    .search-fields {
                        grid-template-columns: 1fr;
                    }

                    .search-actions {
                        justify-content: center;
                    }

                    .page-title {
                        font-size: 1.5rem;
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

                    .modal-body {
                        padding: 1rem;
                    }
                }

                /* Custom Alert Styles */
                .swal2-popup {
                    border-radius: 16px !important;
                }

                .swal2-confirm {
                    background: #667eea !important;
                    border-radius: 8px !important;
                }

                .swal2-cancel {
                    background: #6c757d !important;
                    border-radius: 8px !important;
                }

                /* 讓編輯課程的 Modal 完全置中 */
                .modal-dialog {
                    display: flex;
                    align-items: center;
                    min-height: 100vh;
                    justify-content: center;
                }

                .modal.fade .modal-dialog {
                    transition: transform 0.3s ease-out;
                    transform: translateY(0) !important;
                }
            </style>
        </head>

        <body>
            <div class="container">
                <!-- Header -->
                <div class="header-section">
                    <h1 class="page-title">✏️ 課程編輯與排課</h1>
                </div>

                <!-- Search Section -->
                <div class="search-section">
                    <h3 class="search-title">🔍 課程搜尋與篩選</h3>
                    <div class="search-fields">
                        <div class="form-group">
                            <label class="form-label">使用者</label>
                            <input type="text" id="userSearch" class="form-control" placeholder="搜尋使用者..."
                                onchange="performSearch()">
                        </div>
                        <div class="form-group">
                            <label class="form-label">教室</label>
                            <select id="roomFilter" class="form-control" onchange="performSearch()">
                                <option value="">所有教室</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">課程</label>
                            <select id="courseFilter" class="form-control" onchange="performSearch()">
                                <option value="">所有課程</option>
                            </select>
                        </div>
                    </div>
                    <div class="search-actions">
                        <button class="btn btn-primary" onclick="performSearch()">🔍 搜尋</button>
                        <button class="btn btn-secondary" onclick="clearFilters()">🔄 清除</button>
                    </div>
                    <div id="searchResult" class="search-result" style="display: none;"></div>
                </div>

                <!-- Calendar -->
                <div class="calendar-section">
                    <div id="calendar"></div>
                </div>
            </div>

            <!-- 編輯課程的 Modal -->
            <div class="modal fade" id="editEventModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">✏️ 編輯課程</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="關閉"></button>
                        </div>
                        <div class="modal-body">
                            <form id="editEventForm">
                                <input type="hidden" id="editEventId">

                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="editEventCourse" class="form-label">📚 課程名稱</label>
                                            <select class="form-control" id="editEventCourse" name="courseId">
                                                <option value="">請選擇課程</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="editEventSubject" class="form-label">📖 科目</label>
                                            <select class="form-control" id="editEventSubject" name="subjectId">
                                                <option value="">請先選擇課程</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="editEventClassroom" class="form-label">🏫 教室</label>
                                            <select class="form-control" id="editEventClassroom" name="classroomId">
                                                <option value="">請選擇教室</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="editEventTeacher" class="form-label">👨‍🏫 授課老師</label>
                                            <select class="form-control" id="editEventTeacher" name="teacherId">
                                                <option value="">請選擇老師</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="editEventPeriodStart" class="form-label">⏰ 起始節次</label>
                                            <input type="number" class="form-control" id="editEventPeriodStart" min="1"
                                                max="8" placeholder="1-8">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="editEventPeriodEnd" class="form-label">⏰ 結束節次</label>
                                            <input type="number" class="form-control" id="editEventPeriodEnd" min="1"
                                                max="8" placeholder="1-8">
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" onclick="updateEvent()">💾 儲存</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">❌ 取消</button>
                            <button type="button" class="btn btn-danger" onclick="deleteEvent()">🗑️ 刪除此行程</button>
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
                            openEditModal(info.event);
                        },
                        loading: function (isLoading) {
                            if (isLoading) {
                                console.log('Loading events...');
                            } else {
                                console.log('Events loaded');
                            }
                        }
                    });
                    calendar.render();
                    initFilters();
                });

                function fetchEvents(fetchInfo, successCallback, failureCallback) {
                    const userQuery = document.getElementById('userSearch').value.trim();
                    const roomId = document.getElementById('roomFilter').value;
                    const courseId = document.getElementById('courseFilter').value;

                    let url = '${pageContext.request.contextPath}/api/schedule?';
                    const params = [];

                    if (userQuery) {
                        params.push('userQuery=' + encodeURIComponent(userQuery));
                    }
                    if (roomId) {
                        params.push('roomId=' + encodeURIComponent(roomId));
                    }
                    if (courseId) {
                        params.push('courseId=' + encodeURIComponent(courseId));
                    }

                    if (params.length === 0) {
                        params.push('type=my');
                    } else {
                        params.push('type=search');
                    }

                    url += params.join('&');

                    fetch(url)
                        .then(res => {
                            if (!res.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return res.json();
                        })
                        .then(data => {
                            successCallback(data);
                            updateSearchResult(data.length);
                        })
                        .catch(err => {
                            console.error('Failed to fetch events:', err);
                            failureCallback(err);
                        });
                }

                // 預載搜尋選單
                async function initFilters() {
                    try {
                        const roomsRes = await fetch('${pageContext.request.contextPath}/api/course/room/allId').then(r => r.json());
                        const rooms = roomsRes.data || [];
                        const roomSel = document.getElementById('roomFilter');
                        rooms.forEach(roomId => {
                            const opt = new Option(roomId, roomId);
                            roomSel.appendChild(opt);
                        });

                        const res = await fetch('${pageContext.request.contextPath}/api/course/all').then(r => r.json());
                        const courses = res.data || [];
                        const courseSel = document.getElementById('courseFilter');
                        courses.forEach(c => {
                            const opt = new Option(c.name, c.id);
                            courseSel.appendChild(opt);
                        });
                    } catch (err) {
                        console.error('Failed to load filters:', err);
                    }
                }

                // 載入行事曆
                function performSearch() {
                    calendar.refetchEvents();
                }

                // 清除搜尋條件
                function clearFilters() {
                    document.getElementById('userSearch').value = '';
                    document.getElementById('roomFilter').value = '';
                    document.getElementById('courseFilter').value = '';

                    const resultDiv = document.getElementById('searchResult');
                    resultDiv.style.display = 'none';

                    performSearch();
                }

                // 更新搜尋結果顯示
                function updateSearchResult(count) {
                    const resultDiv = document.getElementById('searchResult');
                    const hasFilters = document.getElementById('userSearch').value.trim() ||
                        document.getElementById('roomFilter').value ||
                        document.getElementById('courseFilter').value;

                    if (hasFilters) {
                        resultDiv.style.display = 'block';
                        resultDiv.innerHTML = `📊 找到 \${count} 個課程事件`;
                    } else {
                        resultDiv.style.display = 'none';
                    }
                }

                
                
                // 更新事件
                function updateEvent() {
                    const id = document.getElementById("editEventId").value;
                    const courseId = document.getElementById("editEventCourse").value;
                    const subjectId = document.getElementById("editEventSubject").value;
                    const classroomId = document.getElementById("editEventClassroom").value;
                    const periodStart = document.getElementById("editEventPeriodStart").value;
                    const periodEnd = document.getElementById("editEventPeriodEnd").value;
                    const teacherId = document.getElementById("editEventTeacher").value;

                    const schedule = {
                        id: id && id.trim() !== '' ? parseInt(id) : null,
                        courseId: courseId && courseId.trim() !== '' ? parseInt(courseId) : null,
                        subjectId: subjectId && subjectId.trim() !== '' ? parseInt(subjectId) : null,
                        classroomId: classroomId && classroomId.trim() !== '' ? parseInt(classroomId) : null,
                        periodStart: periodStart && periodStart.trim() !== '' ? parseInt(periodStart) : null,
                        periodEnd: periodEnd && periodEnd.trim() !== '' ? parseInt(periodEnd) : null,
                        teacherId: teacherId && teacherId.trim() !== '' ? parseInt(teacherId) : null
                    };

                    console.log('儲存事件測試：');
                    console.log('schedule 物件：', schedule);


                    if (!schedule.courseId || !schedule.subjectId || !schedule.classroomId ||
                        !schedule.periodStart || !schedule.periodEnd || !schedule.teacherId) {
                        Swal.fire({
                            icon: 'warning',
                            title: '請填寫所有必填欄位',
                            text: '課程、科目、教室、節次、老師都是必填項目',
                            confirmButtonText: '確定'
                        });
                        return;
                    }

                    fetch('${pageContext.request.contextPath}/api/schedule/update', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json; charset=UTF-8'
                        },
                        body: JSON.stringify(schedule)
                    })
                        .then(response => {
                            console.log('Response status:', response.status);
                            return response.text(); // 改為 text() 因為後端回傳字串
                        })
                        .then(message => {
                            console.log('更新回傳：', message);
                            const isSuccess = message.includes('更新成功') || message.includes('成功');

                            Swal.fire({
                                icon: isSuccess ? 'success' : 'error',
                                title: isSuccess ? '更新成功！' : '更新失敗',
                                text: message.message,
                                confirmButtonText: '確定'
                            }).then(() => {
                                if (isSuccess) {
                                    bootstrap.Modal.getInstance(document.getElementById('editEventModal')).hide();
                                    calendar.refetchEvents();
                                }
                            });
                        })
                        .catch(error => {
                            console.error('更新錯誤:', error);
                            Swal.fire({
                                icon: 'error',
                                title: "更新失敗",
                                text: error.message || "請檢查輸入內容並稍後再試",
                                confirmButtonText: '確定'
                            });
                        });
                }

              

                async function openEditModal(event) {
                    console.log('設定 editEventId：', event.id);
                    document.getElementById('editEventId').value = event.id;
                    console.log("event.extendedProps:", event.extendedProps);

                    document.getElementById('editEventPeriodStart').value = event.extendedProps.periodStart || '';
                    document.getElementById('editEventPeriodEnd').value = event.extendedProps.periodEnd || '';

                    // 載入課程選單
                    try {
                        const res = await fetch('${pageContext.request.contextPath}/api/course/all').then(r => r.json());
                        const courses = res.data || [];
                        console.log("課程資料：", courses);
                        const courseSel = document.getElementById('editEventCourse');
                        courseSel.innerHTML = '<option value="">請選擇課程</option>';

                        const selectedCourseId = event.extendedProps.courseId;

                        courses.forEach(c => {
                            const opt = new Option(c.name, c.id);
                            if (c.id == selectedCourseId) {
                                opt.selected = true;
                            }
                            courseSel.appendChild(opt);
                        });

                        // 載入教室選單
                        const roomsRes = await fetch('${pageContext.request.contextPath}/api/course/room/allId').then(r => r.json());
                        const rooms = roomsRes.data || [];
                        const classroomSel = document.getElementById('editEventClassroom');
                        classroomSel.innerHTML = '<option value="">請選擇教室</option>';
                        const selectedClassroomId = event.extendedProps.classroomId;

                        rooms.forEach(room => {
                            let value, text;
                            if (typeof room === 'object') {
                                value = room.id;
                                text = room.name || room.id;
                            } else {
                                value = room;
                                text = room;
                            }
                            const opt = new Option(text, value);
                            if (value == selectedClassroomId) {
                                opt.selected = true;
                            }
                            classroomSel.appendChild(opt);
                        });

                        // 載入科目選單
                        const subjectSel = document.getElementById('editEventSubject');
                        subjectSel.innerHTML = '';
                        if (event.extendedProps.subjectId && event.extendedProps.subjectName) {
                            subjectSel.innerHTML = `<option value="\${event.extendedProps.subjectId}" selected>\${event.extendedProps.subjectName}</option>`;
                        } else {
                            subjectSel.innerHTML = '<option value="">請選擇科目</option>';
                        }

                        // 載入老師選單
                        const teacherSel = document.getElementById('editEventTeacher');
                        teacherSel.innerHTML = '';
                        if (event.extendedProps.teacherId && event.extendedProps.teacherName) {
                            teacherSel.innerHTML = `<option value="\${event.extendedProps.teacherId}" selected>\${event.extendedProps.teacherName}</option>`;
                        } else {
                            teacherSel.innerHTML = '<option value="">請選擇老師</option>';
                        }

                        // 顯示 modal
                        new bootstrap.Modal(document.getElementById('editEventModal')).show();
                    } catch (err) {
                        console.error('Failed to load modal data:', err);
                        Swal.fire({
                            icon: 'error',
                            title: '載入失敗',
                            text: '無法載入編輯資料，請稍後再試',
                            confirmButtonText: '確定'
                        });
                    }
                }

               async function deleteEvent() {
                    const id = document.getElementById("editEventId").value; // 從隱藏欄位取得 ID
                    const courseschedule = { id: id && id.trim() !== '' ? parseInt(id) : null, };
                    console.log('刪除事件測試：');
                    console.log('courseschedule 物件：', courseschedule);
                    if (!id) {
                        Swal.fire({
                            icon: 'error',
                            title: "錯誤",
                            text: "找不到事件 ID，無法刪除！",
                            confirmButtonText: '確定'
                        });
                        return;
                    }

                    Swal.fire({
                        title: '確定要刪除此課表安排嗎？',
                        text: '此操作無法復原！',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: '確定刪除',
                        cancelButtonText: '取消',
                        confirmButtonColor: '#dc3545',
                        cancelButtonColor: '#6c757d'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            fetch('${pageContext.request.contextPath}/api/schedule/delete', {
                                method: 'DELETE',
                                headers: { 
                                    'Content-Type': 'application/json; charset=UTF-8',
                                    'Accept': 'application/json'
                                },
                                body: JSON.stringify(courseschedule)
                            })
                                .then(response => {
                                    console.log('Response status:', response.status);
                                    if (!response.ok) {
                                        throw new Error(`HTTP error! status: \${response.status}`);
                                    }
                                    return response.json(); // 使用 JSON 格式
                                })
                                .then(result => {
                                    console.log('刪除回傳：', result);
                                    
                                    Swal.fire({
                                        icon: 'success',
                                        title: msg.message ,
                                        text: result.message || (result.message ? '課表已成功刪除' : '刪除過程中發生錯誤'),
                                        confirmButtonText: '確定'
                                    }).then(() => {
                                        if (result.message) {
                                            bootstrap.Modal.getInstance(document.getElementById('editEventModal')).hide();
                                            calendar.refetchEvents(); // 重新載入行事曆
                                        }
                                    });
                                })
                                .catch(error => {
                                    console.error('刪除錯誤:', error);
                                    Swal.fire({
                                        icon: 'error',
                                        title: "刪除失敗",
                                        text: error.message || "請稍後再試",
                                        confirmButtonText: '確定'
                                    });
                                });
                        }
                    });
                }

                // 當選取課程時，載入對應科目
                document.getElementById('editEventCourse').addEventListener('change', async function () {
                    const courseId = this.value;
                    const subjectSel = document.getElementById('editEventSubject');
                    const teacherSel = document.getElementById('editEventTeacher');

                    subjectSel.innerHTML = '<option value="">載入中...</option>';
                    teacherSel.innerHTML = '<option value="">請先選擇科目</option>';

                    if (!courseId) {
                        subjectSel.innerHTML = '<option value="">請先選擇課程</option>';
                        return;
                    }

                    try {
                        const res = await fetch('${pageContext.request.contextPath}/api/course/subjectByCourse?courseId=' + courseId).then(r => r.json());
                        const subjects = res.data || [];
                        subjectSel.innerHTML = '<option value="">請選擇科目</option>';
                        subjects.forEach(s => {
                            subjectSel.innerHTML += `<option value="\${s.id}">\${s.name}</option>`;
                        });
                    } catch (err) {
                        console.error('Failed to load subjects:', err);
                        subjectSel.innerHTML = '<option value="">載入失敗</option>';
                    }
                });

                // 當選取科目時，載入對應老師
                document.getElementById('editEventSubject').addEventListener('change', async function () {
                    const subjectId = this.value;
                    const teacherSel = document.getElementById('editEventTeacher');

                    teacherSel.innerHTML = '<option value="">載入中...</option>';

                    if (!subjectId) {
                        teacherSel.innerHTML = '<option value="">請先選擇科目</option>';
                        return;
                    }

                    try {
                        const res = await fetch('${pageContext.request.contextPath}/api/course/teacherBySubject?subjectId=\${subjectId}').then(r => r.json());
                        const teachers = res.data || [];
                        teacherSel.innerHTML = '<option value="">請選擇老師</option>';
                        teachers.forEach(t => {
                            teacherSel.innerHTML += `<option value="\${t.id}">\${t.name}</option>`;
                        });
                    } catch (err) {
                        console.error('Failed to load teachers:', err);
                        teacherSel.innerHTML = '<option value="">載入失敗</option>';
                    }
                });
            </script>
        </body>

        </html>