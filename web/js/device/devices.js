var contextPath = '';

$(function() {
    setPagination("#list");
    $("#devLayer").remove();
});

function showAdd() {
    $("#addDeviceGroupId").combobox({
        url:contextPath + '/devgroup/list_combo/Y/add',
        method:'get',
        valueField:'id',
        textField:'text',
        panelHeight:'auto'
    });
    openWinFitPos('addWin');
}

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
        $("#versionId").combobox({
            url:contextPath + '/version/list_combo/' + row.version.id + "/all",
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

function searchAll() {
    $("#searchForm").form("clear");
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

function showAllRes() {
    var row = selectedRow("list");
    if (row) {
        $("#deviceId").val(row.id);
        $("#resList").datagrid({
            url: contextPath + '/publish/search_res_pager_dev/' + row.id
        });
        openWinFitPos("allResWin");
    } else {
        $.messager.alert("提示", "请先选择需要要查看资源的设备", "info");
    }
}

function doResSearch() {
    $("#resList").datagrid({
        url:contextPath + '/publish/search_res_pager_dev/' + $("#deviceId").val(),
        pageSize:20,
        queryParams:getQueryParams("resList", "ressearchForm")
    });
    setPagination("#resList");
}

function searchAllRes() {
    $("#ressearchForm").form("clear");
    $("#resList").datagrid({
        url:contextPath + '/publish/search_res_pager_dev/' + $("#deviceId").val(),
        pageSize:20,
        queryParams:getQueryParams("resList", "ressearchForm")
    });
    setPagination("#resList");
}

function refreshAllRes() {
    $("#resList").datagrid("reload");
}

function deleteRes() {
    var rows = selectedRows("resList");
    if (rows) {
        var resIds = "";
        var canDo = true;
        $.each(rows, function (index, row) {
            if (row.deleteStatus != '可删除') {
                canDo = false;
            }
            if (resIds == "") {
                resIds = row.id;
            } else {
                resIds += "," + row.id;
            }
        });
        if (canDo) {
            $.get(contextPath + "/publish/delete_res/" + $("#deviceId").val() + "/" + resIds,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("resList");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function() {
                            toCustomerLoginPage();
                        });
                    }
                });
        } else {
            $.messager.alert("提示", "请只选择可删除的资源", "info");
        }
    }

}