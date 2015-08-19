angular.module('BinPacking2D')
.factory('socket', function ($rootScope, $log) {
	var uri = 'ws://' + document.location.host +
		'/LocalSearch/binpacking';
	$log.log(uri);
    var socket = io.connect('ws://localhost:8080',{
    	resource: 'LocalSearch/binpacking'
    });
    $log.log("socket: " + socket.connected);
    return {
        on: function (eventName, callback) {
            socket.on(eventName, function () {
                var args = arguments;
                $rootScope.$apply(function () {
                    callback.apply(socket, args);
                });
            });
        },
        emit: function (eventName, data, callback) {
            socket.emit(eventName, data, function () {
                var args = arguments;
                $rootScope.$apply(function () {
                    if (callback) {
                        callback.apply(socket, args);
                    }
                });
            })
        }
	};
})