<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <% String path=request.getContextPath(); %>
            <!DOCTYPE html>
            <html lang="zh-Hant">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>考試卷管理</title>

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
                        content: "📋";
                        font-size: 4rem;
                        display: block;
                        margin-bottom: 1rem;
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
                    @media (max-width: 768px) {
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

                        .form-group {
                            flex: 1 1 100%;
                            /* 小螢幕變單欄 */
                        }

                        .form-group label {
                            margin-bottom: 0.25rem;
                            font-weight: 600;
                            color: #4a5568;
                            font-size: 0.9rem;
                        }
                    }

                    /* SweetAlert2 RWD 表單網格系統 */
                    .form-grid {
                        display: flex;
                        flex-wrap: wrap;
                        gap: 1rem;
                        justify-content: space-between;
                    }

                    .form-group {
                        flex: 1 1 calc(50% - 1rem);
                        /* 兩欄 */
                        min-width: 240px;
                        display: flex;
                        flex-direction: column;
                    }

                    .form-group textarea {
                        height: 80px;
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

                    .swal2-input,
                    .swal2-textarea {
                        border: 2px solid #e2e8f0 !important;
                        border-radius: 8px !important;
                        padding: 0.75rem 1rem !important;
                        font-size: 1.1rem !important;
                        transition: all 0.3s ease !important;
                        height: 30px;
                    }

                    .swal2-input:focus,
                    .swal2-textarea:focus {
                        border-color: #667eea !important;
                        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
                    }

                    /* Table cell content styling */
                    .exam-id {
                        font-weight: 600;
                        color: #667eea;
                    }

                    .exam-name {
                        font-weight: 600;
                        color: #4a5568;
                    }

                    .exam-type {
                        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
                        padding: 0.25rem 0.75rem;
                        border-radius: 12px;
                        font-size: 0.8rem;
                        font-weight: 600;
                        color: #667eea;
                        display: inline-block;
                    }

                    .exam-score {
                        font-weight: 700;
                        font-size: 1.1rem;
                        color: #48bb78;
                    }

                    .exam-date {
                        color: #718096;
                        font-size: 0.9rem;
                    }
                </style>
            </head>

            <body>

                <div class="container">
                    <div class="page-header">
                        <h1>考試卷管理</h1>
                        <p>管理和編輯所有考試卷資料</p>
                    </div>

                    <div class="card">
                        <div class="card-body">
                            <table id="myTable" class="display">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>考試名稱</th>
                                        <th>課程名稱</th>
                                        <th>考試類型</th>
                                        <th>考試時間</th>
                                        <th>總分</th>
                                        <th>描述</th>
                                        <th>產生時間</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody></tbody>
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
                    const basePath = "<%= request.getContextPath() %>";
                    console.log(basePath);

                    $(document).ready(function () {
                        const table = $('#myTable').DataTable({
                            language: {
                                lengthMenu: "顯示 _MENU_ 筆資料",
                                zeroRecords: "沒有找到符合的資料",
                                info: "顯示第 _START_ 到 _END_ 筆資料，共 _TOTAL_ 筆",
                                infoEmpty: "顯示第 0 到 0 筆資料，共 0 筆",
                                infoFiltered: "(從 _MAX_ 筆資料中過濾)",
                                search: "搜尋:",
                                paginate: {
                                    first: "第一頁",
                                    last: "最後一頁",
                                    next: "下一頁",
                                    previous: "上一頁"
                                }
                            },
                            pageLength: 10,
                            responsive: true,
                            order: [[0, 'desc']]
                        });

                        $.get(basePath + "/api/exam-papers", function (data) {
                            table.clear();
                            data.forEach(er => {
                                table.row.add([
                                    er.id,
                                    er.name,
                                    er.courseName,
                                    er.examType,
                                    formatDate(er.examDate),
                                    er.totalScore,
                                    er.description,
                                    formatDateTime(er.createTime),
                                    '<button class="action-btn edit-btn"' +
                                    ' data-id="' + er.id + '"' +
                                    ' data-name="' + er.name + '"' +
                                    ' data-courseId="' + er.courseId + '"' +
                                    ' data-type="' + er.examType + '"' +
                                    ' data-date="' + er.examDate + '"' +
                                    ' data-score="' + er.totalScore + '"' +
                                    ' data-courseName="' + er.courseName + '"' +
                                    ' data-description="' + er.description + '">' +
                                    '修改</button> ' +
                                    '<button class="action-btn delete-btn"' +
                                    ' data-id="' + er.id + '"' +
                                    ' data-exampaper="' + er.name + '">' +
                                    '刪除</button>'
                                ]);
                            });
                            table.draw();
                        });

                        function formatDate(str) {
                            if (!str) return '--';
                            const d = new Date(str);
                            return d.toLocaleDateString('zh-TW');
                        }

                        function formatDateTime(str) {
                            if (!str) return '--';
                            const d = new Date(str);
                            return d.toLocaleString('zh-TW', { hour12: false });
                        }

                        function toDateInputValue(dateStr) {
                            if (!dateStr) return '';
                            const d = new Date(dateStr);
                            const year = d.getFullYear();
                            const month = String(d.getMonth() + 1).padStart(2, '0');
                            const day = String(d.getDate()).padStart(2, '0');
                            return `${year}-${month}-${day}`;
                        }
                        // 修改按鈕
                        $(document).on('click', '.edit-btn', function () {
                            const exam = {
                                id: $(this).attr('data-id') || '',
                                name: $(this).attr('data-name') || '',
                                courseId: $(this).attr('data-courseId') || '',
                                courseName: $(this).attr('data-courseName') || '',
                                examType: $(this).attr('data-type') || '',
                                examDate: $(this).attr('data-date') || '',
                                totalScore: $(this).attr('data-score') || '',
                                description: $(this).attr('data-description') || ''
                            };

                            console.log('exam 內容：', exam); // 檢查是否有抓到值

                            const selectedType = exam.examType;
                            const examTypeOptions = ['期中', '期末', '段考', '隨堂測驗']
                                .map(type => '<option value="' + type + '"' + (selectedType === type ? ' selected' : '') + '>' + type + '</option>')
                                .join('');

                            const html =
                                '<div class="form-grid">' +
                                '<input id="swal-courseId" class="swal2-input" placeholder="考試ID" value="' + exam.courseId + '"hidden>' +
                                '<div class="form-group">' +

                                '<label>考試名稱</label>' +
                                '<input id="swal-name" class="swal2-input" placeholder="考試名稱" value="' + exam.name + '">' +
                                '</div>' +
                                '<div class="form-group">' +
                                '<label>課程名稱</label>' +
                                '<input id="swal-courseName" class="swal2-input" placeholder="課程名稱" value="' + exam.courseName + '" readonly>' +
                                '</div>' +
                                '<div class="form-group">' +
                                '<label>考試類型</label>' +
                                '<select id="swal-examType" class="swal2-input">' + examTypeOptions + '</select>' +
                                '</div>' +
                                '<div class="form-group">' +
                                '<label>考試日期</label>' +
                                '<input id="swal-date" type="date" class="swal2-input" value="' + exam.examDate + '">' +
                                '</div>' +
                                '<div class="form-group">' +
                                '<label>總分</label>' +
                                '<input id="swal-score" type="number" class="swal2-input" placeholder="總分" value="' + exam.totalScore + '">' +
                                '</div>' +
                                '<div class="form-group">' +
                                '<label>描述</label>' +
                                '<textarea id="swal-description" class="swal2-textarea" placeholder="描述">' + (exam.description || '') + '</textarea>' +
                                '</div>' +
                                '</div>';
                            Swal.fire({
                                title: '修改考試卷',
                                html: html,
                                focusConfirm: false,
                                showCancelButton: true,
                                confirmButtonText: '儲存修改',
                                preConfirm: () => {
                                    return {
                                        name: $('#swal-name').val(),
                                        courseId: $('#swal-courseId').val(),
                                        examType: $('#swal-examType').val(),
                                        examDate: $('#swal-date').val(),
                                        totalScore: $('#swal-score').val(),
                                        description: $('#swal-description').val()
                                    };
                                }
                            }).then(result => {
                                if (result.isConfirmed) {
                                    const payload = {
                                        id: exam.id,
                                        ...result.value
                                    };
                                    $.ajax({
                                        url: basePath + "/api/exam-papers",
                                        method: 'POST',
                                        contentType: 'application/json',
                                        data: JSON.stringify(payload),
                                        success: () => {
                                            Swal.fire('成功', '已成功更新資料', 'success').then(() => location.reload());
                                        },
                                        error: () => {
                                            Swal.fire('錯誤', '更新失敗', 'error');
                                        }
                                    });
                                }
                            });
                        });

                        // 刪除按鈕
                        $(document).on('click', '.delete-btn', function () {
                            const examId = $(this).data('id');
                            const examName = $(this).data('exampaper');
                            console.log(examId);

                            Swal.fire({
                                title: '確認刪除？',
                                text: `確定要刪除「${examName}」的考卷嗎？`,
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonText: '是，刪除！',
                                cancelButtonText: '取消'
                            }).then((result) => {
                                if (result.isConfirmed) {
                                    $.ajax({
                                        url: basePath + "/api/exam-papers/" + examId,
                                        method: 'DELETE',
                                        success: () => {
                                            Swal.fire('已刪除', '該考卷已被刪除。', 'success').then(() => location.reload());
                                        },
                                        error: () => {
                                            Swal.fire('錯誤', '刪除失敗，請稍後再試', 'error');
                                        }
                                    });
                                }
                            });
                        });
                    });
                </script>

            </body>

            </html>