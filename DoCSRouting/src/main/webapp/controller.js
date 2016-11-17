app = angular.module('docs-application', ['angularFileUpload','ngTable']);

app = app.service('FolderModel', function ($http) {
    var service = this;

    service.list = function (name) {
      return $http.get('/DoCSRouting/webresources/folder?name='+name);
    };

    service.route = function (item) {
      return $http.post('/DoCSRouting/webresources/folder',item);
    };
  });
  
app = app.controller('MainCtrl', ['$scope', '$upload', 'FolderModel', '$filter', 'ngTableParams', function MainCtrl($scope, $upload, FolderModel, $filter, ngTableParams) {

    $scope.name = '';

    $scope.getItems = function() {
        FolderModel.list($scope.name)
          .then(function (result) {
          $scope.shares = result.data;
        });
    }

    var getData = function() {
        return $scope.shares.items;
    };
    
    $scope.$watch("shares", function () {
        $scope.tableParams.reload();
    });
    
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,          // count per page
        sorting: {
            name: 'asc'     // initial sorting
        }
    }, {
        total: function () { return getData().length; }, // length of data
        getData: function($defer, params) {
            var filteredData = getData();
            var orderedData = params.sorting() ?
                                $filter('orderBy')(filteredData, params.orderBy()) :
                                filteredData;
            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        },
        $scope: { $shares: {} }
    });

    $scope.$watch('files', function () {
        $scope.upload($scope.files);
    });

    $scope.upload = function (files) {
        if (files && files.length) {
            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                $upload.upload({
                    url: '/DoCSRouting/webresources/folder',
                    method: 'POST',
                    fields: {'to': $scope.to, 'title': $scope.title, 'message': $scope.message},
                    file: file,
                    fileFormDataName: 'primaryFile'
                }).progress(function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope.result = 'progress: ' + progressPercentage + '% ' + evt.config.file.name;
                }).success(function (data, status, headers, config) {
                    $scope.result = 'file ' + config.file.name + ' uploaded.';
                });
            }
        }
    };
}]);
