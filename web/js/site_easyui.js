var homeTabTitle = '主页';
var defaultPageSize = 50;

function addTab(title, url) {
    if ($('#tabs').tabs('exists', title)) {
        $('#tabs').tabs('select', title);
        var currTab = $('#tabs').tabs('getSelected');
        var url1 = $(currTab.panel('options').content).attr('src');
        if(url1 != undefined && currTab.panel('options').title != homeTabTitle) {
            $('#tabs').tabs('update',{
                tab:currTab,
                options:{
                    content:createFrame(url1)
                }
            })
        }
    } else {
        $('#tabs').tabs('add', {
            title: title,
            content: createFrame(url),
            border: false,
            closable: true
        });
    }
    tabClose();
}

function createFrame(url) {
    return '<iframe scrolling="auto" frameborder="0"  src="' + url + '" style="width:100%;height:100%;"></iframe>';
}

function tabClose() {
    $(".tabs-inner").dblclick(function(){
        var subtitle = $(this).children(".tabs-closable").text();
        $('#tabs').tabs('close', subtitle);
    })
    $(".tabs-inner").bind('contextmenu',function(e){
        $('#mm').menu('show', {
            left: e.pageX,
            top: e.pageY
        });

        var subtitle =$(this).children(".tabs-closable").text();

        $('#mm').data("currtab", subtitle);
        $('#tabs').tabs('select', subtitle);
        return false;
    });
}

function tabCloseEvent() {
    $('#mm-tabupdate').click(function(){
        var currTab = $('#tabs').tabs('getSelected');
        var url = $(currTab.panel('options').content).attr('src');
        if(url != undefined && currTab.panel('options').title != homeTabTitle) {
            $('#tabs').tabs('update',{
                tab:currTab,
                options:{
                    content:createFrame(url)
                }
            })
        }
    })
    $('#mm-tabclose').click(function(){
        var currtab_title = $('#mm').data("currtab");
        $('#tabs').tabs('close',currtab_title);
    })
    $('#mm-tabcloseall').click(function(){
        $('.tabs-inner span').each(function(i,n){
            var t = $(n).text();
            if(t != homeTabTitle) {
                $('#tabs').tabs('close',t);
            }
        });
    });
    $('#mm-tabcloseother').click(function(){
        var prevall = $('.tabs-selected').prevAll();
        var nextall = $('.tabs-selected').nextAll();
        if(prevall.length>0){
            prevall.each(function(i,n){
                var t=$('a:eq(0) span',$(n)).text();
                if(t != homeTabTitle) {
                    $('#tabs').tabs('close',t);
                }
            });
        }
        if(nextall.length>0) {
            nextall.each(function(i,n){
                var t=$('a:eq(0) span',$(n)).text();
                if(t != homeTabTitle) {
                    $('#tabs').tabs('close',t);
                }
            });
        }
        return false;
    });
    $('#mm-tabcloseright').click(function(){
        var nextall = $('.tabs-selected').nextAll();
        if(nextall.length==0){
            alert('后边没有啦~~');
            return false;
        }
        nextall.each(function(i,n){
            var t=$('a:eq(0) span',$(n)).text();
            $('#tabs').tabs('close',t);
        });
        return false;
    });

    $('#mm-tabcloseleft').click(function(){
        var prevall = $('.tabs-selected').prevAll();
        if(prevall.length==0){
            alert('到头了，前边没有啦~~');
            return false;
        }
        prevall.each(function(i,n){
            var t=$('a:eq(0) span',$(n)).text();
            $('#tabs').tabs('close',t);
        });
        return false;
    });

    $("#mm-exit").click(function(){
        $('#mm').menu('hide');
    })
}

$(function() {
    tabCloseEvent();
    $('.site-navi-tab').click(function() {
        var $this = $(this);
        var href = $this.attr('src');
        var title = $this.text();
        addTab(title, href);
    });
});

function setPagination(tableId, pageSize) {
    var ps = defaultPageSize;
    if (typeof pageSize === 'number') {
        ps = pageSize;
    }
    var p = $(tableId).datagrid('getPager');
    $(p).pagination({
        pageSize: ps,
        pageList: [40, 50, 60, 70],
        beforePageText: '第',
        afterPageText: '页    共 {pages} 页',
        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
        onBeforeRefresh: function () {
            $(this).pagination('loading');
            $(this).pagination('loaded');
        }
    });
}

function openWin(id) {
    $("#" + id).window("open");
}

function openDlg(id) {
    $("#" + id).dialog("open");
}

function openWinFitPos(id) {
    var top = ($(document.body).height() - $("#" + id).height()) / 2 - 28;
    var left = ($(document.body).width() - $("#" + id).width()) / 2 - 18;
    $("#" + id).window({
        top:top,
        left:left
    });
    openWin(id);
}

function closeWin(id) {
    $("#" + id).window("close");
}

function closeDlg(id) {
    $("#" + id).dialog("close");
}

