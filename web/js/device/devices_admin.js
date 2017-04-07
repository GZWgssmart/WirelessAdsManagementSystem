var contextPath = '';

$(function() {
    setPagination("#list");
    $("#devAdminLayer").remove();
    $("#groupSearch").combobox({
        onChange:function(n, o){
            if (n != o) {
                doSearch();
            }
        }
    });
    $("#versionSearch").combobox({
        onChange:function(n, o){
            if (n != o) {
                doSearch();
            }
        }
    });
    $("#statusSearch").combobox({
        onChange:function(n, o){
            if (n != o) {
                doSearch();
            }
        }
    });
    $("#onlineSearch").combobox({
        onChange:function(n, o){
            if (n != o) {
                doSearch();
            }
        }
    });
});

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/device/search_pager_admin/' + $("#customerId").val(),
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll(customerId) {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/device/search_pager_admin/' + $("#customerId").val(),
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}