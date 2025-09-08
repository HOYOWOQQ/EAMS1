<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <title>Insert title here</title>
    </head>

    <body>
        <c:if test="${success}">
            <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
            <script>
                Swal.fire({
                    title: "新增成功！",
                    text: "是否要繼續新增？",
                    icon: "success",
                    showCancelButton: true,
                    confirmButtonText: "繼續新增",
                    cancelButtonText: "查看剛剛新增的成績"
                }).then((result) => {
                    if (result.isConfirmed) {
                        // 繼續留在這個頁面，什麼都不用做
                    } else {
                        // 跳轉到查詢頁面
                        window.location.href = "${pageContext.request.contextPath}/SelectExamResult?examPaper=${examPaperName}";
                    }
                });
            </script>
        </c:if>
    </body>

    </html>