function selectedRow(id) {
    return $("#" + id).datagrid("getSelected");
}

function selectedRows(id) {
    return $("#" + id).datagrid("getChecked");
}

function dataGridReload(id) {
    $("#" + id).datagrid("reload");
}

function closeTab(title) {
    $('#tabs').tabs('close', title);
}

$(function(){
    $('.validatebox-text').bind('blur', function(){
        $(this).validatebox('enableValidation').validatebox('validate');
    });
})

function toValidate(formId) {
    $('#' + formId + ' .validatebox-text').validatebox('enableValidation').validatebox('validate');
}

function validateForm(id) {
    return $("#" + id).form("validate");
}

function getQueryParams(dataGridId, formId) {
    var fields =$('#' + formId).serializeArray();
    var params = $("#" + dataGridId).datagrid('options').queryParams;
    $.each( fields, function(i, field){
        params[field.name] = field.value;
    });
    return params;
}

function toPage(url) {
    window.location.href = url;
}

function toCustomerLoginPage() {
    top.location.href =  "/index";
}

function toAdminLoginPage() {
    top.location.href = "/admin/login_page";
}

function checkFile(name, index, type, size) {
    var file = document.getElementsByName(name)[index].files[0];
    if (file != undefined) {
        var fileName = file.name;
        var fileType = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
        var maxSize = size * 1024 * 1024;
        if (file.size >= maxSize) {
            $.messager.alert("提示", "文件大小最大为" + size + "MB", "info");
            return false;
        }
        if (type.indexOf(fileType) < 0) {
            $.messager.alert("提示", "文件后缀只能为" + type, "info");
            return false;
        }
    }
    return true;
}

function getFileName(name, index, dest) {
    var file = document.getElementsByName(name)[index].files[0];
    if (file != undefined) {
        var fileName = file.name.substr(0, file.name.lastIndexOf("."));
        $("#" + dest).textbox("setValue", fileName);
    }
}

//////////////////////////////////////

function formatterDate(value) {
    if (value == undefined || value == null || value == '') {
        return "";
    }
    else {
        var date = new Date(value);
        var year = date.getFullYear().toString();
        var month = (date.getMonth() + 1);
        var day = date.getDate().toString();
        var hour = date.getHours().toString();
        var minutes = date.getMinutes().toString();
        var seconds = date.getSeconds().toString();
        if (month < 10) {
            month = "0" + month;
        }
        if (day < 10) {
            day = "0" + day;
        }
        if (hour < 10) {
            hour = "0" + hour;
        }
        if (minutes < 10) {
            minutes = "0" + minutes;
        }
        if (seconds < 10) {
            seconds = "0" + seconds;
        }
        return year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds;
    }
}

function formatterDate1(value) {
    if (value == undefined || value == null || value == '') {
        return "";
    }
    else {
        var date = new Date(value);
        var year = date.getFullYear().toString();
        var month = (date.getMonth() + 1);
        var day = date.getDate().toString();
        if (month < 10) {
            month = "0" + month;
        }
        if (day < 10) {
            day = "0" + day;
        }

        return year + "-" + month + "-" + day;
    }
}


function formatterRole(value) {
    if (value == 'super') {
        return "超级管理员";
    } else {
        return "普通管理员";
    }
}

function formatterStatus(value) {
    if (value == "Y") {
        return "可用";
    } else {
        return "不可用";
    }
}

function formatterOnline(value) {
    if (value == "Y") {
        return "在线";
    } else {
        return "离线";
    }
}

function formatterLong(value) {
    if (value != undefined && value != '' && value.length >= 35) {
        var abValue = value.substring(0, 34) + "...";
        return "<a href='javascript:void(0);' title='" + value + "' class='easyui-tooltip'>" + abValue + "</a>";
    } else {
        return value;
    }
}

function formatterShowType(value) {
    if (value == 'order') {
        return "顺序播放";
    } else if (value == 'now') {
        return "即时播放";
    } else if (value == "segment") {
        return "时段播放";
    }

}

function formatterCode(value, row, index) {
    return row.device.code;
}

function formatterArea(value) {
    return "区域" + value;
}

function formatterCheckStatus(value) {
    if (value == 'not_submit') {
        return "未提交";
    } else if (value == 'checking') {
        return "审核中";
    } else if (value == "checked") {
        return "已审核";
    } else if (value == "finish") {
        return "已完成";
    }

}

function formatterPlanType(value) {
    if (value == 'one') {
        return '单个';
    } else if (value == 'multiple') {
        return '多个';
    } else if (value == 'group') {
        return '分组';
    } else if (value == 'all') {
        return '全部';
    }
}

function formatterName(value) {
    return value.name;
}

function formatterBoolean(value) {
    if (value) {
        return "是";
    } else {
        return "否";
    }
}

function formatterYN(value) {
    if (value == 'Y') {
        return "是";
    } else {
        return "否";
    }
}
/////////////////////////////////////

function goBack() {
    window.history.back();
}