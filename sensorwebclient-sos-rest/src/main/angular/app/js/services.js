'use strict';

/* Services */

var base = '../api/v0/';

angular.module('myApp.services', [ 'ngResource' ])
	.service('Utils',
		function _construct() {
			this.doubleEncode = function(toEncode) {
				return encodeURIComponent(encodeURIComponent(toEncode));
			};
		})
	.factory('SosInstanceService',
		function($resource) {
			return $resource(base + 'services/:serviceId', {}, {
				getServiceInstances : { method : 'GET', isArray : true },
				getServiceInstance : { method : 'GET', 
					params : {
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('OfferingService',
		function($resource) {
			return $resource(base + 'services/:serviceId/offerings/:offeringId', {}, {
				getOfferings : {
					method : 'GET',
					params : {
						serviceId : ':serviceId'
					},
					isArray : true
				},
				getOffering : {
					method : 'GET',
					params : {
						offeringId : ':offeringId',
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('ProcedureService',
		function($resource) {
			return $resource(base + 'services/:serviceId/procedures/:procedureId', {}, {
				getProcedures : {
					method : 'GET',
					params : {
						serviceId : ':serviceId'
					},
					isArray : true
				},
				getProcedure : {
					method : 'GET',
					params : {
						procedureId : ':procedureId',
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('FeatureService',
		function($resource) {
			return $resource(base + 'services/:serviceId/features/:featureId', {}, {
				getFeatures : {
					method : 'GET',
					params : {
						serviceId : ':serviceId'
					},
					isArray : true
				},
				getFeature : {
					method : 'GET',
					params : {
						featureId : ':featureId',
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('PhenomenonService',
		function($resource) {
			return $resource(base + 'services/:serviceId/phenomenons/:phenomenonId', {}, {
				getPhenomenons : {
					method : 'GET',
					params : {
						serviceId : ':serviceId'
					},
					isArray : true
				},
				getPhenomenon : {
					method : 'GET',
					params : {
						phenomenonId : ':phenomenonId',
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('StationService',
		function($resource) {
			return $resource(base + 'services/:serviceId/stations/:stationId', {}, {
				getStations : {
					method : 'GET',
					params : {
						serviceId : ':serviceId'
					},
					isArray : true
				},
				getStation : {
					method : 'GET',
					params : {
						serviceId : ':serviceId',
						stationId : ':stationId'
					},
					isArray : false
				}
			});
		})
	.factory('ImageService',
		function($resource) {
			return $resource(':baseUrl/timeseries/:timeseriesId.png', {}, {
				getTimeseries : {
					method : 'GET',
					params : {
						baseUrl: ':baseUrl',
						timeseriesId : ':timeseriesId'
					},
					isArray : false
				}
			});
		});
