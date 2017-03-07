/**
 * Created by dan on 2/22/2017.
 */
$(document).on('click', "#sign-in-btn", function() {
    $.ajax({
        url: "Login",
        type: 'POST',
        data: {email: $("textInputUserEmail").value, password: $("textInputPassword").value},
        success: function() {
            window.location.href = SITE_URL + "/Home.html";
        },
        error: function() {
            alert("UserName/Password entered incorrect")
        }
    });
});

$(document).on('click', "#submit-btn", function () {
   $.ajax({
       url: "ValidEmail",
       type: 'POST',
       success:function () {
           $("pwdModel").hideModal();
           $("confModel").show();
       },
       error: function () {
           alert("Invalid Email")
       }
   }) ;
});
