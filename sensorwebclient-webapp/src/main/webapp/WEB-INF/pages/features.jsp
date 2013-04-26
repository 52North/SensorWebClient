<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'feature')}" />
</head>
<body>
	${featureList}
	<c:if test="${not empty featureList}">
		<h3>Features:</h3>
		<ul>
			<c:forEach items="${featureList}" var="feature">
				<li><a href="${url}features/${feature.id}">${feature.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty feature}">
		<table>
			<tr>
				<td>ID</td>
				<td>${feature.id}</td>
			</tr>
		</table>
		<a href="${url}features">back</a>
	</c:if>
	
</body>
</html>