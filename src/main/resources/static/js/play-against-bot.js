var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.holeCard1;
    $scope.holeCard2;

    $scope.startGame = function() {
        $http.get('/startGame/').success(function(data) {
            $scope.holeCard1 = data.myHand[1];
            $scope.holeCard2 = data.myHand[2];
        })
    }
});