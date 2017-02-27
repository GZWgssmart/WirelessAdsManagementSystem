var contextPath = '';

$(function() {
    setPagination("#list")
});

function addType() {
    toValidate("addForm");
    if (validateForm("addForm")) {
        $.post(contextPath + "/restype/add",
            $("#addForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    $("#addWin").window("close");
                    dataGridReload("list");
                    $("#addForm").form("clear");
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function() {
                        toAdminLoginPage();
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
    if (row) {
        var show = false;
        var notShow = false;
        if (row.showDetailSetting == "Y") {
            show = true;
        } else {
            notShow = true;
        }
        $("#editShowDetailSetting").combobox({
            data:[{
                id: 'Y',
                text: '显示',
                selected:show
            },{
                id: 'N',
                text: '不显示',
                selected:notShow
            }]
        });
        $("#editForm").form("load", row);
        openWin("editWin");
    } else {
        $.messager.alert("提示", "请选择需要修改的资源类型信息", "info");
    }
}

function editType() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        $.post(contextPath + "/restype/update",
            $("#editForm").serialize(),
            function (data) {
                if (data.result == "success") {
                    closeWin("editWin");
                    $.messager.alert("提示", data.message, "info", function () {
                        dataGridReload("list");
                    });
                } else if (data.result == 'notLogin') {
                    $.messager.alert("提示", data.message, "info", function() {
                        toAdminLoginPage();
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
    if (row) {
        if (row.status == 'N') {
            $.messager.alert("提示", "资源类型不可用,无需冻结", "info");
        } else {
            $.get(contextPath + "/restype/inactive?id=" + row.id,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function() {
                            toAdminLoginPage();
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
            $.get(contextPath + "/restype/active?id=" + row.id,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function() {
                            toAdminLoginPage();
                        });
                    }
                });
        }
    } else {
        $.messager.alert("提示", "请选择需要激活的资源类型", "info");
    }
}