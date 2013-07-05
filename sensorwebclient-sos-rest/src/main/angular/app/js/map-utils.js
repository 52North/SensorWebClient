
angular.module('myApp.leaflet',[]).directive('leaflet', function() {
    return {
        restrict: 'E',
        replace: true,
        template: '<div></div>',
        link: function(scope, element, attrs) {

        	var map = {};
        	var leafletKey = 'http://{s}.tile.cloudmade.com/fd3f159c3654442a8e7ff82bddc00b29/997/256/{z}/{x}/{y}.png';
        	var mapAttribution = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, '+
        						 '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
        						 'Imagery © <a href="http://cloudmade.com">CloudMade</a>';
        	
        	map = L.map(attrs.id);
        	L.tileLayer(leafletKey, {
                attribution : mapAttribution,
                maxZoom : 18,
            }).addTo(map);
        	
        	
//    		L.geoJson(scope.station, {
//    			onEachFeature : function(feature, layer) {
//                    var content = "";
//                    if (feature.properties) {
//                        var stationName = feature.properties['station'];
//                        content = content + "<b>Station:</b> " + stationName + "<br/>";
//                        var tsProperties = feature.properties['timeseries'];
//                        if (tsProperties) {
//                            if (station.length !== 0) {
//                                parameters = tsProperties[0];
//                                content = content + "<b>Offering:</b> " + parameters.offering + "<br/>";
//                                content = content + "<b>Proceudre:</b> " + parameters.procedure + "<br/>";
//                                content = content + "<b>Phenomenon:</b> " + parameters.phenomenon + "<br/>";
//                                content = content + "<b>Feature:</b> " + parameters.feature;
//                            } else {
//                                var timeseries = "";
//                                for (var i = 0; i < tsProperties.length ; i++) {
//                                    timeseries = timeseries + tsProperties[i] + "<br/>";
//                                }
//                                content = content + "<b>timeseries:</b> " + timeseries;
//                            }
//                        }
//                        layer.bindPopup("<html><body>" + content + "</body></html>");
//                    }
//                }
//            });
//            if (clustered) {
//                var clusteredLayer = new L.MarkerClusterGroup();
//                clusteredLayer.addLayer(stations);
//                map.addLayer(clusteredLayer);
//                map.fitBounds(clusteredLayer.getBounds());
//            } else {
//                map.addLayer(stations);
//                map.fitBounds(stations.getBounds());
//            }
        }
    };
});


function initializeMap(mapId,stations) {

    /*
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
    */
};