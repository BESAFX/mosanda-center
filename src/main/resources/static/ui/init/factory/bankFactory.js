app.factory("BankService",
    ['$http', '$log', function ($http, $log) {
        return {
            fetchMyBanks: function () {
                return $http.get("/api/bank/fetchMyBanks").then(function (response) {
                    return response.data;
                });
            },
            create: function (bank) {
                return $http.post("/api/bank/create", bank).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/bank/delete/" + id);
            },
            update: function (bank) {
                return $http.put("/api/bank/update", bank).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);