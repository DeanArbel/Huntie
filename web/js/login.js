/**
 * Created by dan on 2/22/2017.
 */
$("#sign-in-btn").on('click', function() {
    $.ajax({
        url: "Login",
        type: 'POST',
        data: {email: $("#textInputUserEmail").val(), password: $("#textInputPassword").val()},
        success: function(data) {
            sessionStorage.setItem("access token",data);
            window.location.href = "/home.html";
        },
        error: function(msg) {
            alert(msg.getResponseHeader("errortext"));
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
            success:function (data) {
                sessionStorage.setItem("access token",data);
                window.location.href = "/home.html";
                FB.logout()
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

window.addEventListener('load', loadRoutine);
function tokenCheck() {
    var token = sessionStorage.getItem("access token");
    if (token !== null) {
        $.ajax({
            url: "TokenVerification",
            type: 'POST',
            data: {token: token},
            success: function () {
                window.location.href = "/home.html";
            }
        })
    }
}

function rememberMe() {

    if (localStorage.chkbx && localStorage.chkbx != '') {
        $('#remember_me').attr('checked', 'checked');
        $('#textInputUserEmail').val(localStorage.usrname);
        $('#textInputPassword').val(localStorage.pass);
    } else {
        $('#remember_me').removeAttr('checked');
        $('#textInputUserEmail').val('');
        $('#textInputPassword').val('');
    }
}

$('#remember_me').click(function() {

    if ($('#remember_me').is(':checked')) {
        // save username and password
        localStorage.usrname = $('#textInputUserEmail').val();
        localStorage.pass = $('#textInputPassword').val();
        localStorage.chkbx = $('#remember_me').val();
    } else {
        localStorage.usrname = '';
        localStorage.pass = '';
        localStorage.chkbx = '';
    }
});

function loadRoutine() {
    rememberMe();
    tokenCheck();
}