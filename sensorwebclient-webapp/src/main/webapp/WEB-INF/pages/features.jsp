<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'feature')}" />
</head>
<body>
	
	<c:if test="${not empty featureOfInterestList}">
		<a href="${url}features.json">as Json</a>
		<a href="${url}features?offset=0">Paging</a>
		
		<h3>Features:</h3>
		<ul>
			<c:forEach items="${featureOfInterestList}" var="feature">
				<li><a href="${url}features/${feature.id}">${feature.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty featureOfInterest}">
		<a href="${url}features/${featureOfInterest.id}.json">as Json</a>
		<table>
			<tr>
				<td>ID</td>
				<td>${featureOfInterest.id}</td>
			</tr>
		</table>
		<a href="${url}features">back</a>
	</c:if>

	<c:if test="${not empty resultPage}">
		<a href="${url}features.json">as Json</a>
		<a href="${url}features">All</a>
		<h3>Features ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of ${resultPage.total}</h3>
		<ul>
			<c:forEach items="${resultPage.results}" var="feature">
				<li><a href="${url}features/${feature.id}">${feature.id}</a></li>
			</c:forEach>
		</ul>
		
		<c:choose>
			<c:when test="${(resultPage.offset - 10) ge 0}">
				<a href="${url}features?offset=${resultPage.offset - 10}">previous 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}features?offset=0">previous 10</a>
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${resultPage.offset + 10 le resultPage.total}">
				<a href="${url}features?offset=${resultPage.offset + 10}">next 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}features?offset=${resultPage.offset}">next 10</a>
			</c:otherwise>
		</c:choose>
		  
		<br>
		
		<a href="${url}">back</a>
	</c:if>	
	
</body>
</html>