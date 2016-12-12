var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.holeCard1;
    $scope.holeCard2;

    $scope.holeCard1SuitWritten;
    $scope.holeCard2SuitWritten;

    $scope.holeCard1Class;
    $scope.holeCard1SuitUniCode;

    $scope.holeCard2Class;
    $scope.holeCard2SuitUniCode;

    $scope.startGame = function() {
        $http.get('/startGame/').success(function(data) {
            $scope.holeCard1 = data.myHand[0];
            $scope.holeCard2 = data.myHand[1];

            $scope.holeCard1.rank = convertRankFromIntegerToRank($scope.holeCard1.rank);
            $scope.holeCard2.rank = convertRankFromIntegerToRank($scope.holeCard2.rank);

            setHoleCard1SuitWritten();
            setHoleCard2SuitWritten();
            setHoleCardsCss();
        })
    }

    function setHoleCard1SuitWritten() {
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

    function setHoleCard2SuitWritten() {
        switch($scope.holeCard2.suit) {
            case 's':
                $scope.holeCard2SuitWritten = "spades";
                $scope.holeCard2SuitUniCode = "\u2660";
                break;
            case 'c':
                $scope.holeCard2SuitWritten = "clubs";
                $scope.holeCard2SuitUniCode = "\u2663";
                break;
            case 'd':
                $scope.holeCard2SuitWritten = "diams";
                $scope.holeCard2SuitUniCode = "\u2666";
                break;
            case 'h':
                $scope.holeCard2SuitWritten = "hearts";
                $scope.holeCard2SuitUniCode = "\u2665";
                break;
        }
    }

    function setHoleCardsCss() {
        $scope.holeCard1Class = "card rank-" + $scope.holeCard1.rank + " " + $scope.holeCard1SuitWritten;
        $scope.holeCard2Class = "card rank-" + $scope.holeCard2.rank + " " + $scope.holeCard2SuitWritten;
    }

    function convertRankFromIntegerToRank(rankCard) {
        switch(rankCard) {
            case 14:
                return 'A';
                break;
            case 13:
                return 'K';
                break;
            case 12:
                return 'Q';
                break;
            case 11:
                return 'J';
                break;
            case 10:
                return 'T';
                break;
            default:
                return rankCard;
        }
    }
});