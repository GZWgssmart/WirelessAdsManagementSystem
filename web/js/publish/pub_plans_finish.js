var contextPath = '';

$(function() {
    doSearch();
    $('#list').datagrid({
        onDblClickCell: function(rowIndex, rowData){
            showPlanDetail();
        }
    });
    $("#list").datagrid("hideColumn", 'versionId');
    $("#planFinishLayer").remove();
});

function showPlanDetail() {
    var row = selectedRow("list");
    if (row) {
        addTab(row.planName + " 计划详情", contextPath + "/publish/list_page/" + row.id);
    } else {
        $.messager.alert("提示", "请先选择计划", "info");
    }
}

function showPlanDetailMob() {
    var row = selectedRow("list");
    if (row) {
        addTab(row.planName + " 计划详情", contextPath + "/publish/mob/list_page/" + row.id);
    } else {
        $.messager.alert("提示", "请先选择计划", "info");
    }
}

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/pubplan/search_pager',
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/pubplan/search_pager',
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}