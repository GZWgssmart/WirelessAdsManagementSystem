var contextPath = '';

$(function() {
    setPagination("#list")
});

function addGroup() {
    toValidate("addForm");
    if (validateForm("addForm")) {
        $.post(contextPath + "/devgroup/add",
            $("#addForm").serialize(),
            function (data) {
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
        );
    }
}

function showEdit() {
    var row = selectedRow("list");
    if (row && row.name != "默认分组") {
        $("#editForm").form("load", row);
        openWin("editWin");
    } else {
        $.messager.alert("提示", "请选择需要修改的终端分组信息（不可修改默认分组）", "info");
    }
}

function editGroup() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        $.post(contextPath + "/devgroup/update",
            $("#editForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    closeWin("editWin");
                    $.messager.alert("提示", data.message, "info", function () {
                        dataGridReload("list");
                    });
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function() {
                        toCustomerLoginPage();
                    });
                } else {
                    $("#errMsg").html(data.message);
                }
            }
        );
    }
}

function inactive() {
    var row = selectedRow("list");
    if (row && row.name != "默认分组") {
        if (row.status == 'N') {
            $.messager.alert("提示", "终端分组不可用,无需冻结", "info");
        } else {
            $.get(contextPath + "/devgroup/inactive?id=" + row.id,
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
        $.messager.alert("提示", "请选择需要冻结的终端分组（默认分组不可冻结）", "info");
    }
}

function active() {
    var row = selectedRow("list");
    if (row && row.name != "默认分组") {
        if (row.status == 'Y') {
            $.messager.alert("提示", "终端分组可用,无需激活", "info");
        } else {
            $.get(contextPath + "/devgroup/active?id=" + row.id,
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
        $.messager.alert("提示", "请选择需要激活的终端分组(默认分组无需激活)", "info");
    }
}