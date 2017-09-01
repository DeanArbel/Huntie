/**
 * Created by Dean on 18/2/2017.
 */
$(document).on('click', "#createGame-btn", function() {
   $.ajax({
       url: "newGame",
       type: 'POST',
       data: {token: sessionStorage.getItem("access token")},
       success: function(response) {
           window.location.href =   "/Manager/game-type.html";
       },
       error: function(xhr, status, error) {
           if (xhr.status === 499) {
               $("#myModal").modal('show');
           } else if (xhr.status === 480) {
               window.location.href = "/index.jsp";
           }
           else if (xhr.status === 400) {
               console.log(xhr.getResponseHeader(error));
           }
       }
   });
});

$(document).on('click', "#findGame-btn", function() {
    window.location.href =   "/Player/find-game.html";
});

$(document).on('click', "#profile-btn", function() {
    window.location.href =   "/my-profile.html";
});

$(document).on('click', '#modal-yes-btn', function() { window.location.href =   "/Manager/game-type.html"; });
$(document).on('click', '#modal-no-btn', function() {
    $.ajax({
        url: "newGame",
        type: 'POST',
        data: { createNewGame: true, token: sessionStorage.getItem("access token") },
        success: function(response) {
            window.location.href =   "/Manager/game-type.html";
        },
        error: function(xhr, status, error) {
            if (xhr.status === 400) {
                console.log(xhr.getResponseHeader(error));
            }
        }
    });
});
