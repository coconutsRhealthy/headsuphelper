var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.holeCard1;
    $scope.holeCard2;

    $scope.flopCard1;
    $scope.flopCard2;
    $scope.flopCard3;
    $scope.turnCard;
    $scope.riverCard;

    $scope.holeCard1SuitWritten;
    $scope.holeCard2SuitWritten;
    $scope.flopCard1SuitWritten;
    $scope.flopCard2SuitWritten;
    $scope.flopCard3SuitWritten;
    $scope.turnCardSuitWritten;
    $scope.riverCardSuitWritten;

    $scope.holeCard1Class;
    $scope.holeCard1SuitUniCode;

    $scope.holeCard2Class;
    $scope.holeCard2SuitUniCode;

    $scope.flopCard1Class;
    $scope.flopCard1SuitUniCode;

    $scope.flopCard2Class;
    $scope.flopCard2SuitUniCode;

    $scope.flopCard3Class;
    $scope.flopCard3SuitUniCode;

    $scope.turnCardClass;
    $scope.turnCardSuitUniCode;

    $scope.riverCardClass;
    $scope.riverCardSuitUniCode;

    $scope.myStack;
    $scope.computerStack;

    $scope.myBetSize;
    $scope.computerBetSize;
    $scope.potSize;

    $scope.button;

    $scope.dealerButtonStyle;

    $scope.startGame = function() {
        $http.get('/startGame/').success(function(data) {
            $scope.holeCard1 = data.myHand[0];
            $scope.holeCard2 = data.myHand[1];

            $scope.myStack = data.myStack;
            $scope.computerStack = data.computerStack;

            $scope.myBetSize = data.myBetSize;
            $scope.computerBetSize = data.computerBetSize;
            $scope.potSize = data.potSize;

            $scope.button = data.button;

            $scope.holeCard1.rank = convertRankFromIntegerToRank($scope.holeCard1.rank);
            $scope.holeCard2.rank = convertRankFromIntegerToRank($scope.holeCard2.rank);

            setHoleCard1SuitWritten();
            setHoleCard2SuitWritten();
            setHoleCardsCss();
            setDealerButton();
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
            default:
                return rankCard;
        }
    }

    function setDealerButton() {
        if($scope.button) {
            $scope.dealerButtonStyle = "float: left; padding-top: 30px;";
        } else {
            $scope.dealerButtonStyle = "float: right; padding-top: 30px;";
        }
    }
});