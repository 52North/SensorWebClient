<html>
<head>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<h3>Features: </h3>
	<ul>
	<c:forEach items="${features}" var="feature">
    	<li>${feature.id}</li>
	</c:forEach>
	</ul>
</body>
</html>