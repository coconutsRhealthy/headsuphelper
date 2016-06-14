var mainApp = angular.module("mainApp", []);

 mainApp.controller('pokerController', function($scope, $http) {
    $scope.allHoleCardsWrapper = [
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

    $scope.firstCardSelected = false;
    $scope.secondCardSelected = false;

    $scope.disableResetButton = true;
    $scope.disableOkButton = true;

    $scope.selectCard = function(id) {
        var cardButtonToDisable = "disable_" + id;
        $scope[cardButtonToDisable] = true;

        if($scope.firstCardSelected === false) {
            $scope.firstCard = id;
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
        }
    }

    $scope.reset = function() {
        $scope.firstCard = "";
        $scope.secondCard = "";
        $scope.thirdCard = "";

        if($scope.listOfSelectedHoleCardsFromServer === undefined) {
            $scope.setButtonFieldNgDisabledPropertiesToTrueOrFalse(false);
        }
        else {
            $scope.setButtonFieldNgDisabledPropertiesToTrueOrFalse(false, $scope.listOfSelectedHoleCardsFromServer);
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
            angular.forEach($scope.allHoleCardsWrapper, function(value, index){
                var eijeS = "disable_" + value.spades;
                var eijeC = "disable_" + value.clubs;
                var eijeD = "disable_" + value.diamonds;
                var eijeH = "disable_" + value.hearts;

                $scope[eijeS] = trueOrFalse;
                $scope[eijeC] = trueOrFalse;
                $scope[eijeD] = trueOrFalse;
                $scope[eijeH] = trueOrFalse;
            })
        }
        else {
            alert("eije de list werkt");
            angular.forEach($scope.allHoleCardsWrapper, function(value, index){
                var eijeS = "disable_" + value.spades;
                var eijeC = "disable_" + value.clubs;
                var eijeD = "disable_" + value.diamonds;
                var eijeH = "disable_" + value.hearts;

                $scope[eijeS] = trueOrFalse;
                $scope[eijeC] = trueOrFalse;
                $scope[eijeD] = trueOrFalse;
                $scope[eijeH] = trueOrFalse;
            })
        }
    }

    $scope.submitCardsToServer = function() {
        switch($scope.street) {
            case "Select holecards":
                $scope.submitHoleCards();
                break;
            case "Select flopcards":
                alert("To implement, flop submit function");
                break;
            default:
                alert("This is the default")
        }
    }

    $scope.submitHoleCards = function() {
        var rankCard1 = $scope.firstCard.substring(0,1);
        rankCard1 = getRankInteger(rankCard1);
        $scope.selectedHoleCard1.rank = rankCard1;

        var suitCard1 = $scope.firstCard.substring(1,2);
        $scope.selectedHoleCard1.suit = suitCard1;

        var rankCard2 = $scope.secondCard.substring(0,1);
        rankCard2 = getRankInteger(rankCard2);
        $scope.selectedHoleCard2.rank = rankCard2;

        var suitCard2 = $scope.secondCard.substring(1,2);
        $scope.selectedHoleCard2.suit = suitCard2;

        $scope.holeCards = [$scope.selectedHoleCard1, $scope.selectedHoleCard2];
//        alert(JSON.stringify($scope.holeCards));

        $http.post('/postHoleCards/', $scope.holeCards).success(function(data) {
//            alert(JSON.stringify(data[0]));
            $scope.selectedHoleCard1FromServer = data[0];
            $scope.selectedHoleCard2FromServer = data[1];
            $scope.listOfSelectedHoleCardsFromServer = data;
            $scope.hideHoleCardsBeforeSentToServerDiv = true;
            $scope.hideFlopCardsBeforeSentToServerDiv = false;
            $scope.street = "Select flopcards";
            $scope.reset();
        }).error(function() {
            alert("error");
        });
    }


    function getCardRank(card){
        var rank = card.concat(0,1);
    }

    function getCardSuit(card) {
        var suit = card.concat(1,1);
    }

    $scope.selectedHoleCard1 = {};
    $scope.selectedHoleCard2 = {};
    $scope.holeCards = [];

    function getRankInteger(rankCard1) {
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

    $scope.street = "Select holecards";
    $scope.hideHoleCardsBeforeSentToServerDiv = false;
    $scope.hideFlopCardsBeforeSentToServerDiv = true;


 });