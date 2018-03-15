var contextPath = '';

$(function() {
    setPagination("#list");
    $("#pubLayer").remove();
    $("#publishLogSearch").combobox({
        onChange:function(n, o){
            if (n != o) {
                doSearch();
            }
        }
    });
});

function doSearch(planId) {
    $("#list").datagrid({
        url:contextPath + '/publish/search_pager/' + planId,
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll(planId) {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/publish/search_pager/' + planId,
        pageSize:defaultPageSize,
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
    openWin("allResWin");
}

function formatterTypeName(value, row, index) {
    return row.resource.resourceTypeName;
}

