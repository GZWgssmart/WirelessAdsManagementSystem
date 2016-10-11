var contextPath = '';

var planId = "none";
var addOrDeleteRes = false;

$(function() {
    setPagination("#list");

    $('#list').datagrid({
        onDblClickCell: function(rowIndex, rowData){
            showPlanDetail();
        }
    });
    $("#list").datagrid("hideColumn", 'versionId');
    $("#resList").datagrid("hideColumn", 'fileName');
    $("#resList").datagrid("hideColumn", 'path');
    $("#chosenResWin").window({
            onClose:function () {
                confirmAllRes();
            }
        }
    );
});

function showAdd() {
    planId = 'none';
    addOrDeleteRes = false;
    $("#addAreaR").html("");
    $("#addVersionImg").attr("src", "");
    $("#addForm").form("clear");
    $("#confirmSeg").removeAttr("disabled");
    $("#cancelSeg").removeAttr("disabled");
    $("#moreSeg").removeAttr("disabled");
    openWinFitPos('addWin');
}

function add() {
    toValidate("addForm");
    if (validateForm("addForm")) {
        $("#addPlan").attr("disabled", "true");
        var resourceDetails = getResourceDetails();
        if (resourceDetails == '') {
            $.messager.alert("提示", "请先添加需要发布的资源", "info");
        } else {
            $("#resourceDetails").val(resourceDetails);
            $.post(contextPath + '/pubplan/add',
                $("#addForm").serialize(),
                function (data) {
                    $("#addPlan").removeAttr("disabled");
                    if (data.result == "success") {
                        $("#addWin").window("close");
                        dataGridReload("list");
                        $("#addForm").form("clear");
                    } else {
                        $.messager.alert("提示", data.message, "info");
                    }
                }, "json");
        }
    }
}

function getResourceDetails() {
    var detailInputs = $("input[id*='resourceDetails']");
    var resourceDetails = "";
    for (var i = 0; i < detailInputs.size() - 1; i++) {
        var oDetail = $("#resourceDetails" + (i + 1)).val();
        if (oDetail != undefined && oDetail != '[]') {
            var detail = oDetail.substring(1, oDetail.length - 1);
            if (resourceDetails == "") {
                resourceDetails = detail;
            } else {
                resourceDetails += "," + detail;
            }
        }
    }
    return resourceDetails;
}

function showEdit() {
    var row = selectedRow("list");
    if (row) {
        if (row.checkStatus == 'not_submit') {
            planId = row.id;
            addOrDeleteRes = false;
            $("#editAreaR").html("");
            $("#editForm").form("load", row);
            $("#editArea").combobox({
                url: contextPath + '/version/list_combo_area/' + row.versionId + '/' + row.area,
                method: 'get',
                valueField: 'id',
                textField: 'text',
                panelHeight: 'auto'
            });
            $.get(contextPath + "/pubplan/all_dev/" + planId, function (data) {
                if (data.code == 200) {
                    $("#editDeviceId").val(data.result);
                }
            });
            $("#editDeviceCode").textbox("setValue", row.name);
            $("#editResourceName").textbox("setValue", row.resourceName);
            $("#confirmSeg").removeAttr("disabled");
            $("#cancelSeg").removeAttr("disabled");
            $("#moreSeg").removeAttr("disabled");
            showArea(planId, row.versionId);
            openWin("editWin");
        } else {
            $.messager.alert("提示", "请选择未提交审核的计划", "info");
        }
    } else {
        $.messager.alert("提示", "请选择需要修改的计划", "info");
    }
}

function edit() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        $("#editPlan").attr("disabled", "true");
        $.post(contextPath + '/pubplan/update',
            $("#editForm").serialize() + "&" + $("#addSegmentForm").serialize() + "&resourceDetails=" + getResourceDetails(),
            function (data) {
                $("#editPlan").removeAttr("disabled");
                if (data.result == "success") {
                    closeWin("editWin");
                    dataGridReload("list");
                    $("#editForm").form("clear");
                } else {
                    $.messager.alert("提示", data.message, "info");
                }
            },
            'json'
        );
    }
}

function showPlanDetail() {
    var row = selectedRow("list");
    if (row) {
        addTab(row.name + "计划详情", contextPath + "/publish/list_page/" + row.id);
    } else {
        $.messager.alert("提示", "请先选择计划", "info");
    }
}

