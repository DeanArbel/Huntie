/**
 * Created by Dean on 25/02/2017.
 */

var gameCode;

$(function () {
    $(".loader").hide();
    $(".settings-container").show();
    sessionStorage.setItem("PrevPage", "FindGame");
    initGlobalVars();
});

$(document).on('click', '#prevPage-btn', function() {
    window.location.href =   "/Home.html";
});

$(document).on('click', '#game-find-btn', function() {
    //TODO: Check the given id is in the server (the server will give the right address)
    $.ajax({
        url: FIND_GAME_URL,
        data: { gameCode: gameCode.value, token: sessionStorage.getItem("access token")},
        success: function(data) {
            dataMap = JSON.parse(data);
            window.location.href =   dataMap.url + "?gameCode=" + dataMap.gameCode;
        },
        error: function(errMsg) {
            confirm(errMsg.statusText);
        }
    });
});

function initGlobalVars() {
    gameCode = $("#game-code")[0];
}