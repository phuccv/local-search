/**
*  Module
*
* Description
*/
angular.module('BinPacking2D', []);

'use strict';

angular.module('BinPacking2D')
.controller('BinPacking2DCtrl', function ($scope, $http, $log, $timeout, poller) {
	var HOST_CONTROL = 'http://localhost:8080/LocalSearch/binpacking2d/control';
	var HOST_RESOURCE = 'http://localhost:8080/LocalSearch/binpacking2d/fetch';
	
	var solveConflict = function(items, binSize) {

		// Calculate item bound
		for(var i = 0; i< items.length; i++){
			var itm = items[i];
			itm.conflict = false;
			if(itm.rotated == 0){
				itm.right = itm.xPos + itm.width;
				itm.bottom = itm.yPos + itm.height;
			} else{
				itm.right = itm.xPos + itm.height;
				itm.bottom = itm.yPos + itm.width;
			}
		}
		for(var i = 0; i < items.length; i++){
			var itm = items[i];

			// Check item overlap
			if(itm.right > binSize.width || itm.bottom > binSize.height){
				itm.conflict = true;
			}
			for(var j = i + 1; j < items.length; j++){
				var other = items[j];
				if(!(itm.xPos >= other.right || other.xPos >= itm.right
						|| itm.yPos >= other.bottom || other.yPos >= itm.bottom)){
					itm.conflict = true;
					other.conflict = true;
//					$log.log(i + " " + j + '--- (' + itm.xPos + ',' + itm.yPos +  '--- ' + itm.right + ' ' + itm.bottom +')  (' + other.xPos + ',' + other.yPos +   '--- ' + other.right + ' ' + other.bottom +')' );
				}
			}
		}
		
	}
	
	poller.on('INIT', function(){
		$scope.bp.binSize = this.bin;
		$scope.bp.state.running = false;
		$scope.bp.state.ready = true;
		solveConflict(this.items, this.bin);
		$scope.bp.items = this.items;
		$scope.bp.globalViolations = this.violations;
		$scope.bp.localViolations = this.violations;
	});
	
	poller.on('LOCAL', function(){
		solveConflict(this.items, $scope.bp.binSize);
		$scope.bp.items = this.items;
		$scope.bp.localViolations = this.violations;
	});
	
	poller.on('RESET', function(){
		$log.log("Local Reset" + this.violations);
	});
	
	poller.on('GLOBAL', function(){
		solveConflict(this.items, $scope.bp.binSize);
		$scope.bp.items = this.items;
		$scope.bp.globalViolations = this.violations;
		$scope.bp.localViolations = this.violations;
	});
	
	poller.on('GLOBAL_RESET', function(){
		$log.log("Global Reset" + this.violations);
		$scope.bp.localViolations = this.violations;
	});
	poller.on('FINISH', function(){
		solveConflict(this.items, $scope.bp.binSize);
		$scope.bp.items = this.items;
		$scope.bp.state.running = false;
		$scope.bp.state.ready = false;
		
	});

	$scope.bp = {
		file : 0,
		step : 0,
		initMode : 'RANDOM',
		items : [],
		binSize : {
			width : 700,
			height : 300
		},
		state : {
			ready : false,
			running : false
		},
		globalViolations : 0,
		localViolations :0,
		inputFileLists : [],
		initMethodLists : []
	};
	
	$http.get(HOST_RESOURCE + '?res=files')
	.success(function(data){
		$scope.bp.inputFileLists = data;
		$scope.bp.file = $scope.bp.inputFileLists[0];
		$http.get(HOST_RESOURCE + '?res=init')
		.success(function(data){
			$scope.bp.initMethodLists = data;
			$scope.bp.initMode = $scope.bp.initMethodLists[0];
			$http.get(HOST_RESOURCE + '?res=search')
			.success(function(data){
				$scope.bp.searchMethodLists = data;
				$scope.bp.searchMethod = $scope.bp.searchMethodLists[0];
				init();
			});
		});
	});
	
	var init = function () {
		$http.get(HOST_CONTROL + '?action=init&fileId='+ $scope.bp.file.id + '&initMode='+$scope.bp.initMode + '&search='+ $scope.bp.searchMethod);
	};
	
	var localSearch = function () {
		$http.get('http://localhost:8080/LocalSearch/binpacking2d/control?action=start')
		.success(function(data, status, headers, config) {
			$scope.bp.state.running = true;
		})
	};

	var stopSearch = function () {
		$scope.bp.state.initReady = false;
		$http.get('http://localhost:8080/LocalSearch/binpacking2d/control?action=stop')
		.success(function(data, status, headers, config) {
			$scope.bp.state.running = false;
		})
		//TODO
	};

	/******************* sort items ************/
//	$scope.items.sort(function (item1, item2) {
//		if(item1.yPos > item2.yPos){
//			return 1;
//		} else if(item1.yPos < item2.yPos){
//			return -1;
//		} else {
//			if(item1.xPos > item2.xPos){
//				return 1;
//			} else if (item1.xPos < item2.xPos){
//				return -1;
//			} else {
//				if(item1.width > item2.width){
//					return 1;
//				} else if(item1.width < item2.width){
//					return -1;
//				} else {
//					return 0;
//				}
//			}
//		}
//	});

	/******************** define scope variable *************/
	//function
	$scope.init = init;
	$scope.localSearch = localSearch;
	$scope.stopSearch = stopSearch;

	// init value
//	 init();

});