var gulp = require('gulp');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var cssMin = require('gulp-css');
var gutil = require('gulp-util')


gulp.task('css', function () {

    gulp.src([
        './magnific-popup/magnific-popup.css',
        './css/creative.css',

        './css/animate.css',
        './css/animation.css',
        './css/bootstrap.css',
        './css/fonts.css',
        './css/md-icons.css',
        './css/angular-toggle-switch-bootstrap-3.css',
        './css/highlight.css',
        './bootstrap-select/css/nya-bs-select.css',
        './css/font-awesome-animation.css',
        './kdate/css/jquery.calendars.picker.css',
        './css/select.css',
        './chosen/chosen.css',
        './css/marquee.css'
    ])
        .pipe(concat('app.css'))
        .pipe(cssMin())
        .pipe(gulp.dest('./'));

});

gulp.task('scripts', function () {

    gulp.src([

        './js/material.js',
        './js/fontawesome.js',
        './js/jquery.js',

        './kdate/js/jquery.calendars.js',
        './kdate/js/jquery.calendars-ar.js',
        './kdate/js/jquery.calendars-ar-EG.js',
        './kdate/js/jquery.plugin.js',
        './kdate/js/jquery.calendars.picker.js',
        './kdate/js/jquery.calendars.picker-ar.js',
        './kdate/js/jquery.calendars.picker-ar-EG.js',
        './kdate/js/jquery.calendars.picker.lang.js',
        './kdate/js/jquery.calendars.ummalqura.js',
        './kdate/js/jquery.calendars.ummalqura-ar.js',
        './kdate/js/jquery.calendars.plus.js',

        './js/jquery-ui.js',
        './js/angular.js',
        './js/angular-sanitize.js',
        './js/angular-ui-router.js',
        './js/angular-animate.js',
        './js/angular-touch.js',
        './js/angular-filter.js',

        './kdate/kdate.module.js',
        './kdate/kdate.filter.js',
        './kdate/kdate.picker.js',

        './angular-spinner/spin.js',
        './angular-spinner/angular-spinner.js',
        './angular-spinner/angular-loading-spinner.js',

        './js/ui-bootstrap.js',
        './js/ui-bootstrap-tpls.js',

        './js/select.js',
        './js/marquee.js',

        './sockjs/sockjs.js',
        './stomp-websocket/lib/stomp.js',
        './ng-stomp/ng-stomp.js',
        './js/underscore.js',
        './js/lrDragNDrop.js',
        './js/contextMenu.js',
        './js/lrStickyHeader.js',
        './js/smart-table.js',
        './js/stStickyHeader.js',
        './js/angular-pageslide-directive.js',
        './js/elastic.js',
        './js/scrollglue.js',
        './ng-upload/angular-file-upload.js',
        './bootstrap-select/js/nya-bs-select.js',
        './js/angular-css.js',
        './js/angular-timer-all.js',
        './full-screen/angular-fullscreen.js',
        './animate-counter/angular-counter.js',
        './js/angular-focusmanager.js',
        './js/jcs-auto-validate.js',
        './js/angular-toggle-switch.js',
        './js/chosen.jquery.js',
        './chosen/angular-chosen.js',
        './js/sortable.js',
        './js/FileSaver.js',
        './js/jquery.noty.packaged.js',
        './scrollreveal/scrollreveal.js',
        './magnific-popup/jquery.magnific-popup.js',
        './js/creative.js',
        './angular-chart/Chart.js',
        './angular-chart/angular-chart.js',
        './js/datetime.js',

        //SheetJS js-xlsx library
        './js/shim.js',
        './js/xlsx.full.min.js',

        './init/config/config.js',
        './init/factory/errorHandleFactory.js',
        './init/factory/smsFactory.js',
        './init/factory/historyFactory.js',
        './init/factory/companyFactory.js',

        './init/factory/bankFactory.js',
        './init/factory/bankTransactionFactory.js',

        './init/factory/branchFactory.js',
        './init/factory/masterCategoryFactory.js',
        './init/factory/masterFactory.js',
        './init/factory/offerFactory.js',
        './init/factory/callFactory.js',
        './init/factory/courseFactory.js',
        './init/factory/studentFactory.js',
        './init/factory/accountFactory.js',
        './init/factory/accountPaymentFactory.js',
        './init/factory/accountConditionFactory.js',
        './init/factory/accountNoteFactory.js',
        './init/factory/accountAttachFactory.js',
        './init/factory/attachTypeFactory.js',
        './init/factory/fileFactory.js',
        './init/factory/personFactory.js',
        './init/factory/teamFactory.js',

        './init/service/service.js',
        './init/directive/directive.js',
        './init/filter/filter.js',
        './init/run/run.js',

        './partials/home/home.js',
        './partials/menu/menu.js',

        './partials/branch/branchCreateUpdate.js',

        './partials/master/masterFilter.js',
        './partials/master/masterCreateUpdate.js',

        './partials/masterCategory/masterCategoryCreateUpdate.js',


        './partials/offer/offerFilter.js',
        './partials/offer/offerCreateUpdate.js',
        './partials/offer/offerDetails.js',
        './partials/offer/offerSendMessage.js',

        './partials/offer/callCreate.js',

        './partials/course/courseCreateUpdate.js',
        './partials/course/courseFilter.js',

        './partials/student/studentFilter.js',
        './partials/student/studentCreateUpdate.js',
        './partials/student/studentCreateBatch.js',

        './partials/account/accountFilter.js',
        './partials/account/accountCreate.js',
        './partials/account/accountUpdate.js',
        './partials/account/accountUpdatePrice.js',
        './partials/account/accountContract.js',
        './partials/account/accountDetails.js',
        './partials/account/accountConditionCreate.js',
        './partials/account/accountNoteCreate.js',
        './partials/account/accountAttachCreate.js',
        './partials/account/sendMessage.js',

        './partials/accountPayment/accountPaymentCreate.js',
        './partials/accountPayment/accountPaymentUpdate.js',
        './partials/accountPayment/accountPaymentFilter.js',

        './partials/bank/bankCreateUpdate.js',
        './partials/bankTransaction/bankTransactionFilter.js',
        './partials/bankTransaction/depositCreate.js',
        './partials/bankTransaction/withdrawCreate.js',
        './partials/bankTransaction/transferCreate.js',
        './partials/bankTransaction/expenseCreate.js',

        './partials/report/offer/js/offerByBranch.js',
        './partials/report/offer/js/offerByMaster.js',
        './partials/report/offer/js/offerByPerson.js',

        './partials/report/call/js/callByPerson.js',

        './partials/report/account/js/accountByBranch.js',
        './partials/report/account/js/accountByCourse.js',
        './partials/report/account/js/accountByMaster.js',
        './partials/report/account/js/accountByMasterCategory.js',
        './partials/report/account/js/printContract.js',
        './partials/report/account/js/accountStatement.js',

        './partials/report/account/js/accountDebtByBranch.js',
        './partials/report/account/js/accountDebtByMaster.js',
        './partials/report/account/js/accountDebtByCourse.js',

        './partials/report/payment/js/paymentByBranch.js',
        './partials/report/payment/js/paymentByCourse.js',
        './partials/report/payment/js/paymentByMaster.js',
        './partials/report/payment/js/paymentByMasterCategory.js',
        './partials/report/payment/js/paymentByAccountIn.js',
        './partials/report/payment/js/incomeAnalysisByBranch.js',

        './partials/report/paymentOut/js/paymentOutByBranch.js',
        './partials/report/paymentOut/js/paymentOutByPerson.js',

        './partials/report/billBuy/js/billBuyByBranch.js',

        './partials/team/team.js',
        './partials/team/teamCreateUpdate.js',

        './partials/person/person.js',
        './partials/person/personCreateUpdate.js',

        './partials/profile/profile.js',

        './partials/modal/confirmModal.js'

    ])
        .pipe(concat('app.js'))
        .pipe(uglify())
        .on('error', function (err) { gutil.log(gutil.colors.red('[Error]'), err.toString()); })
        .pipe(gulp.dest('./'))

});

gulp.task('default', ['css', 'scripts']);