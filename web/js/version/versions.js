var contextPath = '';

$(function() {
    setPagination("#list")
});

function add() {
    toValidate("addForm");
    if (validateForm("addForm")) {
        if (checkFile("file", 0, '.jpg,.bmp,.png', 10)) {
            $('#addForm').ajaxSubmit({
                url:contextPath + '/version/add',
                type:'post',
                dataType: 'json',
                success: function (data) {
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
            });
        }
    }
}

function showEdit() {
    var row = selectedRow("list");
    if (row) {
        $("#editForm").form("load", row);
        openWin("editWin");
    } else {
        $.messager.alert("提示", "请选择需要修改的版本信息", "info");
    }
}

function edit() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        if (checkFile("file", 1, '.jpg,.bmp,.png', 10)) {
            $('#editForm').ajaxSubmit({
                url: contextPath + '/version/update',
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    if (data.result == "success") {
                        closeWin("editWin");
                        dataGridReload("list");
                        $("#editForm").form("clear");
                    } else if (data.result == 'notLogin') {
                        $.messager.alert("提示", data.message, "info", function() {
                            toAdminLoginPage();
                        });
                    } else {
                        $.messager.alert("提示", data.message, "info");
                    }
                }
            });
        }
    }
}

function inactive() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'N') {
            $.messager.alert("提示", "版本信息不可用,无需冻结", "info");
        } else {
            $.get(contextPath + "/version/inactive?id=" + row.id,
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
        $.messager.alert("提示", "请选择需要冻结的版本信息", "info");
    }
}

function active() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'Y') {
            $.messager.alert("提示", "版本信息可用,无需激活", "info");
        } else {
            $.get(contextPath + "/version/active?id=" + row.id,
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
        $.messager.alert("提示", "请选择需要激活的版本信息", "info");
    }
}

function viewImg() {
    var row = selectedRow("list");
    if (row) {
        $("#verImg").attr("src", contextPath + "/" + row.path);
        openWin("viewWin");
    } else {
        $.messager.alert("提示", "请选择需要查看的版本信息", "info");
    }
}

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/version/search_pager',
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/version/search_pager',
        pageSize:defaultPageSize,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}