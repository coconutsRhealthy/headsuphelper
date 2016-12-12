var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.holeCard1;
    $scope.holeCard2;

    $scope.holeCard1SuitWritten;

    $scope.holeCard1Class;
    $scope.holeCard1SuitUniCode;

    $scope.suitWritten;



    $scope.startGame = function() {
        $http.get('/startGame/').success(function(data) {
            $scope.holeCard1 = data.myHand[1];
            $scope.holeCard2 = data.myHand[2];

            setSuitWritten();
            setHoleCardsCss();

            alert(JSON.stringify($scope.holeCard1));
        })
    }

    function setSuitWritten() {
        switch($scope.holeCard1.suit) {
            case 's':
                $scope.holeCard1SuitWritten = "spades";
                $scope.holeCard1SuitUniCode = "\u2660";
                break;
            case 'c':
                $scope.holeCard1SuitWritten = "clubs";
                $scope.holeCard1SuitUniCode = "\u2663";
                break;
            case 'd':
                $scope.holeCard1SuitWritten = "diams";
                $scope.holeCard1SuitUniCode = "\u2666";
                break;
            case 'h':
                $scope.holeCard1SuitWritten = "hearts";
                $scope.holeCard1SuitUniCode = "\u2665";
                break;
        }
    }

    function setHoleCardsCss() {
        $scope.holeCard1Class = "card rank-" + $scope.holeCard1.rank + " " + $scope.holeCard1SuitWritten;
    }
});