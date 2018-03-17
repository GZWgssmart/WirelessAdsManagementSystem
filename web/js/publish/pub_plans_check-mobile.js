var contextPath = '';

$(function() {
    doSearch();
    $('#list').datagrid({
        onDblClickCell: function(rowIndex, rowData){
            showPlanDetailMob();
        }
    });
    $("#list").datagrid("hideColumn", 'versionId');
    $("#planCheckLayer").remove();
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
        // addTab(row.planName + " 计划详情", contextPath + "/publish/mob/list_page/" + row.id);
        toPage(contextPath + "/publish/mob/list_page/" + row.id);
    } else {
        $.messager.alert("提示", "请先选择计划", "info");
    }
}

function toCheck() {
    var row = selectedRow("list");
    if (row) {
        $("#toCheckPlanId").val(row.id);
        openWin("checkPwdWin");
    } else {
        $.messager.alert("提示", "请选择需要审核的计划", "info");
    }
}

function cancelCheckPwd() {
    closeWin("checkPwdWin");
    $("#checkPwdForm").form("clear");
}

function conCheckPwd() {
    toValidate("checkPwdForm");
    if (validateForm("checkPwdForm")) {
        $.post(contextPath + "/pubplan/check?checkStatus=checked",
            $("#checkPwdForm").serialize(),
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
        cancelCheckPwd();
    }
}

function rejectCheck() {
    var row = selectedRow("list");
    if (row) {
        $.post(contextPath + "/pubplan/reject?id=" + row.id,
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
        $.messager.alert("提示", "请选择需要驳回的计划", "info");
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