function toCheck() {
    var row = selectedRow("list");
    if (row) {
        if (row.checkStatus != "not_submit") {
            $.messager.alert("提示", "请选择未提交审核的计划", "info");
        } else {
            $.get(contextPath + "/pubplan/check?id=" + row.id + "&checkStatus=checking",
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    } else {
                        $.messager.alert("提示", data.message, "info");
                    }
                });
        }
    } else {
        $.messager.alert("提示", "请选择需要提交审核的计划", "info");
    }
}

function inactive() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'N') {
            $.messager.alert("提示", "计划不可用,无需冻结", "info");
        } else {
            $.get(contextPath + "/pubplan/inactive?id=" + row.id,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    }
                });
        }
    } else {
        $.messager.alert("提示", "请选择需要冻结的计划", "info");
    }
}

function active() {
    var row = selectedRow("list");
    if (row) {
        if (row.status == 'Y') {
            $.messager.alert("提示", "计划可用,无需激活", "info");
        } else {
            $.get(contextPath + "/pubplan/active?id=" + row.id,
                function (data) {
                    if (data.result == "success") {
                        $.messager.alert("提示", data.message, "info");
                        dataGridReload("list");
                    }
                });
        }
    } else {
        $.messager.alert("提示", "请选择需要激活的计划", "info");
    }
}

