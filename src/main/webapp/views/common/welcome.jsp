<%@ page contentType="text/html; charset=UTF-8" %>

    <body>
        <div class="welcome-container">
            <div class="welcome-card">
                <div class="welcome-header">
                    <h2>
                        <span class="welcome-text">
                            歡迎 ${sessionScope.name}！
                        </span>

                    </h2>
                </div>
                <div class="welcome-body">
                    <p>您好，這裡是 <b>學生管理系統</b>。請善用下列各項功能，提升學校管理效率：</p>
                    <ul class="feature-list">
                        <li>
                            <span class="icon">👤</span>
                            <b>學生/老師管理：</b> 管理學生與教師的基本資料、查詢與異動。
                        </li>
                        <li>
                            <span class="icon">📊</span>
                            <b>成績管理：</b> 查詢、登錄與分析學生成績資料。
                        </li>
                        <li>
                            <span class="icon">⏰</span>
                            <b>出缺勤管理：</b> 紀錄學生出缺勤，生成出勤報表。
                        </li>
                        <li>
                            <span class="icon">💳</span>
                            <b>費用管理：</b> 查詢、繳納、管理各類費用明細。
                        </li>
                        <li>
                            <span class="icon">📚</span>
                            <b>課程管理：</b> 查看、規劃課程與排課資訊。
                        </li>
                        <li>
                            <span class="icon">📢</span>
                            <b>班級通知/教師日誌：</b> 發送班級通知，記錄教師教學日誌。
                        </li>
                    </ul>
                    <div class="welcome-links">
                        <a href="MVC.html" class="welcome-link">結構圖</a>
                        <a href="https://docs.google.com/presentation/d/14lvEPGnzJIiei1ofO1HmGXvxnF52axfbQuFr1aruJe0/edit?usp=sharing"
                            class="welcome-link">使用說明</a>
                    </div>
                </div>
                <div class="welcome-footer">
                    <span>祝您有美好的一天，管理更輕鬆！🎉</span>
                </div>
            </div>
        </div>
    </body>
    <style>
        body {
            min-height: 100vh;
            background: url('${pageContext.request.contextPath}/resources/img/003.jpg') center center / cover no-repeat;
            overflow: hidden;
        }

        .welcome-container {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            /* 改成100vh讓內容垂直置中且不超出 */
            width: 100vw;
            /* 讓背景鋪滿整個視窗 */
            box-sizing: border-box;

        }

        .welcome-card {
            background: #fff;
            border-radius: 24px;
            box-shadow: 0 6px 32px 4px #b1b7df44;
            padding: 40px 48px 32px 48px;
            max-width: 540px;
            width: 100%;
            margin: 32px auto;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .welcome-header h2 {
            color: #684bb1;
            margin-bottom: 16px;
            font-size: 2rem;
            letter-spacing: 1px;
        }

        .welcome-body {
            width: 100%;
        }

        .welcome-body p {
            color: #444;
            margin-bottom: 18px;
            font-size: 1.13rem;
            line-height: 1.7;
        }

        .welcome-list {
            margin-bottom: 20px;
            padding-left: 20px;
        }

        .welcome-list li {
            margin-bottom: 8px;
            color: #555;
            font-size: 1rem;
        }

        .welcome-links {
            margin-top: 18px;
        }

        .welcome-link {
            margin-right: 18px;
            color: #6f47c7;
            text-decoration: none;
            font-weight: 500;
            transition: color 0.2s;
        }

        .welcome-link:hover {
            color: #ff7043;
        }

        .welcome-footer {
            margin-top: 24px;
            color: #9d84c9;
            font-size: 1rem;
        }
    </style>