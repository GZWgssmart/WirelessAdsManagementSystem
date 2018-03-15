var contextPath = '';

$(function() {
    setPagination("#list", 50);
    $("#devLayer").remove();
    $("#devGroupSearch").combobox({
        onChange:function(n, o){
            if (n != o) {
                doSearch();
            }
        }
    });
    $("#versionSearch").combobox({
        onChange:function(n, o){
            if (n != o) {
                doSearch();
            }
        }
    });
    $("#onlineSearch").combobox({
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
    $("#resList").datagrid("hideColumn", 'resId');
});

function showAdd() {
    $("#addDeviceGroupId").combobox({
        url:contextPath + '/devgroup/list_combo/Y/add',
        method:'get',
        valueField:'id',
        textField:'text',
        panelHeight:'auto'
    });
    openWin('addWin');
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
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/device/search_pager',
        pageSize:defaultPageSize,
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
        openWin("allResWin");
    } else {
        $.messager.alert("提示", "请先选择需要要查看资源的设备", "info");
    }
}

function doResSearch() {
    $("#resList").datagrid({
        url:contextPath + '/publish/search_res_pager_dev/' + $("#deviceId").val(),
        pageSize:defaultPageSize,
        queryParams:getQueryParams("resList", "ressearchForm")
    });
    setPagination("#resList");
}

function searchAllRes() {
    $("#ressearchForm").form("clear");
    $("#resList").datagrid({
        url:contextPath + '/publish/search_res_pager_dev/' + $("#deviceId").val(),
        pageSize:defaultPageSize,
        queryParams:getQueryParams("resList", "ressearchForm")
    });
    setPagination("#resList");
}

function refreshAllRes() {
    $("#resList").datagrid("reload");
}

/**
 * 从终端删除选择的资源
 */
function deleteResFromDevice() {
    var rows = selectedRows("resList");
    if (rows && rows != undefined && rows != '') {
        var resIds = "";
        var canDo = true;
        $.each(rows, function (index, row) {
            if (row.deleteStatus != '可删除') {
                canDo = false;
            }
            if (resIds == "") {
                resIds = row.resId;
            } else {
                if (resIds.indexOf(row.resId) < 0) {
                    resIds += "," + row.resId;
                }
            }
        });
        if (canDo) {
            $.messager.confirm("提示", "确定从此终端上删除选中的资源?", function(r) {
                if (r) {
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
                }
            });
        } else {
            $.messager.alert("提示", "请只选择可删除的资源", "info");
        }
    } else {
        $.messager.alert("提示", "请只选择可删除的资源", "info");
    }

}

/**
 * 从终端删除所有资源
 */
function deleteAllResFromDevice() {
    $.messager.confirm("提示", "确定从此终端上删除所有已经发布的资源?", function(r) {
        if (r) {
            $.get(contextPath + "/publish/delete_all_res/" + $("#deviceId").val(),
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("resList");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function () {
                            toCustomerLoginPage();
                        });
                    }
                });
        }
    });
}

/**
 * 从所有终端删除选择的资源
 */
function deleteResFromAllDevice() {
    $.messager.confirm("提示", "确定从所有终端上删除选择的资源?", function(r) {
            if (r) {
                var rows = selectedRows("resList");
                if (rows && rows != undefined && rows != '') {
                    var resIds = "";
                    $.each(rows, function (index, row) {
                        if (resIds == "") {
                            resIds = row.resId;
                        } else {
                            if (resIds.indexOf(row.resId) < 0) {
                                resIds += "," + row.resId;
                            }
                        }
                    });
                    $.get(contextPath + "/publish/delete_res_from_all_dev/" + resIds,
                        function (data) {
                            if (data.result == "success") {
                                $.messager.alert("提示", data.message, "info");
                                dataGridReload("resList");
                            } else if (data.result == 'notLogin') {
                                $.messager.alert("提示", data.message, "info", function () {
                                    toCustomerLoginPage();
                                });
                            }
                        });
                } else {
                    $.messager.alert("提示", "请选择要删除的资源", "info");
                }
            }
        });
}