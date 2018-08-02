app.controller('courseFilterCtrl', ['BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.submit = function () {
            $uibModalInstance.close($scope.paramCourse);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            BranchService.fetchBranchMaster().then(function (data) {
                $scope.branches = data;
            });
            window.componentHandler.upgradeAllRegistered();
       }, 800);

    }]);