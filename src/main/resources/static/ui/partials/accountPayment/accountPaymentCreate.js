app.controller('accountPaymentCreateCtrl', ['BranchService', 'AccountPaymentService', 'AccountService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (BranchService, AccountPaymentService, AccountService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, account) {

        $scope.buffer = {};

        $scope.account = account;

        $scope.accounts = [];

        $scope.accountPayment = {};

        $scope.accountPayment.account = account;

        $scope.newAccount = function () {
            ModalProvider.openAccountCreateModel().result.then(function (data) {
                $scope.accounts.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };

        $scope.searchAccounts = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.pageAccount = 0;
                $scope.accounts = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                $scope.pageAccount++;
            }

            var search = [];

            search.push('size=');
            search.push(10);
            search.push('&');

            search.push('page=');
            search.push($scope.pageAccount);
            search.push('&');

            search.push('branchId=');
            search.push($scope.accountPayment.account.course.master.branch.id);
            search.push('&');

            search.push('fullName=');
            search.push($select.search);
            search.push('&');

            search.push('identityNumber=');
            search.push($select.search);
            search.push('&');

            search.push('mobile=');
            search.push($select.search);
            search.push('&');

            search.push('filterCompareType=or');

            return AccountService.filterWithInfo(search.join("")).then(function (data) {
                $scope.buffer.lastAccount = data.last;
                return $scope.accounts = $scope.accounts.concat(data.content);
            });

        };

        $scope.submit = function () {
            AccountPaymentService.create($scope.accountPayment).then(function (data) {
                AccountPaymentService.findOne(data.id).then(function (value) {
                    $uibModalInstance.close(value);
                });
            });
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