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
	.factory('SosServices',
		function($resource) {
			return $resource(base + 'services', {}, {
				query : {
					method : 'GET',
					isArray : true
				}
			});
		})
	.factory('SosService',
		function($resource) {
			return $resource(base + 'services/:serviceId', {}, {
				query : {
					method : 'GET',
					params : {
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('Offerings',
		function($resource) {
			return $resource(base + 'services/:serviceId/offerings', {}, {
				query : {
					method : 'GET',
					params : {
						serviceId : ':serviceId'
					},
					isArray : true
				}
			});
		})
	.factory('Offering',
		function($resource) {
			return $resource(base + 'services/:serviceId/offerings/:offeringId', {}, {
				query : {
					method : 'GET',
					params : {
						offeringId : ':offeringId',
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('Procedures',
		function($resource) {
			return $resource(base + 'services/:serviceId/procedures', {}, {
				query : {
					method : 'GET',
					isArray : true
				}
			});
		})
	.factory('Procedure',
		function($resource) {
			return $resource(base + 'services/:serviceId/procedures/:procedureId', {}, {
				query : {
					method : 'GET',
					params : {
						procedureId : ':procedureId',
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('Features',
		function($resource) {
			return $resource(base + 'services/:serviceId/features', {}, {
				query : {
					method : 'GET',
					isArray : true
				}
			});
		})
	.factory('Feature',
		function($resource) {
			return $resource(base + 'services/:serviceId/features/:featureId', {}, {
				query : {
					method : 'GET',
					params : {
						featureId : ':featureId',
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('Phenomenons',
		function($resource) {
			return $resource(base + 'services/:serviceId/phenomenons', {}, {
				query : {
					method : 'GET',
					isArray : true
				}
			});
		})
	.factory('Phenomenon',
		function($resource) {
			return $resource(base + 'services/:serviceId/phenomenons/:phenomenonId', {}, {
				query : {
					method : 'GET',
					params : {
						phenomenonId : ':phenomenonId',
						serviceId : ':serviceId'
					},
					isArray : false
				}
			});
		})
	.factory('Stations',
		function($resource) {
			return $resource(base + 'services/:serviceId/stations', {}, {
				query : {
					method : 'GET',
					isArray : true
				}
			});
		})
	.factory('Station',
		function($resource) {
			return $resource(base + 'services/:serviceId/stations/:stationId', {}, {
				query : {
					method : 'GET',
					params : {
						serviceId : ':serviceId',
						stationId : ':stationId'
					},
					isArray : false
				}
			});
		})
	.factory('Timeseries',
		function($resource) {
			return $resource(base + 'timeseries/:timeseriesId.png', {}, {
				query : {
					method : 'GET',
					params : {
						timeseriesId : ':timeseriesId',
					},
					isArray : false
				}
			});
		});
