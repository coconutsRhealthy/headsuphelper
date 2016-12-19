var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.computerGame;

    $scope.holeCard1ConvertedRank;
    $scope.holeCard2ConvertedRank;
    $scope.flopCard1ConvertedRank;
    $scope.flopCard2ConvertedRank;
    $scope.flopCard3ConvertedRank;
    $scope.turnCardConvertedRank;
    $scope.riverCardConvertedRank;

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

    $scope.dealerButtonStyle;

    $scope.fold = "fold";
    $scope.check = "check";
    $scope.call = "call";
    $scope.bet = "bet";
    $scope.raise = "raise";

    $scope.startGame = function() {
        $http.get('/startGame/').success(function(data) {
            setScopePropertiesCorrect(data);
        })
    }

    function setScopePropertiesCorrect(data) {
        $scope.computerGame = data;

        $scope.holeCard1ConvertedRank = convertRankFromIntegerToRank($scope.computerGame.myHoleCards[0].rank);
        $scope.holeCard2ConvertedRank = convertRankFromIntegerToRank($scope.computerGame.myHoleCards[1].rank);

        setHoleCard1SuitWritten();
        setHoleCard2SuitWritten();
        setHoleCardsCss();
        setDealerButton();
    }

    function setHoleCard1SuitWritten() {
        switch($scope.computerGame.myHoleCards[0].suit) {
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
        switch($scope.computerGame.myHoleCards[1].suit) {
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
        $scope.holeCard1Class = "card rank-" + $scope.holeCard1ConvertedRank + " " + $scope.holeCard1SuitWritten;
        $scope.holeCard2Class = "card rank-" + $scope.holeCard2ConvertedRank + " " + $scope.holeCard2SuitWritten;
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
        if($scope.computerGame.computerIsButton) {
            $scope.dealerButtonStyle = "float: right; padding-top: 30px;";
        } else {
            $scope.dealerButtonStyle = "float: left; padding-top: 30px;";
        }
    }

    $scope.submitMyAction = function(action) {
        alert(action);

        $scope.computerGame.myAction = action;

        alert($scope.computerGame.myAction);

        $http.post('/submitMyAction/', $scope.computerGame).success(function(data) {
            setScopePropertiesCorrect(data);

            alert("Sjaakie-son!");
        })
    }
});