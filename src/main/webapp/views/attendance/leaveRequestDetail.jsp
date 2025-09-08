<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="java.util.*, model.attendance.LeaveRequest,dao.attendance.UtilDao"%>
<%
    LeaveRequest leave = (LeaveRequest) request.getAttribute("leave");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h2>假單詳細資料</h2>

<% if (leave != null) { %>
    <table border="1">
        <tr><th>學生姓名</th><td><%= leave.getStudentName() %></td></tr>
        <tr><th>課程名稱</th><td><%= leave.getCourseName() %></td></tr>
        <tr><th>上課日期</th><td><%= leave.getLessonDate() %></td></tr>
        <tr><th>節次</th><td><%= leave.getPeriodStart() %> - <%= leave.getPeriodEnd() %></td></tr>
        <tr><th>請假類型</th><td><%= leave.getLeaveType() %></td></tr>
        <tr><th>請假理由</th><td><%= leave.getReason() %></td></tr>
        <tr><th>審核狀態</th><td><%= UtilDao.getStatusText(leave.getStatus()) %></td></tr>
        <tr><th>附件</th>
            <td>
                <% if (leave.getAttachmentPath() != null && !leave.getAttachmentPath().isBlank()) { %>
                    <a href="../uploads/<%= leave.getAttachmentPath() %>" target="_blank">查看附件</a>
                <% } else { %>
                    無
                <% } %>
            </td>
        </tr>
    </table>
<% } else { %>
    <p>查無此假單資料。</p>
<% } %>

<p><a href="javascript:history.back()">← 返回上一頁</a></p>
</body>
</html>