<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<c:set var="url" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'/service')}" />
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

</head>
<body>

    <div class="span12">

        <c:if test="${not empty service}">

            <!-- A SERVICE INDIVIDUUM -->
            
            <div class="masthead">
                <ul class="nav nav-pills pull-right">
                    <li><a href="${url}/services/${service.itemName}.json">As Json</a></li>
                    <li><a href="${url}/services">back</a></li>
                </ul>
                <h3 class="muted">Service</h3>
            </div>

            <table class="table">
                <thead>
                    <tr>
                        <th>Configured Name</th>
                        <th>Service Title</th>
                        <th>Service Url</th>
                        <th>Service Type</th>
                        <th>Service Version</th>
                    </tr>
                </thead>
                <tr>
                    <td>${service.itemName}</td>
                    <td>${service.title}</td>
                    <td><a href="${service.url}">${service.url}</a></td>
                    <td>${service.type}</td>
                    <td>${service.version}</td>
                </tr>
            </table>

            <h4>Available Parameters</h4>

            <ul>
                <li><a href="${url}/services/${service.itemName}/offerings">offerings</a></li>
                <li><a href="${url}/services/${service.itemName}/features">features</a></li>
                <li><a href="${url}/services/${service.itemName}/procedures">procedures</a></li>
                <li><a href="${url}/services/${service.itemName}/phenomenons">phenomenons</a></li>
                <li><a href="${url}/services/${service.itemName}/stations">stations</a></li>
            </ul>
        </c:if>

        <c:if test="${not empty services}">

            <!-- ALL SERVICES IN A SINGLE LIST -->

            <div class="masthead">
                <ul class="nav nav-pills pull-right">
                    <li><a href="${url}/services.json">As Json</a></li>
                    <li><a href="${url}/services">back</a></li>
                </ul>
                <h3 class="muted">Services</h3>
            </div>

            <ul>
                <c:forEach var="service" items="${services}">
                    <li><a href="${url}/services/${service.itemName}">${service.itemName}</a></li>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</body>
</html>