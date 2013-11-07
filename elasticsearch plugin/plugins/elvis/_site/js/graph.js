
angular.module('chartApp', [])
.controller('appController', function appController ($scope, $http, $timeout, $interval) {
    $scope.memoryUsage = [];

    var nodename = "";
    var timerId = 0;

    $scope.getData = function() {
        $http.get('/_nodes/stats?jvm=true').success(function(data, status) {

            // hack: hent ut node-hashen for den første noden i listen
            if (!nodename) {
                for (node in data.nodes) {
                    nodename = node;
                    break;
                }
            }

            $scope.memoryUsage.push(data.nodes[nodename].jvm.mem.heap_used_in_bytes);
        });
    };    

    timerId = $interval(function() {
        $scope.getData();
    }, 1000);

    $scope.$on("$destroy", function(){
        $interval.cancel(timerId);
    });

})
.directive('graph', function() {

    var width = 800,
        height = 600;

    return {
        restrict: 'E',
        scope: {
            values: '='
        },
        link: function(scope, element, attrs) {

            var graph = d3.select(element[0])
                        .append("svg")
                        .attr("width", width)
                        .attr("height", height);

            scope.$watch('values', function (newVal, oldVal) {

                var x = d3.scale.linear()
                        .domain([0, newVal.length])
                        .range([0, width]);

                var y = d3.scale.linear()
                        .domain([0, d3.max(newVal)])
                        .range([height, 0]);

                var line = d3.svg.line()
                            .x(function(d,i) {
                                return x(i); 
                            })
                            .y(function(d) { 
                               return y(d); 
                            });

                graph.selectAll('*').remove();
                graph.append("path").attr("d", line(newVal));
            }, true);
        }
    }
});