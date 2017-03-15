/**
 * Created by dan on 3/14/2017.
 */

$("#submit").on('click', function () {
    if($("#email").value !== null && $("#UserName").value !== null && $("#password").value !== null && $("#confirm_password").value !== null) {
        if ($("#password").value === $("#confirm_password").value) {
            $.ajax({
                url: "SignUp",
                type: 'POST',
                data: {email: $("#email").value, username: $("#UserName").value, password: $("#password").value},
                success: function () {
                    window.location.href = SITE_URL + "/Login.html";
                },
                error: function () {
                    //todo alert what is already in use
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