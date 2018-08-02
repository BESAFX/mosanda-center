app.controller('accountPaymentFilterCtrl', ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function ($scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageAccountPayment.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageAccountPayment.page = $scope.pageAccountPayment.currentPage - 1;
            $uibModalInstance.close($scope.paramAccountPayment);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
       }, 800);

    }]);