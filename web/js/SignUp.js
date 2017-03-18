/**
 * Created by dan on 3/14/2017.
 */

$("#submit").on('click', function () {
    if($("#email").val() !== null && $("#UserName").value !== null && $("#password").val() !== null && $("#confirm_password").val() !== null) {
        if ($("#password").val() === $("#confirm_password").val()) {
            $.ajax({
                url: "SignUp",
                type: 'POST',
                data: {email: $("#email").val(), username: $("#UserName").val(), password: $("#password").val()},
                success: function () {
                    window.location.href = SITE_URL + "/Login.html";
                },
                error: function (data) {
                    alert(data);
                }
            });
        }
        else {
            alert("please type the same password at 'password' and 'confirm password'")
        }
    }
    else{
        alert("Please fill all the fields")
    }
});