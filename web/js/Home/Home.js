/**
 * Created by Dean on 18/2/2017.
 */
$(document).on('click', "#joinGame-btn", function() {
   $.ajax({
       url: "newGame",
       type: 'POST',
       success: function(response) {
           window.location = SITE_URL + "/Manager/GameType.html";
       },
       error: function(xhr, status, error) {
           if (xhr.status === 400) {
               displayError(xhr.getResponseHeader("errorText"));
           }
       }
   });
});
