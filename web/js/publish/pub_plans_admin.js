var contextPath = '';

$(function() {
    setPagination("#list");
    $('#list').datagrid({
        onDblClickCell: function(rowIndex, rowData){
            showPlanDetail();
        }
    });
    $("#list").datagrid("hideColumn", 'versionId');
    $("#planAdminLayer").remove();
    $("#checkSearch").combobox({
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
        // addTab(row.planName + " 计划详情", contextPath + "/publish/list_page/" + row.id);
        toPage(contextPath + "/publish/mob/list_page/" + row.id);
    } else {
        $.messager.alert("提示", "请先选择计划", "info");
    }
}

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/pubplan/search_pager_admin/' + $("#customerId").val(),
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/pubplan/search_pager_admin/' + $("#customerId").val(),
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}