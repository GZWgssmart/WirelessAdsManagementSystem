var contextPath = '';

$(function() {
    setPagination("#list");
    $("#list").datagrid("hideColumn", 'fileName');
    $("#list").datagrid("hideColumn", 'path');
    $("#resAdminLayer").remove();
    $("#resTypeSearch").combobox({
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
});

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/res/search_pager_admin/' + $("#customerId").val(),
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/res/search_pager_admin/' + $("#customerId").val(),
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}