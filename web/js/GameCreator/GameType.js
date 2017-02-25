/**
 * Created by Dean on 17/2/2017.
 */
var TEAM_GAME = "Team Game";
var INDIVIDUAL_GAME = "Individual Game";
var MAX_GROUPS = 10;
var mTeams = {};
var sCurGameType;
var teamNameInput;
var maxPlayerIndividualInput;
var maxPlayerTeamInput;

$(function() {
    if (sessionStorage.getItem("PrevPage") === "GameArea") {
        window.location.reload(true); // force refresh page1
    }
    else {
        dropdownChange($('.dropdown-btn')[0].innerText);
        document.getElementById('teamName').oninput = onTeamNameInput;
        document.getElementById('maxPlayersIndividual').oninput = onMaxPlayersIndividualInput;
        document.getElementById('maxPlayersTeam').oninput = onMaxPlayersTeamInput;
        teamNameInput = $('#teamName')[0];
        maxPlayerIndividualInput = $('#maxPlayersIndividual')[0];
        maxPlayerTeamInput = $('#maxPlayersTeam')[0];
        initPageElementsFromServer();
    }
    sessionStorage.setItem("PrevPage", "GameType");
});

function initPageElementsFromServer() {
    $.ajax({
        url: "GameCreator/GameComponents",
        data: { requestType: "GameType" },
        type: 'GET',
        success: function(previousGameType) {
            $(".loader").hide();
            $(".container").show();
            if(previousGameType[0]) {
                updateDropdownValue($('.dropdown-selection')[1].innerText);
            }
            maxPlayerIndividualInput.value = previousGameType[1];
            maxPlayerTeamInput.value = previousGameType[2];
            //TODO: Add teamName (previousGameType[3]) support
            var size = previousGameType[3].length;
            for(var i = 0; i < size; i++) {
                _addTeam(previousGameType[3][i]);
            }
        }
    });
}

function onTeamNameInput() {
    var inputValue = teamNameInput.value;
    $('#addTeam-btn')[0].disabled = mTeams[inputValue];
}

function onMaxPlayersIndividualInput() {
    _inputLimiter(maxPlayerIndividualInput, 3);
}

function onMaxPlayersTeamInput() {
    _inputLimiter(maxPlayerTeamInput, 2);
}

function _inputLimiter(input, max) {
    if (input.value.length >= max) {
        input.value = input.value.substr(0,max);
    }
}

function dropdownChange(innerText) {
    sCurGameType = innerText;
    if (innerText === TEAM_GAME) {
        $('.show-team').show();
        $('.show-individual').hide();
    }
    else {
        $('.show-team').hide();
        $('.show-individual').show();
    }
}

$(document).on('click','#openModal-btn', function() {
    $('#addTeam-btn')[0].disabled = true;
});

$(document).on('shown.bs.modal', '#myModal', function () {
    $('#addTeam-btn')[0].disabled = true;
    teamNameInput.focus();
});

$(document).on('click', '#addTeam-btn', function() {
    _addTeam(teamNameInput.value);
});

function _addTeam(inputValue) {
    mTeams[inputValue] = {};
    teamNameInput.value = "";
    addRow(inputValue);
}

$(document).on('click', '#nextPage-btn', function() {
   var gameType = $('.dropdown-btn')[0].innerText,
       errMsg = checkErrorsBeforeSubmit(gameType);
    if (errMsg == "") {
        $.ajax({
            url: "GameCreator/GameComponents",
            type: "POST",
            data: {
                requestType: "GameType",
                teams: JSON.stringify(mTeams),
                maxPlayers: maxPlayerIndividualInput.value,
                maxPlayersInTeam: maxPlayerTeamInput.value,
                gameType: gameType
            },
            success: function (response) {
                window.location.href = SITE_URL + "/Manager/GameArea.html";
            },
            error: function(e) {
                console.log(JSON.stringify(e));
            }
        });
    }
    else {
        confirm(errMsg);
    }
});

function checkErrorsBeforeSubmit(gameType) {
    var errMsg = "";
    if (gameType === TEAM_GAME) {
        if (maxPlayerTeamInput.value === "" || parseInt(maxPlayerTeamInput.value) < 1) {
            errMsg += "- Team size should be greater than zero\n";
        }
        if ($('table > tbody').children().length < 2) {
            errMsg += "- Team game should have at least two teams\n"
        }
    } else {
        if (maxPlayerIndividualInput.value === "" || parseInt(maxPlayerIndividualInput.value) < 1) {
            errMsg += "- Max players should be greater than zero\n";
        }
    }

    return errMsg;
}

function addRow(sRowName) {
    var eTbody = $('table > tbody');
    var $eRow = $('<tr>');
    $eRow.append('<td>' + sRowName + '</td>');
    $eRow.append(REMOVE_BUTTON_SVG);
    eTbody.append($eRow);
    if (_isTableFull()) {
        $('#openModal-btn')[0].disabled = true;
    }
}

function rowWasRemoved(that) {
    var removedValue = $(that).parents('td').siblings()[0].innerText;
    delete mTeams[removedValue];
    $('#openModal-btn')[0].disabled = false;
}

function _isTableFull() {
    return $('table > tbody').children().length >= MAX_GROUPS;
}