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
    sessionStorage.setItem("PrevPage", "my-profile");
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", "#profile-table-manager > tbody > tr", function(clickedEvent) {
    if (tableRowClicked(clickedEvent)) {
        //TODO: Change implementation after login system works
        window.location.href =   "/Player/join-game.html?gameCode=" + mChosenGameId;
    }
});

$(document).on("click", "#profile-table-played > tbody > tr", function(clickedEvent) {
    if (tableRowClicked(clickedEvent)) {
        //TODO: Change implementation after login system works
        window.location.href =   "/Player/join-game.html?gameCode=" + mChosenGameId;
    }
});

$(document).on("click", "#prevPage-btn", function() {
    window.location.href =   "/home.html";
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
        data: {request: "UserInfo", token: sessionStorage.getItem("access token")},
        success: function(gameData) {
            mUsername[0].value = gameData.username;
            mEmail[0].value = gameData.email;
            $(".loading-area").hide();
            $(".lobby-container").show();
            getGameTablesFromServer();
        },
        error: function(xhr) {
            alert(xhr.getResponseHeader("errortext"));
        }
    });
}

function getGameTablesFromServer() {
    $.ajax({
        url: PROFILE_URL,
        type: 'GET',
        data: {request: "Tables", token: sessionStorage.getItem("access token")},
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
            $eRow.append('<td style="width:' + mPlayerGamesTable.width() + 'px">' + gamesObject[keys[i]] + '</td>');
            $eRow.append('<td hidden>' + keys[i] + '</td>');
            table.append($eRow);
        }
    }
}

function resetPasswordFields() {
    mOldPassword[0].value = "";
    mNewPassword[0].value = "";
}

function tableRowClicked(clickedEvent) {
    if (mClickedRow) {
        mClickedRow.classList.remove("active");
    }
    mClickedRow = clickedEvent.currentTarget;
    mClickedRow.classList.add("active");
    mChosenGameId = mClickedRow.lastChild.innerText;
    return confirm("Would you like to go to game page?");
}