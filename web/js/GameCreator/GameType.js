/**
 * Created by Dean on 17/2/2017.
 */
var TEAM_GAME = "Team Game";
var INDIVIDUAL_GAME = "Individual Game";
var MAX_GROUPS = 10;
var mTeams = {};
var sCurGameType;
var groupNameInput;

$(function() {
    dropdownChange($('.dropdown-btn')[0].innerText);
    document.getElementById('groupName').oninput = onTeamNameInput;
    groupNameInput = $('#groupName')[0];
});

function onTeamNameInput() {
    var inputValue = groupNameInput.value;
    $('#addTeam-btn')[0].disabled = mTeams[inputValue];
}

function dropdownChange(innerText) {
    console.log("dropdown-btn value is now " + innerText);
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
    groupNameInput.focus();
});

$(document).on('click', '#addTeam-btn', function() {
    var inputValue = groupNameInput.value;
    mTeams[inputValue] = {};
    groupNameInput.value = "";
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