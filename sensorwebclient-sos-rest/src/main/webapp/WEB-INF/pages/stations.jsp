<!DOCTYPE html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:set var="url" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'station')}" />
</head>
<body>

	<c:if test="${not empty station}">

		<!-- A STATION INDIVIDUUM -->

        <section>
			<a href="${url}stations/${station.properties.station}.json">as Json</a>
			<a href="${url}stations">back</a>
		</section>

        
		<h3>Station</h3>

		<table>
			<thead>
				<tr>
					<th>ID</th>
				</tr>
			</thead>
			<tr>
				<td>${station.properties.station}</td>
			</tr>
		</table>

		<h4>Timeseries</h4>

		<table>
			<thead>
				<tr>
                    <th>Timeseries ID</th>
                    <th>Offering</th>
                    <th>Feature</th>
                    <th>Procedure</th>
                    <th>Phenomenon</th>
                    <th>Diagram</th>
				</tr>
			</thead>
			<c:forEach var="timeseries" items="${station.properties.timeseries}">
				<tr>
                    <td>${timeseries.timeseriesId}</td>
					<td>${timeseries.offering}</td>
                    <td>${timeseries.feature}</td>
                    <td>${timeseries.procedure}</td>
                    <td>${timeseries.phenomenon}</td>
                    <td><a href="${url}image?offering=${timeseries.offering}&feature=${timeseries.feature}&procedure=${timeseries.procedure}&phenomenon=${timeseries.phenomenon}">diagram</a></td>
				</tr>
			</c:forEach>
			</tr>
		</table>
	</c:if>

	<c:if test="${not empty stations}">

		<!-- ALL STATIONS IN A SINGLE LIST -->

		<a href="${url}stations.json">as Json</a>
		<a href="${url}stations?offset=0">Paging</a>
        <a href="${url}">back</a>

		<h3>Stations</h3>
		<ul>
			<c:forEach var="station" items="${stations}">
				<li><a href="${url}stations/${station.properties.station}">${station.properties.station}</a></li>
			</c:forEach>
		</ul>
	</c:if>

	<c:if test="${not empty resultPage}">

		<!-- A PAGED STATION LIST -->

		<a href="${url}stations.json">as Json</a>
		<a href="${url}stations">All</a>
        <a href="${url}">back</a>
        
		<h3>Stations ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of
			${resultPage.total}</h3>
		<ul>
			<c:forEach var="station" items="${resultPage.results}">
				<li><a href="${url}stations/${station.properties.station}">${station.properties.station}</a></li>
			</c:forEach>
		</ul>

		<c:choose>
			<c:when test="${(resultPage.offset - 10) ge 0}">
				<a href="${url}stations?offset=${resultPage.offset - 10}">previous 10</a>
			</c:when>
			<c:otherwise>
				<a href="${url}stations?offset=0">previous 10</a>
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test="${resultPage.offset + 10 le resultPage.total}">
				<a href="${url}stations?offset=${resultPage.offset + 10}">next 10</a>
			</c:when>
			<c:otherwise>
				<a href="${url}stations?offset=${resultPage.offset}">next 10</a>
			</c:otherwise>
		</c:choose>
	</c:if>


    <script src="http://cdn.leafletjs.com/leaflet-0.5/leaflet.js"></script>
</body>
</html>