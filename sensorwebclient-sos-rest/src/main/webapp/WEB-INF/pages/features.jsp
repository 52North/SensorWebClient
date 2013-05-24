<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'feature')}" />
</head>
<body>
	
    <c:if test="${not empty feature}">
    
        <!-- A FEATURE INDIVIDUUM -->
    
        <a href="${url}features/${feature.id}.json">as Json</a>
        <a href="${url}features">back</a>
        
        <h3>Feature</h3>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                </tr>
            </thead>
            <tr>
                <td>${feature.id}</td>
            </tr>
        </table>
    </c:if>
	
	<c:if test="${not empty features}">
	
	   <!-- ALL FEATURES IN A SINGLE LIST -->
	
		<a href="${url}features.json">as Json</a>
		<a href="${url}features?offset=0">Paging</a>
        <a href="${url}">back</a>
		
		<h3>Features</h3>
		<ul>
			<c:forEach var="feature" items="${features}" >
				<li><a href="${url}features/${feature.id}">${feature.id}</a></li>
			</c:forEach>
		</ul>
	</c:if>

	<c:if test="${not empty resultPage}">
	
	   <!-- A PAGED FEATURE LIST -->
	
		<a href="${url}features.json">as Json</a>
		<a href="${url}features">All</a>
		<a href="${url}">back</a>
		
		<h3>Features ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of ${resultPage.total}</h3>
		<ul>
			<c:forEach var="feature" items="${resultPage.results}" >
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
		
	</c:if>	
	
</body>
</html>