<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'stations')}" />
</head>
<body>
	<h3>Stations:</h3>
	<ul>
	<c:forEach items="${stations}" var="station">
    	<li>${station.id}</li>
	</c:forEach>
	</ul>
	<a href="${url}">back</a>
</body>
</html>