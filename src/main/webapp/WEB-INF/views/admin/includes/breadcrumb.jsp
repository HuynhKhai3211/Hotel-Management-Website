<<<<<<< HEAD
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
=======
﻿<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a></li>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        <c:forEach var="item" items="${breadcrumbs}" varStatus="status">
            <c:choose>
                <c:when test="${status.last}">
                    <li class="breadcrumb-item active">${item.label}</li>
                </c:when>
                <c:otherwise>
                    <li class="breadcrumb-item"><a href="${item.url}">${item.label}</a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </ol>
</nav>
