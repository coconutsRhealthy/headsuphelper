var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.newHand;
    $scope.gameVariables;
    $scope.actionVariables;

    $scope.fillFields = function() {
        $http.post('/fillFields/', $scope.newHand).success(function(data) {
            $scope.gameVariables = data;
        })
    }

    $scope.getAction = function() {
        $http.post('/getAction/', $scope.gameVariables).success(function(data) {
            $scope.actionVariables = data;
        })
    }

    function setBoardString() {
        $scope.riverCardConvertedRank = null;
        $scope.riverCardSuitUniCode = null;
        $scope.riverCardClass = null;
    }

    function setBoardString() {
        $scope.riverCardConvertedRank = null;
        $scope.riverCardSuitUniCode = null;
        $scope.riverCardClass = null;
    }



});