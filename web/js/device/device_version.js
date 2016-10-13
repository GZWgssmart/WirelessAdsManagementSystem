var contextPath = '';

$(function() {
    doSearch();
});

function add() {
    toValidate("addForm");
    if (validateForm("addForm")) {
        $('#addForm').ajaxSubmit({
            url:contextPath + '/device/add',
            type:'post',
            dataType: 'json',
            success: function (data) {
                if (data.result == "success") {
                    $("#addWin").window("close");
                    dataGridReload("list");
                    $("#addForm").form("clear");
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function() {
                        toCustomerLoginPage();
                    });
                } else {
                    $.messager.alert("提示", data.message, "info");
                }
            }
        });
    }
}

function showEdit() {
    var row = selectedRow("list");
    if (row) {
        $("#deviceGroupId").combobox({
            url:contextPath + '/devgroup/list_combo/Y/' + row.deviceGroup.id,
            method:'get',
            valueField:'id',
            textField:'text',
            panelHeight:'auto'
        });
        $("#editForm").form("load", row);
        openWin("editWin");
    } else {
        $.messager.alert("提示", "请选择需要修改的终端设备", "info");
    }
}

function edit() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        $('#editForm').ajaxSubmit({
            url:contextPath + '/device/update',
            type:'post',
            dataType: 'json',
            success: function (data) {
                if (data.result == "success") {
                    closeWin("editWin");
                    dataGridReload("list");
                    $("#editForm").form("clear");
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function() {
                        toCustomerLoginPage();
                    });
                } else {
                    $.messager.alert("提示", data.message, "info");
                }
            }
        });
    }
}

function inactive() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'N') {
            $.messager.alert("提示", "终端设备不可用,无需冻结", "info");
        } else {
            $.get(contextPath + "/device/inactive?id=" + row.id,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function() {
                            toCustomerLoginPage();
                        });
                    }
                });
        }
    } else {
        $.messager.alert("提示", "请选择需要冻结的终端设备", "info");
    }
}

function active() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'Y') {
            $.messager.alert("提示", "终端设备可用,无需激活", "info");
        } else {
            $.get(contextPath + "/device/active?id=" + row.id,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function() {
                            toCustomerLoginPage();
                        });
                    }
                });
        }
    } else {
        $.messager.alert("提示", "请选择需要激活的终端设备", "info");
    }
}

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/device/search_pager',
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll(versionId) {
    $("#searchForm").form("clear");
    $("#versionId").val(versionId);
    $("#list").datagrid({
        url:contextPath + '/device/search_pager',
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}