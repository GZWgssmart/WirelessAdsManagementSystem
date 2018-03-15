var contextPath = '';

$(function() {
    doSearch();
    $('#list').datagrid({
        onDblClickCell: function(rowIndex, rowData){
            showPlanDetail();
        }
    });
    $("#list").datagrid("hideColumn", 'versionId');
    $("#planCheckedLayer").remove();
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

function toCheck() {
    var row = selectedRow("list");
    if (row) {
        $.get(contextPath + "/pubplan/check?id=" + row.id + "&checkStatus=checked",
            function (data) {
                if (data.result == "success") {
                    $.messager.alert("提示", data.message, "info");
                    dataGridReload("list");
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function() {
                        toCustomerLoginPage();
                    });
                } else {
                    $.messager.alert("提示", data.message, "info");
                }
            });
    } else {
        $.messager.alert("提示", "请选择需要审核的计划", "info");
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