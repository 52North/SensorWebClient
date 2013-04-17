<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<h3>Stations: </h3>
	<ul>
	<c:forEach items="${stations}" var="station">
    	<li>${station.id}</li>
	</c:forEach>
	</ul>
</body>
</html>