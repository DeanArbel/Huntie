/**
 * Created by Dean Arbel on 24/02/2017.
 */
$(function () {
    $(".loader").hide();
    $(".container").show();
    sessionStorage.setItem("PublishGame", "True");
});

$(document).on('click', '#prevPage-btn', function() {
    window.location.href = SITE_URL + "/Manager/GameBuilder.html";
});

$(document).on('click', '#nextPage-btn', function() {
    //TODO: Implement (update server and on success open "published page successfuly" dialog, which will take you to the game or home)
});