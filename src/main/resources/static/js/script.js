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


    $scope.rs = {};

    $scope.addNewRailwayStation = function(rs) {

        alert(JSON.stringify(rs));

        $http.post('/add/', rs).success(function() {
            $scope.rs.name = '';
        }).error(function() {
            alert("error");
        });
    };


//    $scope.SendData = function () {
//       // use $.param jQuery function to serialize data from JSON
//        var data = $.param({
//            fName: "Lennart",
//            lName: "Popma"
//        });
//
//        var config = {
//            headers : {
//                'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'
//            }
//        }
//
//        $http.post('/aappost/', data, config)
//        .success(function (data) {
//            alert("hallo");
////            $scope.PostDataResponse = data;
//        })
//        .error(function (data, config) {
//            alert("error");
////            $scope.ResponseDetails = "Data: " + data +
////                "<hr />status: " + status +
////                "<hr />headers: " + header +
////                "<hr />config: " + config;
//        });
//    };




 });