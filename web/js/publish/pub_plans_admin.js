var contextPath = '';

$(function() {
    setPagination("#list");
    $('#list').datagrid({
        onDblClickCell: function(rowIndex, rowData){
            showPlanDetail();
        }
    });
});

function showPlanDetail() {
    var row = selectedRow("list");
    if (row) {
        addTab(row.name + "计划详情", contextPath + "/publish/list_page/" + row.id);
    } else {
        $.messager.alert("提示", "请先选择计划", "info");
    }
}

function doSearch(customerId) {
    $("#list").datagrid({
        url:contextPath + '/pubplan/search_pager_admin/' + customerId,
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll(customerId) {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/pubplan/search_pager_admin/' + customerId,
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}