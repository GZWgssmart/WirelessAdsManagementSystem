var contextPath = '';

$(function() {
    setPagination("#list")
});

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/device/search_pager_admin/${customerId }',
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/device/search_pager_admin/${customerId }',
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}