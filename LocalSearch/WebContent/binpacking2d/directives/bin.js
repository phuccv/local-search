angular.module('BinPacking2D')

.directive('bin', ['$log', function ($log) {
	return {
		restrict: 'A',
		scope : {
			bp : '=',
		},
		link: function (scope, element, attrs) {
			var itemsColor = [
			];
			
			function getRandomColor() {
			    var letters = '0123456789ABCDEF'.split('');
			    var color = '#33';
			    for (var i = 0; i < 4; i++ ) {
			        color += letters[Math.floor(Math.random() * 16)];
			    }
			    return color;
			}
			
			var ctx = element[0].getContext('2d');
			
			var updateCanvas = function (oldValue, newValue) {
				ctx.clearRect(0, 0, element[0].width, element[0].height);
			
				// set size
				var MAX_SIZE = {
					width: 792,
					height : 400,
				};
				
				var binSize = scope.bp.binSize;
				var items = scope.bp.items;
				
				// gen color
				for(var i = 0; i< items.length; i++){
					itemsColor.push(getRandomColor());
				}
				
				var scale = {
					x : parseInt(MAX_SIZE.width / binSize.width),
					y : parseInt(MAX_SIZE.height / binSize.height)
				};

				scale.x = scale.x < 1 ? 1 : scale.x;
				scale.y = scale.y < 1 ? 1 : scale.y;
				
				if(scale.x > scale.y){
					scale.x = scale.y;
				} else {
					scale.y = scale.x;
				}

				element[0].width = binSize.width * scale.x;
				element[0].height = binSize.height * scale.y;

				// draw all item
				for(i = 0; i< items.length; i++){
					var itm = items[i];
					// draw items
					var x = itm.xPos * scale.x +1;
					var y = itm.yPos * scale.y +1;
					var w = itm.width * scale.x -2;
					var h = itm.height * scale.y -2;
					if(itm.rotated == 1){
						w = itm.height * scale.x -2;
						h = itm.width * scale.y -2;
					}
					
//					ctx.globalCompositeOperation = 'lighter';
					ctx.globalCompositeOperation = 'source-over';
					ctx.beginPath();
					ctx.rect(x, y, w, h);
					ctx.fillStyle = itemsColor[i];
					if(itm.conflict == true){
						ctx.strokeStyle = 'red';
						ctx.fillStyle = '#ff0000';
					} else {
						ctx.strokeStyle = 'black';
					}
					ctx.fill();
					ctx.lineWidth = 1;
					ctx.stroke();
				}
			};

			// watch items value
			scope.$watch('bp.items', updateCanvas, true);
//			scope.$watch('bp.binSize', updateCanvas, true);
			// scope.$watch('size', updateCanvas(), true);
		},

	};
}])