<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'procedure')}" />
</head>
<body>

	<c:if test="${not empty procedureList}">
		<a href="${url}procedures.json">as Json</a>
		<a href="${url}procedures?offset=0">Paging</a>
	
		<h3>Procedure:</h3>
		<ul>
			<c:forEach items="${procedureList}" var="procedure">
				<li><a href="${url}procedures/${procedure.id}">${procedure.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty procedure}">
		<a href="${url}procedures/${procedure.id}.json">as Json</a>
		<table>
			<tr>
				<td>ID</td>
				<td>${procedure.id}</td>
			</tr>
		</table>
		<a href="${url}procedures">back</a>
	</c:if>
	
	<c:if test="${not empty resultPage}">
		<a href="${url}procedures.json">as Json</a>
		<a href="${url}procedures">All</a>
		<h3>Procedures ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of ${resultPage.total}</h3>
		<ul>
			<c:forEach items="${resultPage.results}" var="procedure">
				<li><a href="${url}procedures/${procedure.id}">${procedure.id}</a></li>
			</c:forEach>
		</ul>
		
		<c:choose>
			<c:when test="${(resultPage.offset - 10) ge 0}">
				<a href="${url}procedures?offset=${resultPage.offset - 10}">previous 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}procedures?offset=0">previous 10</a>
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${resultPage.offset + 10 le resultPage.total}">
				<a href="${url}procedures?offset=${resultPage.offset + 10}">next 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}procedures?offset=${resultPage.offset}">next 10</a>
			</c:otherwise>
		</c:choose>
		  
		<br>
		
		<a href="${url}">back</a>
	</c:if>
	
</body>
</html>