function doSearch() {
    $("#list").datagrid({
        url:contextPath + '/pubplan/search_pager',
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll() {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/pubplan/search_pager',
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}

function showDevWin() {
    openWinFitPos("devWin");
    $("#devWin").window("refresh", contextPath + "/device/list_page_choose");
}

function doSearchDev() {
    $("#devList").datagrid({
        url:contextPath + '/device/search_pager',
        pageSize:20,
        queryParams:getQueryParams("devList", "devSearchForm")
    });
    setPagination("#devList");
}

function searchAllDev() {
    $("#devSearchForm").form("clear");
    $("#devList").datagrid({
        url:contextPath + '/device/search_pager',
        pageSize:20,
        queryParams:getQueryParams("devList", "devSearchForm")
    });
    setPagination("#devList");
}

function refreshAllDev() {
    $("#devList").datagrid("reload");
}

function chooseDev(type) {
    $("#addType").val(type);
    $("#editType").val(type);
    if (type == "multiple") {
        var rows = selectedRows("devList");
        if (rows) {
            var deviceIds = "";
            var deviceCodes = "";
            var versionId = '';
            var versionName = '';
            var sameVersion = true;
            $.each(rows, function (index, row) {
                if (versionId == '') {
                    versionId = row.version.id;
                } else {
                    if (versionId != row.version.id) {
                        sameVersion = false;
                    }
                }
                if (versionName == '') {
                    $("#addVersionName").val(row.version.name);
                    $("#editVersionName").val(row.version.name);
                }
                if (deviceIds == "") {
                    deviceIds = row.id;
                } else {
                    deviceIds += "," + row.id;
                }
                if (deviceCodes == "") {
                    deviceCodes = row.code;
                } else {
                    deviceCodes += "," + row.code;
                }
            });
            if (sameVersion) {
                $("#addDeviceId").val(deviceIds);
                $("#editDeviceId").val(deviceIds);
                $("#addDeviceCode").textbox("setValue", deviceCodes);
                $("#editDeviceCode").textbox("setValue", deviceCodes);
                $("#addVersionId").val(versionId);
                $("#editVersionId").val(versionId);
                showArea(planId, versionId);
                closeWin("devWin");
            } else {
                $.messager.alert("提示", "请选择相同版本的设备", "info");
            }
        } else {
            $.messager.alert("提示", "请选择设备", "info");
        }
    } else if (type == "group") {
        var groupId = $("#deviceGroupId").combobox("getValue");
        var versionId = $("#versionId").combobox("getValue");
        var versionName = $("#versionId").combobox("getText");
        if (groupId != undefined && groupId != '' && versionId != undefined && versionId != '') {
            $("#addDeviceId").val(groupId);
            $("#editDeviceId").val(groupId);
            $("#addVersionId").val(versionId);
            $("#editVersionId").val(versionId);
            var groupName = $("#deviceGroupId").combobox("getText");
            $("#addDeviceCode").textbox("setValue", groupName + "及" + versionName + "的全部终端");
            $("#editDeviceCode").textbox("setValue", groupName + "及" + versionName + "的全部终端");
            $("#addGroupName").val(groupName);
            $("#editGroupName").val(groupName);
            showArea(planId, versionId);
            $("#addVersionName").val(versionName);
            $("#editVersionName").val(versionName);
            closeWin("devWin");
        } else {
            $.messager.alert("提示", "请选择分组及版本", "info");
        }
    } else if (type == "all") {
        var groupId = $("#deviceGroupId").combobox("getValue");
        if (groupId != undefined && groupId != '') {
            $.messager.alert("提示", "请点击查询所有后,只选择版本", "info");
        } else {
            var versionId = $("#versionId").combobox("getValue");
            var versionName = $("#versionId").combobox("getText");
            if (versionId != undefined && versionId != '') {
                $("#addVersionId").val(versionId);
                $("#editVersionId").val(versionId);
                $("#addDeviceCode").textbox("setValue", versionName + "的全部终端");
                $("#editDeviceCode").textbox("setValue", versionName + "的全部终端");
                showArea(planId, versionId);
                $("#addVersionName").val(versionName);
                $("#editVersionName").val(versionName);
                closeWin("devWin");
            } else {
                $.messager.alert("提示", "请选择版本", "info");
            }
        }
    }
}

function showArea(planId, versionId) {
    $.get(contextPath + '/version/list_combo_area/' + versionId + '/0',
        function (data) {
            $("#addAreaR").html("");
            $("#editAreaR").html("");
            $("#resourceDetailDiv").html("");
            $.get(contextPath + "/version/querybyid/" + versionId, function (data) {
                if (data != null && data != "") {
                    $("#addVersionImg").attr("src", "/" + data);
                    $("#editVersionImg").attr("src", "/" + data);
                    $("#addVersionImg").attr("style", "max-width:200px; max-height:200px;");
                    $("#editVersionImg").attr("style", "max-width:200px; max-height:200px;");
                }
            });
            $.each(data, function(idx, item) {
                if ((idx + 1) % 4 == 0) {
                    $("#addAreaR").append("<br />");
                    $("#editAreaR").append("<br />");
                }
                var str = "<a style='font-size:16px;' href='javascript:;' onclick='showChosenResWin(\"" + planId + "\", " + item.id + ")'>区域" +  item.id + "资源</a>&nbsp;&nbsp;&nbsp;&nbsp;";
                $("#addAreaR").append(str);
                $("#editAreaR").append(str);
                $("#resourceDetailDiv").append('<input id="resourceDetails' + (idx + 1) + '" type="hidden" name="resourceDetails' + (idx + 1) + '" />');
                $.get(contextPath + '/publish/search_chosen_res/' + planId + "/" + item.id, function (data) {
                    var details = JSON.stringify(data.rows);
                    $("#resourceDetails" + item.id).val(details);
                });
            });
        }, "json"
    );
}

var currentArea;

function showChosenResWin(planId, area) {
    currentArea = area;
    var rowsJSON = {'rows':[]};
    var rowsData = $("#resourceDetails" + area).val();
    if (planId != 'none' && !addOrDeleteRes) {
        $("#chresList").datagrid({
            url:contextPath + '/publish/search_chosen_res/' + planId + "/" + area,
            method:'get'
        });
    } else {
        if (rowsData != "") {
            rowsJSON.rows = JSON.parse(rowsData);
        }
        $("#chresList").datagrid("loadData", rowsJSON);
    }
    openWinFitPos("chosenResWin");
}

function showResWin() {
    $("#resList").datagrid({
        url:contextPath + '/res/search_pager'
    });
    openWinFitPos("resWin");
}

function addResourceToArea() {
    var row = selectedRow("resList");
    if (row) {
        hideStayTime();
        hideShowCount();
        if (row.status == 'Y') {
            if (row.resourceType.name == '图片' || row.resourceType.name == '文字') {
                showStayTime();
            } else if (row.resourceType.name == '视频') {
                showShowCount();
            }
            $("#resourceId").val(row.id);
            openWinFitPos("detailWin");
        } else {
            $.messager.alert("提示", "必须选择可用状态的资源", "info");
        }
    } else {
        $.messager.alert("提示", "请选择需要添加到区域的资源", "info");
    }
}

function confirmAddResourceToArea() {
    toValidate("detailForm");
    if (validateForm("detailForm")) {
        var resRow = selectedRow("resList");
        var chresRow = selectedRow("chresList");
        var resourceId = '';
        var resourceName = '';
        var rowsJSON = $("#chresList").datagrid("getData");
        if (resRow) { // 否则如果是添加资源
            var detail = '{"resourceId":"' + resRow.id + '","resourceName":"' + resRow.name + '",'
                + '"area":' + currentArea + ',"showType":"' + $("#showType").combobox("getValue") + '","startTimeStr":"' + $("#startTimeStr").datebox("getValue") + '","'
                + 'endTimeStr":"' + $("#endTimeStr").datebox("getValue") + '","stayTime":"' + $("#stayTime").textbox("getValue")
                + '","showCount":"' + $("#showCount").textbox("getValue")+ '","segments":"' + getSegments() + '"}';
            var detailJSON = JSON.parse(detail);
            rowsJSON.rows.push(detailJSON);
            $('#resList').datagrid('clearSelections');
        } else if (chresRow) { // 如果是修改已经添加的资源
            var chresRowindex = $("#chresList").datagrid("getRowIndex", chresRow);
            chresRow.showType = $("#showType").combobox("getValue");
            chresRow.startTimeStr = $("#startTimeStr").datebox("getValue");
            chresRow.endTimeStr = $("#endTimeStr").datebox("getValue");
            chresRow.stayTime = $("#stayTime").textbox("getValue");
            chresRow.showCount = $("#showCount").textbox("getValue");
            chresRow.segments = $("#segments").val();
            rowsJSON.rows[chresRowindex] = chresRow;
        }
        $("#chresList").datagrid("loadData", rowsJSON);
        $("#detailForm").form("clear");
        $("#addSegmentForm").form("clear");
        addOrDeleteRes = true;
        closeWin("detailWin");
        closeWin("resWin");
    }
}

function showResEdit() {
    var row = selectedRow("chresList");
    if (row) {
        hideStayTime();
        hideShowCount();
        $("#resourceId").val(row.id);
        $("#detailForm").form("load", row);
        if (row.stayTime != null && row.stayTime != '') {
            showStayTime();
        }
        if (row.showCount != null && row.showCount != '') {
            showShowCount();
        }
        $("#segments").val(row.segments);
        openWinFitPos("detailWin");
    } else {
        $.messager.alert("提示", "请选择需要修改的资源", "info");
    }
}

function hideStayTime() {
    $("#stayTime").textbox({"required":false,"novalidate":true});
    $("#stayTimeTR").attr("style", "display:none");
    $("#stayTime").textbox("setValue", "");
}

function showStayTime() {
    $("#stayTime").textbox({"required":true,"novalidate":true});
    $("#stayTimeTR").attr("style", "");
}

function hideShowCount() {
    $("#showCount").textbox({"required":false,"novalidate":true});
    $("#showCountTR").attr("style", "display:none");
    $("#showCount").textbox("setValue", "");
}

function showShowCount() {
    $("#showCount").textbox({"required":true,"novalidate":true});
    $("#showCountTR").attr("style", "");
}

function deleteRes() {
    var row = selectedRow("chresList");
    if (row) {
        var index = $("#chresList").datagrid("getRowIndex", row);
        $('#chresList').datagrid('deleteRow', index);
        addOrDeleteRes = true;
    } else {
        $.messager.alert("提示", "请选择需要删除的资源", "info");
    }
}

function confirmAllRes() {
    var data = $("#chresList").datagrid("getData");
    var details = JSON.stringify(data.rows);
    $("#resourceDetails" + currentArea).val(details);
}

function confirmAllResAndClose() {
    confirmAllRes();
    closeWin("chosenResWin");
}

function doSearchRes() {
    $("#resList").datagrid({
        url:contextPath + '/res/search_pager',
        pageSize:20,
        queryParams:getQueryParams("resList", "resSearchForm")
    });
    setPagination("#list");
}

function searchAllRes() {
    $("#resSearchForm").form("clear");
    $("#resList").datagrid({
        url:contextPath + '/res/search_pager',
        pageSize:20,
        queryParams:getQueryParams("resList", "resSearchForm")
    });
    setPagination("#list");
}