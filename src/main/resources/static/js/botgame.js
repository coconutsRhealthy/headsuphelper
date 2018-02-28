var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.botTable;

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

    $scope.showGame;

    $scope.startBotTable = function() {
        $http.get('/startBotTable/').success(function(data) {
            setScopePropertiesCorrect(data);
            $scope.showGame = true;
        })
    }

    $scope.getNewBotAction = function() {
        $http.post('/getNewBotActionInBotTable/', $scope.botTable).success(function(data) {
            setScopePropertiesCorrect(data);
        })
    }

    $scope.runBotTableContinuously = function() {
        $http.get('/runBotTableContinuously/')
    }

    function setScopePropertiesCorrect(data) {
        $scope.botTable = data;

        $scope.holeCard1ConvertedRank = convertRankFromIntegerToRank($scope.botTable.botHand.botHoleCards[0].rank);
        $scope.holeCard2ConvertedRank = convertRankFromIntegerToRank($scope.botTable.botHand.botHoleCards[1].rank);

        setSuitWrittenAndUniCode("holeCard1SuitWritten", "holeCard1SuitUniCode", $scope.botTable.botHand.botHoleCards[0].suit);
        setSuitWrittenAndUniCode("holeCard2SuitWritten", "holeCard2SuitUniCode", $scope.botTable.botHand.botHoleCards[1].suit);

        $scope.holeCard1Class = "card rank-" + $scope.holeCard1ConvertedRank + " " + $scope.holeCard1SuitWritten;
        $scope.holeCard2Class = "card rank-" + $scope.holeCard2ConvertedRank + " " + $scope.holeCard2SuitWritten;

        if($scope.botTable.botHand.flopCards != undefined) {
            $scope.flopCard1ConvertedRank = convertRankFromIntegerToRank($scope.botTable.botHand.flopCards[0].rank);
            $scope.flopCard2ConvertedRank = convertRankFromIntegerToRank($scope.botTable.botHand.flopCards[1].rank);
            $scope.flopCard3ConvertedRank = convertRankFromIntegerToRank($scope.botTable.botHand.flopCards[2].rank);

            setSuitWrittenAndUniCode("flopCard1SuitWritten", "flopCard1SuitUniCode", $scope.botTable.botHand.flopCards[0].suit);
            setSuitWrittenAndUniCode("flopCard2SuitWritten", "flopCard2SuitUniCode", $scope.botTable.botHand.flopCards[1].suit);
            setSuitWrittenAndUniCode("flopCard3SuitWritten", "flopCard3SuitUniCode", $scope.botTable.botHand.flopCards[2].suit);

            $scope.flopCard1Class = "card rank-" + $scope.flopCard1ConvertedRank + " " + $scope.flopCard1SuitWritten;
            $scope.flopCard2Class = "card rank-" + $scope.flopCard2ConvertedRank + " " + $scope.flopCard2SuitWritten;
            $scope.flopCard3Class = "card rank-" + $scope.flopCard3ConvertedRank + " " + $scope.flopCard3SuitWritten;
        } else {
            resetFlopCards();
        }

        if($scope.botTable.botHand.turnCard != undefined) {
            $scope.turnCardConvertedRank = convertRankFromIntegerToRank($scope.botTable.botHand.turnCard.rank);
            setSuitWrittenAndUniCode("turnCardSuitWritten", "turnCardSuitUniCode", $scope.botTable.botHand.turnCard.suit);
            $scope.turnCardClass = "card rank-" + $scope.turnCardConvertedRank + " " + $scope.turnCardSuitWritten;
        } else {
            resetTurnCard();
        }

        if($scope.botTable.botHand.riverCard != undefined) {
            $scope.riverCardConvertedRank = convertRankFromIntegerToRank($scope.botTable.botHand.riverCard.rank);
            setSuitWrittenAndUniCode("riverCardSuitWritten", "riverCardSuitUniCode", $scope.botTable.botHand.riverCard.suit);
            $scope.riverCardClass = "card rank-" + $scope.riverCardConvertedRank + " " + $scope.riverCardSuitWritten;
        } else {
            resetRiverCard();
        }

        setDealerButton();
        setWidthOfBoardCards();
    }

    function setSuitWrittenAndUniCode(scopeVariableSuitWritten, scopeVariableSuitUniCode, suit) {
        switch(suit) {
            case 's':
                $scope[scopeVariableSuitWritten] = "spades";
                $scope[scopeVariableSuitUniCode] = "\u2660";
                break;
            case 'c':
                $scope[scopeVariableSuitWritten] = "clubs";
                $scope[scopeVariableSuitUniCode] = "\u2663";
                break;
            case 'd':
                $scope[scopeVariableSuitWritten] = "diams";
                $scope[scopeVariableSuitUniCode] = "\u2666";
                break;
            case 'h':
                $scope[scopeVariableSuitWritten] = "hearts";
                $scope[scopeVariableSuitUniCode] = "\u2665";
                break;
        }
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
        if($scope.botTable.botHand.botIsButton) {
            $scope.dealerButtonStyle = "float: left; padding-top: 30px;";
        } else {
            $scope.dealerButtonStyle = "float: right; padding-top: 30px;";
        }
    }

    function setWidthOfBoardCards() {
        if($scope.botTable.botHand.flopCards != undefined) {
            if($scope.botTable.botHand.turnCard != undefined) {
                if($scope.botTable.botHand.riverCard != undefined) {
                    $scope.boardCardsStyle = "width: 430px;";
                    return;
                }
                $scope.boardCardsStyle = "width: 345px;";
                return;
            }
            $scope.boardCardsStyle = "width: 254px;";
            return;
        }
    }

    function resetFlopCards() {
        $scope.flopCard1ConvertedRank = null;
        $scope.flopCard2ConvertedRank = null;
        $scope.flopCard3ConvertedRank = null;

        $scope.flopCard1SuitUniCode = null;
        $scope.flopCard2SuitUniCode = null;
        $scope.flopCard3SuitUniCode = null;

        $scope.flopCard1Class = null;
        $scope.flopCard2Class = null;
        $scope.flopCard3Class = null;
    }

    function resetTurnCard() {
        $scope.turnCardConvertedRank = null;
        $scope.turnCardSuitUniCode = null;
        $scope.turnCardClass = null;
    }

    function resetRiverCard() {
        $scope.riverCardConvertedRank = null;
        $scope.riverCardSuitUniCode = null;
        $scope.riverCardClass = null;
    }
});