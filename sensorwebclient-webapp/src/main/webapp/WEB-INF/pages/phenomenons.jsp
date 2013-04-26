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
		<a href="${url}phenomenons.json">as Json</a>
		<a href="${url}phenomenons?offset=0">Paging</a>
	
		<h3>Phenomenons:</h3>
		<ul>
			<c:forEach items="${phenomenonList}" var="phenomenon">
				<li><a href="${url}phenomenons/${phenomenon.id}">${phenomenon.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty phenomenon}">
		<a href="${url}phenomenons/${phenomenon.id}.json">as Json</a>
		<table>
			<tr>
				<td>ID</td>
				<td>${phenomenon.id}</td>
			</tr>
		</table>
		<a href="${url}phenomenons">back</a>
	</c:if>
	
	<c:if test="${not empty resultPage}">
		<a href="${url}phenomenons.json">as Json</a>
		<a href="${url}phenomenons">All</a>
		<h3>Phenomenons ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of ${resultPage.total}</h3>
		<ul>
			<c:forEach items="${resultPage.results}" var="phenomenon">
				<li><a href="${url}phenomenons/${phenomenon.id}">${phenomenon.id}</a></li>
			</c:forEach>
		</ul>
		
		<c:choose>
			<c:when test="${(resultPage.offset - 10) ge 0}">
				<a href="${url}phenomenons?offset=${resultPage.offset - 10}">previous 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}phenomenons?offset=0">previous 10</a>
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${resultPage.offset + 10 le resultPage.total}">
				<a href="${url}phenomenons?offset=${resultPage.offset + 10}">next 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}phenomenons?offset=${resultPage.offset}">next 10</a>
			</c:otherwise>
		</c:choose>
		  
		<br>
		
		<a href="${url}">back</a>
	</c:if>
	
</body>
</html>