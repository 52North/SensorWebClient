<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'offering')}" />
</head>
<body>
<<<<<<< HEAD

	<c:if test="${not empty offeringList}">
		<h3>Offerings:</h3>
		<ul>
			<c:forEach items="${offeringList}" var="offering">
				<li><a href="${url}offerings/${offering.id}">${offering.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty offering}">
		<table>
			<tr>
				<td>ID</td>
				<td>${offering.id}</td>
			</tr>
		</table>
		<a href="${url}offerings">back</a>
	</c:if>
	
=======
	<c:choose>
		<c:when test="${fn:length(offerings) gt 1}">
			<h3>Offerings:</h3>
			<ul>
				<c:forEach items="${offerings}" var="offering">
					<li><a href="${url}offerings/${offering.id}">${offering.id}</a></li>
				</c:forEach>
			</ul>
			<a href="${url}">back</a>
		</c:when>
		<c:otherwise>
			<table>
				<tr>
					<td>ID:</td>
					<td>${offerings[0].id}</td>
				</tr>
			</table>
			<a href="${url}offerings">back</a>
		</c:otherwise>
	</c:choose>
>>>>>>> FETCH_HEAD
</body>
</html>