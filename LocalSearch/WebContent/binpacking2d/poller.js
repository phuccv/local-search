angular.module('BinPacking2D')

.factory('poller', function($rootScope, $http, $log, $timeout) {
	var errCount = 0;
	var TRY_PERIOD = 50;
	var RESOURCE = 'http://localhost:8080/LocalSearch/binpacking2d/events';
	
	var pollForEvent = function(timeout){
		$http.get(RESOURCE)
		.success(function(data, status, headers, config) {
			var events = data.events;
			for(var i = 0; i< events.length; i++){
				var event = events[i];
				if(service.handlers[event.name]){
					var handlers = service.handlers[event.name];
					for (var j = 0; j < handlers.length; j++) {
						handlers[j].apply(event);
//						$timeout(function() {
//							$rootScope.$apply();
//						});
					}
				} else {
					$log.log('Event without handle');
				}
			}
			$timeout(function() {
				pollForEvent(TRY_PERIOD);
			}, TRY_PERIOD);
		})
		.error(function() {
			$log.error('Http get error');
			$timeout(function() {
				pollForEvent(TRY_PERIOD);
			}, TRY_PERIOD);
		});
	};

	pollForEvent(1000);
	

	var service = {
		handlers : {},
		on: function(evtName, callback) {
			if(!service.handlers[evtName]){
				service.handlers[evtName] = [];
			}
			service.handlers[evtName].push(callback);
		}
	};
	
	return service;
})


