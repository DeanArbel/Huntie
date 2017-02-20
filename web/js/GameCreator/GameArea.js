/**
 * Created by Dean on 17/2/2017.
 */
$(function () {
    $(".loader").hide();
    $(".container").show();
    sessionStorage.setItem("GameAreaVisited", "True");
});

$(document).on('change', 'input:radio[id^="radio_"]', function (event) {
    $('.area-limit-enabled').toggle();
});

$(document).on('click', '#nextPage-btn', function() {
   //TODO: After implementing google maps api adjust this function so it'll update the server
    window.location.href = SITE_URL + "/Manager/GameBuilder.html";
});