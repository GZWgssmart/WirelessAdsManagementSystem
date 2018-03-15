function showAddSegmentWin() {
    var v = $("#showType").combobox("getValue");
    if (v == "segment") {
        showAlreadySegments($("#segments").val())
        openWin("addSegment");
    } else {
        $.messager.alert("提示", "时段播放模式才可设置时段", "info");
    }
}

function showAlreadySegments(segments) {
    moreSegment();
    if (segments != null && segments != "") {
        var data = segments.split(",");
        if (data) {
            if (data.length > 6 && data.length <= 12) {
                moreSegment();
            } else if (data.length > 12 && data.length <= 18) {
                moreSegment();
            } else if (data.length > 18) {
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

function confirmSegment() {
    $("#segments").val(getSegments());
    closeWin("addSegment");
}

function cancelSegment() {
    $("#addSegmentForm").form("clear");
    closeWin("addSegment")
}

function moreSegment() {
    var beginIndex = $("#segBeginIndex").val();
    var trs = $("tr[style='display:none;']");
    if (trs.length > 0) {
        for (var i = beginIndex; i < beginIndex + 6; i++) {
            $('#startTime' + i).timespinner({
                showSeconds: true
            });
            $('#endTime' + i).timespinner({
                showSeconds: true
            });
        }
        for (var j = 0; j < 3; j++) {
            var tr = trs[j];
            $(tr).removeAttr("style");

        }
        $("#segBeginIndex").val(parseInt(beginIndex) + 6);
    } else {
        $.messager.alert("提示", "最多支持设置24个时段", "info");
    }
}

function getSegments() {
    var segments = "";
    var beginIndex = $("#segBeginIndex").val();
    for (var i = 0; i < beginIndex; i++) {
        var start = $("#startTime" + i).timespinner("getValue");
        var end = $("#endTime" + i).timespinner("getValue");
        if (segments == "") {
            if (start != "" && end != "") {
                segments = start + "-" + end;
            }
        } else {
            if (start != "" && end != "") {
                segments += "," + start + "-" + end;
            }
        }
    }
    return segments;
}

function showSegments() {
    var row = selectedRow("list");
    if (row) {
        if (row.showType == "segment") {
            showAlreadySegments(row.segments);
            openWin("addSegment")
        } else {
            $.messager.alert("提示", "请选择时段播放的计划查看", "info");
        }
    } else {
        $.messager.alert("提示", "请选择时段播放的计划查看", "info");
    }
}