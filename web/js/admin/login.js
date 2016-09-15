var contextPath = '';

function login() {
    $.post(contextPath + "/admin/login",
        $("#login_form").serialize(),
        function (data) {
            var result = data.result;
            if(result == "success") {
                window.location.href = contextPath + "/admin/home";
            } else {
                $("#errMsg").html(data.message);
            }
        }
    );
}