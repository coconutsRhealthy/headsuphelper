var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.newHand;
    $scope.gameVariables;
    $scope.actionVariables;

    $scope.fillFields = function() {
        if($scope.newHand === "true") {
            $http.get('/fillFieldsInitial/').success(function(data) {
                $scope.gameVariables = data;
            })
        } else {
            $http.post('/fillFieldsSubsequent/', $scope.gameVariables).success(function(data) {
                $scope.gameVariables = data;
            })
        }
    }

    $scope.getAction = function() {
        $http.post('/getAction/', $scope.gameVariables).success(function(data) {
            $scope.gameVariables = data;
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