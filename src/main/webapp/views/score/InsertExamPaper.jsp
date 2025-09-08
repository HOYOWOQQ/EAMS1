<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <% String path=request.getContextPath(); %>
            <!DOCTYPE html>
            <html lang="zh-Hant">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
                <title>新增考試資料</title>
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
                        max-width: 800px;
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
                        content: "📝";
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

                    .form-control,
                    .form-select {
                        width: 100%;
                        padding: 1rem 1.5rem;
                        border: 2px solid #e2e8f0;
                        border-radius: 12px;
                        font-size: 1rem;
                        transition: all 0.3s ease;
                        background: white;
                        color: #333;
                    }

                    .form-control:focus,
                    .form-select:focus {
                        outline: none;
                        border-color: #667eea;
                        box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
                        transform: translateY(-2px);
                    }

                    .form-control:hover,
                    .form-select:hover {
                        border-color: #cbd5e0;
                    }

                    textarea.form-control {
                        resize: vertical;
                        min-height: 100px;
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

                    .btn-primary {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                        box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
                    }

                    .btn-primary:hover {
                        transform: translateY(-3px);
                        box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
                    }

                    .btn-primary:active {
                        transform: translateY(-1px);
                    }

                    .btn::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%;
                        width: 100%;
                        height: 100%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
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

                    /* Input Icons */
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

                    .form-group.has-icon .form-control,
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

                    .form-group:nth-child(5) {
                        animation-delay: 0.5s;
                    }

                    .form-group:nth-child(6) {
                        animation-delay: 0.6s;
                    }

                    /* Loading state */
                    .btn.loading {
                        position: relative;
                        color: transparent;
                    }

                    .btn.loading::after {
                        content: '';
                        position: absolute;
                        width: 20px;
                        height: 20px;
                        top: 50%;
                        left: 50%;
                        margin-left: -10px;
                        margin-top: -10px;
                        border: 2px solid transparent;
                        border-top-color: #ffffff;
                        border-radius: 50%;
                        animation: spin 1s linear infinite;
                    }

                    @keyframes spin {
                        0% {
                            transform: rotate(0deg);
                        }

                        100% {
                            transform: rotate(360deg);
                        }
                    }

                    /* Responsive Design */
                    @media (max-width: 768px) {
                        body {
                            padding: 1rem;
                        }

                        .card-body {
                            padding: 2rem;
                        }

                        .card-header h4 {
                            font-size: 1.5rem;
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
                </style>
            </head>

            <body>
                <div class="container">
                    <div class="card">
                        <div class="card-header">
                            <h4>新增考試資料</h4>
                        </div>
                        <div class="card-body">
                            <form id="examForm" action="InsertExamPaperServlet" method="post">
                                <div class="form-group has-icon" data-icon="✏️">
                                    <label for="name" class="form-label">考試名稱</label>
                                    <input type="text" class="form-control" id="name" name="name" placeholder="請輸入考試名稱"
                                        required>
                                </div>

                                <div class="form-group has-icon" data-icon="📚">
                                    <label for="courseId" class="form-label">課程名稱</label>
                                    <select class="form-select" id="courseId" name="courseId" required>
                                        <option value="">請選擇課程</option>

                                    </select>
                                </div>
                                <div class="form-group has-icon" data-icon="📋">
                                    <label for="examType" class="form-label">考試類型</label>
                                    <select class="form-select" id="examType" name="examType" required>
                                        <option value="">請選擇考試類型</option>
                                        <option value="隨堂測驗">隨堂測驗</option>
                                        <option value="段考">段考</option>
                                        <option value="期中">期中</option>
                                        <option value="期末">期末</option>
                                    </select>
                                </div>

                                <div class="form-group has-icon" data-icon="📅">
                                    <label for="examDate" class="form-label">考試日期</label>
                                    <input type="date" class="form-control" id="examDate" name="examDate" required>
                                </div>

                                <div class="form-group has-icon" data-icon="💯">
                                    <label for="totalScore" class="form-label">總分（最大 100 分）</label>
                                    <input type="number" class="form-control" id="totalScore" name="totalScore" min="0"
                                        max="100" placeholder="請輸入總分" required>
                                </div>

                                <div class="form-group has-icon" data-icon="📝">
                                    <label for="description" class="form-label">描述</label>
                                    <textarea class="form-control" id="description" name="description" rows="4"
                                        placeholder="請輸入考試相關描述（選填）"></textarea>
                                </div>

                                <div class="submit-section">
                                    <button type="button" id="submitBtn" class="btn btn-primary">
                                        確認送出
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <script>
                    const basePath = "<%= request.getContextPath() %>";
                    console.log(basePath);
                    // Set minimum date to today
                    document.getElementById('examDate').min = new Date().toISOString().split('T')[0];

                    document.getElementById("submitBtn").addEventListener("click", function () {
                        const form = document.getElementById("examForm");

                        if (!form.checkValidity()) {
                            form.reportValidity();
                            return;
                        }

                        Swal.fire({
                            title: '確定要新增這筆考試資料嗎？',
                            text: "送出後將寫入資料庫",
                            icon: 'question',
                            showCancelButton: true,
                            confirmButtonText: '確定新增',
                            cancelButtonText: '取消',
                            reverseButtons: true,
                            customClass: {
                                popup: 'swal2-popup'
                            }
                        }).then((result) => {
                            if (result.isConfirmed) {
                                const submitBtn = document.getElementById("submitBtn");
                                submitBtn.classList.add('loading');
                                submitBtn.disabled = true;


                                const dto = {
                                    name: document.getElementById("name").value,
                                    courseId: document.getElementById("courseId").value,
                                    examType: document.getElementById("examType").value,
                                    examDate: document.getElementById("examDate").value,
                                    totalScore: document.getElementById("totalScore").value,
                                    description: document.getElementById("description").value
                                };


                                fetch(basePath + "/api/exam-papers", {
                                    method: "POST",
                                    headers: {
                                        "Content-Type": "application/json"
                                    },
                                    body: JSON.stringify(dto)
                                })
                                    .then(response => {
                                        if (!response.ok) {
                                            throw new Error("伺服器錯誤，狀態碼：" + response.status);
                                        }
                                        return response.json();
                                    })
                                    .then(data => {
                                        Swal.fire({
                                            title: '新增成功！',
                                            text: '考試資料已成功新增，是否要繼續新增？',
                                            icon: 'success',
                                            showCancelButton: true,
                                            confirmButtonText: '是，繼續新增',
                                            cancelButtonText: '否，返回查詢',
                                            reverseButtons: true,
                                            customClass: {
                                                popup: 'swal2-popup'
                                            }
                                        }).then(result => {
                                            if (result.isConfirmed) {
                                                form.reset();

                                                document.getElementById('examDate').value = new Date().toISOString().split('T')[0];
                                            } else {
                                                window.location.href = basePath + "/SelectExamPaper.jsp";
                                            }
                                        });
                                    })
                                    .catch(error => {
                                        console.error("新增失敗：", error);
                                        Swal.fire("錯誤", "無法新增資料，請稍後再試", "error");
                                    })
                                    .finally(() => {
                                        submitBtn.classList.remove('loading');
                                        submitBtn.disabled = false;
                                    });

                            } else {
                                Swal.fire({
                                    title: '已取消',
                                    text: '您已取消新增操作',
                                    icon: 'info',
                                    timer: 1500,
                                    showConfirmButton: false,
                                    customClass: {
                                        popup: 'swal2-popup'
                                    }
                                });
                            }
                        });
                    });


                    //抓取課程資料
                    fetch(basePath + "/api/course/all")
                        .then(response => response.json())
                        .then(data => {
                            console.log("取得的課程資料：", data);


                            const courses = data.data;
                            const select = document.getElementById("courseId");

                            courses.forEach(course => {
                                const option = document.createElement("option");
                                option.value = course.id;
                                option.textContent = course.name;
                                select.appendChild(option);
                            });
                        })
                        .catch(error => {
                            console.error("載入課程失敗：", error);
                            Swal.fire("錯誤", "無法載入課程資料，請稍後再試", "error");
                        });


                    // Add smooth focus effects
                    document.querySelectorAll('.form-control, .form-select').forEach(element => {
                        element.addEventListener('focus', function () {
                            this.parentElement.style.transform = 'scale(1.02)';
                        });

                        element.addEventListener('blur', function () {
                            this.parentElement.style.transform = 'scale(1)';
                        });
                    });
                </script>
            </body>

            </html>