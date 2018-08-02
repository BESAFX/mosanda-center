app.controller('studentFilterCtrl', ['$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance',
    function ($scope, $rootScope, $timeout, $log, $uibModalInstance) {

        $scope.addSortBy = function () {
            var sortBy = {};
            $scope.pageStudent.sorts.push(sortBy);
        };

        $scope.submit = function () {
            $scope.pageStudent.page = $scope.pageStudent.currentPage - 1;
            $uibModalInstance.close($scope.paramStudent);
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
       }, 800);

    }]);