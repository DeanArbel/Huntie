/**
 * Created by Dean on 20/2/2017.
 */
var riddles = [];
var riddlesTable;
var riddleNameInput;
var riddleAppearanceInput;
var riddleType;
var riddleLocationCheckBox;
var riddleLocation;
var riddleModal;

$(function() {
    sessionStorage.setItem("GameBuilderVisited", "True");
    dropdownChange($('.dropdown-btn')[0].innerText);
    initGlobalVars();
    initPageElementsFromServer();
});

function initGlobalVars() {
    riddlesTable = $("tbody");
    riddleNameInput = $("#riddle-name")[0];
    riddleAppearanceInput = $("#riddle-appearance-number")[0];
    riddleType = $(".dropdown-btn")[0];
    riddleLocationCheckBox = $("#riddle-location-checkbox")[0];
    //TODO: Add this after google maps integration: riddleLocation = $("#riddle-location")[0];
    riddleModal = $("#myModal").modal;
}

$(document).on("click", "#riddle-submit-btn", function() {
    var riddle = { name: riddleNameInput.value,
        appearanceNumber: riddleAppearanceInput.value,
        type: riddleType.innerText //TODO: after google maps add location
        //TODO: Add here question and answer
    };
    $.ajax({
        url: GAME_CREATOR_COMPONENTS_URL,
        data: { requestType: "GameBuilder" },
        type: 'POST',
        success: function() {
            addRow(riddle);
            riddleModal("toggle");
        } //TODO: Add error notification
    });
});

function initPageElementsFromServer() {
    $.ajax({
        url: "GameCreator/GameComponents",
        data: { requestType: "GameBuilder" },
        type: 'GET',
        success: function(serverRiddles) {
            $(".loader").hide();
            $(".container").show();
            if (serverRiddles.length > 0) {
                var riddleArrSize = serverRiddles.length;
                for (var i = 0; i < riddleArrSize; i++) {
                    riddles[i] = {};
                    var currApperanceSize = serverRiddles[i].length;
                    for (var j = 0; j < currApperanceSize; j++) {
                        riddles[i][j] = serverRiddles[i][j];
                    }
                }
                updateRiddlesTable();
            }
        }
    });
}

function updateRiddlesTable() {
    riddlesTable.empty();
    var riddlesArrSize = riddles.length;
    for (var i = 0; i < riddlesArrSize; i++) {
        var riddleArrSize = riddles[i].length;
        for (var j = 0; j < riddleArrSize; j++) {
            addRow(riddles[i][j]);
        }
    }
}

function addRow(riddle) {
    var $eRow = $('<tr>');
    $eRow.append('<td>' + riddle.apperanceNumber + '</td>');
    $eRow.append('<td>' + riddle.name + '</td>');
    $eRow.append(REMOVE_BUTTON_SVG);
    riddlesTable.append($eRow);
}

function dropdownChange() {
    console.log("Dropdown changed");
}