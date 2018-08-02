app.factory("StudentService",
    ['$http', '$log', function ($http, $log) {
        return {
            findOne: function (id) {
                return $http.get("/api/student/findOne/" + id).then(function (response) {
                    return response.data;
                });
            },
            create: function (student) {
                return $http.post("/api/student/create", student).then(function (response) {
                    return response.data;
                });
            },
            createBatch: function (student) {
                return $http.post("/api/student/createBatch", student).then(function (response) {
                    return response.data;
                });
            },
            remove: function (id) {
                return $http.delete("/api/student/delete/" + id).then(function (response) {
                    return response.data;
                });
            },
            update: function (student) {
                return $http.put("/api/student/update", student).then(function (response) {
                    return response.data;
                });
            },
            filter: function (search) {
                return $http.get("/api/student/filter?" + search).then(function (response) {
                    return response.data;
                });
            }
        };
    }]);