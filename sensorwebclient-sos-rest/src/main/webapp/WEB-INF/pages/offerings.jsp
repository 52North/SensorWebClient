<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'offering')}" />
</head>
<body>
	
    <c:if test="${not empty offering}">
    
        <!-- AN OFFERING INDIVIDUUM -->
    
        <a href="${url}offerings/${offering.id}.json">as Json</a>
        <a href="${url}offerings">back</a>
        
        <h3>Offering</h3>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                </tr>
            </thead>
            <tr>
                <td>${offering.id}</td>
            </tr>
        </table>
    </c:if>
    
	<c:if test="${not empty offerings}">
	
	   <!-- ALL OFFERINGS IN A SINGLE LIST -->
	
		<a href="${url}offerings.json">as Json</a>
		<a href="${url}offerings?offset=0">Paging</a>
        <a href="${url}">back</a>
        
		<h3>Offerings</h3>
		<ul>
			<c:forEach var="offering" items="${offerings}" >
				<li><a href="${url}offerings/${offering.id}">${offering.id}</a></li>
			</c:forEach>
		</ul>
	</c:if>

	<c:if test="${not empty resultPage}">
	
	   <!-- A PAGED OFFERING LIST -->
	
		<a href="${url}offerings.json">as Json</a>
		<a href="${url}offerings">All</a>
        <a href="${url}">back</a>
        
		<h3>Offerings ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of ${resultPage.total}</h3>
		<ul>
			<c:forEach var="offering" items="${resultPage.results}" >
				<li><a href="${url}offerings/${offering.id}">${offering.id}</a></li>
			</c:forEach>
		</ul>
		
		<c:choose>
			<c:when test="${(resultPage.offset - 10) ge 0}">
				<a href="${url}offerings?offset=${resultPage.offset - 10}">previous 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}offerings?offset=0">previous 10</a>
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${resultPage.offset + 10 le resultPage.total}">
				<a href="${url}offerings?offset=${resultPage.offset + 10}">next 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}offerings?offset=${resultPage.offset}">next 10</a>
			</c:otherwise>
		</c:choose>
		
	</c:if>
</body>
</html>