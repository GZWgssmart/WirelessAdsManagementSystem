var contextPath = '';

function edit() {
    toValidate("editForm");
    if (validateForm("editForm")) {
        $.post(contextPath + "/admin/update",
            $("#editForm").serialize(),
            function(data) {
                if(data.result == "success") {
                    closeWin("editWin");
                    $.messager.alert("提示", data.message, "info", function() {
                        window.location.href = contextPath + "/admin/query/${requestScope.admin.id }";
                    });
                } else {
                    $("#errMsg").html(data.message);
                }
            }
        );
    }
}