/**
 * Created by dan on 2/22/2017.
 */
$(document).on('click', "#sign-in-btn", function() {
    $.ajax({
        url: "Verify",
        type: 'POST',
        success: function() {
            window.location.href = SITE_URL + "/Home/Home.html";
        },
        error: function() {
            !red commenet  username/password invalid
        }
    });
});

