/**
 * Created by dan on 2/22/2017.
 */
$("#sign-in-btn").on('click', function() {
    $.ajax({
        url: "Login",
        type: 'POST',
        data: {email: $("#textInputUserEmail").value, password: $("#textInputPassword").value},
        success: function() {
            //todo get token
            window.location.href = SITE_URL + "/Home.html";
        },
        error: function() {
            alert("UserName/Password entered incorrect")
        }
    });
});

$("#submit-btn").on('click', function () {
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



function checkLoginState(data) {
    data = data || {};
    if (data.status === "connected") {
        var auth = data.authResponse;
        var accessToken  = auth.accessToken;
        var userID  = auth.userID;
      //  var loginURL = "https://graph.facebook.com/v2.8/" + userID +"?access_token=" + accessToken+"&fields=name,email";
     //   loginURL = "Login"
        //todo move to server and use my url
        //$.post(loginURL,
        $.ajax({
            url: "FaceBookLogin",
            type: 'POST',
            data: {userID: userID, accessToken: accessToken},
            success:function () {
                //todo get token
                window.location.href = SITE_URL + "/Home.html";
            },
            error: function () {
                alert("Invalid Login")
            }
        })
    }
    else {
        console.log('checkLoginState fail')
    }
}

