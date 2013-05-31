<!DOCTYPE html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<c:set var="url" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'/station')}" />
<c:set var="base" value="${fn:substringBefore(requestScope['javax.servlet.forward.request_uri'],'/rest/')}" />

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link href="${base}/css/bootstrap/bootstrap.css" rel="stylesheet" media="screen">
<link href="${base}/css/bootstrap/bootstrap-responsive.css" rel="stylesheet">
<link href="http://cdn.leafletjs.com/leaflet-0.5/leaflet.css" rel="stylesheet" />
<link rel="stylesheet" href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.css" />
<link rel="stylesheet" href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.Default.css" />
<!--[if lte IE 8]>
     <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5/leaflet.ie.css" />
     <link rel="stylesheet" href="${base}/css/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/MarkerCluster.Default.ie.css" />
<![endif]-->
<link rel="stylesheet" href="${base}/css/jsp-styles.css" />

<script type="text/javascript">
    var map = {};
    var leafletKey = 'http://{s}.tile.cloudmade.com/fd3f159c3654442a8e7ff82bddc00b29/997/256/{z}/{x}/{y}.png';
    var mapAttribution = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>';

    function initMap(station, clustered) {
        station = typeof station === 'undefined' ? '' : station;
        clustered = typeof clustered === 'undefined' ? true : clustered;
        
        map = L.map('map');
        L.tileLayer(leafletKey, {
            attribution : mapAttribution,
            maxZoom : 18,
        }).addTo(map);

        var stations = jQuery.ajax({
            type : "GET",
            dataType : "json",
            url : "${url}/stations/" + station
        }).done(function(stations) {
            var stations = L.geoJson(stations, {
                onEachFeature : function(feature, layer) {
                    var content = "";
                    if (feature.properties) {
                        var stationName = feature.properties['station'];
                        content = content + "<b>Station:</b> " + stationName + "<br/>";
                        var tsProperties = feature.properties['timeseries'];
                        if (tsProperties) {
                            if (station.length !== 0) {
                                parameters = tsProperties[0];
                                content = content + "<b>Offering:</b> " + parameters.offering + "<br/>";
                                content = content + "<b>Proceudre:</b> " + parameters.procedure + "<br/>";
                                content = content + "<b>Phenomenon:</b> " + parameters.phenomenon + "<br/>";
                                content = content + "<b>Feature:</b> " + parameters.feature;
                            } else {
                                var timeseries = "";
                                for (var i = 0; i < tsProperties.length ; i++) {
                                    timeseries = timeseries + tsProperties[i] + "<br/>";
                                }
                                content = content + "<b>timeseries:</b> " + timeseries;
                            }
                        }
                        layer.bindPopup("<html><body>" + content + "</body></html>");
                    }
                }
            });
            if (clustered) {
                var clusteredLayer = new L.MarkerClusterGroup();
                clusteredLayer.addLayer(stations);
                map.addLayer(clusteredLayer);
                map.fitBounds(clusteredLayer.getBounds());
            } else {
                map.addLayer(stations);
                map.fitBounds(stations.getBounds());
            }
        }).fail(function(xhr) {
            console.log(xhr);
        });
    };
</script>
</head>


<body onload="initMap('${station.properties.station}')">
    <div class="span12">

    <c:if test="${not empty station}">
    
            <!-- A STATION INDIVIDUUM -->

            <div class="masthead">
                <ul class="nav nav-pills pull-right">
                    <li><a href="${url}/stations/${station.properties.station}.json">As Json</a></li>
                    <li><a href="${url}/stations">back</a></li>
                </ul>
                <h3 class="muted">Station</h3>
            </div>

            <section>
                <div id="map" class="span4" style="height: 250px"></div>
                <div class="span7 pull-right">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                            </tr>
                        </thead>
                        <tr>
                            <td>${station.properties.station}</td>
                        </tr>
                    </table>
                </div>
            </section>
            <section class="span12">
            
                <h4>Timeseries</h4>
    
                <table class="table table-condensed">
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
                            <td><a
                                href="${url}/timeseries/${timeseries.timeseriesId}.png">diagram</a></td>
                        </tr>
                    </c:forEach>
                    </tr>
                </table>
            </section>
        </c:if>

        <c:if test="${not empty stations}">

            <!-- ALL STATIONS IN A SINGLE LIST -->

            <div class="masthead">
                <ul class="nav nav-pills pull-right">
                    <li><a href="${url}/stations.json">As Json</a></li>
                    <li><a href="${url}/stations?offset=0">Paging</a></li>
                    <li><a href="${url}">back</a></li>
                </ul>
                <h3 class="muted">Stations</h3>
            </div>

            <div class="span5 pull-left parameterList">
                 <ul>
                    <c:forEach var="station" items="${stations}">
                        <li><a href="${url}/stations/${station.properties.station}">${station.properties.station}</a></li>
                    </c:forEach>
                </ul>
            </div>
            <div id="map" class="span6 pull-right"></div>
        </c:if>

        <c:if test="${not empty resultPage}">

            <!-- A PAGED STATION LIST -->
            
            <div class="masthead">
                <ul class="nav nav-pills pull-right">
                    <li><a href="${url}/stations.json">As Json</a></li>
                    <li><a href="${url}/stations">All</a></li>
                    <li><a href="${url}">back</a></li>
                </ul>
                <h3 class="muted">Stations ${resultPage.offset + 1} - ${resultPage.offset + fn:length(resultPage.results)}
                    of ${resultPage.total}</h3>
            </div>

            <ul>
                <c:forEach var="station" items="${resultPage.results}">
                    <li><a href="${url}/stations/${station.properties.station}">${station.properties.station}</a></li>
                </c:forEach>
            </ul>

            <c:choose>
                <c:when test="${(resultPage.offset - 10) ge 0}">
                    <a href="${url}/stations?offset=${resultPage.offset - 10}">previous 10</a>
                </c:when>
                <c:otherwise>
                    <a href="${url}/stations?offset=0">previous 10</a>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${resultPage.offset + 10 le resultPage.total}">
                    <a href="${url}/stations?offset=${resultPage.offset + 10}">next 10</a>
                </c:when>
                <c:otherwise>
                    <a href="${url}/stations?offset=${resultPage.offset}">next 10</a>
                </c:otherwise>
            </c:choose>
        </c:if>

    </div class="container-narrow">
    
    <script src="http://code.jquery.com/jquery.js"></script>
    <script src="${base}/js/bootstrap/v2.3.2/bootstrap.min.js"></script>
    <script src="http://cdn.leafletjs.com/leaflet-0.5/leaflet.js"></script>
    <script src="${base}/js/Leaflet.markercluster/6fda9a206f47f446bd42a931caa4a68aaca511d9/leaflet.markercluster-src.js"></script>
</body>
</html>