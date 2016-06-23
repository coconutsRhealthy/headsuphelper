var mainApp = angular.module("mainApp", []);

 mainApp.controller('pokerController', function($scope, $http) {
    $scope.firstCardSelected = false;
    $scope.secondCardSelected = false;
    $scope.disableResetButton = true;
    $scope.disableOkButton = true;

    $scope.selectedCard1 = {};
    $scope.selectedCard2 = {};
    $scope.selectedCard3 = {};

    $scope.holeCards = [];
    $scope.flopCards = [];

    $scope.street = "Select holecards";
    $scope.hideHoleCardsBeforeSentToServerDiv = false;
    $scope.hideFlopCardsBeforeSentToServerDiv = true;
    $scope.hideTurnCardBeforeSentToServerDiv = true;

    $scope.showSelectedHoleCardsFromServerInHandAdviceDiv = false;
    $scope.showSelectedFlopCardsFromServerInHandAdviceDiv = false;
    $scope.showSelectedTurnCardFromServerInHandAdviceDiv = false;

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
    $scope.flopCards;



    $scope.allBooleanFunctionResults = [];



    //functions
    $scope.selectCard = function(id) {
        var cardButtonToDisable = "disable_" + id;
        $scope[cardButtonToDisable] = true;

        if($scope.firstCardSelected === false) {
            $scope.firstCard = id;
            if($scope.street === "Select turncard") {
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
                $scope.disableOkButton = false;
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

        $scope.firstCardSelected = false;
        $scope.secondCardSelected = false;
        $scope.disableOkButton = true;
        $scope.disableResetButton = true;

//        $http.get('/resource/').success(function(data) {
//          alert(JSON.stringify(data));
//        })
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
            var lengthOfServerCardList = selectedCardsInPreviousStreets.length;
            switch(lengthOfServerCardList) {
                case 2:
                    var card1rank = selectedCardsInPreviousStreets[0].rank;
                    var card1suit = selectedCardsInPreviousStreets[0].suit;
                    var card2rank = selectedCardsInPreviousStreets[1].rank;
                    var card2suit = selectedCardsInPreviousStreets[1].suit;

                    var card1 = "disable_" + card1rank + card1suit;
                    var card2 = "disable_" + card2rank + card2suit;
                    break;
                case 5:
                    var card1rank = selectedCardsInPreviousStreets[0].rank;
                    var card1suit = selectedCardsInPreviousStreets[0].suit;
                    var card2rank = selectedCardsInPreviousStreets[1].rank;
                    var card2suit = selectedCardsInPreviousStreets[1].suit;
                    var card3rank = selectedCardsInPreviousStreets[2].rank;
                    var card3suit = selectedCardsInPreviousStreets[2].suit;
                    var card4rank = selectedCardsInPreviousStreets[3].rank;
                    var card4suit = selectedCardsInPreviousStreets[3].suit;
                    var card5rank = selectedCardsInPreviousStreets[4].rank;
                    var card5suit = selectedCardsInPreviousStreets[4].suit;

                    var card1 = "disable_" + card1rank + card1suit;
                    var card2 = "disable_" + card2rank + card2suit;
                    var card3 = "disable_" + card3rank + card3suit;
                    var card4 = "disable_" + card4rank + card4suit;
                    var card5 = "disable_" + card5rank + card5suit;
                    break;
                case 6:
                    alert("to implement: turn selected");
                default:
                    alert("List with weird number of cards");
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

            $scope[card1] = !trueOrFalse;
            $scope[card2] = !trueOrFalse;
            $scope[card3] = !trueOrFalse;
            $scope[card4] = !trueOrFalse;
            $scope[card5] = !trueOrFalse;
        }
    }

    $scope.submitCardsToServer = function() {
        switch($scope.street) {
            case "Select holecards":
                $scope.submitHoleCards();
                break;
            case "Select flopcards":
                $scope.submitFlopCards();
                //alert("To implement, flop submit function");
                break;
            default:
                alert("This is the default")
        }
    }

    $scope.submitHoleCards = function() {
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
            $scope.street = "Select flopcards";
            $scope.reset();
        }).error(function() {
            alert("Failed to submit holecards");
        });
    }

    $scope.submitFlopCards = function() {
        setCorrectPropertiesForJsonToSendToServer();
        $scope.flopCards = [$scope.selectedCard1, $scope.selectedCard2, $scope.selectedCard3];
        $http.post('/postFlopCards/', $scope.flopCards).success(function(data) {
            $scope.selectedHoleCard1FromServer = data[0];
            $scope.selectedHoleCard1FromServer.rank = convertRankFromIntegerToRank(data[0].rank);
            $scope.selectedHoleCard2FromServer = data[1];
            $scope.selectedHoleCard2FromServer.rank = convertRankFromIntegerToRank(data[1].rank);
            $scope.selectedFlopCard1FromServer = data[2];
            $scope.selectedFlopCard1FromServer.rank = convertRankFromIntegerToRank(data[2].rank);
            $scope.selectedFlopCard2FromServer = data[3];
            $scope.selectedFlopCard2FromServer.rank = convertRankFromIntegerToRank(data[3].rank);
            $scope.selectedFlopCard3FromServer = data[4];
            $scope.selectedFlopCard3FromServer.rank = convertRankFromIntegerToRank(data[4].rank);

            $scope.listOfSelectedCardsFromServer = data;
            $scope.hideHoleCardsBeforeSentToServerDiv = true;
            $scope.hideFlopCardsBeforeSentToServerDiv = true;
            $scope.hideTurnCardBeforeSentToServerDiv = false;
            $scope.showSelectedFlopCardsFromServerInHandAdviceDiv = true;
            $scope.street = "Select turncard";
            $scope.reset();

            $http.get('/eije/').success(function(data) {
              $scope.allBooleanFunctionResults = data;
              //alert(JSON.stringify(data[1]));
            })

        }).error(function() {
            alert("Failed to submit flopcards");
        });


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