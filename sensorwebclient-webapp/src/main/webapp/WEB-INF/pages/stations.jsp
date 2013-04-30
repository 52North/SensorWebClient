<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:set var="url"
	value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'station')}" />
</head>
<body>

	<c:if test="${not empty stationList}">
		<a href="${url}stations.json">as Json</a>
		<a href="${url}stations?offset=0">Paging</a>

		<h3>Stations:</h3>
		<ul>
			<c:forEach items="${stationList}" var="station">
				<li><a href="${url}stations/${station.id}">${station.id}</a></li>
			</c:forEach>
		</ul>
		<a href="${url}">back</a>
	</c:if>

	<c:if test="${not empty station}">
		<a href="${url}stations/${station.id}.json">as Json</a>
		<table>
			<tr>
				<td>ID</td>
				<td>${station.id}</td>
			</tr>
			<tr>
				<td>Constellations</td>
				<c:forEach items="${station.parameterConstellations}"
					var="paramConst">
					<tr>
						<td></td>
						<td>Offering:</td>
						<td>${paramConst.offering}</td>
					</tr>
					<tr>
						<td></td>
						<td>Feature:</td>
						<td>${paramConst.featureOfInterest}</td>
					</tr>
					<tr>
						<td></td>
						<td>Procedure:</td>
						<td>${paramConst.procedure}</td>
					</tr>
					<tr>
						<td></td>
						<td>Phenomenon:</td>
						<td>${paramConst.phenomenon}</td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td><a
							href="${url}image?offering=${paramConst.offering}&feature=${paramConst.featureOfInterest}&procedure=${paramConst.procedure}&phenomenon=${paramConst.phenomenon}">diagram</a></td>
					</tr>
				</c:forEach>
			</tr>
		</table>
		<a href="${url}stations">back</a>
	</c:if>

	<c:if test="${not empty resultPage}">
		<a href="${url}stations.json">as Json</a>
		<a href="${url}stations">All</a>
		<h3>Stations ${resultPage.offset + 1} - ${resultPage.offset +
			fn:length(resultPage.results)} of ${resultPage.total}</h3>
		<ul>
			<c:forEach items="${resultPage.results}" var="station">
				<li><a href="${url}stations/${station.id}">${station.id}</a></li>
			</c:forEach>
		</ul>

		<c:choose>
			<c:when test="${(resultPage.offset - 10) ge 0}">
				<a href="${url}stations?offset=${resultPage.offset - 10}">previous
					10</a>
			</c:when>
			<c:otherwise>
				<a href="${url}stations?offset=0">previous 10</a>
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test="${resultPage.offset + 10 le resultPage.total}">
				<a href="${url}stations?offset=${resultPage.offset + 10}">next
					10</a>
			</c:when>
			<c:otherwise>
				<a href="${url}stations?offset=${resultPage.offset}">next 10</a>
			</c:otherwise>
		</c:choose>

		<br>

		<a href="${url}">back</a>
	</c:if>

</body>
</html>