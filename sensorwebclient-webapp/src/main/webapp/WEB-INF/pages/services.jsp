<html>
<head>
<%@ page isELIgnored="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<h3>Services: </h3>
	<ul>
	<%
		for(int i = 0; i < "<%=${services}.size()%>" ; i++) {
    		<li>"<%=${services}.item(i).getId()%>"<br>${service.url}</li>
		}
	%>
	</ul>
</body>
</html>