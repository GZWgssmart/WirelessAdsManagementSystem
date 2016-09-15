var contextPath = '';

$(function() {
    setPagination("#list");
});

function doSearch(planId) {
    $("#list").datagrid({
        url:contextPath + '/publish/search_pager/' + planId,
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function searchAll(planId) {
    $("#searchForm").form("clear");
    $("#list").datagrid({
        url:contextPath + '/publish/search_pager/' + planId,
        pageSize:20,
        queryParams:getQueryParams("list", "searchForm")
    });
    setPagination("#list");
}

function refreshAll() {
    $("#list").datagrid("reload");
}

function showSegments() {
    var row = selectedRow("list");
    if (row) {
        if (row.showType == "segment") {
            showAlreadySegments(row.segments);
            openWinFitPos("addSegment")
        } else {
            $.messager.alert("提示", "请选择时段播放的计划查看", "info");
        }
    } else {
        $.messager.alert("提示", "请选择时段播放的计划查看", "info");
    }
}

function showAlreadySegments(segments) {
    if (segments != null && segments != "") {
        var data = segments.split(",");
        if (data) {
            if (data.length > 6 && data.length <= 12) {
                moreSegment();
            } else if (data.length > 12 && data.length <= 18) {
                moreSegment();
                moreSegment();
            } else if (data.length > 18) {
                moreSegment();
                moreSegment();
                moreSegment();
            }
            for (var i = 0; i < data.length; i++) {
                var time = data[i].split("-");
                $("#startTime" + i).timespinner("setValue", time[0]);
                $("#endTime" + i).timespinner("setValue", time[1]);
            }
        }
    }
}

