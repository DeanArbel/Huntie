/**
 * Created by Dean on 26/02/2017.
 */
var teamsPlayerCanJoin;
var maxPlayersInTeam;
var teamTable;
var gameCode;

$(function () {
    sessionStorage.setItem("PrevPage", "GameEntry");
    initGlobalVars();
    initPageElementsFromServer();
});

function initGlobalVars() {
    teamsPlayerCanJoin = [];
    teamTable = $('table > tbody');
    gameCode = getParameterByName("gameCode");
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
                    if (size === 1) { // if not team game
                        registerPlayerToGame();
                    } else {
                        maxPlayersInTeam = gameData.maxPlayersInTeam;
                        for(var i = 0; i < size; i++) {
                            _addTeam(i, gameData.teams[i])
                        }
                    }
                    $(".loader").hide();
                    $(".container").show(); //TODO: Put it somewhere more logical
                } else {
                    confirm(gameData.errMsg);
                }
            } else {
                window.location.href = SITE_URL + "/Player/GameLobby.html?gameCode=" + gameCode;
            }
        }
    });
}

//TODO: Make sure player can't send "join game" unless he has chosen a team with enough space

function registerPlayerToGame() {
    //TODO: Add ajax here
}

function _addTeam(index, teamData) {
    teamsPlayerCanJoin[index] = teamData.count !== maxPlayersInTeam;
    _addRow(teamData);
}

function _addRow(teamData) {
    var $eRow = $('<tr>');
    $eRow.append('<td>' + teamData.name + '</td>');
    $eRow.append('<td>' + teamData.count + " / " + maxPlayersInTeam + '</td>');
    teamTable.append($eRow);
}