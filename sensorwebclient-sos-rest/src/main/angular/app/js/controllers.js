'use strict';


/* Controllers */

angular.module('myApp.controllers', [])
  .controller('ServiceListCtrl', [ '$scope','SosServices','Utils',function ($scope,SosServices,utils) {
	  $scope.utils = utils;
	  $scope.services = SosServices.query();
  }])
  .controller('ServiceDetailCtrl', [ '$scope','$routeParams','SosService','Stations', function ($scope,$routeParams,SosService,Stations) {
	  $scope.service = SosService.get({serviceId: $routeParams.serviceId});
	  
	  //angular.element(document).ready(function() {
	  //	  initializeMap('service-detail-map', Stations.get({serviceId: $routeParams.serviceId}));
	  //});
  }])
  .controller('OfferingListCtrl', [ '$scope','$routeParams','Offerings','Utils',function ($scope,$routeParams,Offerings,utils) {
	  $scope.utils = utils;
	  $scope.offerings = Offerings.query({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('OfferingDetailCtrl', [ '$scope','$routeParams','Offering',function ($scope,$routeParams,Offering) {
	  $scope.offering = Offering.get({
			  	serviceId: $routeParams.serviceId,
			  	offeringId: $routeParams.offeringId
		  	}, function() {
		  		$scope.serviceId = $routeParams.serviceId;
		  	});
  }])
  .controller('ProcedureListCtrl', [ '$scope','$routeParams','Procedures','Utils',function ($scope,$routeParams,Procedures,utils) {
	  $scope.utils = utils;
	  $scope.procedures = Procedures.query({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('ProcedureDetailCtrl', [ '$scope','$routeParams','Procedure',function ($scope,$routeParams,Procedure) {
	  $scope.procedure = Procedure.get({
			  	serviceId: $routeParams.serviceId,
		  		procedureId: $routeParams.procedureId
	  		}, function() {
	  			$scope.serviceId = $routeParams.serviceId;
	  		});
  }])
  .controller('FeatureListCtrl', [ '$scope','$routeParams','Features','Utils',function ($scope,$routeParams,Features,utils) {
	  $scope.utils = utils;
	  $scope.features = Features.query({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('FeatureDetailCtrl', [ '$scope','$routeParams','Feature',function ($scope,$routeParams,Feature) {
	  $scope.feature = Feature.get({
			  	serviceId: $routeParams.serviceId,
		  		featureId: $routeParams.featureId
	  		}, function() {
	  			$scope.serviceId = $routeParams.serviceId;
	  		});
  }])
  .controller('PhenomenonListCtrl', [ '$scope','$routeParams','Phenomenons','Utils',function ($scope,$routeParams,Phenomenons,utils) {
	  $scope.utils = utils;
	  $scope.phenomenons = Phenomenons.query({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('PhenomenonDetailCtrl', [ '$scope','$routeParams','Phenomenon',function ($scope,$routeParams,Phenomenon) {
	  $scope.phenomenon = Phenomenon.get({
			  	serviceId: $routeParams.serviceId,
			  	phenomenonId: $routeParams.phenomenonId
	  		}, function() {
	  			$scope.serviceId = $routeParams.serviceId;
	  		});
  }])
  .controller('StationListCtrl', [ '$scope','$routeParams','Stations','Utils',function ($scope,$routeParams,Stations,utils) {
	  $scope.utils = utils;
	  $scope.stations = Stations.query({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('StationDetailCtrl', [ '$scope','$routeParams','Station',function ($scope,$routeParams,Station) {
	  
	  $scope.station = Station.get({
			  	serviceId: $routeParams.serviceId,
			  	stationId: $routeParams.stationId
	  		}, function() {
	  			$scope.serviceId = $routeParams.serviceId;
	  		});
	  
  }]);
