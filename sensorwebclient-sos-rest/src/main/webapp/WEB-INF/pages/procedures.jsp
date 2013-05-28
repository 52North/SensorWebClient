<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="url" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'procedure')}" />
</head>
<body>

	<c:if test="${not empty procedure}">

		<!-- A PROCEDURE INDIVIDUUM -->

		<a href="${url}procedures/${procedure.id}.json">as Json</a>
		<a href="${url}procedures">back</a>

		<h3>Procedure</h3>

		<table>
			<thead>
				<tr>
					<th>ID</th>
				</tr>
			</thead>
			<tr>
				<td>${procedure.id}</td>
			</tr>
		</table>

		<h4>Reference Values</h4>
		<c:if test="${not empty procedure.referenceValues}">
			<table>
				<thead>
					<tr>
						<th>Label</th>
						<th>Value</th>
					</tr>
				</thead>
				<c:forEach var="referenceValue" items="${procedure.referenceValues}">
					<tr>
						<td>${referenceValue.key}</td>
						<td>${referenceValue.value}</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</c:if>

	<c:if test="${not empty procedures}">

		<!-- ALL PROCEDURES IN A SINGLE LIST -->

		<a href="${url}procedures.json">as Json</a>
		<a href="${url}procedures?offset=0">Paging</a>
		<a href="${url}">back</a>

		<h3>Procedure</h3>
		<ul>
			<c:forEach var="procedure" items="${procedures}">
				<li><a href="${url}procedures/${procedure.id}">${procedure.id}</a></li>
			</c:forEach>
		</ul>
	</c:if>

	<c:if test="${not empty resultPage}">

		<!-- A PAGED PROCEDURE LIST -->

		<a href="${url}procedures.json">as Json</a>
		<a href="${url}procedures">All</a>
		<a href="${url}">back</a>

		<h3>Procedures ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of
			${resultPage.total}</h3>
		<ul>
			<c:forEach var="procedure" items="${resultPage.results}">
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

	</c:if>

</body>
</html>