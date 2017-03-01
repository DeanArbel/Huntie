/**
 * Created by Dean on 28/02/2017.
 */
var mGameEndedMessage;
var mGameName;
var mPlayerMessage;
var mGameTimeMessage;
var mTeamTable;
var mOthersTable;
var mRiddleTable;
var mClickedRow;
var mChosenRiddleIdx;
var mGameCode;
var mIsGameActive;
var mHasGameStarted;

$(function () {
    sessionStorage.setItem("PrevPage", "GameLobby");
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", ".table > tbody > tr", function(clickedEvent) {
    if (mClickedRow) {
        mClickedRow.classList.remove("active");
    }
    mClickedRow = clickedEvent.currentTarget;
    mClickedRow.classList.add("active");
    mChosenRiddleIdx = clickedEvent.currentTarget.rowIndex - 1;
});

$(document).on("click", "#prevPage-btn", function() {
    window.location.href = SITE_URL + "/Home.html";
});

function initGlobalVars() {
    mGameEndedMessage = $("#game-ended-message");
    mGameName = $('#game-name');
    mPlayerMessage = $("#player-message");
    mGameTimeMessage = $('#game-start-time');
    mTeamTable = $('#table-my-score');
    mOthersTable = $('#table-others-score');
    mRiddleTable = $('#riddle-table > tbody');
    mGameCode = getParameterByName("gameCode");
}

function initPageElementsFromServer() {
    getCrucialPageElementsFromServer();
}

function getCrucialPageElementsFromServer() {
    $.ajax({
        url: GAME_LOBBY_URL,
        type: 'GET',
        data: {request: "getGameInfo", gameCode: mGameCode},
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
                initTeamTable(gameData.myTeamScore);
                initOthersTable(gameData.otherTeamsScore);
            }
        }
    });
}

function initRiddleTable(riddleNames) {
    var size = riddleNames.length;
    $('#riddle-table').show();
    mRiddleTable.show();
    for (var i = 0; i < size; i++) {
        var $eRow = $('<tr>');
        $eRow.append('<td>' + riddleNames[i] + '</td>');
        mRiddleTable.append($eRow);
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