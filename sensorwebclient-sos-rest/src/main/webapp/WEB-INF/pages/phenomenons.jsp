<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<c:set var="url" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'/phenomenon')}" />
<c:set var="base" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'/rest/')}" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="${base}/css/bootstrap/bootstrap.css" rel="stylesheet" media="screen">
<link href="${base}/css/bootstrap/bootstrap-responsive.css" rel="stylesheet">
<link href="http://cdn.leafletjs.com/leaflet-0.5/leaflet.css" rel="stylesheet" />
<link rel="stylesheet"
	href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.css" />
<link rel="stylesheet"
	href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.Default.css" />
<!--[if lte IE 8]>
     <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5/leaflet.ie.css" />
     <link rel="stylesheet" href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.Default.ie.css" />
<![endif]-->
<link rel="stylesheet" href="${base}/css/jsp-styles.css" />

</head>
<body>

	<div class="span12">

		<c:if test="${not empty phenomenon}">

			<!-- A PHENOMENON INDIVIDUUM -->

			<div class="masthead">
				<ul class="nav nav-pills pull-right">
					<li><a href="${url}/phenomenons/${phenomenon.id}.json">As Json</a></li>
					<li><a href="${url}/phenomenons">back</a></li>
				</ul>
				<h3 class="muted">Phenomenon</h3>
			</div>

			<table class="table">
				<thead>
					<tr>
						<th>ID</th>
					</tr>
				</thead>
				<tr>
					<td>${phenomenon.id}</td>
				</tr>
			</table>
		</c:if>

		<c:if test="${not empty phenomenons}">

			<!-- ALL PHENOMENONS IN A SINGLE LIST -->

			<div class="masthead">
				<ul class="nav nav-pills pull-right">
					<li><a href="${url}/phenomenons.json">As Json</a></li>
					<li><a href="${url}/phenomenons?offset=0">Paging</a></li>
					<li><a href="${url}">back</a></li>
				</ul>
				<h3 class="muted">Phenomenons</h3>
			</div>

			<div class="span5 pull-left parameterList">
				<ul>
					<c:forEach var="phenomenon" items="${phenomenons}">
						<li><a href="${url}phenomenons/${phenomenon.id}">${phenomenon.id}</a></li>
					</c:forEach>
				</ul>
			</div>
		</c:if>

		<c:if test="${not empty resultPage}">

			<!-- A PAGED PHONENOMENON LIST -->

			<div class="masthead">
				<ul class="nav nav-pills pull-right">
					<li><a href="${url}/phenomenons.json">As Json</a></li>
					<li><a href="${url}/phenomenons">All</a></li>
					<li><a href="${url}">back</a></li>
				</ul>
				<h3 class="muted">Phenomenons ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)} of
					${resultPage.total}</h3>
			</div>
			
			<ul>
				<c:forEach var="phenomenon" items="${resultPage.results}">
					<li><a href="${url}/phenomenons/${phenomenon.id}">${phenomenon.id}</a></li>
				</c:forEach>
			</ul>

			<c:choose>
				<c:when test="${(resultPage.offset - 10) ge 0}">
					<a href="${url}/phenomenons?offset=${resultPage.offset - 10}">previous 10</a>
				</c:when>
				<c:otherwise>
					<a href="${url}/phenomenons?offset=0">previous 10</a>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="${resultPage.offset + 10 le resultPage.total}">
					<a href="${url}/phenomenons?offset=${resultPage.offset + 10}">next 10</a>
				</c:when>
				<c:otherwise>
					<a href="${url}/phenomenons?offset=${resultPage.offset}">next 10</a>
				</c:otherwise>
			</c:choose>

		</c:if>

	</div>
</body>
</html>