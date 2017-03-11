/**
 * Created by Dean on 06/03/2017.
 */
// Web elements
var mUsername;
var mEmail;
var mOldPassword;
var mNewPassword;
var mManagerGamesTable;
var mPlayerGamesTable;
var mClickedRow;

// Logic
var mChosenGameId;

$(function () {
    sessionStorage.setItem("PrevPage", "MyProfile");
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", "tbody > tr", function(clickedEvent) {
    if (mClickedRow) {
        mClickedRow.classList.remove("active");
    }
    mClickedRow = clickedEvent.currentTarget;
    mClickedRow.classList.add("active");
    //mChosenGameId = mClickedRow. //TODO: Check this row to know how to access the game id
    //mSolveRiddleBtn.disabled = false;
});

$(document).on("click", "#prevPage-btn", function() {
    window.location.href = SITE_URL + "/Home.html";
});

function initGlobalVars() {
    mUsername =  $('#profile-username');
    mEmail = $('#profile-email');
    mOldPassword = $('#profile-password-old');
    mNewPassword = $('#profile-password-new');
    mManagerGamesTable = $('#profile-table-manager > tbody');
    mPlayerGamesTable = $('#profile-table-played > tbody');
}

function initPageElementsFromServer() {
    $.ajax({
        url: PROFILE_URL,
        type: 'GET',
        data: {request: "UserInfo"},
        success: function(gameData) {
            mUsername[0].value = gameData.username;
            mEmail[0].value = gameData.email;
            $(".loading-area").hide();
            $(".lobby-container").show();
            getGameTablesFromServer();
        },
        error: function(err) {
            alert(err);
        }
    });
}

function getGameTablesFromServer() {
    $.ajax({
        url: PROFILE_URL,
        type: 'GET',
        data: {request: "Tables"},
        success: function(gameData) {
            initTables(gameData);
        }
    });
}

function initTables(data) {
    initGameTable(mManagerGamesTable, data.myGames);
    initGameTable(mPlayerGamesTable, data.playedGames);
}

function initGameTable(table, gamesObject) {
    var keys = Object.keys(gamesObject);
    var size = keys.length;
    if (size > 0) {
        for (var i = 0; i < size; i++) {
            var $eRow = $('<tr>');
            $eRow.append('<td>' + gamesObject[keys[i]] + '</td>');
            $eRow.append('<td hidden>' + keys[i] + '</td>');
            table.append($eRow);
        }
    }
}

function resetPasswordFields() {
    mOldPassword[0].value = "";
    mNewPassword[0].value = "";
}