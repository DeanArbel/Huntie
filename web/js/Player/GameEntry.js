/**
 * Created by I337243 on 26/02/2017.
 */
var teamsPlayerCanJoin;
var maxPlayersInTeam;
var teamTable;
var gameid;

$(function () {
    // $(".loader").hide();
    // $(".settings-container").show();
    sessionStorage.setItem("PrevPage", "GameEntry");
    initGlobalVars();
    initPageElementsFromServer();
});

function initGlobalVars() {
    teamsPlayerCanJoin = [];
    teamTable = $('table > tbody');
    gameid = getParameterByName("gameid");
}

function initPageElementsFromServer() {
    $.ajax({
        url: "GameEntry",
        type: 'GET',
        data: {gameid: gameid},
        /**
         * gameData[0] - is player in game
         * gameData[1] - error msg (if empty then no error)
         * gameData[2] - teams
         * gameData[3] - max players
         */
        success: function(gameData) {
            // $(".loader").hide();
            // $(".container").show();
            if (!gameData.isPlayerInGame) {
                if (gameData.errMsg === "") {
                    var size = gameData.teams.length;
                    if (size === 1) { // if not team game
                        registerPlayerToGame();
                    } else {
                        maxPlayersInTeam = gameData.maxTeamPlayers;
                        for(var i = 0; i < size; i++) {
                            _addTeam(i, gameData.teams[i])
                        }
                    }
                    $(".loader").hide();
                    $(".container").show(); //TODO: Put it somewhere more logical
                } else {
                    confirm(gameData[1]);
                }
            } else {
                window.location.href = SITE_URL + "/Player/GameLobby.html?gameid=" + gameid;
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