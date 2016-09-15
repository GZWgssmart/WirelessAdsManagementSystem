var contextPath = '';

$(function() {
    setPagination("#list")
});

function doSearch(customerId) {
    $("#list").datagrid({
        url:contextPath + '/res/search_pager_admin/' + customerId,
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll(customerId) {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/res/search_pager_admin/' + customerId,
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}