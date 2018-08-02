app.controller('offerDetailsCtrl', ['OfferService', 'CallService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'offer',
    function (OfferService, CallService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, offer) {

        $scope.offer = offer;
        $scope.refreshOffer = function () {
          OfferService.findOne($scope.offer.id).then(function (data) {
              $scope.offer = data;
          })
        };
        $scope.refreshOfferCalls = function () {
            CallService.findByOffer($scope.offer.id).then(function (data) {
               $scope.offer.calls = data;
            });
        };
        $scope.callOffer = function () {
            ModalProvider.openCallCreateModel($scope.offer).result.then(function (call) {
                return $scope.offer.calls.push(call);
            }, function () {});
        };
        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
       }, 800);

    }]);