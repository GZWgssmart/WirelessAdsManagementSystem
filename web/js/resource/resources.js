var contextPath = '';

$(function() {
    setPagination("#list");
    $("#list").datagrid("hideColumn", 'fileName');
    $("#list").datagrid("hideColumn", 'path');
    $("#resLayer").remove();
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

function add() {
    toValidate("addForm");
    if (validateForm("addForm")) {
        var resTypeId = $("#addResourceTypeId").combobox("getValue");
        $.get(contextPath + "/restype/queryJSON/" + resTypeId, function (data) {
            if (data != undefined && data.extension != undefined && data.extension != '') {
                if (checkFile('file', 0, data.extension, 200)) {
                    $('#addForm').ajaxSubmit({
                        url: contextPath + '/res/add',
                        type: 'post',
                        dataType: 'json',
                        beforeSend: function () {
                            $("#addBtn").text("正在添加...");
                            $("#addBtn").attr("disabled", "true");
                        },
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
                        },
                        complete: function () {
                            $("#addBtn").text("确认");
                            $("#addBtn").removeAttr("disabled");
                        }
                    });
                }
            }
        }, "json");
    }
}

function showEdit() {
    var row = selectedRow("list");
    if (row) {
        $("#resourceTypeId").combobox({
            url:contextPath + '/res/list_combo/' + row.id + "/Y",
            method:'get',
            valueField:'id',
            textField:'text',
            panelHeight:'auto'
        });
        $("#editForm").form("load", row);
        openWin("editWin");
    } else {
        $.messager.alert("提示", "请选择需要修改的资源类型信息", "info");
    }
}

function edit() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        var resTypeId = $("#resourceTypeId").combobox("getValue");
        $.get(contextPath + "/restype/queryJSON/" + resTypeId, function (data) {
            if (data != undefined && data.extension != undefined && data.extension != '') {
                if (checkFile('file', 1, data.extension, 200)) {
                    $('#editForm').ajaxSubmit({
                        url: contextPath + '/res/update',
                        type: 'post',
                        dataType: 'json',
                        beforeSend: function () {
                            $("#editBtn").text("正在修改...");
                            $("#cancelBtn").attr("disabled", "true");
                            $("#editBtn").attr("disabled", "true");
                        },
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
                        },
                        complete: function () {
                            $("#editBtn").text("确认");
                            $("#cancelBtn").removeAttr("disabled");
                            $("#editBtn").removeAttr("disabled");
                        }
                    });
                }
            }
        }, "json");
    }
}

function inactive() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'N') {
            $.messager.alert("提示", "资源类型不可用,无需冻结", "info");
        } else {
            $.get(contextPath + "/res/inactive?id=" + row.id,
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
        $.messager.alert("提示", "请选择需要冻结的资源类型", "info");
    }
}

function active() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'Y') {
            $.messager.alert("提示", "资源类型可用,无需激活", "info");
        } else {
            $.get(contextPath + "/res/active?id=" + row.id,
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
        $.messager.alert("提示", "请选择需要激活的资源类型", "info");
    }
}

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/res/search_pager',
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/res/search_pager',
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}