var mainApp = angular.module("mainApp", []);

mainApp.controller('backendDbController', function($scope, $http) {

    $scope.fillDbs = function() {
        alert("Starting to fill databases");

        $http.get('/fillDbs').success(function(data) {
            alert("Done fillDbs");
        })
    }

    $scope.startHandSimulation = function() {
        alert("Starting handsimulation");

        $http.get('/startHandSimulation').success(function(data) {
            alert("Done handsimulation");
        })
    }
});