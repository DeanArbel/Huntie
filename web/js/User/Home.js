/**
 * Created by Dean on 18/2/2017.
 */
$(document).on('click', "#createGame-btn", function() {
   $.ajax({
       url: "newGame",
       type: 'POST',
       success: function(response) {
           window.location.href = SITE_URL + "/Manager/GameType.html";
       },
       error: function(xhr, status, error) {
           if (xhr.status === 499) {
               $("#myModal").modal('show');
           }
           else if (xhr.status === 400) {
               console.log(xhr.getResponseHeader(error));
           }
       }
   });
});

$(document).on('click', "#findGame-btn", function() {
    window.location.href = SITE_URL + "/Player/FindGame.html";
});

$(document).on('click', "#profile-btn", function() {
    window.location.href = SITE_URL + "/MyProfile.html";
});

$(document).on('click', '#modal-yes-btn', function() { window.location.href = SITE_URL + "/Manager/GameType.html"; });
$(document).on('click', '#modal-no-btn', function() {
    $.ajax({
        url: "newGame",
        type: 'POST',
        data: { createNewGame: true },
        success: function(response) {
            window.location.href = SITE_URL + "/Manager/GameType.html";
        },
        error: function(xhr, status, error) {
            if (xhr.status === 400) {
                console.log(xhr.getResponseHeader(error));
            }
        }
    });
});