/**
 * Created by Dean on 28/02/2017.
 */
var mGameEndedMessage;
var mGameName;
var mPlayerMessage;
var mGameTimeMessage;
var mPlayerWonMessage;
var mTeamTable;
var mOthersTable;
var mRiddleTable;
var mClickedRow;
var mChosenRiddleIdx;
var mGameCode;
var mIsGameActive;
var mHasGameStarted;
var mSolveRiddleBtn;

$(function () {
    sessionStorage.setItem("PrevPage", "GameLobby");
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", "#riddle-table > tbody > tr", function(clickedEvent) {
    if (mClickedRow) {
        mClickedRow.classList.remove("active");
    }
    mClickedRow = clickedEvent.currentTarget;
    mClickedRow.classList.add("active");
    mChosenRiddleIdx = clickedEvent.currentTarget.rowIndex - 1;
    mSolveRiddleBtn.disabled = false;
});

$(document).on("click", "#prevPage-btn", function() {
    window.location.href = SITE_URL + "/Home.html";
});

$(document).on("click", "#solveRiddle-btn", function() {
   window.location.href = SITE_URL + "/Player/Riddle.html?gameCode=" + mGameCode + "&riddle=" + mChosenRiddleIdx;
});

function initGlobalVars() {
    mGameEndedMessage = $("#game-ended-message");
    mGameName = $('#game-name');
    mPlayerMessage = $("#player-message");
    mGameTimeMessage = $('#game-start-time');
    mPlayerWonMessage = $('#player-message-won');
    mTeamTable = $('#table-my-score');
    mOthersTable = $('#table-others-score');
    mRiddleTable = $('#riddle-table > tbody');
    mGameCode = getParameterByName("gameCode");
    mSolveRiddleBtn = $("#solveRiddle-btn")[0];
}

function initPageElementsFromServer() {
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
            mGameTimeMessage[0].innerText = mHasGameStarted ? "End Time: " + formatDate(endTime) : "Start Time: " + formatDate(startTime);
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
                $('#message-my-team-score')[0].innerHTML = gameData.myTeamName + " Player Scores";
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