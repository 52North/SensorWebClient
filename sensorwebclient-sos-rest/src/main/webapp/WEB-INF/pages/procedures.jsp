<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="url" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'/procedure')}" />
<c:set var="base" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'/api/v0/')}" />
<!DOCTYPE html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" href="${base}/css/bootstrap/bootstrap.css" media="screen">
<link rel="stylesheet" href="${base}/css/bootstrap/bootstrap-responsive.css">

<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5/leaflet.css">
<link rel="stylesheet" href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.css">
<link rel="stylesheet" href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.Default.css">
<!--[if lte IE 8]>
     <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5/leaflet.ie.css">
     <link rel="stylesheet" href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.Default.ie.css">
<![endif]-->

<link rel="stylesheet" href="${base}/css/jsp-styles.css" />

</head>
<body>

	<div class="span12">

		<c:if test="${not empty procedure}">

			<!-- A PROCEDURE INDIVIDUUM -->

			<div class="masthead">
				<ul class="nav nav-pills pull-right">
					<li><a href="${url}/procedures/${pprocedure.id}.json">As Json</a></li>
					<li><a href="${url}/procedures">back</a></li>
				</ul>
				<h3 class="muted">Procedures</h3>
			</div>

			<table class="table">
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
				<table class="table">
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

			<div class="masthead">
				<ul class="nav nav-pills pull-right">
					<li><a href="${url}/procedures.json">As Json</a></li>
					<li><a href="${url}/procedures?offset=0">Paging</a></li>
					<li><a href="${url}">back</a></li>
				</ul>
				<h3 class="muted">Procedures</h3>
			</div>

			<div class="span5 pull-left parameterList">
				<ul>
					<c:forEach var="procedure" items="${procedures}">
						<li><a href="${url}/procedures/${procedure.id}">${procedure.id}</a></li>
					</c:forEach>
				</ul>
			</div>
		</c:if>

		<c:if test="${not empty resultPage}">

			<!-- A PAGED PROCEDURE LIST -->

			<div class="masthead">
				<ul class="nav nav-pills pull-right">
					<li><a href="${url}/procedures.json">As Json</a></li>
					<li><a href="${url}/procedures">All</a></li>
					<li><a href="${url}">back</a></li>
				</ul>
				<h3 class="muted">Procedures ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)}
					of ${resultPage.total}</h3>
			</div>
			
			<ul>
				<c:forEach var="procedure" items="${resultPage.results}">
					<li><a href="${url}/procedures/${procedure.id}">${procedure.id}</a></li>
				</c:forEach>
			</ul>

			<c:choose>
				<c:when test="${(resultPage.offset - 10) ge 0}">
					<a href="${url}/procedures?offset=${resultPage.offset - 10}">previous 10</a>
				</c:when>
				<c:otherwise>
					<a href="${url}/procedures?offset=0">previous 10</a>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="${resultPage.offset + 10 le resultPage.total}">
					<a href="${url}/procedures?offset=${resultPage.offset + 10}">next 10</a>
				</c:when>
				<c:otherwise>
					<a href="${url}/procedures?offset=${resultPage.offset}">next 10</a>
				</c:otherwise>
			</c:choose>

		</c:if>
	</div>
</body>
</html>