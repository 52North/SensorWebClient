'use strict';


/* Controllers */

angular.module('myApp.controllers', [])
  .controller('ServiceListCtrl', [ '$scope','SosInstanceService','Utils',function ($scope,service,utils) {
	  $scope.utils = utils;
	  $scope.services = service.getServiceInstances();
  }])
  .controller('ServiceDetailCtrl', [ '$scope','$routeParams','SosInstanceService',function ($scope,$routeParams,service) {
	  $scope.service = service.getServiceInstance({serviceId: $routeParams.serviceId});
  }])
  .controller('OfferingListCtrl', [ '$scope','$routeParams','OfferingService','Utils',function ($scope,$routeParams,service,utils) {
	  debugger;
	  $scope.utils = utils;
	  $scope.offerings = service.getOfferings({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('OfferingDetailCtrl', [ '$scope','$routeParams','OfferingService',function ($scope,$routeParams,service) {
	  $scope.offering = service.getOffering({
			  	serviceId: $routeParams.serviceId,
			  	offeringId: $routeParams.offeringId
		  	}, function() {
		  		$scope.serviceId = $routeParams.serviceId;
		  	});
  }])
  .controller('ProcedureListCtrl', [ '$scope','$routeParams','ProcedureService','Utils',function ($scope,$routeParams,service,utils) {
	  $scope.utils = utils;
	  $scope.procedures = service.getProcedures({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('ProcedureDetailCtrl', [ '$scope','$routeParams','ProcedureService',function ($scope,$routeParams,service) {
	  $scope.procedure = service.getProcedure({
			  	serviceId: $routeParams.serviceId,
		  		procedureId: $routeParams.procedureId
	  		}, function() {
	  			$scope.serviceId = $routeParams.serviceId;
	  		});
  }])
  .controller('FeatureListCtrl', [ '$scope','$routeParams','FeatureService','Utils',function ($scope,$routeParams,service,utils) {
	  $scope.utils = utils;
	  $scope.features = service.getFeatures({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('FeatureDetailCtrl', [ '$scope','$routeParams','FeatureService',function ($scope,$routeParams,service) {
	  $scope.feature = service.getFeature({
			  	serviceId: $routeParams.serviceId,
		  		featureId: $routeParams.featureId
	  		}, function() {
	  			$scope.serviceId = $routeParams.serviceId;
	  		});
  }])
  .controller('PhenomenonListCtrl', [ '$scope','$routeParams','PhenomenonService','Utils',function ($scope,$routeParams,service,utils) {
	  $scope.utils = utils;
	  $scope.phenomenons = service.getPhenomenons({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('PhenomenonDetailCtrl', [ '$scope','$routeParams','PhenomenonService',function ($scope,$routeParams,service) {
	  $scope.phenomenon = service.getPhenomenon({
			  	serviceId: $routeParams.serviceId,
			  	phenomenonId: $routeParams.phenomenonId
	  		}, function() {
	  			$scope.serviceId = $routeParams.serviceId;
	  		});
  }])
  .controller('StationListCtrl', [ '$scope','$routeParams','StationService','Utils',function ($scope,$routeParams,service,utils) {
	  $scope.utils = utils;
	  $scope.stations = service.getStations({serviceId: $routeParams.serviceId});
	  $scope.serviceId = $routeParams.serviceId;
  }])
  .controller('StationDetailCtrl', [ '$scope','$routeParams','StationService',function ($scope,$routeParams,service) {
	  $scope.station = service.getStation({
			  	serviceId: $routeParams.serviceId,
			  	stationId: $routeParams.stationId
	  		}, function() {
	  			$scope.serviceId = $routeParams.serviceId;
	  		});
	  
  }]);
