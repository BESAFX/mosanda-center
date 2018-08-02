app.factory("AccountPaymentService",
    ['$http', '$log', function ($http, $log) {
        return {
            findOne: function (id) {
                return $http.get("/api/accountPayment/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (accountPayment) {
                return $http.post("/api/accountPayment/create", accountPayment).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/accountPayment/delete/" + id);
            },
            findByAcount: function (id) {
                return $http.get("/api/accountPayment/findByAcount/" + id).then(function (response) {
                    return response.data;
                });
            },
            findByDateBetween: function (startDate, endDate) {
                return $http.get("/api/accountPayment/findByDateBetween/" + startDate + "/" + endDate)
                    .then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/accountPayment/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);