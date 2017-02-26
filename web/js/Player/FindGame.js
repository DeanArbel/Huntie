/**
 * Created by Dean Arbel on 25/02/2017.
 */

var gameCode;

$(function () {
    $(".loader").hide();
    $(".settings-container").show();
    sessionStorage.setItem("PrevPage", "FindGame");
    initGlobalVars();
});

$(document).on('click', '#prevPage-btn', function() {
    window.location.href = SITE_URL + "/Home.html";
});

$(document).on('click', '#game-find-btn', function() {
    //TODO: Check the given id is in the server (the server will give the right address)
    window.location.href = SITE_URL + "/Player/GameEntry.html?gameid=" + gameCode.value;
});

function initGlobalVars() {
    gameCode = $("#game-code")[0];
}