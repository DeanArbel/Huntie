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
        success: function(gameData) {
            var now = new Date();
            var startTime = new Date(gameData.startTime);
            var endTime = new Date(gameData.endTime);
            mIsGameActive = endTime >= now;
            mHasGameStarted = startTime <= now;
            mGameName[0].innerText = gameData.gameName;
            mGameTimeMessage[0].innerText = mHasGameStarted ? "Game End Time: " + formatDate(endTime) : "Game Start Time: " + formatDate(startTime);
            $(".loading-area").hide();
            $(".lobby-container").show();
            if (gameData.playerHasWon) {
                mPlayerWonMessage.show();
            }
            if (mIsGameActive && mHasGameStarted) {
                initRiddleTable(gameData.riddlesNames);
            }
            else if (!mHasGameStarted) {
                mPlayerMessage[0].innerText = "The Game hasn't started yet, please come again later";
            }
            else {
                mGameEndedMessage.show();
            }
            getNonCrucialPageElementsFromServer();
        },
        error: function(err) {
            alert(err);
        }
    });
}

function getNonCrucialPageElementsFromServer() {
    $.ajax({
        url: GAME_LOBBY_URL,
        type: 'GET',
        data: {request: "getPlayerTables", gameCode: mGameCode},
        success: function(gameData) {
            if (gameData.isTeamGame) {
                $('.score-content').show();
                initTeamTable(gameData.myTeamScore);
                initOthersTable(gameData.otherTeamsScore);
            }
        }
    });
}

function initRiddleTable(riddleNames) {
    var size = riddleNames.length;
    if (size > 0) {
        $('.riddle-content').show();
        mRiddleTable.show();
        for (var i = 0; i < size; i++) {
            var $eRow = $('<tr>');
            $eRow.append('<td>' + riddleNames[i] + '</td>');
            mRiddleTable.append($eRow);
        }
    }
}

function initTeamTable(teamScores) {
    var tableBody = $('#table-my-score > tbody');
    mTeamTable.show();
    initTeamsTable(teamScores, tableBody);
}

function initOthersTable(otherTeamsScore) {
    var tableBody = $('#table-others-score > tbody');
    mOthersTable.show();
    initTeamsTable(otherTeamsScore, tableBody);
}

function initTeamsTable(teamMap, tableBody) {
    for (var name in teamMap) {
        var $eRow = $('<tr>');
        $eRow.append('<td>' + name + '</td>');
        $eRow.append('<td>' + teamMap[name] + '</td>');
        tableBody.append($eRow);
    }
}

function resetPasswordFields() {
    mOldPassword[0].value = "";
    mNewPassword[0].value = "";
}