app.controller('studentCreateBatchCtrl', ['BranchService', 'StudentService', '$scope', '$rootScope', '$timeout', '$filter', '$uibModalInstance',
    function (BranchService, StudentService, $scope, $rootScope, $timeout, $filter, $uibModalInstance) {

        $scope.buffer = {};

        $scope.buffer.excelStudents = [];

        $scope.students = [];

        $scope.branches = [];

        $scope.$watch(function ($scope) {
            return $scope.buffer.excelStudents;
        }, function (newVal) {
            angular.forEach(newVal, function (row) {
                var student = {};
                student.contact = {};
                student.contact.firstName = row['الاسم الأول'];
                student.contact.secondName = row['الاسم الثاني'];
                student.contact.thirdName = row['الاسم الثالث'];
                student.contact.forthName = row['الاسم الرابع'];
                student.contact.mobile = row['الجوال'];
                student.contact.phone = row['الهاتف'];
                student.contact.identityNumber = row['رقم البطاقة'];
                student.contact.address = row['العنوان'];
                student.branch = $filter('filter')($scope.branches, { name: row['الفرع'] })[0];
                $scope.students.push(student);
            });
        }, true);

        $scope.clear = function () {
            $scope.form.$setPristine();
            $scope.buffer = {};
            $scope.buffer.excelStudents = [];
            $scope.students = [];
        };

        $scope.uploadExcelFile = function () {
            $scope.clear();
            document.getElementById('excelUploader').click();
        };

        $scope.submit = function () {
            StudentService.createBatch($scope.students).then(function (data) {
                $uibModalInstance.close(data);
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