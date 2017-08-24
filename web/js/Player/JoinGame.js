/**
 * Created by Dean on 26/02/2017.
 */
var isTeamAvailableArray;
var maxPlayersInTeam;
var teamTable;
var gameCode;
var clickedRow;
var chosenTeamIdx;
var joinGameBtn;

$(function () {
    sessionStorage.setItem("PrevPage", "GameEntry");
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", ".table > tbody > tr", function(clickedEvent) {
    if (clickedRow) {
        clickedRow.classList.remove("active");
    }
    clickedRow = clickedEvent.currentTarget;
    clickedRow.classList.add("active");
    chosenTeamIdx = clickedEvent.currentTarget.rowIndex - 1;
    joinGameBtn[0].disabled = !isTeamAvailableArray[chosenTeamIdx];
});

$(document).on("click", "#prevPage-btn", function() {
    window.location.href =   "/Home.html";
});

function initGlobalVars() {
    isTeamAvailableArray = [];
    teamTable = $('table > tbody');
    gameCode = getParameterByName("gameCode");
    joinGameBtn = $("#joinGame-btn");
}

function initPageElementsFromServer() {
    $.ajax({
        url: GAME_ENTRY_URL,
        type: 'GET',
        data: {gameCode: gameCode},
        success: function(gameData) {
            if (!gameData.isPlayerInGame) {
                if (gameData.errMsg === "") {
                    var size = gameData.teams.length;
                    if (size <= 1) { // if not team game
                        registerPlayerToGame();
                    } else {
                        maxPlayersInTeam = gameData.maxPlayersInTeam;
                        for(var i = 0; i < size; i++) {
                            _addTeam(i, gameData.teams[i])
                        }
                    }
                    $(".loading-area").hide();
                    $(".container").show(); //TODO: Put it somewhere more logical
                } else {
                    confirm(gameData.errMsg);
                }
            } else {
                window.location.href =   "/Player/GameLobby.html?gameCode=" + gameCode;
            }
        }
    });
}

function registerPlayerToGame() {
    if (!chosenTeamIdx) {
        chosenTeamIdx = 0;
    }
    $.ajax({
        url: GAME_ENTRY_URL,
        type: 'POST',
        data: { gameCode: gameCode, teamIndex: chosenTeamIdx },
        success: function() {
            window.location.href =   "/Player/GameLobby.html?gameCode=" + gameCode;
        },
        error: function(errMsg) {
            confirm(errMsg.error);
        }
    });
}

function _addTeam(index, teamData) {
    isTeamAvailableArray[index] = parseInt(teamData.count) !== maxPlayersInTeam;
    _addRow(teamData);
}

function _addRow(teamData) {
    var $eRow = $('<tr>');
    $eRow.append('<td>' + teamData.name + '</td>');
    $eRow.append('<td>' + teamData.count + " / " + maxPlayersInTeam + '</td>');
    teamTable.append($eRow);
}