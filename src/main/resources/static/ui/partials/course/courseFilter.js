app.controller('courseFilterCtrl', ['BranchService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title',
    function (BranchService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title) {

        $scope.modalTitle = title;

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageCourse.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageCourse.page = $scope.pageCourse.currentPage - 1;
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