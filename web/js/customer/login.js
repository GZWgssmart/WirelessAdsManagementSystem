var contextPath = '';

function login() {
    $.post(contextPath + "/customer/login",
        $("#login_form").serialize(),
        function (data) {
            var result = data.result;
            if(result == "success") {
                window.location.href = contextPath + "/customer/home";
            } else {
                $("#errMsg").html(data.message);
            }
        }
    );
}