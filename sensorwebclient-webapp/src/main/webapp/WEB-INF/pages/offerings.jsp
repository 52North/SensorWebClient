<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<h3>Offerings: </h3>
	<ul>
	<c:forEach items="${offerings}" var="offering">
    	<li>${offering.id}</li>
	</c:forEach>
	</ul>
</body>
</html>