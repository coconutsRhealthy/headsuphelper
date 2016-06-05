var mainApp = angular.module("mainApp", []);

 mainApp.controller('studentController', function($scope, $http) {
    $scope.allHoleCardsWrapper = {
       allHoleCards:[
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
       ]
    };

    $scope.firstHoleCardSelected = false;
    $scope.disableResetButton = true;
    $scope.disableOkButton = true;

    $scope.setHoleCard = function(id) {
        if($scope.firstHoleCardSelected === false) {
            $scope.firstHoleCard = id;
            $scope.firstHoleCardSelected = true;
            $scope.disableResetButton = false;
        }
        else {
            $scope.secondHoleCard = id;
            $scope.disabledS = true;
            $scope.disabledC = true;
            $scope.disabledD = true;
            $scope.disabledH = true;
            $scope.disableOkButton = false;
        }
    }

    $scope.reset = function() {
        $scope.firstHoleCard = "";
        $scope.secondHoleCard = "";
        $scope.allHoleCardsWrapper = {
           allHoleCards:[
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
           ]
        };
        $scope.disabledS = false;
        $scope.disabledC = false;
        $scope.disabledD = false;
        $scope.disabledH = false;
        $scope.firstHoleCardSelected = false;
        $scope.disableOkButton = true;
        $scope.disableResetButton = true;

        $http.get('/resource/').success(function(data) {
          alert(JSON.stringify(data));
        })
    }



    $scope.submitHoleCardsToServer = function() {
        var rankCard1 = $scope.firstHoleCard.substring(0,1);
        rankCard1 = getRankInteger(rankCard1);
        $scope.selectedHoleCard1.rank = rankCard1;

        var suitCard1 = $scope.firstHoleCard.substring(1,2);
        $scope.selectedHoleCard1.suit = suitCard1;

        var rankCard2 = $scope.secondHoleCard.substring(0,1);
        rankCard2 = getRankInteger(rankCard2);
        $scope.selectedHoleCard2.rank = rankCard2;

        var suitCard2 = $scope.secondHoleCard.substring(1,2);
        $scope.selectedHoleCard2.suit = suitCard2;


        $scope.holeCards = [$scope.selectedHoleCard1, $scope.selectedHoleCard2];

        alert(JSON.stringify($scope.holeCards));

        $http.post('/bertus/', $scope.holeCards).success(function(data) {
            alert(JSON.stringify(data));
            //$scope.rs.name = '';
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





    $scope.rs1 = {};
    $scope.rs2 = {};
    $scope.eije = [];

    $scope.addNewRailwayStation = function(rs1, rs2) {
        $scope.eije = [rs1, rs2];
        alert(JSON.stringify($scope.eije));
        $http.post('/add/', $scope.eije).success(function(data) {
            alert(JSON.stringify(data));
            //$scope.rs.name = '';
        }).error(function() {
            alert("error");
        });
    };


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


 });