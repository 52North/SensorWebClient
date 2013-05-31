<!DOCTYPE html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'timeseries')}" />
</head>
<body>
	
    <c:if test="${not empty singleTimeseries}">
    
        <!-- A FEATURE INDIVIDUUM -->
    
        <a href="${url}timeseries/${singleTimeseries.id}.json">as Json</a>
        <a href="${url}timeseries">back</a>
        
        <h3>Feature</h3>
        
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                </tr>
            </thead>
            <tr>
                <td>${singleTimeseries.id}</td>
            </tr>
        </table>
    </c:if>
	
	<c:if test="${not empty multipleTimeseries}">
	
	   <!-- ALL FEATURES IN A SINGLE LIST -->
	
		<a href="${url}timeseries.json">as Json</a>
		<a href="${url}timeseries?offset=0">Paging</a>
        <a href="${url}">back</a>
		
		<h3>Timeseries</h3>
		<ul>
			<c:forEach var="timeseries" items="${multipleTimeseries}" >
				<li><a href="${url}timeseries/${timeseries.id}">${timeseries.id}</a></li>
			</c:forEach>
		</ul>
	</c:if>

	<c:if test="${not empty resultPage}">
	
	   <!-- A PAGED FEATURE LIST -->
	
		<a href="${url}timeseries.json">as Json</a>
		<a href="${url}timeseries">All</a>
		<a href="${url}">back</a>
		
		<h3>Features ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of ${resultPage.total}</h3>
		<ul>
			<c:forEach var="timeseries" items="${resultPage.results}" >
				<li><a href="${url}timeseries/${timeseries.id}">${timeseries.id}</a></li>
			</c:forEach>
		</ul>
		
		<c:choose>
			<c:when test="${(resultPage.offset - 10) ge 0}">
				<a href="${url}timeseries?offset=${resultPage.offset - 10}">previous 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}timeseries?offset=0">previous 10</a>
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test="${resultPage.offset + 10 le resultPage.total}">
				<a href="${url}timeseries?offset=${resultPage.offset + 10}">next 10</a>	
			</c:when>
			<c:otherwise>
				<a href="${url}timeseries?offset=${resultPage.offset}">next 10</a>
			</c:otherwise>
		</c:choose>
		
	</c:if>	
	
</body>
</html>