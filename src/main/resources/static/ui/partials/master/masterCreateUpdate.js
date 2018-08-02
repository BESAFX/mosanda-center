app.controller('masterCreateUpdateCtrl', ['MasterCategoryService' ,'BranchService', 'MasterService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'master',
    function (MasterCategoryService, BranchService, MasterService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, master) {

        $scope.master = master;

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    MasterService.create($scope.master).then(function (data) {
                        $uibModalInstance.close(data);
                    });
                    break;
                case 'update' :
                    MasterService.update($scope.master).then(function (data) {
                        $scope.master = data;
                        $scope.form.$setPristine();
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            MasterCategoryService.findAllCombo().then(function (data) {
                $scope.masterCategories = data;
            });
            BranchService.fetchBranchCombo().then(function (data) {
                $scope.branches = data;
            });
       }, 800);

    }]);