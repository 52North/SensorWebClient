<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'procedure')}" />
</head>
<body>

	<c:if test="${not empty procedureList}">
		<h3>Procedure:</h3>
		<ul>
			<c:forEach items="${procedureList}" var="procedure">
				<li><a href="${url}procedures/${procedure.id}">${procedure.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty procedure}">
		<table>
			<tr>
				<td>ID</td>
				<td>${procedure.id}</td>
			</tr>
		</table>
		<a href="${url}procedures">back</a>
	</c:if>
	
</body>
</html>