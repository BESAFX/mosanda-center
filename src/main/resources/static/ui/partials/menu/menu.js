app.controller("menuCtrl", [
    'CompanyService',
    'HistoryService',
    'BranchService',
    'MasterService',
    'MasterCategoryService',
    'OfferService',
    'CallService',
    'CourseService',
    'StudentService',
    'AccountService',
    'AccountAttachService',
    'AttachTypeService',
    'AccountPaymentService',
    'BankService',
    'BankTransactionService',
    'PersonService',
    'TeamService',
    'ModalProvider',
    '$scope',
    '$rootScope',
    '$state',
    '$uibModal',
    '$timeout',
    function (CompanyService,
              HistoryService,
              BranchService,
              MasterService,
              MasterCategoryService,
              OfferService,
              CallService,
              CourseService,
              StudentService,
              AccountService,
              AccountAttachService,
              AttachTypeService,
              AccountPaymentService,
              BankService,
              BankTransactionService,
              PersonService,
              TeamService,
              ModalProvider,
              $scope,
              $rootScope,
              $state,
              $uibModal,
              $timeout) {

        $scope.$watch('toggleState', function (newValue, oldValue) {
            switch (newValue) {
                case 'menu': {
                    $scope.pageTitle = 'القائمة';
                    break;
                }
                case 'company': {
                    $scope.pageTitle = 'الشركة';
                    break;
                }
                case 'branch': {
                    $scope.pageTitle = 'الفروع';
                    break;
                }
                case 'master': {
                    $scope.pageTitle = 'التخصصات';
                    break;
                }
                case 'offer': {
                    $scope.pageTitle = 'العروض';
                    break;
                }
                case 'course': {
                    $scope.pageTitle = 'الدورات';
                    break;
                }
                case 'student': {
                    $scope.pageTitle = 'الطلاب';
                    break;
                }
                case 'account': {
                    $scope.pageTitle = 'التسجيل';
                    break;
                }
                case 'accountPayment': {
                    $scope.pageTitle = 'سندات القبض';
                    break;
                }
                case 'bankTransaction': {
                    $scope.pageTitle = 'المعاملات المالية';
                    break;
                }
                case 'team': {
                    $scope.pageTitle = 'الصلاحيات';
                    break;
                }
                case 'person': {
                    $scope.pageTitle = 'المستخدمين';
                    break;
                }
                case 'profile': {
                    $scope.pageTitle = 'الملف الشخصي';
                    break;
                }
                case 'help': {
                    $scope.pageTitle = 'المساعدة';
                    break;
                }
                case 'about': {
                    $scope.pageTitle = 'عن البرنامج';
                    break;
                }
                case 'report': {
                    $scope.pageTitle = 'التقارير';
                    break;
                }
            }
            $timeout(function () {
                window.componentHandler.upgradeAllRegistered();
            }, 500);
        }, true);
        $scope.toggleState = 'menu';
        $scope.openStateMenu = function () {
            $scope.toggleState = 'menu';
            $rootScope.refreshGUI();
        };
        $scope.openStateCompany = function () {
            $scope.toggleState = 'company';
            $rootScope.refreshGUI();
        };
        $scope.openStateBranch = function () {
            $scope.toggleState = 'branch';
            $rootScope.refreshGUI();
            $scope.fetchBranchTableData();
        };
        $scope.openStateMaster = function () {
            $scope.toggleState = 'master';
            $rootScope.refreshGUI();
        };
        $scope.openStateOffer = function () {
            $scope.toggleState = 'offer';
            $rootScope.refreshGUI();
        };
        $scope.openStateCourse = function () {
            $scope.toggleState = 'course';
            $rootScope.refreshGUI();
        };
        $scope.openStateStudent = function () {
            $scope.toggleState = 'student';
            $rootScope.refreshGUI();
        };
        $scope.openStateAccount = function () {
            $scope.toggleState = 'account';
            $rootScope.refreshGUI();
        };
        $scope.openStateAccountPayment = function () {
            $scope.toggleState = 'accountPayment';
            $rootScope.refreshGUI();
        };
        $scope.openStateBankTransaction = function () {
            $scope.toggleState = 'bankTransaction';
            $scope.searchBankTransactions({});
            $rootScope.refreshGUI();
        };
        $scope.openStateTeam = function () {
            $scope.toggleState = 'team';
            $scope.fetchTeamTableData();
            $rootScope.refreshGUI();
        };
        $scope.openStatePerson = function () {
            $scope.toggleState = 'person';
            $scope.fetchPersonTableData();
            $rootScope.refreshGUI();
        };
        $scope.openStateProfile = function () {
            $scope.toggleState = 'profile';
            $rootScope.refreshGUI();
        };
        $scope.openStateHelp = function () {
            $scope.toggleState = 'help';
            $rootScope.refreshGUI();
        };
        $scope.openStateAbout = function () {
            $scope.toggleState = 'about';
            $rootScope.refreshGUI();
        };
        $scope.openStateReport = function () {
            $scope.toggleState = 'report';
            $rootScope.refreshGUI();
        };
        $scope.menuOptionsBody = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/admin.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>الإدارة</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_CREATE']);
                },
                click: function ($itemScope, $event, value) {

                },
                children: [
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/company.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>بيانات الشركة</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_COMPANY_UPDATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateCompany();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/branch.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل الفروع</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_CREATE', 'ROLE_BRANCH_UPDATE', 'ROLE_BRANCH_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateBranch();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>فرع جديد</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newBranch();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/person.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل المستخدمين</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_CREATE', 'ROLE_PERSON_UPDATE', 'ROLE_PERSON_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStatePerson();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>مستخدم جديد</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newPerson();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/team.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل الصلاحيات</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_CREATE', 'ROLE_TEAM_UPDATE', 'ROLE_TEAM_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateTeam();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>صلاحيات جديدة</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newTeam();
                        }
                    }
                ]
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/register.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>التسجيل</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_CREATE']);
                },
                click: function ($itemScope, $event, value) {

                },
                children: [
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/master.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل التخصصات</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_MASTER_CREATE', 'ROLE_MASTER_UPDATE', 'ROLE_MASTER_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateMaster();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>تخصص جديد</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_MASTER_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newMaster();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/offer.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل العروض</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_OFFER_CREATE', 'ROLE_OFFER_UPDATE', 'ROLE_OFFER_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateOffer();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>عرض جديد</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_OFFER_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newOffer();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/course.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل الدورات</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_COURSE_CREATE', 'ROLE_COURSE_UPDATE', 'ROLE_COURSE_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateCourse();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>دورة جديدة</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_COURSE_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newCourse();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/account.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سجل الطلاب</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_CREATE', 'ROLE_ACCOUNT_UPDATE', 'ROLE_ACCOUNT_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateAccount();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>طالب جديد</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newAccount();
                        }
                    }
                ]
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/calculate.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>المالية</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_CREATE']);
                },
                click: function ($itemScope, $event, value) {

                },
                children: [
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/accountPayment.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سندات القبض</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PAYMENT_CREATE', 'ROLE_PAYMENT_UPDATE', 'ROLE_PAYMENT_DELETE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.openStateAccountPayment();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>سند قبض جديد</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PAYMENT_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newPayment();
                        }
                    },
                    {
                        html: '<div class="drop-menu">' +
                        '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                        '<span>حساب بنك جديد</span>' +
                        '</div>',
                        enabled: function () {
                            return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_CREATE']);
                        },
                        click: function ($itemScope, $event, value) {
                            $scope.newBank();
                        }
                    }
                ]
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Company                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $rootScope.selectedCompany = {};
        $rootScope.selectedCompany.options = {};
        $rootScope.sms = {};
        $scope.submitCompany = function () {
            $rootScope.selectedCompany.options = JSON.stringify($rootScope.selectedCompany.options);
            CompanyService.update($rootScope.selectedCompany).then(function (data) {
                $rootScope.selectedCompany = data;
                $rootScope.selectedCompany.options = JSON.parse(data.options);
            });
        };
        $scope.updateCompanyOptions = function () {

            var param = [];

            param.push("smsUserName=");
            param.push($rootScope.selectedCompany.options.smsUserName);
            param.push("&");

            param.push("smsPassword=");
            param.push($rootScope.selectedCompany.options.smsPassword);
            param.push("&");

            param.push("vatFactor=");
            param.push($rootScope.selectedCompany.options.vatFactor);
            param.push("&");

            param.push("logo=");
            param.push($rootScope.selectedCompany.options.logo);
            param.push("&");

            param.push("background=");
            param.push($rootScope.selectedCompany.options.background);
            param.push("&");

            param.push("reportTitle=");
            param.push($rootScope.selectedCompany.options.reportTitle);
            param.push("&");

            param.push("reportSubTitle=");
            param.push($rootScope.selectedCompany.options.reportSubTitle);
            param.push("&");

            param.push("reportFooter=");
            param.push($rootScope.selectedCompany.options.reportFooter);
            param.push("&");

            param.push("tokenLengthInHours=");
            param.push($rootScope.selectedCompany.options.tokenLengthInHours);
            param.push("&");

            CompanyService.updateOptions(param.join("")).then(function (value) {

            });
        };
        $scope.browseCompanyLogo = function () {
            document.getElementById('uploader-company').click();
        };
        $scope.uploadCompanyLogo = function (files) {
            CompanyService.uploadCompanyLogo(files[0]).then(function (data) {
                $rootScope.selectedCompany.logo = data;
                CompanyService.update($rootScope.selectedCompany).then(function (data) {
                    $rootScope.selectedCompany = data;
                });
            });
        };
        $rootScope.findSmsCredit = function () {

        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Branch                                                                                                     *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.branchesDetails = [];
        $scope.fetchBranchTableData = function () {
            BranchService.fetchTableData().then(function (data) {
                $scope.branchesDetails = data;
            });
        };
        $scope.newBranch = function () {
            ModalProvider.openBranchCreateModel().result.then(function (data) {
                return $scope.branchesDetails.splice(0, 0, data);
                $rootScope.refreshTable();
            });
        };
        $scope.duplicate = function (branch) {
            BranchService.duplicate(branch).then(function (data) {
                return $scope.branchesDetails.splice(0, 0, data);
                $rootScope.refreshTable();
            });
        };
        $scope.deleteBranch = function (branch) {
            ModalProvider.openConfirmModel("حذف الفروع", "delete", "هل تود حذف الفرع فعلاً؟").result.then(function (value) {
                if (value) {
                    BranchService.remove(branch.id).then(function () {
                        var index = $scope.branchesDetails.indexOf(branch);
                        $scope.branchesDetails.splice(index, 1);
                    });
                }
            });
        };
        $scope.rowMenuBranch = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newBranch();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBranchUpdateModel($itemScope.branch);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/copy.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>نسخ</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.duplicate($itemScope.branch);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BRANCH_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteBranch($itemScope.branch);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Master Category                                                                                            *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.masterCategories = [];
        $scope.fetchMasterCategories = function () {
            MasterCategoryService.findAll().then(function (data) {
                $scope.masterCategories = data;
            });
        };
        $scope.newMasterCategory = function () {
            ModalProvider.openMasterCategoryCreateModel().result.then(function (data) {
                $scope.masterCategories.splice(0, 0, data);
                $rootScope.refreshTable();
            }, function () {});
        };
        $scope.deleteMasterCategory = function (masterCategory) {
            ModalProvider.openConfirmModel("حذف التصنيفات", "delete", "هل تود حذف التصنيف فعلاً؟").result.then(function (value) {
                if (value) {
                    MasterCategoryService.remove(masterCategory.id).then(function () {
                        var index = $scope.masterCategories.indexOf(masterCategory);
                        $scope.masterCategories.splice(index, 1);
                    });
                }
            })
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Master                                                                                                     *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.paramMaster = {};
        $scope.itemsMaster = [];
        $scope.itemsMaster.push({'id': 2, 'type': 'title', 'name': 'التخصصات'});
        $scope.newMaster = function () {
            ModalProvider.openMasterCreateModel().result.then(function (data) {
                $scope.masters.splice(0, 0, data);
                $rootScope.refreshTable();
            }, function () {
            });
        };
        $scope.deleteMaster = function (master) {
            ModalProvider.openConfirmModel("حذف التخصصات", "delete", "هل تود حذف التخصص فعلاً؟").result.then(function (value) {
                if (value) {
                    MasterService.remove(master.id).then(function () {
                        var index = $scope.masters.indexOf(master);
                        $scope.masters.splice(index, 1);
                    });
                }
            })
        };
        $scope.openFilterMaster = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/master/masterFilter.html',
                controller: 'masterFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى التخصصات';
                    }
                }
            });

            modalInstance.result.then(function (paramMaster) {
                $scope.paramMaster = paramMaster;
                $scope.searchMaster($scope.paramMaster);
            }, function () {
            });
        };
        $scope.searchMaster = function (paramMaster) {

            var search = [];

            if (paramMaster.name) {
                search.push('name=');
                search.push(paramMaster.name);
                search.push('&');
            }
            if (paramMaster.codeFrom) {
                search.push('codeFrom=');
                search.push(paramMaster.codeFrom);
                search.push('&');
            }
            if (paramMaster.codeTo) {
                search.push('codeTo=');
                search.push(paramMaster.codeTo);
                search.push('&');
            }
            if (paramMaster.branch) {
                search.push('branchId=');
                search.push(paramMaster.branch.id);
                search.push('&');
            }

            MasterService.filter(search.join("")).then(function (data) {
                $scope.masters = data;
                $scope.itemsMaster = [];
                $scope.itemsMaster.push({
                    'id': 2,
                    'type': 'title',
                    'name': 'التخصصات',
                    'style': 'font-weight:bold'
                });
                $scope.itemsMaster.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsMaster.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + paramMaster.branch.code + ' ] ' + paramMaster.branch.name
                });
            });

        };
        $scope.rowMenuMaster = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openMasterCreateModel();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openMasterUpdateModel($itemScope.master);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteMaster($itemScope.master);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Offer                                                                                                      *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.paramOffer = {};
        $scope.offers = [];
        $scope.offers.checkAll = false;
        $scope.itemsOffer = [];
        $scope.itemsOffer.push({'id': 2, 'type': 'title', 'name': 'العروض'});

        $scope.pageOffer = {};
        $scope.pageOffer.sorts = [];
        $scope.pageOffer.page = 0;
        $scope.pageOffer.totalPages = 0;
        $scope.pageOffer.currentPage = $scope.pageOffer.page + 1;
        $scope.pageOffer.currentPageString = ($scope.pageOffer.page + 1) + ' / ' + $scope.pageOffer.totalPages;
        $scope.pageOffer.size = 25;
        $scope.pageOffer.first = true;
        $scope.pageOffer.last = true;

        $scope.filterOffer = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/offer/offerFilter.html',
                controller: 'offerFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى العروض';
                    }
                }
            });
            modalInstance.result.then(function (paramOffer) {
                $scope.paramOffer = paramOffer;
                $scope.searchOffer($scope.paramOffer);
            }, function () {
            });
        };
        $scope.searchOffer = function (paramOffer) {
            var search = [];

            search.push('size=');
            search.push($scope.pageOffer.size);
            search.push('&');

            search.push('page=');
            search.push($scope.pageOffer.page);
            search.push('&');

            angular.forEach($scope.pageOffer.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });

            if (paramOffer.codeFrom) {
                search.push('codeFrom=');
                search.push(paramOffer.codeFrom);
                search.push('&');
            }
            if (paramOffer.codeTo) {
                search.push('codeTo=');
                search.push(paramOffer.codeTo);
                search.push('&');
            }
            if (paramOffer.dateFrom) {
                search.push('dateFrom=');
                search.push(paramOffer.dateFrom.getTime());
                search.push('&');
            }
            if (paramOffer.dateTo) {
                search.push('dateTo=');
                search.push(paramOffer.dateTo.getTime());
                search.push('&');
            }
            if (paramOffer.customerName) {
                search.push('customerName=');
                search.push(paramOffer.customerName);
                search.push('&');
            }
            if (paramOffer.customerIdentityNumber) {
                search.push('customerIdentityNumber=');
                search.push(paramOffer.customerIdentityNumber);
                search.push('&');
            }
            if (paramOffer.customerMobile) {
                search.push('customerMobile=');
                search.push(paramOffer.customerMobile);
                search.push('&');
            }
            if (paramOffer.priceFrom) {
                search.push('priceFrom=');
                search.push(paramOffer.priceFrom);
                search.push('&');
            }
            if (paramOffer.priceTo) {
                search.push('priceTo=');
                search.push(paramOffer.priceTo);
                search.push('&');
            }
            if (paramOffer.branch) {
                search.push('branch=');
                search.push(paramOffer.branch.id);
                search.push('&');
            }
            if (paramOffer.master) {
                search.push('master=');
                search.push(paramOffer.master.id);
                search.push('&');
            }
            if (paramOffer.person) {
                search.push('personId=');
                search.push(paramOffer.person.id);
                search.push('&');
            }
            OfferService.filter(search.join("")).then(function (data) {
                $scope.offers = data.content;

                $scope.pageOffer.currentPage = $scope.pageOffer.page + 1;
                $scope.pageOffer.first = data.first;
                $scope.pageOffer.last = data.last;
                $scope.pageOffer.number = data.number;
                $scope.pageOffer.numberOfElements = data.numberOfElements;
                $scope.pageOffer.size = data.size;
                $scope.pageOffer.totalElements = data.totalElements;
                $scope.pageOffer.totalPages = data.totalPages;
                $scope.pageOffer.currentPageString = ($scope.pageOffer.page + 1) + ' / ' + $scope.pageOffer.totalPages;

                $scope.itemsOffer = [];
                $scope.itemsOffer.push({'id': 2, 'type': 'title', 'name': 'العروض', 'style': 'font-weight:bold'});
                $scope.itemsOffer.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsOffer.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + paramOffer.branch.code + ' ] ' + paramOffer.branch.name
                });
                if (paramOffer.master) {
                    $scope.itemsOffer.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                    $scope.itemsOffer.push({
                        'id': 6,
                        'type': 'title',
                        'name': ' [ ' + paramOffer.master.code + ' ] ' + paramOffer.master.name
                    });
                }
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextOffersPage = function () {
            $scope.pageOffer.page++;
            $scope.searchOffer($scope.paramOffer);
        };
        $scope.selectPrevOffersPage = function () {
            $scope.pageOffer.page--;
            $scope.searchOffer($scope.paramOffer);
        };
        $scope.newOffer = function () {
            ModalProvider.openOfferCreateModel().result.then(function (data) {
                $scope.offers.splice(0, 0, data);
                $rootScope.refreshTable();
                ModalProvider.openConfirmModel("العروض", "print", "هل تود طباعة العرض ؟").result.then(function (value) {
                    if(value){
                        $scope.printOffer(data);
                    }
                })
            });
        };
        $scope.copyOffer = function (offer) {
            ModalProvider.openOfferCopyModel(offer).result.then(function (data) {
                $scope.offers.splice(0, 0, data);
                $rootScope.refreshTable();
                ModalProvider.openConfirmModel("العروض", "هل تود طباعة العرض ؟", "notification", "fa-info", function () {
                    $scope.printOffer(data);
                });
            });
        };
        $scope.printOffer = function (offer) {
            window.open('/report/OfferById/' + offer.id + '?exportType=PDF');
        };
        $scope.deleteOffer = function (offer) {
            ModalProvider.openConfirmModel("حذف العروض", "delete", "هل تود حذف العرض فعلاً؟").result.then(function (value) {
                if (value) {
                    OfferService.remove(offer.id).then(function () {
                        var index = $scope.offers.indexOf(offer);
                        $scope.offers.splice(index, 1);
                    });
                }
            })
        };
        $scope.callOffer = function (offer) {
            ModalProvider.openCallCreateModel(offer).result.then(function (call) {
                return offer.calls.push(call);
            }, function () {
            });
        };
        $scope.rowMenuOffer = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.newOffer();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/copy.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>نسخ...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.copyOffer($itemScope.offer);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openOfferUpdateModel($itemScope.offer);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteOffer($itemScope.offer);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/print.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>طباعة</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.printOffer($itemScope.offer);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/about.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>التفاصيل...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openOfferDetailsModel($itemScope.offer);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/call.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>نتيجة اتصال...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.callOffer($itemScope.offer);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/send.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>ارسال رسالة...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_SEND_SMS']);
                },
                click: function ($itemScope, $event, value) {
                    var selectedOffers = [];
                    angular.forEach($scope.offers, function (offer) {
                        if(offer.isSelected){
                            selectedOffers.push(offer);
                        }
                    });
                    if(selectedOffers.length > 0){
                        ModalProvider.openOfferSendMessageModel(selectedOffers);
                    }else{
                        $rootScope.showNotify('', 'فضلا قم باختيار عرض واحد على الأقل', 'error', '');
                    }
                }
            }
        ];
        $scope.checkAllOffers = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllOffers input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.offers, function (offer) {
                offer.isSelected = $scope.offers.checkAll;
            });
        };
        $scope.checkOffer = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllOffers').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllOffers').MaterialCheckbox.uncheck();
                }
            }
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Course                                                                                                     *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.paramCourse = {};
        $scope.courses = [];
        $scope.itemsCourse = [];
        $scope.itemsCourse.push({'id': 2, 'type': 'title', 'name': 'الدورات'});
        $scope.newCourse = function () {
            ModalProvider.openCourseCreateModel().result.then(function (data) {
                $scope.courses.splice(0, 0, data);
                $rootScope.refreshTable();
            }, function () {
            });
        };
        $scope.deleteCourse = function (course) {
            ModalProvider.openConfirmModel("حذف الدورات", "delete", "هل تود حذف الدورة فعلاً؟").result.then(function (value) {
                if (value) {
                    CourseService.remove(course.id).then(function () {
                        var index = $scope.courses.indexOf(course);
                        $scope.courses.splice(index, 1);
                    });
                }
            })
        };
        $scope.deleteAccounts = function (course) {
            ModalProvider.openConfirmModel("حذف طلاب الدورات", "delete", "هل تود حذف طلاب الدورة فعلاً؟").result.then(function (value) {
                if (value) {
                    AccountService.removeByCourse(course.id).then(function () {
                    });
                }
            })
        };
        $scope.deletePayments = function (course) {
            ModalProvider.openConfirmModel("حذف ايرادات الدورات", "delete", "هل تود حذف جميع سندات طلاب الدورة فعلاً؟").result.then(function (value) {
                if (value) {
                    AccountPaymentService.removeByCourse(course.id).then(function () {
                    });
                }
            })
        };
        $scope.openFilterCourse = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/course/courseFilter.html',
                controller: 'courseFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى الدورات';
                    }
                }
            });

            modalInstance.result.then(function (paramCourse) {
                $scope.paramCourse = paramCourse;
                $scope.searchCourse($scope.paramCourse);
            }, function () {
            });
        };
        $scope.searchCourse = function (paramCourse) {
            var search = [];
            if (paramCourse.instructor) {
                search.push('instructor=');
                search.push(paramCourse.instructor);
                search.push('&');
            }
            if (paramCourse.codeFrom) {
                search.push('codeFrom=');
                search.push(paramCourse.codeFrom);
                search.push('&');
            }
            if (paramCourse.codeTo) {
                search.push('codeTo=');
                search.push(paramCourse.codeTo);
                search.push('&');
            }
            if (paramCourse.branch) {
                search.push('branchId=');
                search.push(paramCourse.branch.id);
                search.push('&');
            }
            if (paramCourse.master) {
                search.push('masterId=');
                search.push(paramCourse.master.id);
                search.push('&');
            }

            CourseService.filter(search.join("")).then(function (data) {
                $scope.courses = data;
                $scope.itemsCourse = [];
                $scope.itemsCourse.push({'id': 2, 'type': 'title', 'name': 'الدورات', 'style': 'font-weight:bold'});
                $scope.itemsCourse.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsCourse.push({
                    'id': 4,
                    'type': 'title',
                    'name': ' [ ' + paramCourse.branch.code + ' ] ' + paramCourse.branch.name
                });
            });
        };
        $scope.rowMenuCourse = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.newCourse();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openCourseUpdateModel($itemScope.course);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف الدورة</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.delete($itemScope.course);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف الطلاب</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteAccounts($itemScope.course);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف الايرادات</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.deletePayments($itemScope.course);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Student                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.paramStudent = {};
        $scope.students = [];
        $scope.students.checkAll = false;
        $scope.itemsStudent = [];
        $scope.itemsStudent.push({'id': 2, 'type': 'title', 'name': 'سجل الطلاب'});

        $scope.pageStudent = {};
        $scope.pageStudent.sorts = [];
        $scope.pageStudent.page = 0;
        $scope.pageStudent.totalPages = 0;
        $scope.pageStudent.currentPage = $scope.pageStudent.page + 1;
        $scope.pageStudent.currentPageString = ($scope.pageStudent.page + 1) + ' / ' + $scope.pageStudent.totalPages;
        $scope.pageStudent.size = 25;
        $scope.pageStudent.first = true;
        $scope.pageStudent.last = true;

        $scope.openFilterStudent = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/student/studentFilter.html',
                controller: 'studentFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramStudent) {
                $scope.paramStudent = paramStudent;
                $scope.searchStudent($scope.paramStudent);
            }, function () {});
        };
        $scope.searchStudent = function (paramStudent) {
            var search = [];

            search.push('size=');
            search.push($scope.pageStudent.size);
            search.push('&');

            search.push('page=');
            search.push($scope.pageStudent.page);
            search.push('&');

            angular.forEach($scope.pageStudent.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });

            if (paramStudent.firstName) {
                search.push('firstName=');
                search.push(paramStudent.firstName);
                search.push('&');
            }
            if (paramStudent.secondName) {
                search.push('secondName=');
                search.push(paramStudent.secondName);
                search.push('&');
            }
            if (paramStudent.thirdName) {
                search.push('thirdName=');
                search.push(paramStudent.thirdName);
                search.push('&');
            }
            if (paramStudent.forthName) {
                search.push('forthName=');
                search.push(paramStudent.forthName);
                search.push('&');
            }
            if (paramStudent.fullName) {
                search.push('fullName=');
                search.push(paramStudent.fullName);
                search.push('&');
            }
            if (paramStudent.registerDateFrom) {
                search.push('registerDateFrom=');
                search.push(paramStudent.registerDateFrom.getTime());
                search.push('&');
            }
            if (paramStudent.registerDateTo) {
                search.push('registerDateTo=');
                search.push(paramStudent.registerDateTo.getTime());
                search.push('&');
            }
            if (paramStudent.studentIdentityNumber) {
                search.push('studentIdentityNumber=');
                search.push(paramStudent.studentIdentityNumber);
                search.push('&');
            }
            if (paramStudent.studentMobile) {
                search.push('studentMobile=');
                search.push(paramStudent.studentMobile);
                search.push('&');
            }
            if (paramStudent.branch) {
                search.push('branchId=');
                search.push(paramStudent.branch.id);
                search.push('&');
            }

            search.push('filterCompareType=');
            search.push('and');
            search.push('&');

            StudentService.filter(search.join("")).then(function (data) {
                $scope.students = data.content;

                $scope.pageStudent.currentPage = $scope.pageStudent.page + 1;
                $scope.pageStudent.first = data.first;
                $scope.pageStudent.last = data.last;
                $scope.pageStudent.number = data.number;
                $scope.pageStudent.numberOfElements = data.numberOfElements;
                $scope.pageStudent.size = data.size;
                $scope.pageStudent.totalElements = data.totalElements;
                $scope.pageStudent.totalPages = data.totalPages;
                $scope.pageStudent.currentPageString = ($scope.pageStudent.page + 1) + ' / ' + $scope.pageStudent.totalPages;

                $scope.itemsStudent = [];
                $scope.itemsStudent.push({'id': 1, 'type': 'title', 'name': 'سجل الطلاب', 'style': 'font-weight:bold'});
                $scope.itemsStudent.push({'id': 2, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsStudent.push({'id': 3, 'type': 'title', 'name': ' [ ' + paramStudent.branch.code + ' ] ' + paramStudent.branch.name});
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });

        };
        $scope.selectNextStudentsPage = function () {
            $scope.pageStudent.page++;
            $scope.searchStudent($scope.paramStudent);
        };
        $scope.selectPrevStudentsPage = function () {
            $scope.pageStudent.page--;
            $scope.searchStudent($scope.paramStudent);
        };
        $scope.newStudent = function () {
            ModalProvider.openStudentCreateModel().result.then(function (data) {
                $scope.students.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            }, function () {});
        };
        $scope.newStudentBatch = function () {
            ModalProvider.openStudentCreateBatchModel().result.then(function (data) {
                Array.prototype.push.apply($scope.students, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            }, function () {});
        };
        $scope.deleteStudent = function (student) {
            ModalProvider.openConfirmModel("حذف الطلاب", "delete", "هل تود حذف التسجيل فعلاً؟").result.then(function (value) {
                if (value) {
                    StudentService.remove(student.id).then(function () {
                        var index = $scope.students.indexOf(student);
                        $scope.students.splice(index, 1);
                    });
                }
            })
        };
        $scope.rowMenuStudent = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_STUDENT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newStudent();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_STUDENT_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openStudentUpdateModel($itemScope.student);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_STUDENT_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteStudent($itemScope.student);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/about.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>التفاصيل</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openStudentDetailsModel($itemScope.student);
                }
            }
        ];
        $scope.checkAllStudents = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllStudents input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.students, function (account) {
                account.isSelected = $scope.students.checkAll;
            });
        };
        $scope.checkStudent = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllStudents').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllStudents').MaterialCheckbox.uncheck();
                }
            }
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Account                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.paramAccount = {};
        $scope.accountToUploadAttaches = {};
        $scope.accounts = [];
        $scope.accounts.checkAll = false;
        $scope.itemsAccount = [];
        $scope.itemsAccount.push({'id': 2, 'type': 'title', 'name': 'تسجيل الطلاب'});

        $scope.pageAccount = {};
        $scope.pageAccount.sorts = [];
        $scope.pageAccount.page = 0;
        $scope.pageAccount.totalPages = 0;
        $scope.pageAccount.currentPage = $scope.pageAccount.page + 1;
        $scope.pageAccount.currentPageString = ($scope.pageAccount.page + 1) + ' / ' + $scope.pageAccount.totalPages;
        $scope.pageAccount.size = 25;
        $scope.pageAccount.first = true;
        $scope.pageAccount.last = true;

        $scope.openFilterAccount = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountFilter.html',
                controller: 'accountFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى التسجيل';
                    }
                }
            });

            modalInstance.result.then(function (paramAccount) {
                $scope.paramAccount = paramAccount;
                $scope.searchAccount($scope.paramAccount);
            }, function () {});
        };
        $scope.searchAccount = function (paramAccount) {
            var search = [];

            search.push('size=');
            search.push($scope.pageAccount.size);
            search.push('&');

            search.push('page=');
            search.push($scope.pageAccount.page);
            search.push('&');

            angular.forEach($scope.pageAccount.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });

            if (paramAccount.firstName) {
                search.push('firstName=');
                search.push(paramAccount.firstName);
                search.push('&');
            }
            if (paramAccount.secondName) {
                search.push('secondName=');
                search.push(paramAccount.secondName);
                search.push('&');
            }
            if (paramAccount.thirdName) {
                search.push('thirdName=');
                search.push(paramAccount.thirdName);
                search.push('&');
            }
            if (paramAccount.forthName) {
                search.push('forthName=');
                search.push(paramAccount.forthName);
                search.push('&');
            }
            if (paramAccount.fullName) {
                search.push('fullName=');
                search.push(paramAccount.fullName);
                search.push('&');
            }
            if (paramAccount.registerDateFrom) {
                search.push('registerDateFrom=');
                search.push(paramAccount.registerDateFrom.getTime());
                search.push('&');
            }
            if (paramAccount.registerDateTo) {
                search.push('registerDateTo=');
                search.push(paramAccount.registerDateTo.getTime());
                search.push('&');
            }
            if (paramAccount.studentIdentityNumber) {
                search.push('studentIdentityNumber=');
                search.push(paramAccount.studentIdentityNumber);
                search.push('&');
            }
            if (paramAccount.studentMobile) {
                search.push('studentMobile=');
                search.push(paramAccount.studentMobile);
                search.push('&');
            }
            if (paramAccount.branch) {
                search.push('branchIds=');
                var branchIds = [];
                branchIds.push(paramAccount.branch.id);
                search.push(branchIds);
                search.push('&');
            }
            if (paramAccount.master) {
                search.push('masterIds=');
                var masterIds = [];
                masterIds.push(paramAccount.master.id);
                search.push(masterIds);
                search.push('&');
            }
            if (paramAccount.course) {
                search.push('courseIds=');
                var courseIds = [];
                courseIds.push(paramAccount.course.id);
                search.push(courseIds);
                search.push('&');
            }

            search.push('filterCompareType=');
            search.push('and');
            search.push('&');

            AccountService.filterWithInfo(search.join("")).then(function (data) {
                $scope.accounts = data.content;

                $scope.pageAccount.currentPage = $scope.pageAccount.page + 1;
                $scope.pageAccount.first = data.first;
                $scope.pageAccount.last = data.last;
                $scope.pageAccount.number = data.number;
                $scope.pageAccount.numberOfElements = data.numberOfElements;
                $scope.pageAccount.size = data.size;
                $scope.pageAccount.totalElements = data.totalElements;
                $scope.pageAccount.totalPages = data.totalPages;
                $scope.pageAccount.currentPageString = ($scope.pageAccount.page + 1) + ' / ' + $scope.pageAccount.totalPages;

                $scope.itemsAccount = [];
                $scope.itemsAccount.push({'id': 2, 'type': 'title', 'name': 'تسجيل الطلاب', 'style': 'font-weight:bold'});
                $scope.itemsAccount.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsAccount.push({'id': 4, 'type': 'title', 'name': ' [ ' + $scope.paramAccount.branch.code + ' ] ' + $scope.paramAccount.branch.name});
                if ($scope.paramAccount.master) {
                    $scope.itemsAccount.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                    $scope.itemsAccount.push({'id': 6, 'type': 'title', 'name': ' [ ' + $scope.paramAccount.master.code + ' ] ' + $scope.paramAccount.master.name});
                }
                if ($scope.paramAccount.course) {
                    $scope.itemsAccount.push({'id': 7, 'type': 'title', 'name': 'رقم الدورة', 'style': 'font-weight:bold'});
                    $scope.itemsAccount.push({'id': 8, 'type': 'title', 'name': ' [ ' + $scope.paramAccount.course.code + ' ] '});
                }
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });

        };
        $scope.selectNextAccountsPage = function () {
            $scope.pageAccount.page++;
            $scope.searchAccount($scope.paramAccount);
        };
        $scope.selectPrevAccountsPage = function () {
            $scope.pageAccount.page--;
            $scope.searchAccount($scope.paramAccount);
        };
        $scope.newAccount = function () {
            ModalProvider.openAccountCreateModel().result.then(function (data) {
                $scope.accounts.splice(0, 0, data);
                $rootScope.refreshTable();
            }, function () {});
        };
        $scope.newAccountCondition = function (account) {
            ModalProvider.openAccountConditionCreateModel(account).result.then(function (data) {
                return account.accountConditions.push(data);
            });
        };
        $scope.newAccountNote = function (account) {
            ModalProvider.openAccountNoteCreateModel(account).result.then(function (data) {
                return account.accountNotes.push(data);
            });
        };
        $scope.printAccount = function (account) {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountContract.html',
                controller: 'accountContractCtrl',
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    account: function () {
                        return account;
                    }
                }
            });

            modalInstance.result.then(function (buffer) {

            }, function () {
                console.info('Modal dismissed at: ' + new Date());
            });
        };
        $scope.deleteAccount = function (account) {
            ModalProvider.openConfirmModel("حذف الطلاب", "delete", "هل تود حذف التسجيل فعلاً؟").result.then(function (value) {
                if (value) {
                    AccountService.remove(account.id).then(function () {
                        var index = $scope.accounts.indexOf(account);
                        $scope.accounts.splice(index, 1);
                    });
                }
            })
        };
        $scope.refreshAccountAttaches = function (account) {
            AccountAttachService.findByAccount(account).then(function (data) {
                return account.accountAttaches = data;
            })
        };
        $scope.browseAccountAttaches = function (account) {
            $scope.accountToUploadAttaches = account;
            document.getElementById('uploader-account-attach').click();
        };
        $scope.uploadAccountAttaches = function (files) {
            AccountAttachService.upload($scope.accountToUploadAttaches, files).then(function (data) {
                return Array.prototype.push.apply($scope.accountToUploadAttaches.accountAttaches, data);
            });
        };
        $scope.setAccountAttachType = function (accountAttach) {
            AccountAttachService.setType(accountAttach, accountAttach.attachType);
        };
        $scope.rowMenuAccount = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newAccount();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل بيانات الطالب</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_STUDENT_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openStudentUpdateModel($itemScope.account.student);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل رسوم الدورة</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openAccountUpdatePriceModel($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteAccount($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/accountPayment.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>سداد</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_PAYMENT_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newAccountPayment($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/student-case.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تغيير حالة الطالب</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_CONDITION_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newAccountCondition($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/note.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>ملاحظة جديدة</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_ACCOUNT_NOTE_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newAccountNote($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/about.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>التفاصيل</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openAccountDetailsModel($itemScope.account);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/report-one.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>طباعة عقد</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.printAccount($itemScope.account);
                }
            }
        ];
        $scope.checkAllAccounts = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllAccounts input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.accounts, function (account) {
                account.isSelected = $scope.accounts.checkAll;
            });
        };
        $scope.checkAccount = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllAccounts').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllAccounts').MaterialCheckbox.uncheck();
                }
            }
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * AccountPayment                                                                                             *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.paramAccountPayment = {};
        $scope.accountPayments = [];
        $scope.accountPayments.checkAll = false;
        $scope.itemsAccountPayment = [];
        $scope.itemsAccountPayment.push({'id': 2, 'type': 'title', 'name': 'سندات القبض'});

        $scope.pageAccountPayment = {};
        $scope.pageAccountPayment.sorts = [];
        $scope.pageAccountPayment.page = 0;
        $scope.pageAccountPayment.totalPages = 0;
        $scope.pageAccountPayment.currentPage = $scope.pageAccountPayment.page + 1;
        $scope.pageAccountPayment.currentPageString = ($scope.pageAccountPayment.page + 1) + ' / ' + $scope.pageAccountPayment.totalPages;
        $scope.pageAccountPayment.size = 25;
        $scope.pageAccountPayment.first = true;
        $scope.pageAccountPayment.last = true;

        $scope.openFilterAccountPayment = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/payment/paymentFilter.html',
                controller: 'paymentFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    title: function () {
                        return 'البحث فى سندات القبض';
                    }
                }
            });

            modalInstance.result.then(function (paramAccountPayment) {
                $scope.paramAccountPayment = paramAccountPayment;
                $scope.searchAccountPayment($scope.paramAccountPayment);
            }, function () {});
        };
        $scope.searchAccountPayment = function (paramAccountPayment) {

            var search = [];

            search.push('size=');
            search.push($scope.pageAccountPayment.size);
            search.push('&');

            search.push('page=');
            search.push($scope.pageAccountPayment.page);
            search.push('&');

            angular.forEach($scope.pageAccountPayment.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });

            if (paramAccountPayment.codeFrom) {
                search.push('codeFrom=');
                search.push(paramAccountPayment.codeFrom);
                search.push('&');
            }
            if (paramAccountPayment.codeTo) {
                search.push('codeTo=');
                search.push(paramAccountPayment.codeTo);
                search.push('&');
            }
            if (paramAccountPayment.dateFrom) {
                search.push('dateFrom=');
                search.push(paramAccountPayment.dateFrom.getTime());
                search.push('&');
            }
            if (paramAccountPayment.dateTo) {
                search.push('dateTo=');
                search.push(paramAccountPayment.dateTo.getTime());
                search.push('&');
            }
            if (paramAccountPayment.firstName) {
                search.push('firstName=');
                search.push(paramAccountPayment.firstName);
                search.push('&');
            }
            if (paramAccountPayment.secondName) {
                search.push('secondName=');
                search.push(paramAccountPayment.secondName);
                search.push('&');
            }
            if (paramAccountPayment.thirdName) {
                search.push('thirdName=');
                search.push(paramAccountPayment.thirdName);
                search.push('&');
            }
            if (paramAccountPayment.forthName) {
                search.push('forthName=');
                search.push(paramAccountPayment.forthName);
                search.push('&');
            }
            if (paramAccountPayment.registerDateFrom) {
                search.push('registerDateFrom=');
                search.push(paramAccountPayment.registerDateFrom.getTime());
                search.push('&');
            }
            if (paramAccountPayment.registerDateTo) {
                search.push('registerDateTo=');
                search.push(paramAccountPayment.registerDateTo.getTime());
                search.push('&');
            }
            if (paramAccountPayment.studentIdentityNumber) {
                search.push('studentIdentityNumber=');
                search.push(paramAccountPayment.studentIdentityNumber);
                search.push('&');
            }
            if (paramAccountPayment.studentMobile) {
                search.push('studentMobile=');
                search.push(paramAccountPayment.studentMobile);
                search.push('&');
            }
            if (paramAccountPayment.branch) {
                search.push('branch=');
                search.push(paramAccountPayment.branch.id);
                search.push('&');
            }
            if (paramAccountPayment.master) {
                search.push('master=');
                search.push(paramAccountPayment.master.id);
                search.push('&');
            }
            if (paramAccountPayment.course) {
                search.push('course=');
                search.push(paramAccountPayment.course.id);
                search.push('&');
            }
            if (paramAccountPayment.type) {
                search.push('type=');
                search.push(paramAccountPayment.type);
                search.push('&');
            }
            AccountPaymentService.filter(search.join("")).then(function (data) {
                $scope.accountPayments = data.content;

                $scope.pageAccountPayment.currentPage = $scope.pageAccountPayment.page + 1;
                $scope.pageAccountPayment.first = data.first;
                $scope.pageAccountPayment.last = data.last;
                $scope.pageAccountPayment.number = data.number;
                $scope.pageAccountPayment.numberOfElements = data.numberOfElements;
                $scope.pageAccountPayment.size = data.size;
                $scope.pageAccountPayment.totalElements = data.totalElements;
                $scope.pageAccountPayment.totalPages = data.totalPages;
                $scope.pageAccountPayment.currentPageString = ($scope.pageAccountPayment.page + 1) + ' / ' + $scope.pageAccountPayment.totalPages;

                $scope.itemsAccountPayment = [];
                $scope.itemsAccountPayment.push({'id': 2, 'type': 'title', 'name': 'سندات القبض', 'style': 'font-weight:bold'});
                $scope.itemsAccountPayment.push({'id': 3, 'type': 'title', 'name': 'فرع', 'style': 'font-weight:bold'});
                $scope.itemsAccountPayment.push({'id': 4, 'type': 'title', 'name': ' [ ' + paramAccountPayment.branch.code + ' ] ' + paramAccountPayment.branch.name});
                if (paramAccountPayment.master) {
                    $scope.itemsAccountPayment.push({'id': 5, 'type': 'title', 'name': 'تخصص', 'style': 'font-weight:bold'});
                    $scope.itemsAccountPayment.push({'id': 6, 'type': 'title', 'name': ' [ ' + paramAccountPayment.master.code + ' ] ' + paramAccountPayment.master.name});
                }
                if (paramAccountPayment.course) {
                    $scope.itemsAccountPayment.push({'id': 7, 'type': 'title', 'name': 'رقم الدورة', 'style': 'font-weight:bold'});
                    $scope.itemsAccountPayment.push({'id': 8, 'type': 'title', 'name': ' [ ' + paramAccountPayment.course.code + ' ] '});
                }
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextPaymentsPage = function () {
            $scope.pageAccountPayment.page++;
            $scope.searchAccountPayment($scope.paramAccountPayment);
        };
        $scope.selectPrevPaymentsPage = function () {
            $scope.pageAccountPayment.page--;
            $scope.searchAccountPayment($scope.paramAccountPayment);
        };
        $scope.newAccountPayment = function (account) {
            ModalProvider.openAccountPaymentCreateModel(account).result.then(function (data) {
                $scope.accountPayments.splice(0, 0, data);
            });
        };
        $scope.deletePayment = function (payment) {
            ModalProvider.openConfirmModel("سندات القبض", 'trash', "هل تود حذف سند القبض فعلاً؟").result.then(function (value) {
                if (value) {
                    AccountPaymentService.remove(payment.id).then(function () {
                        var index = $scope.accountPayments.indexOf(payment);
                        $scope.accountPayments.splice(index, 1);
                    });
                }
            })
        };
        $scope.rowMenuPayment = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.newPayment();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PAYMENT_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openPaymentUpdateModel($itemScope.payment);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    $scope.deletePayment($itemScope.payment);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/print.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>طباعة</span>' +
                '</div>',
                enabled: function () {
                    return true
                },
                click: function ($itemScope, $event, value) {
                    window.open('report/CashReceipt/' + $itemScope.payment.id);
                }
            }
        ];
        $scope.checkAllPayments = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('#checkAllPayments input').is(":checked")) {
                    element.MaterialCheckbox.check();
                }
                else {
                    element.MaterialCheckbox.uncheck();
                }
            }
            angular.forEach($scope.accountPayments, function (payment) {
                payment.isSelected = $scope.accountPayments.checkAll;
            });
        };
        $scope.checkPayment = function () {
            var elements = document.querySelectorAll('.check');
            for (var i = 0, n = elements.length; i < n; i++) {
                var element = elements[i];
                if ($('.check input:checked').length == $('.check input').length) {
                    document.querySelector('#checkAllPayments').MaterialCheckbox.check();
                } else {
                    document.querySelector('#checkAllPayments').MaterialCheckbox.uncheck();
                }
            }
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Bank                                                                                                       *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.fetchMyBanks = function () {
            BankService.fetchMyBanks().then(function (value) {
                $rootScope.banks = value;
            });
        };
        $scope.newBank = function () {
            ModalProvider.openBankCreateModel().result.then(function (data) {
                $rootScope.banks.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.rowMenuBank = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BANK_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newBank();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BANK_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openBankUpdateModel($itemScope.bank);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_BANK_DELETE']);
                },
                click: function ($itemScope, $event, value) {

                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * BankTransaction                                                                                            *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.bankTransactions = [];
        $scope.paramBankTransaction = {};
        $scope.paramBankTransaction.transactionTypeCodes = [];
        $scope.bankTransactions.checkAll = false;

        $scope.pageBankTransaction = {};
        $scope.pageBankTransaction.sorts = [];
        $scope.pageBankTransaction.page = 0;
        $scope.pageBankTransaction.totalPages = 0;
        $scope.pageBankTransaction.currentPage = $scope.pageBankTransaction.page + 1;
        $scope.pageBankTransaction.currentPageString = ($scope.pageBankTransaction.page + 1) + ' / ' + $scope.pageBankTransaction.totalPages;
        $scope.pageBankTransaction.size = 25;
        $scope.pageBankTransaction.first = true;
        $scope.pageBankTransaction.last = true;

        $scope.openBankTransactionsFilter = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/bankTransaction/bankTransactionFilter.html',
                controller: 'bankTransactionFilterCtrl',
                scope: $scope,
                backdrop: 'static',
                keyboard: false
            });

            modalInstance.result.then(function (paramBankTransaction) {
                $scope.searchBankTransactions(paramBankTransaction);
            }, function () {
            });
        };
        $scope.searchBankTransactions = function (paramBankTransaction) {
            var search = [];
            search.push('size=');
            search.push($scope.pageBankTransaction.size);
            search.push('&');
            search.push('page=');
            search.push($scope.pageBankTransaction.page);
            search.push('&');
            angular.forEach($scope.pageBankTransaction.sorts, function (sortBy) {
                search.push('sort=');
                search.push(sortBy.name + ',' + sortBy.direction);
                search.push('&');
            });
            if ($scope.pageBankTransaction.sorts.length === 0) {
                search.push('sort=date,desc&');
            }
            if (paramBankTransaction.codeFrom) {
                search.push('codeFrom=');
                search.push(paramBankTransaction.codeFrom);
                search.push('&');
            }
            if (paramBankTransaction.codeTo) {
                search.push('codeTo=');
                search.push(paramBankTransaction.codeTo);
                search.push('&');
            }
            if (paramBankTransaction.dateTo) {
                search.push('dateTo=');
                search.push(paramBankTransaction.dateTo.getTime());
                search.push('&');
            }
            if (paramBankTransaction.dateFrom) {
                search.push('dateFrom=');
                search.push(paramBankTransaction.dateFrom.getTime());
                search.push('&');
            }
            if (paramBankTransaction.supplierName) {
                search.push('supplierName=');
                search.push(paramBankTransaction.supplierName);
                search.push('&');
            }
            if (paramBankTransaction.supplierMobile) {
                search.push('supplierMobile=');
                search.push(paramBankTransaction.supplierMobile);
                search.push('&');
            }
            if (paramBankTransaction.supplierIdentityNumber) {
                search.push('supplierIdentityNumber=');
                search.push(paramBankTransaction.supplierIdentityNumber);
                search.push('&');
            }
            if (paramBankTransaction.transactionTypeCode) {
                search.push('transactionTypeCodes=');
                search.push([paramBankTransaction.transactionTypeCode]);
                search.push('&');
            }

            BankTransactionService.filter(search.join("")).then(function (data) {
                $scope.bankTransactions = data.content;

                $scope.pageBankTransaction.currentPage = $scope.pageBankTransaction.page + 1;
                $scope.pageBankTransaction.first = data.first;
                $scope.pageBankTransaction.last = data.last;
                $scope.pageBankTransaction.number = data.number;
                $scope.pageBankTransaction.numberOfElements = data.numberOfElements;
                $scope.pageBankTransaction.size = data.size;
                $scope.pageBankTransaction.totalElements = data.totalElements;
                $scope.pageBankTransaction.totalPages = data.totalPages;
                $scope.pageBankTransaction.currentPageString = ($scope.pageBankTransaction.page + 1) + ' / ' + $scope.pageBankTransaction.totalPages;

                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.selectNextBankTransactionsPage = function () {
            $scope.pageBankTransaction.page++;
            $scope.searchBankTransactions($scope.paramBankTransaction);
        };
        $scope.selectPrevBankTransactionsPage = function () {
            $scope.pageBankTransaction.page--;
            $scope.searchBankTransactions($scope.paramBankTransaction);
        };
        $scope.newDeposit = function () {
            ModalProvider.openDepositCreateModel().result.then(function (data) {
                $scope.bankTransactions.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.newWithdraw = function () {
            ModalProvider.openWithdrawCreateModel().result.then(function (data) {
                $scope.bankTransactions.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.newTransfer = function () {
            ModalProvider.openTransferCreateModel().result.then(function () {
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };
        $scope.newExpense = function () {
            ModalProvider.openExpenseCreateModel().result.then(function (data) {
                $scope.bankTransactions.splice(0, 0, data);
                $timeout(function () {
                    window.componentHandler.upgradeAllRegistered();
                }, 300);
            });
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Person                                                                                                     *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.fetchPersonTableData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
            });
        };
        $scope.newPerson = function () {
            ModalProvider.openPersonCreateModel().result.then(function (data) {
                return $scope.persons.splice(0, 0, data);
            });
        };
        $scope.deletePerson = function (person) {
            ModalProvider.openConfirmModel("حذف المستخدمين", "delete", "هل تود حذف المستخدم فعلاً؟").result.then(function (value) {
                if (value) {
                    PersonService.remove(person.id).then(function () {
                        var index = $scope.persons.indexOf(person);
                        $scope.persons.splice(index, 1);
                    });
                }
            });
        };
        $scope.rowMenuPerson = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newPerson();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openPersonUpdateModel($itemScope.person);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_PERSON_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deletePerson($itemScope.person);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Team                                                                                                       *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.fetchTeamTableData = function () {
            TeamService.findAll().then(function (data) {
                $scope.teams = data;
            });
        };
        $scope.newTeam = function () {
            ModalProvider.openTeamCreateModel().result.then(function (data) {
                return $scope.teams.splice(0, 0, data);
            });
        };
        $scope.deleteTeam = function (team) {
            ModalProvider.openConfirmModel("حذف مجموعات الصلاحيات", "delete", "هل تود حذف المجموعة فعلاً؟").result.then(function (value) {
                if (value) {
                    TeamService.remove(team.id).then(function () {
                        var index = $scope.teams.indexOf(team);
                        $scope.teams.splice(index, 1);
                    });
                }
            });
        };
        $scope.rowMenuTeam = [
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/add.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>جديد...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_CREATE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.newTeam();
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/edit.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>تعديل...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_UPDATE']);
                },
                click: function ($itemScope, $event, value) {
                    ModalProvider.openTeamUpdateModel($itemScope.team);
                }
            },
            {
                html: '<div class="drop-menu">' +
                '<img src="/ui/img/' + $rootScope.iconSet + '/delete.' + $rootScope.iconSetType + '" width="24" height="24">' +
                '<span>حذف...</span>' +
                '</div>',
                enabled: function () {
                    return $rootScope.contains($rootScope.me.team.authorities, ['ROLE_TEAM_DELETE']);
                },
                click: function ($itemScope, $event, value) {
                    $scope.deleteTeam($itemScope.team);
                }
            }
        ];

        /**************************************************************************************************************
         *                                                                                                            *
         * Profile                                                                                                    *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.submitProfile = function () {
            PersonService.updateProfile($rootScope.me).then(function (data) {
                $rootScope.me = data;
            });
        };
        $scope.browseProfilePhoto = function () {
            document.getElementById('uploader-profile').click();
        };
        $scope.uploadProfilePhoto = function (files) {
            PersonService.uploadContactPhoto(files[0]).then(function (data) {
                $rootScope.me.logo = data;
                PersonService.update($rootScope.me).then(function (data) {
                    $rootScope.me = data;
                });
            });
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Print                                                                                                      *
         *                                                                                                            *
         **************************************************************************************************************/
        $rootScope.printToCart = function (printSectionId, title) {
            var innerContents = document.getElementById(printSectionId).innerHTML;
            var mywindow = window.open(title, '_blank', 'height=400,width=600');
            mywindow.document.write('<html><head><title></title>');
            mywindow.document.write('<link rel="stylesheet" href="/ui/app.css" type="text/css" />');
            mywindow.document.write('<link rel="stylesheet" href="/ui/css/style.css" type="text/css" />');
            mywindow.document.write('</head><body >');
            mywindow.document.write(innerContents);
            mywindow.document.write('</body></html>');
            mywindow.document.close();
            mywindow.focus();
            $timeout(function () {
                mywindow.print();
                mywindow.close();
            }, 1500);
            return true;
        };

        /**************************************************************************************************************
         *                                                                                                            *
         * Widget: Daily History                                                                                      *
         *                                                                                                            *
         **************************************************************************************************************/
        $scope.findDailyHistory = function () {
            HistoryService.findDaily().then(function (value) {
                $scope.dailyHistories = value;
            });
        };

        $timeout(function () {
            CompanyService.get().then(function (data) {
                $rootScope.selectedCompany = data;
                $rootScope.selectedCompany.options = JSON.parse(data.options);
            });
            BranchService.fetchBranchMasterCourse().then(function (data) {
                $scope.branches = data;
            });
            PersonService.findAllCombo().then(function (data) {
                $scope.personsCombo = data;
            });
            AttachTypeService.findAll().then(function (data) {
                $scope.attachTypes = data;
            });
            $scope.fetchMyBanks();
            $scope.fetchMasterCategories();
            $scope.findDailyHistory();
            window.componentHandler.upgradeAllRegistered();
        }, 800);

    }]);