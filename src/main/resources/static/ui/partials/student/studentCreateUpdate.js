app.controller('studentCreateUpdateCtrl', ['BranchService', 'StudentService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', '$uibModal', 'title', 'action', 'student',
    function (BranchService, StudentService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, $uibModal, title, action, student) {

        $scope.student = student;

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    StudentService.create($scope.student).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    StudentService.update($scope.student).then(function (data) {
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