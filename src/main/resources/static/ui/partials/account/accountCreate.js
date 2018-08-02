app.controller('accountCreateCtrl', ['StudentService', 'CourseService', 'AccountService', 'ModalProvider', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', '$uibModal',
    function (StudentService, CourseService, AccountService, ModalProvider, $scope, $rootScope, $timeout, $log, $uibModalInstance, $uibModal) {

        $scope.buffer = {};

        $scope.account = {};

        $scope.students = [];

        $scope.clear = function () {
            $scope.account = {};
            $scope.account.registerDate = $rootScope.now;
            $scope.account.paymentType = 'Cash';
            $scope.account.price = 0.0;
            $scope.account.discount = 0.0;
            $scope.account.profit = 0.0;
            $scope.account.premiumAmount = 0.0;
        };

        $scope.newStudent = function () {
            ModalProvider.openStudentCreateModel().result.then(function (data) {
                $scope.students.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            }, function () {});
        };

        $scope.searchStudents = function ($select, $event) {

            // no event means first load!
            if (!$event) {
                $scope.pageStudent = 0;
                $scope.students = [];
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                $scope.pageStudent++;
            }

            var search = [];

            search.push('size=');
            search.push(10);
            search.push('&');

            search.push('page=');
            search.push($scope.pageStudent);
            search.push('&');

            search.push('branchId=');
            search.push($scope.account.course.master.branch.id);
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

            return StudentService.filter(search.join("")).then(function (data) {
                $scope.buffer.lastStudent = data.last;
                return $scope.students = $scope.students.concat(data.content);
            });

        };

        $scope.submit = function () {
            AccountService.create($scope.account).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            $scope.clear();
            CourseService.fetchCourseMasterBranchCombo().then(function (data) {
                $scope.courses = data;
            });
            window.componentHandler.upgradeAllRegistered();
       }, 800);

    }]);