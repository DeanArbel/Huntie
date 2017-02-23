/**
 * Created by Dean on 20/2/2017.
 */
var TEXT_ANSWER = "Text Answer";
var PHOTO_ANSWER = "Photo Answer";

var riddles = [];
var riddlesTable;
var riddleNameInput;
var riddleAppearanceInput;
var riddleType;
var riddleQuestion;
var riddleOptionalImage;
var riddleTextAnswer;
var riddleLocationCheckBox;
var riddleLocation;
var riddleModal;

$(function() {
    sessionStorage.setItem("GameBuilderVisited", "True");
    dropdownChange(TEXT_ANSWER);
    initGlobalVars();
    initPageElementsFromServer();
});

function initGlobalVars() {
    riddlesTable = $("tbody");
    riddleNameInput = $("#riddle-name")[0];
    riddleAppearanceInput = $("#riddle-appearance-number")[0];
    riddleType = $(".dropdown-btn")[0];
    riddleQuestion = $("#riddle-question-text")[0];
    riddleOptionalImage = $("#riddle-question-optionalimage")[0];
    riddleTextAnswer = $("#riddle-answer-text")[0];
    riddleLocationCheckBox = $("#riddle-location-checkbox")[0];
    //TODO: Add this after google maps integration: riddleLocation = $("#riddle-location")[0];
    riddleModal = $("#myModal");
}

$(document).on("click", "#riddle-submit-btn", function() {
    var riddleErr = getRiddleInTableFormat();
    if (riddleErr[1] === "") {
        var riddle = riddleErr[0];
        $.ajax({
            url: GAME_CREATOR_COMPONENTS_URL,
            data: {requestType: "GameBuilder", action:"add", riddle: JSON.stringify(riddle)},
            type: 'POST',
            success: function () {
                if (!riddles[riddle.appearanceNumber]) {
                    riddles[riddle.appearanceNumber] = [];
                }
                riddles[riddle.appearanceNumber][riddles[riddle.appearanceNumber].length] = riddle;
                updateRiddlesTable();
                riddleModal.modal("toggle");
                resetForm();
            } //TODO: Add error notification
        });
    }
    else {
        confirm(riddleErr[1]);
    }
});

$(document).on("click", "#riddle-reset-btn", resetForm);

function resetForm() {
    document.getElementById("riddle-form").reset();
    var image = document.getElementById('riddle-question-optionalimage');
    image.src = "";
    image.hidden = true;
    //TODO: Remove location
}

function initPageElementsFromServer() {
    $.ajax({
        url: "GameCreator/GameComponents",
        datatype: "application/json; charset=utf-8",
        data: { requestType: "GameBuilder" },
        type: 'GET',
        success: function(serverRiddles) {
            $(".loader").hide();
            $(".container").show();
            if (serverRiddles.length > 0) {
                var riddleArrSize = serverRiddles.length;
                for (var i = 0; i < riddleArrSize; i++) {
                    riddles[i] = {};
                    var currAppearanceSize = serverRiddles[i].length;
                    for (var j = 0; j < currAppearanceSize; j++) {
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
        if (riddles[i]) {
            var riddleArrSize = riddles[i].length;
            for (var j = 0; j < riddleArrSize; j++) {
                riddles[i][j].index = j;
                addRow(riddles[i][j]);
            }
        }
    }
}

function addRow(riddle) {
    var $eRow = $('<tr class="iIndex' + riddle.appearanceNumber + '">');
    $eRow.append('<td>' + riddle.appearanceNumber + '</td>');
    $eRow.append('<td hidden>' + (riddle.index + 1) + '</td>');
    $eRow.append('<td>' + riddle.name + '</td>');
    $eRow.append(REMOVE_BUTTON_SVG);
    riddlesTable.append($eRow);
}

function readURL(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#riddle-question-optionalimage')
                .attr('src', e.target.result)
                .width(150)
                .height(200)[0].hidden = false;
        };

        reader.readAsDataURL(input.files[0]);
    }
}

function dropdownChange(newDropdownValue) {
    if (PHOTO_ANSWER === newDropdownValue) {
        $("#riddle-text").hide();
        $("#riddle-photo").show();
    }
    else {
        $("#riddle-text").show();
        $("#riddle-photo").hide();
    }
}

function rowWasRemoved(that) {
    var removedCells = $(that).parents('td').siblings(),
        removedFirstIdx = parseInt(removedCells[0].innerText),
        removedSecondIdx = parseInt(removedCells[1].innerText) - 1;
    $.ajax({
        url: "GameCreator/GameComponents",
        data: { requestType: "GameBuilder", action: "delete", riddle: JSON.stringify(riddles[removedFirstIdx][removedSecondIdx]) },
        type: 'POST',
        error: function() { confirm("Encountered error while deleting, please refresh");}
        //TODO: Handle error properly
    });
    riddles[removedFirstIdx].splice(removedSecondIdx, 1);
    var size = riddles[removedFirstIdx].length,
        rowTable = $('.iIndex' + removedFirstIdx);
    for (var i = removedSecondIdx; i < size; i++) {
        rowTable[i].children[1].innerText = (i + 1);
        riddles[removedFirstIdx][i].index = i;
    }
}

function checkRiddleErrors(riddle) {
    var errMsg = "";
    if (!riddle.appearanceNumber || riddle.appearanceNumber > 99 || riddle.appearanceNumber < 1) {
        errMsg += "- Appearance number should be between 1 and 99\n";
    }
    if (riddle.questionText === "") {
        errMsg += "- Riddle must have a question\n";
    }
    if (riddle.type !== PHOTO_ANSWER) {
        if (riddle.answerText === "") {
            errMsg += "- Riddle must have an answer\n";
        }
        else {
            //TODO: Add check if photo riddle has an answer
        }
    }
    //TODO: Add checks for map location

    return errMsg;
}

function getRiddleInTableFormat() {
    var riddle = {
        name: riddleNameInput.value,
        appearanceNumber: parseInt(riddleAppearanceInput.value),
        type: riddleType.innerText,
        questionText: riddleQuestion.value,
        questionOptionalImage: riddleOptionalImage, //TODO: Debug this
        answerText: riddleTextAnswer.value
        //TODO: after google maps add location
        //TODO: Add here question and answer
    }, errMsg = checkRiddleErrors(riddle);
    //TODO: Add checks here, if one fails return null
    return [riddle, errMsg];
}