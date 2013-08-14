'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', ['myApp.services', 'myApp.directives', 'myApp.controllers']).
  config(['$routeProvider', function($routeProvider) {
	
	// 
	// service instance routes
	$routeProvider.when('/', {
			templateUrl: 'partials/service-list.html', 
			controller: 'ServiceListCtrl'
		});
	$routeProvider.when('/:serviceId', {
			templateUrl: 'partials/service-detail.html', 
			controller: 'ServiceDetailCtrl'
		});
	
	//
	// offering routes
	$routeProvider.when('/:serviceId/offerings', {
			templateUrl: 'partials/offering-list.html', 
			controller: 'OfferingListCtrl' 
		});
	$routeProvider.when('/:serviceId/offerings/:offeringId', {
			templateUrl: 'partials/offering-detail.html', 
			controller: 'OfferingDetailCtrl' 
		});

	//
	// procedure routes
	$routeProvider.when('/:serviceId/procedures', {
			templateUrl: 'partials/procedure-list.html', 
			controller: 'ProcedureListCtrl'
		});
	$routeProvider.when('/:serviceId/procedures/:procedureId', {
			templateUrl: 'partials/procedure-detail.html', 
			controller: 'ProcedureDetailCtrl' 
		});
	
	//
	// feature routes
	$routeProvider.when('/:serviceId/features', {
			templateUrl: 'partials/feature-list.html', 
			controller: 'FeatureListCtrl'
		});
	$routeProvider.when('/:serviceId/features/:featureId', {
			templateUrl: 'partials/feature-detail.html', 
				controller: 'FeatureDetailCtrl' 
			});
	
	//
	// phenomenon routes
	$routeProvider.when('/:serviceId/phenomenons', {
			templateUrl: 'partials/phenomenon-list.html', 
			controller: 'PhenomenonListCtrl' 
		});
	$routeProvider.when('/:serviceId/phenomenons/:phenomenonId', {
			templateUrl: 'partials/phenomenon-detail.html', 
			controller: 'PhenomenonDetailCtrl' 
		});

	//
	// station routes
	$routeProvider.when('/:serviceId/stations', {
			templateUrl: 'partials/station-list.html', 
			controller: 'StationListCtrl' 
		});
	$routeProvider.when('/:serviceId/stations/:stationId', {
			templateUrl: 'partials/station-detail.html', 
			controller: 'StationDetailCtrl'
		});
	
	//
	// timeseries routes
//	$routeProvider.when('/timeseries/:timeseriesId', {
//			templateUrl: 'partials/timeseries-img.html', 
//			template : '<a ng-href="{{ts_url}}"></a>',
//			controller: 'TimeseriesImgCtrl' 
//		});
//	$routeProvider.when('/timeseries/:timeseriesId', {
//			templateUrl: 'partials/timeseries-data.html', 
//			controller: 'TimeseriesDataCtrl' 
//		});
	
	//
	// fallback route
    $routeProvider.otherwise({redirectTo: '/'});
  }]);
