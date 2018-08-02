app.controller('bankCreateUpdateCtrl', ['BranchService', 'BankService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'bank',
    function (BranchService, BankService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, bank) {

        $scope.title = title;

        $scope.action = action;

        $scope.bank = bank;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    BankService.create($scope.bank).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    BankService.update($scope.bank).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);