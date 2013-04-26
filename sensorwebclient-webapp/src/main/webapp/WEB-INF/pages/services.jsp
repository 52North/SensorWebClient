<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<h3>Services: </h3>
	<ul>
	<c:forEach items="${serviceInstances}" var="service">
        <li>${service.itemName}</li>
    </c:forEach>
	</ul>
</body>
</html>