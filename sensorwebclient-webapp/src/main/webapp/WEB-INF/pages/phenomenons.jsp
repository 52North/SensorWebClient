<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'phenomenon')}" />
</head>
<body>

	<c:if test="${not empty phenomenonList}">
		<h3>Phenomenons:</h3>
		<ul>
			<c:forEach items="${phenomenonList}" var="phenomenon">
				<li><a href="${url}phenomenons/${phenomenon.id}">${phenomenon.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty phenomenon}">
		<table>
			<tr>
				<td>ID</td>
				<td>${phenomenon.id}</td>
			</tr>
		</table>
		<a href="${url}phenomenons">back</a>
	</c:if>
	
</body>
</html>