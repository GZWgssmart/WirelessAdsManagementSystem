var contextPath = '';

$(function() {
    setPagination("#list");
});

function doSearch(planId) {
    $("#list").datagrid({
        url:contextPath + '/publish/search_pager/' + planId,
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll(planId) {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/publish/search_pager/' + planId,
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}

function showAllRes(planId) {
    $("#resList").datagrid({
        url:contextPath + '/publish/search_res_pager/' + planId
    });
    openWinFitPos("allResWin");
}

