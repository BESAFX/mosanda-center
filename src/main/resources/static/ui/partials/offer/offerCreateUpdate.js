app.controller('offerCreateUpdateCtrl', ['OfferService', 'MasterService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'offer',
    function (OfferService, MasterService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, offer) {

        $scope.title = title;

        $scope.action = action;

        $scope.groupByBranch = function (item) {
            return item.branch.name;
        };

        $scope.clear = function () {
            $scope.offer = {};
            $scope.offer.paymentType = 'Cash';
            $scope.offer.price = 0.0;
            $scope.offer.discount = 0.0;
            $scope.offer.profit = 0.0;
            $scope.offer.premiumAmount = 0.0;
        };

        if (offer) {
            $scope.offer = offer;
        } else {
            $scope.clear();
        }

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    OfferService.create($scope.offer).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    OfferService.update($scope.offer).then(function (data) {
                        $scope.offer = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            MasterService.fetchMasterBranchCombo().then(function (data) {
                $scope.masters = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);