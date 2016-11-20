var mainApp = angular.module("mainApp", []);

 mainApp.controller('pokerController', function($scope, $http) {
    $scope.firstCardSelected = false;
    $scope.secondCardSelected = false;
    $scope.disableResetButton = true;
    $scope.disableOkButton = true;
    $scope.disablePositionButtons = false;

    $scope.selectedCard1 = {};
    $scope.selectedCard2 = {};
    $scope.selectedCard3 = {};

    $scope.holeCards = [];
    $scope.flopCards = [];
    $scope.turnCard = {};
    $scope.riverCard = {};

    $scope.initialGameVariables = [];

    $scope.street = "Select holecards";
    $scope.hideHoleCardsBeforeSentToServerDiv = false;
    $scope.hideFlopCardsBeforeSentToServerDiv = true;
    $scope.hideTurnCardBeforeSentToServerDiv = true;
    $scope.hideRiverCardBeforeSentToServerDiv = true;

    $scope.showSelectedHoleCardsFromServerInHandAdviceDiv = false;
    $scope.showSelectedFlopCardsFromServerInHandAdviceDiv = false;
    $scope.showSelectedTurnCardFromServerInHandAdviceDiv = false;
    $scope.showSelectedRiverCardFromServerInHandAdviceDiv = false;

    $scope.showActionBlock = false;
    $scope.showChosenPosition = false;

    $scope.ip = "IP";
    $scope.oop = "OOP";

    $scope.allHoleCards = [
          {spades:'As', clubs:'Ac', diamonds:'Ad', hearts:'Ah'},
          {spades:'Ks', clubs:'Kc', diamonds:'Kd', hearts:'Kh'},
          {spades:'Qs', clubs:'Qc', diamonds:'Qd', hearts:'Qh'},
          {spades:'Js', clubs:'Jc', diamonds:'Jd', hearts:'Jh'},
          {spades:'Ts', clubs:'Tc', diamonds:'Td', hearts:'Th'},
          {spades:'9s', clubs:'9c', diamonds:'9d', hearts:'9h'},
          {spades:'8s', clubs:'8c', diamonds:'8d', hearts:'8h'},
          {spades:'7s', clubs:'7c', diamonds:'7d', hearts:'7h'},
          {spades:'6s', clubs:'6c', diamonds:'6d', hearts:'6h'},
          {spades:'5s', clubs:'5c', diamonds:'5d', hearts:'5h'},
          {spades:'4s', clubs:'4c', diamonds:'4d', hearts:'4h'},
          {spades:'3s', clubs:'3c', diamonds:'3d', hearts:'3h'},
          {spades:'2s', clubs:'2c', diamonds:'2d', hearts:'2h'},
       ];

    //uninitialized scope variables
    $scope.firstCard;
    $scope.secondCard;
    $scope.thirdCard;
    $scope.listOfSelectedCardsFromServer;
    $scope.selectedHoleCard1FromServer;
    $scope.selectedHoleCard2FromServer;
    $scope.selectedFlopCard1FromServer;
    $scope.selectedFlopCard2FromServer;
    $scope.selectedFlopCard3FromServer;
    $scope.selectedTurnCardFromServer;
    $scope.selectedRiverCardFromServer;
    $scope.flopCards;
    $scope.stakes;
    $scope.myStack;
    $scope.opponentStack;
    $scope.position;
    $scope.potSize
    $scope.action;
    $scope.handPath;
    $scope.myAdditionToPot;
    $scope.opponentAdditionToPot;

    //functions
    $scope.selectCard = function(id) {
        var cardButtonToDisable = "disable_" + id;
        $scope[cardButtonToDisable] = true;

        if($scope.firstCardSelected === false) {
            $scope.firstCard = id;
            if($scope.street === "Select turncard" || $scope.street === "Select rivercard") {
                $scope.setButtonFieldNgDisabledPropertiesToTrueOrFalse(true);
                $scope.disableOkButton = false;
            }
            $scope.firstCardSelected = true;
            $scope.disableResetButton = false;
        }
        else if($scope.secondCardSelected === false) {
            $scope.secondCard = id;
            if($scope.street === "Select holecards") {
                $scope.setButtonFieldNgDisabledPropertiesToTrueOrFalse(true);
                if($scope.position === "IP" || $scope.position === "OOP") {
                    $scope.disableOkButton = false;
                }
            }
            $scope.secondCardSelected = true;
        }
        else {
            $scope.thirdCard = id;
            $scope.disableOkButton = false;
            $scope.setButtonFieldNgDisabledPropertiesToTrueOrFalse(true);
        }
    }

    $scope.reset = function() {
        $scope.firstCard = "";
        $scope.secondCard = "";
        $scope.thirdCard = "";

        if($scope.listOfSelectedCardsFromServer === undefined) {
            $scope.setButtonFieldNgDisabledPropertiesToTrueOrFalse(false);
        }
        else {
            $scope.setButtonFieldNgDisabledPropertiesToTrueOrFalse(false, $scope.listOfSelectedCardsFromServer);
        }

        if($scope.street === "Select holecards") {
            $scope.disablePositionButtons = false;
            $scope.showChosenPosition = false;
        }

        $scope.firstCardSelected = false;
        $scope.secondCardSelected = false;
        $scope.disableOkButton = true;
        $scope.disableResetButton = true;
    }

    $scope.setButtonFieldNgDisabledPropertiesToTrueOrFalse = function(trueOrFalse, selectedCardsInPreviousStreets) {
        if (selectedCardsInPreviousStreets === undefined) {
            angular.forEach($scope.allHoleCards, function(value, index){
                var spades = "disable_" + value.spades;
                var clubs = "disable_" + value.clubs;
                var diamonds = "disable_" + value.diamonds;
                var hearts = "disable_" + value.hearts;

                $scope[spades] = trueOrFalse;
                $scope[clubs] = trueOrFalse;
                $scope[diamonds] = trueOrFalse;
                $scope[hearts] = trueOrFalse;
            })
        }
        else {
            var rankCard = [];
            var suitCard = [];
            var cardsToBeButtonDisabled = [];

            for(var i = 0; i < selectedCardsInPreviousStreets.length; i++) {
                rankCard[i] = selectedCardsInPreviousStreets[i].rank;
                suitCard[i] = selectedCardsInPreviousStreets[i].suit;
                cardsToBeButtonDisabled[i] = "disable_" + rankCard[i] + suitCard[i];
            }

            angular.forEach($scope.allHoleCards, function(value, index){
                var spades = "disable_" + value.spades;
                var clubs = "disable_" + value.clubs;
                var diamonds = "disable_" + value.diamonds;
                var hearts = "disable_" + value.hearts;

                $scope[spades] = trueOrFalse;
                $scope[clubs] = trueOrFalse;
                $scope[diamonds] = trueOrFalse;
                $scope[hearts] = trueOrFalse;
            })

            for(var i = 0; i < selectedCardsInPreviousStreets.length; i++) {
                $scope[cardsToBeButtonDisabled[i]] = !trueOrFalse;
            }
        }
    }

    $scope.submitCardsToServer = function() {
        switch($scope.street) {
            case "Select holecards":
                $scope.submitHoleCardsAndInitialGameVariables();
                break;
            case "Select flopcards":
                $scope.submitFlopCards();
                break;
            case "Select turncard":
                $scope.submitTurnCard();
                break;
            case "Select rivercard":
                $scope.submitRiverCard();
                break;
        }
    }

    $scope.submitHoleCardsAndInitialGameVariables = function() {
        $scope.initialGameVariables = [$scope.stakes, $scope.myStack, $scope.opponentStack, $scope.position];
        $http.post('/postInitialGameVariables/', $scope.initialGameVariables).success(function(data) {
            $scope.myStack = data[0];
            $scope.opponentStack = data[1];
            $scope.myAdditionToPot = data[2];
            $scope.opponentAdditionToPot = data[3];
            $scope.potSize = data[4];
            $scope.handPath = data[5];

         }).error(function() {
             alert("Failed to post initial game variables");
         });

        setCorrectPropertiesForJsonToSendToServer();
        $scope.holeCards = [$scope.selectedCard1, $scope.selectedCard2];

        $http.post('/postHoleCards/', $scope.holeCards).success(function(data) {
            $scope.selectedHoleCard1FromServer = data[0];
            $scope.selectedHoleCard1FromServer.rank = convertRankFromIntegerToRank(data[0].rank);
            $scope.selectedHoleCard2FromServer = data[1];
            $scope.selectedHoleCard2FromServer.rank = convertRankFromIntegerToRank(data[1].rank);
            $scope.listOfSelectedCardsFromServer = data;
            $scope.hideHoleCardsBeforeSentToServerDiv = true;
            $scope.hideFlopCardsBeforeSentToServerDiv = false;
            $scope.showSelectedHoleCardsFromServerInHandAdviceDiv = true;
            $scope.showActionBlock = true;
            $scope.street = "Select flopcards";
            $scope.reset();

            $http.get('/getAction/').success(function(data) {
              $scope.action = data.action;
            })
        }).error(function() {
            alert("Failed to submit holecards");
        });
    }

    $scope.submitFlopCards = function() {
        setCorrectPropertiesForJsonToSendToServer();
        $scope.flopCards = [$scope.selectedCard1, $scope.selectedCard2, $scope.selectedCard3];
        $http.post('/postFlopCards/', $scope.flopCards).success(function(data) {
            $scope.selectedFlopCard1FromServer = data[0];
            $scope.selectedFlopCard1FromServer.rank = convertRankFromIntegerToRank(data[0].rank);
            $scope.selectedFlopCard2FromServer = data[1];
            $scope.selectedFlopCard2FromServer.rank = convertRankFromIntegerToRank(data[1].rank);
            $scope.selectedFlopCard3FromServer = data[2];
            $scope.selectedFlopCard3FromServer.rank = convertRankFromIntegerToRank(data[2].rank);

            $scope.listOfSelectedCardsFromServer = $scope.listOfSelectedCardsFromServer.concat(data);
            $scope.hideFlopCardsBeforeSentToServerDiv = true;
            $scope.hideTurnCardBeforeSentToServerDiv = false;
            $scope.showSelectedFlopCardsFromServerInHandAdviceDiv = true;
            $scope.street = "Select turncard";
            $scope.reset();
        }).error(function() {
            alert("Failed to submit flopcards");
        });
    }


    $scope.submitTurnCard = function() {
        setCorrectPropertiesForJsonToSendToServer();
        $scope.turnCard = $scope.selectedCard1;
        $http.post('/postTurnCard/', $scope.turnCard).success(function(data) {
            $scope.selectedTurnCardFromServer = data;
            $scope.selectedTurnCardFromServer.rank = convertRankFromIntegerToRank(data.rank);
            $scope.listOfSelectedCardsFromServer = $scope.listOfSelectedCardsFromServer.concat(data);
            $scope.hideTurnCardBeforeSentToServerDiv = true;
            $scope.hideRiverCardBeforeSentToServerDiv = false;
            $scope.showSelectedTurnCardFromServerInHandAdviceDiv = true;
            $scope.street = "Select rivercard";
            $scope.reset();
        }).error(function() {
            alert("Failed to submit turncard");
        });
    }

    $scope.submitRiverCard = function() {
        setCorrectPropertiesForJsonToSendToServer();
        $scope.riverCard = $scope.selectedCard1;
        $http.post('/postRiverCard/', $scope.riverCard).success(function(data) {
            $scope.selectedRiverCardFromServer = data;
            $scope.selectedRiverCardFromServer.rank = convertRankFromIntegerToRank(data.rank);
            $scope.listOfSelectedCardsFromServer = $scope.listOfSelectedCardsFromServer.concat(data);
            $scope.hideTurnCardBeforeSentToServerDiv = true;
            $scope.hideRiverCardBeforeSentToServerDiv = true;
            $scope.showSelectedRiverCardFromServerInHandAdviceDiv = true;
            $scope.street = "All cards selected";
            $scope.reset();
        }).error(function() {
            alert("Failed to submit turncard");
        });
    }

    $scope.selectPosition = function(position) {
        $scope.position = position;
        $scope.showChosenPosition = true;
        $scope.disablePositionButtons = true;
        if($scope.secondCardSelected === true) {
            $scope.disableOkButton = false;
        }
    }


    function convertRankFromCharacterToInteger(rankCard1) {
        switch(rankCard1) {
            case 'A':
                return 14;
                break;
            case 'K':
                return 13;
                break;
            case 'Q':
                return 12;
                break;
            case 'J':
                return 11;
                break;
            case 'T':
                return 10;
                break;
            default:
                return rankCard1;
        }
    }

    function convertRankFromIntegerToRank(rankCard1) {
        switch(rankCard1) {
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
                return rankCard1;
        }
    }

    function setCorrectPropertiesForJsonToSendToServer() {
        var rankCard1 = $scope.firstCard.substring(0,1);
        rankCard1 = convertRankFromCharacterToInteger(rankCard1);
        $scope.selectedCard1.rank = rankCard1;

        var suitCard1 = $scope.firstCard.substring(1,2);
        $scope.selectedCard1.suit = suitCard1;

        var rankCard2 = $scope.secondCard.substring(0,1);
        rankCard2 = convertRankFromCharacterToInteger(rankCard2);
        $scope.selectedCard2.rank = rankCard2;

        var suitCard2 = $scope.secondCard.substring(1,2);
        $scope.selectedCard2.suit = suitCard2;

        if(!($scope.listOfSelectedCardsFromServer === undefined)) {
           var rankCard3 = $scope.thirdCard.substring(0,1);
           rankCard3 = convertRankFromCharacterToInteger(rankCard3);
           $scope.selectedCard3.rank = rankCard3;

           var suitCard3 = $scope.thirdCard.substring(1,2);
           $scope.selectedCard3.suit = suitCard3;
        }
    }
 });