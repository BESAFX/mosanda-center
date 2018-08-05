app.controller('studentDetailsCtrl', ['StudentService', 'AccountService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$uibModalInstance', '$uibModal', 'student',
    function (StudentService, AccountService, ModalProvider, $scope, $rootScope, $timeout, $uibModalInstance, $uibModal, student) {

        $scope.student = student;

        $scope.refreshStudent = function () {
            StudentService.findOne($scope.student.id).then(function (data) {
                $scope.student = data;
            })
        };

        $scope.refreshAccounts = function () {
            AccountService.findByStudent($scope.student).then(function (data) {
                $scope.account.accountAttaches = data;
            })
        };

        $scope.newAccount = function () {
            ModalProvider.openAccountCreateModel($scope.student).result.then(function (data) {
                return $scope.student.account.push(data);
            });
        };

        $scope.deleteAccount = function (account) {
            $rootScope.showConfirmNotify("التسجيل", "هل تود حذف التسجيل فعلاً؟", "error", "fa-ban", function () {
                AccountService.remove(account).then(function (data) {
                    var index = $scope.student.accounts.indexOf(account);
                    $scope.student.accounts.splice(index, 1);
                });
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
       }, 800);

    }]);