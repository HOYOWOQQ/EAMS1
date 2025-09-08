<%@ page contentType="text/html; charset=UTF-8" %>
    <!DOCTYPE html>
    <html lang="zh-TW">

    <head>
        <meta charset="UTF-8">
        <title>課程管理系統</title>
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
                max-width: 1400px;
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
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 1rem;
            }

            .page-title {
                font-size: 2rem;
                font-weight: 700;
                color: #333;
            }

            .header-actions {
                display: flex;
                gap: 12px;
                flex-wrap: wrap;
            }

            /* Stats Section */
            .stats-section {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 1.5rem;
                margin-bottom: 2rem;
            }

            .stat-card {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                padding: 1.5rem;
                border-radius: 16px;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                text-align: center;
                transition: transform 0.3s ease;
            }

            .stat-card:hover {
                transform: translateY(-2px);
            }

            .stat-number {
                font-size: 2rem;
                font-weight: 700;
                color: #667eea;
                margin-bottom: 0.5rem;
            }

            .stat-label {
                color: #666;
                font-size: 0.9rem;
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

            .search-input {
                width: 100%;
                padding: 16px 24px;
                border: 2px solid rgba(102, 126, 234, 0.2);
                border-radius: 25px;
                font-size: 16px;
                background: white;
                transition: all 0.3s ease;
                margin-bottom: 1rem;
            }

            .search-input:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            .search-filters {
                display: flex;
                gap: 16px;
                flex-wrap: wrap;
                align-items: center;
            }

            .filter-group {
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .filter-group label {
                font-weight: 600;
                color: #4a5568;
                white-space: nowrap;
            }

            .filter-select {
                padding: 10px 16px;
                border: 2px solid rgba(102, 126, 234, 0.2);
                border-radius: 8px;
                background: white;
                font-size: 14px;
                cursor: pointer;
                transition: all 0.3s ease;
                min-width: 120px;
            }

            .filter-select:focus {
                outline: none;
                border-color: #667eea;
            }

            .clear-search {
                background: linear-gradient(135deg, #6c757d, #495057);
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 8px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                transition: all 0.3s ease;
            }

            .clear-search:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }

            .search-results-info {
                margin-top: 1rem;
                padding: 12px 16px;
                background: rgba(102, 126, 234, 0.05);
                border-radius: 8px;
                color: #666;
                font-size: 14px;
                font-weight: 500;
            }

            .search-section {
                display: flex;
                align-items: center;
                gap: 20px;
                flex-wrap: wrap;
            }

            .search-input {
                flex: 2 1 320px;
                min-width: 220px;
                max-width: 380px;
                margin-bottom: 0;
            }

            .filter-group {
                margin-bottom: 0;
            }

            .search-results-info {
                margin-top: 0;
                margin-left: 20px;
                padding: 12px 16px;
                background: rgba(102, 126, 234, 0.05);
                border-radius: 8px;
                color: #666;
                font-size: 14px;
                font-weight: 500;
            }

            /* Buttons */
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
                gap: 8px;
            }

            .btn:hover {
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }

            .btn-primary {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
            }

            .btn-orange {
                background: linear-gradient(135deg, #ff9a56, #ff6b35);
                color: white;
            }

            .btn-green {
                background: linear-gradient(135deg, #56d364, #28a745);
                color: white;
            }

            .btn-secondary {
                background: #6c757d;
                color: white;
            }

            /* Table Section */
            .table-section {
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
                overflow: hidden;
                margin: 20px auto;
                max-width: 1400px;
            }

            .course-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 14px;
                background: white;
            }

            .course-table thead {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
            }

            .course-table th {
                padding: 18px 12px;
                text-align: left;
                font-weight: 600;
                font-size: 13px;
                letter-spacing: 0.5px;
                border: none;
                position: relative;
            }

            /* 表頭圖標樣式 */
            .course-table th::before {
                margin-right: 8px;
                font-size: 16px;
            }

            .course-table th:nth-child(1)::before {
                content: "📚";
            }

            .course-table th:nth-child(2)::before {
                content: "🏷️";
            }

            .course-table th:nth-child(3)::before {
                content: "📝";
            }

            .course-table th:nth-child(4)::before {
                content: "👥";
            }

            .course-table th:nth-child(5)::before {
                content: "👤";
            }

            .course-table th:nth-child(6)::before {
                content: "💰";
            }

            .course-table th:nth-child(7)::before {
                content: "📅";
            }

            .course-table th:nth-child(8)::before {
                content: "🏁";
            }

            .course-table th:nth-child(9)::before {
                content: "🔄";
            }

            .course-table th:nth-child(10)::before {
                content: "⚙️";
            }

            .course-table th:not(:last-child)::after {
                content: '';
                position: absolute;
                right: 0;
                top: 25%;
                height: 50%;
                width: 1px;
                background: rgba(255, 255, 255, 0.3);
            }

            .course-table tbody tr {
                transition: all 0.3s ease;
                border-bottom: 1px solid #e8ecf0;
            }

            .course-table tbody tr:hover {
                background: linear-gradient(90deg, #f8f9ff 0%, #fff 100%);
                transform: translateY(-1px);
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            }

            .course-table tbody tr:nth-child(even) {
                background-color: #fafbfc;
            }

            .course-table tbody tr:nth-child(even):hover {
                background: linear-gradient(90deg, #f0f2ff 0%, #fff 100%);
            }

            .course-table td {
                padding: 16px 12px;
                border: none;
                vertical-align: middle;
                color: #2c3e50;
                line-height: 1.5;
            }

            /* 課程名稱樣式 */
            .course-table td:first-child {
                font-weight: 600;
                color: #3498db;
            }

            /* 類型標籤樣式 */
            .course-table td:nth-child(2) {
                font-weight: 500;
            }

            /* 描述文字樣式 */
            .course-table td:nth-child(3) {
                max-width: 200px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                color: #7f8c8d;
            }

            /* 數字欄位樣式 */
            .course-table td:nth-child(4),
            .course-table td:nth-child(5),
            .course-table td:nth-child(6) {
                text-align: center;
                font-weight: 500;
            }

            /* 費用特殊樣式 */
            .course-table td:nth-child(6) {
                color: #e74c3c;
                font-weight: 600;
            }

            /* 日期樣式 */
            .course-table td:nth-child(7),
            .course-table td:nth-child(8) {
                font-family: 'Courier New', monospace;
                font-size: 13px;
                color: #34495e;
            }

            /* 狀態標籤樣式 */
            .course-table td:nth-child(9) {
                text-align: center;
            }

            .status-badge {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
                text-align: center;
                min-width: 80px;
                border: 2px solid;
            }

            .status-badge.status-active {
                background: #e8f5e8;
                color: #2e7d32;
                border-color: #4caf50;
            }

            .status-badge.status-inactive {
                background: #fce4ec;
                color: #c2185b;
                border-color: #e91e63;
            }

            .status-badge.status-finished {
                background: #fff3e0;
                color: #f57c00;
                border-color: #ff9800;
            }

            /* 不同狀態的顏色 */
            .status-recruiting {
                background: #e8f5e8;
                color: #2e7d32;
                border-color: #4caf50;
            }

            .status-starting {
                background: #fff3e0;
                color: #f57c00;
                border-color: #ff9800;
            }

            .status-registering {
                background: #e3f2fd;
                color: #1976d2;
                border-color: #2196f3;
            }

            .status-full {
                background: #fce4ec;
                color: #c2185b;
                border-color: #e91e63;
            }

            .status-ended {
                background: #f5f5f5;
                color: #616161;
                border-color: #9e9e9e;
            }

            /* 按鈕容器 */
            .course-table td:last-child {
                text-align: center;
                padding: 12px 8px;
            }

            /* 按鈕基本樣式 */
            .course-table button {
                padding: 8px 16px;
                border: none;
                border-radius: 6px;
                font-size: 12px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                margin: 0 3px;
                min-width: 60px;
            }

            /* 編輯按鈕 */
            .course-table button[onclick*="editCourse"] {
                background: linear-gradient(135deg, #3498db, #2980b9);
                color: white;
            }

            .course-table button[onclick*="editCourse"]:hover {
                background: linear-gradient(135deg, #2980b9, #21618c);
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
            }

            /* 刪除按鈕 */
            .course-table button[onclick*="deleteCourse"] {
                background: linear-gradient(135deg, #e74c3c, #c0392b);
                color: white;
            }

            .course-table button[onclick*="deleteCourse"]:hover {
                background: linear-gradient(135deg, #c0392b, #a93226);
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
            }

            /* 響應式設計 */
            @media (max-width: 1200px) {
                .course-table {
                    font-size: 13px;
                }

                .course-table th,
                .course-table td {
                    padding: 12px 8px;
                }

                .course-table td:nth-child(3) {
                    max-width: 150px;
                }
            }

            @media (max-width: 768px) {
                .table-section {
                    margin: 10px;
                    border-radius: 8px;
                }

                .course-table {
                    font-size: 12px;
                }

                .course-table th,
                .course-table td {
                    padding: 10px 6px;
                }

                .course-table button {
                    padding: 6px 12px;
                    font-size: 11px;
                    margin: 2px 1px;
                }

                .course-table td:nth-child(3) {
                    max-width: 100px;
                }
            }

            /* 載入動畫 */
            .course-table tbody {
                animation: fadeInUp 0.6s ease-out;
            }

            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }

                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* 空表格提示 */
            .empty-state {
                text-align: center;
                padding: 60px 20px;
                color: #7f8c8d;
                font-size: 16px;
            }

            .empty-state::before {
                content: "📚";
                display: block;
                font-size: 48px;
                margin-bottom: 16px;
            }

            /* Modal Styles - Enhanced */
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.6);
                backdrop-filter: blur(8px);
                animation: fadeIn 0.3s ease;
            }

            .modal-content {
                background: white;
                margin: 2% auto;
                padding: 0;
                border-radius: 20px;
                width: 90%;
                max-width: 800px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
                position: relative;
                animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
                max-height: 95vh;
                overflow: hidden;
                border: 1px solid rgba(255, 255, 255, 0.2);
            }

            /* Modal Header */
            .modal-header {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 24px 32px;
                border-radius: 20px 20px 0 0;
                position: relative;
                overflow: hidden;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .modal-header::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="20" cy="20" r="1" fill="white" opacity="0.1"/><circle cx="80" cy="80" r="1" fill="white" opacity="0.1"/><circle cx="40" cy="60" r="1" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
                pointer-events: none;
            }

            /* Modal Body */
            .modal-body {
                padding: 32px;
                max-height: calc(95vh - 140px);
                overflow-y: auto;
            }

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
                    transform: translateY(60px) scale(0.95);
                    opacity: 0;
                }

                to {
                    transform: translateY(0) scale(1);
                    opacity: 1;
                }
            }

            /* Close Button Enhanced */
            .close {
                position: absolute;
                right: 24px;
                top: 50%;
                transform: translateY(-50%);
                font-size: 32px;
                cursor: pointer;
                color: rgba(255, 255, 255, 0.8);
                transition: all 0.3s ease;
                z-index: 2;
                width: 40px;
                height: 40px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.1);
            }

            .close:hover {
                color: white;
                background: rgba(255, 255, 255, 0.2);
                transform: translateY(-50%) rotate(90deg);
            }

            /* Modal Title */
            .modal h3 {
                margin: 0;
                font-size: 24px;
                font-weight: 700;
                position: relative;
                z-index: 1;
            }

            /* Form Labels with Icons */
            .form-label {
                display: block;
                margin-bottom: 8px;
                font-weight: 600;
                color: #2c3e50;
                font-size: 14px;
                position: relative;
            }

            .form-label::before {
                margin-right: 6px;
                font-size: 16px;
            }

            .form-label[for="courseIdShow"]::before {
                content: "🔢";
            }

            .form-label[for="courseName"]::before {
                content: "📚";
            }

            .form-label[for="courseType"]::before {
                content: "🏷️";
            }

            .form-label[for="courseDescription"]::before {
                content: "📝";
            }

            .form-label[for="maxCapacity"]::before {
                content: "👥";
            }

            .form-label[for="minCapacity"]::before {
                content: "👤";
            }

            .form-label[for="fee"]::before {
                content: "💰";
            }

            .form-label[for="startDate"]::before {
                content: "📅";
            }

            .form-label[for="endDate"]::before {
                content: "🏁";
            }

            .form-label[for="status"]::before {
                content: "🔄";
            }

            .form-label[for="remark"]::before {
                content: "📌";
            }

            /* 手動排課 Modal 圖標 */
            .form-label[for="courseSelect"]::before {
                content: "📚";
            }

            .form-label[for="subjectSelect"]::before {
                content: "📖";
            }

            .form-label[for="teacherSelect"]::before {
                content: "👨‍🏫";
            }

            .form-label[for="classroomSelect"]::before {
                content: "🏫";
            }

            .form-label[for="lessonDate"]::before {
                content: "📅";
            }

            .form-label[for="periodStart"]::before {
                content: "🕐";
            }

            .form-label[for="periodEnd"]::before {
                content: "🕘";
            }

            /* 自動排課 Modal 圖標 */
            .form-label[for="autoStartDate"]::before {
                content: "📅";
            }

            .form-label[for="autoEndDate"]::before {
                content: "🏁";
            }

            .form-label[for="autoCourseSelect"]::before {
                content: "📚";
            }

            .form-label[for="autoSubjectSelect"]::before {
                content: "📖";
            }

            .form-label[for="autoTeacherSelect"]::before {
                content: "👨‍🏫";
            }

            .form-label[for="autoClassroomSelect"]::before {
                content: "🏫";
            }

            /* Enhanced Form Controls */
            .form-control {
                width: 100%;
                padding: 14px 18px;
                border: 2px solid #e8ecf0;
                border-radius: 12px;
                font-size: 16px;
                transition: all 0.3s ease;
                background: white;
                font-family: inherit;
            }

            .form-control:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
                transform: translateY(-1px);
            }

            .form-control:hover {
                border-color: #bdc3c7;
            }

            /* 複選框群組樣式 - 完全使用 form-control 基礎樣式 */
            .checkbox-group {
                width: 100%;
                padding: 10px 14px;
                border: 2px solid #e8ecf0;
                border-radius: 12px;
                background: white;
                font-family: inherit;
                transition: all 0.3s ease;
                max-height: 120px;
                overflow-y: auto;
            }

            .checkbox-group:hover {
                border-color: #bdc3c7;
            }

            .checkbox-group:focus-within {
                border-color: #667eea;
                box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
                transform: translateY(-1px);
            }



            /* 複選框項目樣式 */
            .checkbox-item {
                display: flex;
                align-items: center;
                padding: 6px 8px;
                margin: 2px 0;
                border-radius: 8px;
                cursor: pointer;
                transition: all 0.2s ease;
            }


            .checkbox-item:hover {
                background-color: rgba(102, 126, 234, 0.05);
            }

            .checkbox-item input[type="checkbox"] {
                margin-right: 10px;
                transform: scale(1.2);
                accent-color: #667eea;
            }

            .checkbox-item label {
                margin: 0;
                cursor: pointer;
                font-weight: normal;
                flex: 1;
                color: #2c3e50;
                font-size: 14px;
            }

            .checkbox-item.checked {
                background-color: rgba(102, 126, 234, 0.1);
                border-radius: 8px;
            }

            .checkbox-item.checked label {
                font-weight: 600;
                color: #667eea;
            }

            /* 已選擇項目顯示 */
            .selected-display {
                margin-top: 10px;
                padding: 10px 14px;
                background-color: #f8f9fa;
                border-radius: 8px;
                font-size: 13px;
                min-height: 24px;
                border: 1px solid #e8ecf0;
            }

            .selected-tag {
                display: inline-block;
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 4px 8px;
                margin: 2px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 500;
            }

            .selected-tag .remove {
                margin-left: 6px;
                cursor: pointer;
                font-weight: bold;
                opacity: 0.8;
            }

            .selected-tag .remove:hover {
                opacity: 1;
                color: #ffeb3b;
            }

            /* 自定義滾軸樣式 */
            .checkbox-group::-webkit-scrollbar {
                width: 6px;
            }

            .checkbox-group::-webkit-scrollbar-track {
                background: #f1f1f1;
                border-radius: 3px;
            }

            .checkbox-group::-webkit-scrollbar-thumb {
                background: #667eea;
                border-radius: 3px;
            }

            .checkbox-group::-webkit-scrollbar-thumb:hover {
                background: #5a6fd8;
            }

            /* Textarea Styling */
            textarea.form-control {
                resize: vertical;
                min-height: 80px;
                font-family: inherit;
            }

            /* Select Styling */
            select.form-control {
                cursor: pointer;
                background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6,9 12,15 18,9"></polyline></svg>');
                background-repeat: no-repeat;
                background-position: right 12px center;
                background-size: 16px;
                padding-right: 40px;
                appearance: none;
            }

            /* Enhanced Form Groups */
            .form-group {
                margin-bottom: 24px;
                position: relative;
            }

            .form-row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }

            .form-row .form-group {
                margin-bottom: 0;
            }

            /* Form Actions Enhanced */
            .form-actions {
                display: flex;
                justify-content: flex-end;
                gap: 16px;
                margin-top: 32px;
                padding-top: 24px;
                border-top: 2px solid #f8f9fa;
            }

            /* Enhanced Buttons */
            .btn {
                padding: 12px 24px;
                border: none;
                border-radius: 10px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                min-width: 100px;
                position: relative;
                overflow: hidden;
                font-family: inherit;
            }

            .btn::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
                transition: left 0.5s;
            }

            .btn:hover::before {
                left: 100%;
            }

            .btn-primary {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
            }

            .btn-primary:hover {
                background: linear-gradient(135deg, #5a6fd8, #6a3d91);
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
            }

            .btn-secondary {
                background: linear-gradient(135deg, #95a5a6, #7f8c8d);
                color: white;
                box-shadow: 0 4px 15px rgba(149, 165, 166, 0.3);
            }

            .btn-secondary:hover {
                background: linear-gradient(135deg, #839496, #6c7b7d);
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(149, 165, 166, 0.4);
            }

            /* Input Validation States */
            .form-control:invalid:not(:focus):not(:placeholder-shown) {
                border-color: #e74c3c;
                background-color: #fdf2f2;
            }

            .form-control:valid:not(:focus):not(:placeholder-shown) {
                border-color: #27ae60;
                background-color: #f2f9f2;
            }

            /* Number Input Styling */
            input[type="number"]::-webkit-outer-spin-button,
            input[type="number"]::-webkit-inner-spin-button {
                -webkit-appearance: none;
                margin: 0;
            }

            input[type="number"] {
                -moz-appearance: textfield;
            }

            /* Date Input Styling */
            input[type="date"] {
                cursor: pointer;
            }

            /* Custom Scrollbar for Modal Body */
            .modal-body::-webkit-scrollbar {
                width: 8px;
            }

            .modal-body::-webkit-scrollbar-track {
                background: #f1f1f1;
                border-radius: 4px;
            }

            .modal-body::-webkit-scrollbar-thumb {
                background: #667eea;
                border-radius: 4px;
            }

            .modal-body::-webkit-scrollbar-thumb:hover {
                background: #5a6fd8;
            }

            /* Responsive Design */
            @media (max-width: 768px) {
                .modal-content {
                    margin: 5% auto;
                    width: 95%;
                    max-height: 90vh;
                    border-radius: 16px;
                }

                .modal-header {
                    padding: 20px 24px;
                    border-radius: 16px 16px 0 0;
                }

                .modal h3 {
                    font-size: 20px;
                }

                .modal-body {
                    padding: 24px 20px;
                }

                .form-row {
                    grid-template-columns: 1fr;
                    gap: 0;
                }

                .form-row .form-group {
                    margin-bottom: 24px;
                }

                .form-actions {
                    flex-direction: column;
                }

                .btn {
                    width: 100%;
                }

                .close {
                    right: 20px;
                    font-size: 28px;
                    width: 36px;
                    height: 36px;
                }
            }

            @media (max-width: 480px) {
                .modal-content {
                    width: 98%;
                    margin: 2% auto;
                    border-radius: 12px;
                }

                .modal-header {
                    padding: 16px 20px;
                    border-radius: 12px 12px 0 0;
                }

                .modal h3 {
                    font-size: 18px;
                }

                .modal-body {
                    padding: 20px 16px;
                }

                .form-control {
                    padding: 12px 16px;
                    font-size: 16px;
                }

                .close {
                    right: 16px;
                    font-size: 24px;
                    width: 32px;
                    height: 32px;
                }
            }

            /* Focus Enhancement */
            .form-control:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
            }

            /* Disabled State */
            .form-control:disabled {
                background-color: #f8f9fa;
                color: #6c757d;
                cursor: not-allowed;
            }

            /* Required Field Styling */
            .form-control[required] {
                border-left: 4px solid #667eea;
            }

            /* Loading State for Buttons */
            .btn:disabled {
                opacity: 0.6;
                cursor: not-allowed;
                transform: none;
            }

            .btn:disabled::before {
                display: none;
            }

            /*sweetalert2 custom styles*/
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

            /* Responsive */
            @media (max-width: 768px) {
                .container {
                    padding: 0 10px;
                }

                .header-section {
                    flex-direction: column;
                    align-items: stretch;
                    text-align: center;
                }

                .header-actions {
                    justify-content: center;
                }

                .search-filters {
                    flex-direction: column;
                    align-items: stretch;
                }

                .filter-group {
                    justify-content: space-between;
                }

                .course-table th,
                .course-table td {
                    padding: 12px 8px;
                    font-size: 14px;
                }

                .modal-content {
                    margin: 10% auto;
                    width: 95%;
                    padding: 1.5rem;
                }

                .form-row {
                    flex-direction: column;
                }

                .page-title {
                    font-size: 1.5rem;
                }

                .stats-section {
                    grid-template-columns: repeat(2, 1fr);
                    gap: 1rem;
                }
            }

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
        <!-- SweetAlert2 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css" rel="stylesheet">
        <!-- SweetAlert2 JS -->
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    </head>

    <body>
        <div class="container">
            <!-- Header -->
            <div class="header-section">
                <h2 class="page-title">📚 課程管理</h2>
                <div class="header-actions">
                    <button class="btn btn-primary" onclick="showNewCourseForm()">➕ 新增課程</button>
                    <button class="btn btn-orange" onclick="showScheduleForm()">📋 手動排課</button>
                    <button class="btn btn-green" onclick="showAutoScheduleModal()">🤖 自動排課</button>
                </div>
            </div>

            <!-- Stats -->
            <div class="stats-section">
                <div class="stat-card">
                    <div class="stat-number" id="totalCourses">0</div>
                    <div class="stat-label">總課程數</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="activeCourses">0</div>
                    <div class="stat-label">進行中課程</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="totalStudents">-</div>
                    <div class="stat-label">總學生數</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number" id="completionRate">-</div>
                    <div class="stat-label">完成率</div>
                </div>
            </div>

            <!-- Search Section -->
            <div class="search-section">
                <input type="text" id="searchInput" class="search-input" placeholder="🔍 搜尋課程名稱、類型、描述..."
                    oninput="filterCourses()">
                <div class="filter-group">
                    <label for="statusFilter">狀態</label>
                    <select id="statusFilter" class="filter-select" onchange="filterCourses()">
                        <option value="">全部</option>
                        <option value="active">啟用</option>
                        <option value="inactive">停用</option>
                        <option value="finished">已結束</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label for="typeFilter">類型</label>
                    <select id="typeFilter" class="filter-select" onchange="filterCourses()">
                        <option value="">全部</option>
                    </select>
                </div>
                <button class="clear-search" onclick="clearSearch()">🔄 清除搜尋</button>
                <div id="searchResults" class="search-results-info"></div>
            </div>

            <!-- Table -->
            <div class="table-section">
                <table id="courseTable" class="course-table">
                    <thead>
                        <tr>
                            <th>課程名稱</th>
                            <th>類型</th>
                            <th>描述</th>
                            <th>最大人數</th>
                            <th>最少人數</th>
                            <th>費用</th>
                            <th>開始日期</th>
                            <th>結束日期</th>
                            <th>狀態</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>
        </div>

        <!-- 自動排課 Modal -->
        <div id="autoScheduleModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>🤖 自動排課</h3>
                    <span class="close" onclick="hideAutoScheduleModal()">&times;</span>
                </div>
                <div class="modal-body">
                    <form id="autoScheduleForm" onsubmit="submitAutoSchedule(event)">
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label" for="autoStartDate">開始日期</label>
                                <input type="date" id="autoStartDate" name="start_date" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="autoEndDate">結束日期</label>
                                <input type="date" id="autoEndDate" name="end_date" class="form-control" required>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="autoCourseSelect">選擇課程（可多選）</label>
                            <div class="checkbox-group form-control" id="courseCheckboxGroup" style="height: 160px;">
                                <!-- 課程選項會動態載入這裡 -->
                            </div>
                            <div class="selected-display">
                                <strong>已選課程：</strong>
                                <span id="selectedCourses"></span>
                            </div>
                            <input type="hidden" name="courseIds[]" id="courseIdsInput">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="autoSubjectSelect">選擇科目（可多選）</label>
                            <div class="checkbox-group form-control" id="subjectCheckboxGroup" style="height: 160px;">
                                <!-- 科目選項會動態載入這裡 -->
                            </div>
                            <div class="selected-display">
                                <strong>已選科目：</strong>
                                <span id="selectedSubjects"></span>
                            </div>
                            <input type="hidden" name="subjectIds[]" id="subjectIdsInput">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="autoTeacherSelect">選擇老師（可多選）</label>
                            <div class="checkbox-group form-control" id="teacherCheckboxGroup" style="height: 160px;">
                                <!-- 老師選項會動態載入這裡 -->
                            </div>
                            <div class="selected-display">
                                <strong>已選老師：</strong>
                                <span id="selectedTeachers"></span>
                            </div>
                            <input type="hidden" name="teacherIds[]" id="teacherIdsInput">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="autoClassroomSelect">選擇教室（可多選）</label>
                            <div class="checkbox-group form-control" id="classroomCheckboxGroup" style="height: 90px;">
                                <!-- 教室選項會動態載入這裡 -->
                            </div>
                            <div class="selected-display">
                                <strong>已選教室：</strong>
                                <span id="selectedClassrooms"></span>
                            </div>
                            <input type="hidden" name="classroomIds[]" id="classroomIdsInput">
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">送出排課</button>
                            <button type="button" class="btn btn-secondary"
                                onclick="hideAutoScheduleModal()">取消</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- 課程新增/編輯 Modal -->
        <div id="courseModal" class="modal" aria-hidden="true">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>📚 新增 / 編輯課程</h3>
                    <span class="close" onclick="hideCourseForm()" aria-label="關閉">&times;</span>
                </div>
                <div class="modal-body">
                    <form id="courseForm" onsubmit="saveCourse(event)">
                        <div id="idRow" class="form-group">
                            <label class="form-label" for="courseIdShow">課程編號</label>
                            <input type="text" id="courseIdShow" class="form-control" name="id" readonly>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="courseName">課程名稱</label>
                            <input type="text" id="courseName" name="name" class="form-control" required
                                placeholder="請輸入課程名稱">
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="courseType">課程類型</label>
                            <input type="text" id="courseType" name="type" class="form-control"
                                placeholder="例如：技術課程、語言課程">
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="courseDescription">課程描述</label>
                            <textarea id="courseDescription" name="description" class="form-control" rows="3"
                                placeholder="請描述課程內容與特色"></textarea>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label" for="maxCapacity">最大人數</label>
                                <input type="number" id="maxCapacity" name="max_capacity" class="form-control" min="1"
                                    placeholder="最多幾人">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="minCapacity">最少人數</label>
                                <input type="number" id="minCapacity" name="min_capacity" class="form-control" min="0"
                                    placeholder="最少幾人開班">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="fee">費用</label>
                            <input type="number" id="fee" name="fee" class="form-control" min="0" step="1"
                                placeholder="課程費用 (NT$)">
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label" for="startDate">開始日期</label>
                                <input type="date" id="startDate" name="start_date" class="form-control">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="endDate">結束日期</label>
                                <input type="date" id="endDate" name="end_date" class="form-control">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="status">狀態</label>
                            <select id="status" name="status" class="form-control" required>
                                <option value="">請選擇狀態</option>
                                <option value="active">✅ 啟用 (active)</option>
                                <option value="inactive">⏸️ 停用 (inactive)</option>
                                <option value="finished">🏁 已結束 (finished)</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="remark">備註</label>
                            <input type="text" id="remark" name="remark" class="form-control" placeholder="其他相關資訊">
                        </div>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">💾 儲存</button>
                            <button type="button" class="btn btn-secondary" onclick="hideCourseForm()">❌ 取消</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- 手動排課 Modal -->
        <div id="scheduleModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>📋 手動排課</h3>
                    <span class="close" onclick="hideScheduleForm()">&times;</span>
                </div>
                <div class="modal-body">
                    <form id="manualScheduleForm">
                        <div class="form-group">
                            <label for="courseSelect" class="form-label">課程名稱</label>
                            <select id="courseSelect" name="courseId" class="form-control" required></select>
                        </div>
                        <div class="form-group">
                            <label for="subjectSelect" class="form-label">科目</label>
                            <select id="subjectSelect" name="subjectId" class="form-control" required></select>
                        </div>
                        <div class="form-group">
                            <label for="teacherSelect" class="form-label">授課老師</label>
                            <select id="teacherSelect" name="teacherId" class="form-control" required></select>
                        </div>
                        <div class="form-group">
                            <label for="classroomSelect" class="form-label">教室</label>
                            <select id="classroomSelect" name="classroomId" class="form-control" required></select>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="lessonDate" class="form-label">日期</label>
                                <input type="date" id="lessonDate" name="lessonDate" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label for="periodStart" class="form-label">起始節次</label>
                                <input type="number" id="periodStart" name="periodStart" class="form-control" min="1"
                                    max="8" required>
                            </div>
                            <div class="form-group">
                                <label for="periodEnd" class="form-label">結束節次</label>
                                <input type="number" id="periodEnd" name="periodEnd" class="form-control" min="1"
                                    max="8" required>
                            </div>
                        </div>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">儲存</button>
                            <button type="button" class="btn btn-secondary" onclick="hideScheduleForm()">取消</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script>
            let allCourses = [];
            let filteredCourses = [];

            // 複選框管理器
            class CheckboxManager {
                constructor() {
                    this.selections = {
                        courses: new Set(),
                        subjects: new Set(),
                        teachers: new Set(),
                        classrooms: new Set()
                    };
                    this.courses = [];
                    this.subjects = [];
                    this.teachers = [];
                    this.classrooms = [];
                }

                // 初始化所有複選框群組（現在不載入資料，等 loadAutoScheduleOptions 來載入）
                init() {
                    // 空的初始化，資料會由 loadAutoScheduleOptions() 載入
                }

                // 渲染複選框群組
                renderCheckboxGroup(type, data, containerId) {
                    const container = document.getElementById(containerId);
                    container.innerHTML = '';

                    data.forEach(item => {
                        const checkboxItem = document.createElement('div');
                        checkboxItem.className = 'checkbox-item';
                        checkboxItem.innerHTML = `
                    <input type="checkbox" id="\${type}_\${item.id}" value="\${item.id}" data-name="\${item.name}">
                    <label for="\${type}_\${item.id}">\${item.name}</label>
                `;
                        container.appendChild(checkboxItem);
                    });

                    // 綁定事件
                    container.addEventListener('change', (e) => this.handleCheckboxChange(e, type));
                }

                // 處理複選框變化
                handleCheckboxChange(e, type) {
                    if (e.target.type === 'checkbox') {
                        const item = e.target.closest('.checkbox-item');
                        const itemId = parseInt(e.target.value);
                        const itemName = e.target.dataset.name;

                        if (e.target.checked) {
                            item.classList.add('checked');
                            this.selections[type + 's'].add({ id: itemId, name: itemName });
                        } else {
                            item.classList.remove('checked');
                            const toRemove = [...this.selections[type + 's']].find(s => s.id === itemId);
                            this.selections[type + 's'].delete(toRemove);
                        }

                        this.updateSelectedDisplay(type + 's');
                        this.updateHiddenInput(type + 's');
                    }
                }

                // 更新已選擇項目顯示
                updateSelectedDisplay(type) {
                    const displayId = `selected\${type.charAt(0).toUpperCase() + type.slice(1)}`;
                    const display = document.getElementById(displayId);
                    const items = [...this.selections[type]];

                    if (items.length === 0) {
                        display.innerHTML = '<span style="color: #999;">尚未選擇</span>';
                    } else {
                        display.innerHTML = items.map(item =>
                            `<span class="selected-tag">\${item.name}<span class="remove" onclick="checkboxManager.removeSelection('\${type}', \${item.id})">&times;</span></span>`
                        ).join('');
                    }
                }

                // 更新隱藏 input 的值
                updateHiddenInput(type) {
                    const inputId = `\${type.slice(0, -1)}IdsInput`;
                    const input = document.getElementById(inputId);
                    const ids = [...this.selections[type]].map(item => item.id);
                    input.value = ids.join(',');
                }

                // 移除選擇
                removeSelection(type, itemId) {
                    const toRemove = [...this.selections[type]].find(s => s.id === itemId);
                    this.selections[type].delete(toRemove);

                    // 更新 UI
                    const checkbox = document.getElementById(`\${type.slice(0, -1)}_\${itemId}`);
                    if (checkbox) {
                        checkbox.checked = false;
                        checkbox.closest('.checkbox-item').classList.remove('checked');
                    }

                    this.updateSelectedDisplay(type);
                    this.updateHiddenInput(type);
                }

                // 取得所有選擇
                getSelections() {
                    const result = {};
                    Object.keys(this.selections).forEach(key => {
                        result[key] = [...this.selections[key]].map(item => item.id);
                    });
                    return result;
                }
            }

            // 創建全域複選框管理器實例
            const checkboxManager = new CheckboxManager();

            async function loadOptions() {
                const [courses, classrooms] = await Promise.all([
                    fetch('CourseListApi').then(r => r.json()),
                    fetch('RoomApi').then(r => r.json())
                ]);
                const courseSel = document.getElementById('courseSelect');
                courses.forEach(c => {
                    const opt = new Option(c.name, c.id);
                    courseSel.appendChild(opt);
                });
                const classroomSel = document.getElementById('classroomSelect');
                classrooms.forEach(r => {
                    classroomSel.innerHTML += `<option value="\${r}">\${r}</option>`;
                });
            }

            // 統計並更新四個數字欄位
            function updateStats(courses) {
                // 總課程數
                document.getElementById('totalCourses').textContent = courses.length;

                // 進行中課程（假設 status 為 'active'）
                const activeCount = courses.filter(c => c.status === 'active').length;
                document.getElementById('activeCourses').textContent = activeCount;

                // 總學生數
                fetch('StudentEnrolledListApi')
                    .then(r => r.json())
                    .then(stats => {
                        console.log('[updateStats] 學生數 API 回傳:', stats);
                        document.getElementById('totalStudents').textContent =
                            (typeof stats.student_count === 'number') ? stats.student_count : '-';
                    })
                    .catch(() => {
                        document.getElementById('totalStudents').textContent = '-';
                    });
                // 完成率
                const finishedCount = courses.filter(c => c.status === 'finished').length;
                const completionRate = courses.length > 0 ? Math.round((finishedCount / courses.length) * 100) : 0;
                document.getElementById('completionRate').textContent = courses.length > 0 ? completionRate + '%' : '-';
            }

            //載入課程列表
            async function loadCourseList() {
                const courses = await fetch('CourseListApi').then(r => r.json());
                console.log('載入課程列表:', courses);
                allCourses = courses;
                filteredCourses = courses; // 初始化為全部課程
                updateTypeFilter(courses);
                displayCourses(courses);
                updateSearchResults();
                updateStats(courses);
            }

            //載入課程
            function displayCourses(courses) {
                const tbody = document.querySelector('#courseTable tbody');
                tbody.innerHTML = '';
                if (courses.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="10" class="no-results">沒有找到符合條件的課程</td></tr>';
                    return;
                }
                courses.forEach(c => {
                    tbody.innerHTML += `
            <tr>
                <td>\${c.name}</td>
                <td>\${c.type || ''}</td>
                <td>\${c.description || ''}</td>
                <td>\${c.maxCapacity || ''}</td>
                <td>\${c.minCapacity || ''}</td>
                <td>\${c.fee || ''}</td>
                <td>\${c.startDate || ''}</td>
                <td>\${c.endDate || ''}</td>
                <td>
                    <span class="status-badge status-\${c.status || ''}">
                         \${c.status === 'active' ? '啟用' : c.status === 'inactive' ? '停用' : c.status === 'finished' ? '已結束' : ''}
                    </span>
                </td>
                <td>
                    <button onclick="editCourse('\${c.id}','\${c.name}','\${c.type || ''}','\${c.description || ''}','\${c.maxCapacity || ''}','\${c.minCapacity || ''}','\${c.fee || ''}','\${c.startDate || ''}','\${c.endDate || ''}','\${c.status || ''}','\${c.remark || ''}')">編輯</button>
                    <button onclick="deleteCourse('\${c.id}')">刪除</button>
                </td>
            </tr>`;
                });
            }

            //主要搜尋函數
            function filterCourses() {
                const searchText = document.getElementById('searchInput').value.trim();
                const statusFilter = document.getElementById('statusFilter').value;
                const typeFilter = document.getElementById('typeFilter').value;
                console.log('[filterCourses] 搜尋條件:', {
                    searchText,
                    statusFilter,
                    typeFilter
                });

                filteredCourses = allCourses.filter(course => {
                    const matchText = !searchText ||
                        (course.name && course.name.includes(searchText)) ||
                        (course.type && course.type.includes(searchText)) ||
                        (course.description && course.description.includes(searchText));
                    console.log('[filterCourses] 匹配文字:', matchText, '課程:', course.name);

                    const matchStatus = !statusFilter || course.status === statusFilter;
                    const matchType = !typeFilter || course.type === typeFilter;
                    return matchText && matchStatus && matchType;
                });
                console.log('[filterCourses] 篩選後課程:', filteredCourses);

                displayCourses(filteredCourses);
                updateSearchResults();
            }

            //清除搜尋函數
            function clearSearch() {
                document.getElementById('searchInput').value = '';
                document.getElementById('statusFilter').value = '';
                document.getElementById('typeFilter').value = '';
                filteredCourses = allCourses; // 重置為全部課程
                displayCourses(filteredCourses);
                updateSearchResults();
            }

            //更新類型篩選選項
            function updateTypeFilter(courses) {
                const typeFilter = document.getElementById('typeFilter');

                // 從課程中提取所有不重複的類型
                const types = [...new Set(courses.map(c => c.type).filter(type => type && type.trim()))];

                // 重建選項（保留"全部"）
                typeFilter.innerHTML = '<option value="">全部</option>';

                // 加入類型選項
                types.forEach(type => {
                    typeFilter.innerHTML += `<option value="\${type}">\${type}</option>`;
                });
            }

            // 更新搜尋結果資訊
            function updateSearchResults() {
                const resultsDiv = document.getElementById('searchResults');
                const total = allCourses.length;
                const filtered = filteredCourses.length;

                if (filtered === total) {
                    resultsDiv.textContent = `顯示全部 \${total} 門課程`;
                } else {
                    resultsDiv.textContent = `找到 \${filtered} 門課程（共 \${total} 門）`;
                }
            }

            function showCourseForm(
                id = '', name = '', type = '', description = '', maxCapacity = '', minCapacity = '', fee = '', startDate = '', endDate = '', status = 'active', remark = '', isEdit = false
            ) {
                document.getElementById('courseModal').style.display = 'block';

                const idInput = document.getElementById('courseIdShow');
                const idRow = document.getElementById('idRow');

                idInput.value = id;
                idInput.readOnly = isEdit;

                if (isEdit) {
                    idRow.style.display = 'block'; // 顯示但唯讀
                } else {
                    idRow.style.display = 'block'; // 顯示且可編輯
                }

                document.getElementById('courseName').value = name;
                document.getElementById('courseType').value = type;
                document.getElementById('courseDescription').value = description;
                document.getElementById('maxCapacity').value = maxCapacity;
                document.getElementById('minCapacity').value = minCapacity;
                document.getElementById('fee').value = fee;
                document.getElementById('startDate').value = startDate;
                document.getElementById('endDate').value = endDate;
                document.getElementById('status').value = status;
                document.getElementById('remark').value = remark;
            }

            function hideCourseForm() {
                document.getElementById('courseModal').style.display = 'none';
            }

            function showScheduleForm() {
                document.getElementById('scheduleModal').style.display = 'block';
                document.getElementById('subjectSelect').innerHTML = '<option value="">請先選擇課程</option>';
                // document.getElementById('teacherSelect').innerHTML = '<option value="">請先選擇課程</option>';
            }

            function hideScheduleForm() {
                document.getElementById('scheduleModal').style.display = 'none';
            }

            function editCourse(id, name, type, description, maxCapacity, minCapacity, fee, startDate, endDate, status, remark) {
                showCourseForm(id, name, type, description, maxCapacity, minCapacity, fee, startDate, endDate, status, remark, true);
            }

            function showNewCourseForm() {
                showCourseForm('', '', '', '', '', '', '', '', '', 'active', '', false);
            }

            function hideAutoScheduleModal() {
                document.getElementById('autoScheduleModal').style.display = 'none';
            }

            function showAutoScheduleModal() {
                document.getElementById('autoScheduleModal').style.display = 'block';
                checkboxManager.init(); // 初始化複選框
                loadAutoScheduleOptions(); // 載入真實資料
            }

            //載入課程與老師多選清點 - 修改為使用複選框管理器
            async function loadAutoScheduleOptions() {
                try {
                    // 載入課程資料
                    const courses = await fetch('CourseListApi').then(r => r.json());
                    checkboxManager.courses = courses;
                    checkboxManager.renderCheckboxGroup('course', courses, 'courseCheckboxGroup');

                    // 載入科目資料
                    const subjects = await fetch('SubjectListApi').then(r => r.json());
                    checkboxManager.subjects = subjects;
                    checkboxManager.renderCheckboxGroup('subject', subjects, 'subjectCheckboxGroup');

                    // 載入老師資料
                    const teachers = await fetch('TeacherListApi').then(r => r.json());
                    checkboxManager.teachers = teachers;
                    checkboxManager.renderCheckboxGroup('teacher', teachers, 'teacherCheckboxGroup');

                    // 載入教室資料
                    const classrooms = await fetch('RoomApi').then(r => r.json());
                    // 注意：教室資料格式可能需要調整
                    const formattedClassrooms = classrooms.map((room, index) => ({
                        id: room, // 如果是字串，就用字串本身作為 id
                        name: room
                    }));
                    checkboxManager.classrooms = formattedClassrooms;
                    checkboxManager.renderCheckboxGroup('classroom', formattedClassrooms, 'classroomCheckboxGroup');

                } catch (error) {
                    console.error('載入選項資料失敗:', error);
                }
            }

            //送出自動排課表單 - 修改為使用複選框管理器
            async function submitAutoSchedule(event) {
                event.preventDefault(); // 阻止表單預設送出行為
                const form = document.getElementById('autoScheduleForm');
                const formData = new FormData(form);

                // 使用新的複選框管理器來取得選中的項目
                const selections = checkboxManager.getSelections();

                // 移除原有的資料
                formData.delete('courseIds[]');
                formData.delete('subjectIds[]');
                formData.delete('teacherIds[]');
                formData.delete('classroomIds[]');

                // 重新添加選中的項目
                selections.courses.forEach(id => formData.append('courseIds[]', id));
                selections.subjects.forEach(id => formData.append('subjectIds[]', id));
                selections.teachers.forEach(id => formData.append('teacherIds[]', id));
                selections.classrooms.forEach(id => formData.append('classroomIds[]', id));

                console.log("送出自動排課資料：", Object.fromEntries(formData.entries()));
                const params = new URLSearchParams(formData);

                try {
                    const resp = await fetch('AutoScheduleServlet', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        body: params
                    }).then(r => r.json());

                    if (resp.success) {
                        Swal.fire({
                            icon: 'success',
                            title: "自動排課成功！",
                            confirmButtonText: '確定'
                        });
                        hideAutoScheduleModal();
                        loadCourseList();
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: "自動排課失敗",
                            text: resp.message || "請稍後再試",
                            confirmButtonText: '確定'
                        });
                    }
                } catch (e) {
                    Swal.fire({
                        icon: 'error',
                        title: "自動排課失敗",
                        text: "請稍後再試",
                        confirmButtonText: '確定'
                    });
                }
            }

            // 綁定按鈕
            function autoSchedule() {
                showAutoScheduleModal();
            }

            // 儲存課程
            function saveCourse(event) {
                event.preventDefault(); // 阻止表單預設送出行為

                const id = document.getElementById('courseIdShow').value;
                const name = document.getElementById('courseName').value;
                const type = document.getElementById('courseType').value;
                const description = document.getElementById('courseDescription').value;
                const maxCapacity = document.getElementById('maxCapacity').value;
                const minCapacity = document.getElementById('minCapacity').value;
                const fee = document.getElementById('fee').value;
                const startDate = document.getElementById('startDate').value;
                const endDate = document.getElementById('endDate').value;
                const status = document.getElementById('status').value;
                const remark = document.getElementById('remark').value;

                // 除錯輸出
                console.log("送出資料：");
                console.log("id:", id);
                console.log("name:", name);
                console.log("type:", type);
                console.log("description:", description);
                console.log("max_capacity:", maxCapacity);
                console.log("min_capacity:", minCapacity);
                console.log("fee:", fee);
                console.log("start_date:", startDate);
                console.log("end_date:", endDate);
                console.log("status:", status);
                console.log("remark:", remark);

                // 使用 URL 編碼格式組成 body
                const body = `id=\${encodeURIComponent(id)}&name=\${encodeURIComponent(name)}&type=\${encodeURIComponent(type)}&description=\${encodeURIComponent(description)}&max_capacity=\${encodeURIComponent(maxCapacity)}&min_capacity=\${encodeURIComponent(minCapacity)}&fee=\${encodeURIComponent(fee)}&start_date=\${encodeURIComponent(startDate)}&end_date=\${encodeURIComponent(endDate)}&status=\${encodeURIComponent(status)}&remark=\${encodeURIComponent(remark)}`;

                fetch('CourseSaveApi', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: body
                })
                    .then(res => res.ok ? res.json() : Promise.reject())
                    .then(msg => {
                        Swal.fire({
                            icon: 'success',
                            title: msg.message || "儲存成功！",
                            confirmButtonText: '確定'
                        }).then(() => {
                            hideCourseForm();
                            loadCourseList();
                        });
                    })
                    .catch(() => Swal.fire({
                        icon: 'error',
                        title: "儲存失敗，請稍後再試",
                        confirmButtonText: '確定'
                    }));
            }

            async function deleteCourse(id) {
                Swal.fire({
                    title: '確定要刪除嗎？',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: '確定',
                    cancelButtonText: '取消'
                }).then((result) => {
                    if (result.isConfirmed) {
                        fetch('CourseDeleteServlet?id=' + id, { method: 'POST' })
                            .then(res => res.ok ? res.text() : Promise.reject())
                            .then(msg => {
                                Swal.fire({
                                    icon: 'success',
                                    title: msg || '刪除成功',
                                    confirmButtonText: '確定'
                                });
                                loadCourseList();
                            })
                            .catch(() => {
                                Swal.fire({
                                    icon: 'error',
                                    title: '刪除失敗',
                                    text: '請稍後再試',
                                    confirmButtonText: '確定'
                                });
                            });
                    }
                });
            }

            document.addEventListener('DOMContentLoaded', () => {
                loadOptions();
                loadCourseList();
            });

            // 手動排課表單提交
            document.getElementById('manualScheduleForm').addEventListener('submit', async function (event) {
                event.preventDefault(); // 阻止表單預設送出
                const form = event.target;
                const formData = new FormData(form);
                const params = new URLSearchParams(formData);
                console.log("手動排課資料：", Object.fromEntries(formData.entries()));

                try {
                    const resp = await fetch('CourseScheduleServlet', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        body: params
                    });
                    const result = await resp.json();
                    if (result.success) {
                        Swal.fire({
                            icon: 'success',
                            title: '手動排課成功！',
                            confirmButtonText: '確定'
                        });
                        hideScheduleForm();
                        loadCourseList();
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: '手動排課失敗',
                            text: result.message || '未知錯誤',
                            confirmButtonText: '確定'
                        });
                    }
                } catch (e) {
                    Swal.fire({
                        icon: 'error',
                        title: '手動排課失敗',
                        text: '請稍後再試',
                        confirmButtonText: '確定'
                    });
                }
            });

            // 當選取課程時，載入對應科目
            document.getElementById('courseSelect').addEventListener('change', async function () {
                const courseId = this.value;
                // 清空科目與老師選單
                const subjectSel = document.getElementById('subjectSelect');
                const teacherSel = document.getElementById('teacherSelect');
                subjectSel.innerHTML = '<option value="">載入中...</option>';
                teacherSel.innerHTML = '<option value="">請先選擇科目</option>';

                // 取得該課程的科目
                const subjects = await fetch('SubjectByCourseApi?courseId=' + courseId).then(r => r.json());
                console.log(subjects);
                subjectSel.innerHTML = '<option value="">請選擇科目</option>';
                subjects.forEach(s => {
                    subjectSel.innerHTML += `<option value="\${s.id}">\${s.name}</option>`;
                });
            });

            // 當選取科目時，載入對應老師
            document.getElementById('subjectSelect').addEventListener('change', async function () {
                const courseId = document.getElementById('courseSelect').value;
                const subjectId = this.value;
                const teacherSel = document.getElementById('teacherSelect');
                teacherSel.innerHTML = '<option value="">載入中...</option>';

                // 取得該課程該科目的老師
                const teachers = await fetch(`TeacherByCourseSubjectApi?subjectId=\${subjectId}`).then(r => r.json());
                console.log(teachers);
                teacherSel.innerHTML = '<option value="">請選擇老師</option>';
                teachers.forEach(t => {
                    teacherSel.innerHTML += `<option value="\${t.id}">\${t.name}</option>`;
                });
            });

            // 點擊 modal 外部關閉
            window.onclick = function (event) {
                const autoModal = document.getElementById('autoScheduleModal');
                const courseModal = document.getElementById('courseModal');
                const scheduleModal = document.getElementById('scheduleModal');

                if (event.target === autoModal) {
                    hideAutoScheduleModal();
                }
                if (event.target === courseModal) {
                    hideCourseForm();
                }
                if (event.target === scheduleModal) {
                    hideScheduleForm();
                }
            }

            // ESC 鍵關閉 modal
            document.addEventListener('keydown', function (event) {
                if (event.key === 'Escape') {
                    hideAutoScheduleModal();
                    hideCourseForm();
                    hideScheduleForm();
                }
            });
        </script>
    </body>

    </html>