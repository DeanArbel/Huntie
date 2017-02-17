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
    dropdownChange($('.dropdown-btn')[0].innerText);
    document.getElementById('teamName').oninput = onTeamNameInput;
    document.getElementById('maxPlayersIndividual').oninput = onMaxPlayersIndividualInput;
    document.getElementById('maxPlayersTeam').oninput = onMaxPlayersTeamInput;
    teamNameInput = $('#teamName')[0];
    maxPlayerIndividualInput = $('#maxPlayersIndividual')[0];
    maxPlayerTeamInput = $('#maxPlayersTeam')[0];
});

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
    var inputValue = teamNameInput.value;
    mTeams[inputValue] = {};
    teamNameInput.value = "";
    addRow(inputValue);
});

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
    $('#openModal-btn').disabled = false;
}

function _isTableFull() {
    return $('table > tbody').children().length >= MAX_GROUPS;
}