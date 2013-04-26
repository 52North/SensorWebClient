<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'station')}" />
</head>
<body>

	<c:if test="${not empty stationList}">
		<h3>Stations:</h3>
		<ul>
			<c:forEach items="${stationList}" var="station">
				<li><a href="${url}stations/${station.id}">${station.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty station}">
		<table>
			<tr>
				<td>ID</td>
				<td>${station.id}</td>
			</tr>
		</table>
		<a href="${url}stations">back</a>
	</c:if>
	
</body>
</html>