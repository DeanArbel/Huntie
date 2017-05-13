/**
 * Created by Dean on 20/2/2017.
 */
var TEXT_ANSWER = "Text Answer";
var PHOTO_ANSWER = "Photo Answer";
var RIDDLE_LVL_ID_FORMAT = 'riddle-table-level-';
var ADD_TO_LVL_BTN_ID_FORMAT = 'add-riddle-level-';
var MAX_IMG_WIDTH = 200;
var MAX_IMG_HEIGHT = 300;

var riddles = [];
var riddlesTable;
var riddleNameInput;
var riddleAppearanceInput;
var riddleType;
var riddleTextQuestion;
var riddleOptionalImage;
var riddleTextAnswer;
var riddleImageAnswer;
var riddleLocationCheckBox;
var riddleLocation;
var riddleModal;
var editFlag = false;
var edittedRiddle;
var edittedRiddleDeleteButton;
var rowIsMidRemoval;

$(function() {
    sessionStorage.setItem("PrevPage", "GameBuilder");
    updateDropdownValue(TEXT_ANSWER);
    initGlobalVars();
    initPageElementsFromServer();
});

function initGlobalVars() {
    riddlesTable = $("#riddles-table > tbody");
    riddleNameInput = $("#riddle-name")[0];
    riddleAppearanceInput = $("#riddle-appearance-number")[0];
    riddleType = $(".dropdown-btn")[0];
    riddleTextQuestion = $("#riddle-question-text")[0];
    riddleOptionalImage = $("#riddle-question-optionalimage")[0];
    riddleImageAnswer = $("#riddle-photo-answer-image")[0];
    riddleTextAnswer = $("#riddle-answer-text")[0];
    riddleLocationCheckBox = $("#riddle-location-checkbox")[0];
    //TODO: Add this after google maps integration: riddleLocation = $("#riddle-location")[0];
    riddleModal = $("#myModal");

    // NEW ELEMENTS: //TODO: Organize this in refactor
    m_eRiddleLevelTable = $("#table-riddles > tbody");
    m_iNextRiddleLvl = 1;
}

$(document).on('click', '#prevPage-btn', function() {
    window.location.href = SITE_URL + "/Manager/GameArea.html";
});

$(document).on('click', '#nextPage-btn', function() {
        if ($("tbody > tr").length > 0) {
        window.location.href = SITE_URL + "/Manager/GameSettings.html";
    }
    else {
        confirm("Game must have at least 1 riddle");
    }
});

$(document).on("click", "#riddle-submit-btn", function() {
    var riddleAndErr = getRiddleInServerFormat();
    if (riddleAndErr[1] === "") {
        //TODO: add loading screen to prevent user from using page
        if (editFlag) {
            edittedRiddle = riddleAndErr[0];
            edittedRiddleDeleteButton.click();
        } else {
            sendRiddleToServer(riddleAndErr[0]);
        }
    }
    else {
        confirm(riddleAndErr[1]);
    }
});

$(document).on("click", "#riddle-reset-btn", resetForm);

$(document).on("click", ".table > tbody > tr", function(clickedEvent) {
    if (!rowIsMidRemoval) {
        var clickedRowCells = clickedEvent.currentTarget.cells,
            clickedRiddle = riddles[clickedRowCells[0].innerText][clickedRowCells[1].innerText];
        edittedRiddleDeleteButton = $(clickedRowCells[3]).children();
        editFlag = false;
        editRiddle(clickedRiddle);
    }
});

function onAddRiddleButtonClick() {
    resetForm();
    editFlag = false;
    riddleAppearanceInput.disabled = false;
}

function sendRiddleToServer(riddle) {
    $.ajax({
        url: GAME_CREATOR_COMPONENTS_URL,
        data: "requestType=GameBuilder&action=add&riddle=" + JSON.stringify(riddle),
        type: 'POST',
        success: onSuccessfulRiddlePost(riddle),
        error: function(err) {
            confirm("Encountered error on server, please try again");
        }
    });
}

function onSuccessfulRiddlePost(riddle) {
    //TODO: Close loading screen
    editFlag = false;
    if (!riddles[riddle.level]) {
        riddles[riddle.level] = [];
    }
    riddles[riddle.level][riddles[riddle.level].length] = riddle;
    updateRiddlesTable();
    //addRiddleToRiddleLevelTable(riddle);
    riddleModal.modal("toggle");
    resetForm();
}

function resetForm() {
    document.getElementById("riddle-form").reset();
    resetImage('riddle-question-optionalimage');
    resetImage('riddle-photo-answer-image');
    $('.location-info').show();
    //TODO: Remove location
}

function resetImage(imageId) {
    var image = document.getElementById(imageId);
    image.src = "";
    image.hidden = true;
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
                    if (serverRiddles[i]) {
                        riddles[i] = [];
                        var currAppearanceSize = serverRiddles[i].length;
                        for (var j = 0; j < currAppearanceSize; j++) {
                            riddles[i][j] = convertRiddleToClientFormat(serverRiddles[i][j]);
                        }
                    }
                }
                updateRiddlesTable();
            }
        }
    });
}

function convertRiddleToClientFormat(serverRiddle) {
    var riddle = {};
    riddle.level = serverRiddle.m_AppearanceNumber;
    riddle.name = serverRiddle.m_Name;
    riddle.questionText = serverRiddle.m_TextQuestion;
    riddle.answer = serverRiddle.m_Answer;
    riddle.type = serverRiddle.m_IsTextType ? TEXT_ANSWER : PHOTO_ANSWER;
    riddle.optImg = serverRiddle.m_OptionalQuestionImage;
    return riddle;
    //TODO: Add support for optional image
    //TODO: Add here photo support
}

function updateRiddlesTable() {
    riddlesTable.empty();
    m_eRiddleLevelTable.empty();
    m_iNextRiddleLvl = 1;
    var riddlesArrSize = riddles.length;
    for (var i = 1; i < riddlesArrSize; i++) {
        addLevelToRiddleTable();
        if (riddles[i]) {
            var riddleArrSize = riddles[i].length;
            for (var j = 0; j < riddleArrSize; j++) {
                riddles[i][j].index = j;
                addRow(riddles[i][j]);
                addRiddleToRiddleLevelTable(riddles[i][j]);
            }
        }
    }
}

function addRow(riddle) {
    var $eRow = $('<tr class="iIndex' + riddle.level + '">');
    $eRow.append('<td>' + riddle.level + '</td>');
    $eRow.append('<td hidden>' + riddle.index + '</td>');
    $eRow.append('<td>' + riddle.name + '</td>');
    $eRow.append(REMOVE_BUTTON_SVG);
    riddlesTable.append($eRow);
}

function editRiddle(riddle) {
    if (!editFlag) {
        riddleNameInput.value = riddle.name;
        riddleAppearanceInput.value = riddle.level;
        riddleTextQuestion.value = riddle.questionText;
        if (riddle.type === TEXT_ANSWER) {
            riddleTextAnswer.value = riddle.answer;
            riddleImageAnswer.hidden = true;
        } else {
            riddleImageAnswer.src = riddle.answer;
            riddleImageAnswer.hidden = false;
        }
        if (riddle.optImg && riddle.optImg !== 'data:,') {
            riddleOptionalImage.src = riddle.optImg;
            riddleOptionalImage.hidden = false;
        }
        updateDropdownValue(riddle.type);
        editFlag = true;
        riddleModal.modal("toggle");
        //TODO: Add other relevant fields
    }
}

function readPictureURL(input, imgId) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            var image = $(imgId);
            image.removeAttr("width").removeAttr("height");
            image.attr('src', e.target.result)[0].hidden = false;
            image[0].width = 200;
            // //image.style.width = image.style.height = 100%;
            // if (image[0].width > MAX_IMG_WIDTH) {
            //     imgProp = image[0].height / image[0].width;
            //     image[0].style.width = MAX_IMG_WIDTH;
            //     image[0].height = imgProp * MAX_IMG_WIDTH;
            // }
            // if (image[0].height > MAX_IMG_HEIGHT) {
            //     imgProp = image[0].width / image[0].height;
            //     image[0].height = MAX_IMG_HEIGHT;
            //     image[0].width = imgProp * MAX_IMG_HEIGHT;
            // }
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
        removedSecondIdx = parseInt(removedCells[1].innerText);
    rowIsMidRemoval = true;
    $.ajax({
        url: "GameCreator/GameComponents",
        data: { requestType: "GameBuilder", action: "delete", riddle: JSON.stringify(riddles[removedFirstIdx][removedSecondIdx]) },
        type: 'POST',
        success: function() {
            rowIsMidRemoval = false;
            riddles[removedFirstIdx].splice(removedSecondIdx, 1);
            var size = riddles[removedFirstIdx].length,
                rowTable = $('.iIndex' + removedFirstIdx);
            for (var i = removedSecondIdx; i < size; i++) {
                rowTable[i].children[1].innerText = i;
                riddles[removedFirstIdx][i].index = i;
            }
            //todo remove row below
            updateRiddlesTable();
            if (editFlag) {
                sendRiddleToServer(edittedRiddle);
            }
        },
        error: function() {
            confirm("Encountered error while deleting, please refresh");
        }
        //TODO: Handle error properly
    });
}

function checkRiddleErrors(riddle) {
    var errMsg = "";
    if (!riddle.level || riddle.level > 99 || riddle.level < 1) {
        errMsg += "- Appearance number should be between 1 and 99\n";
    }
    if (riddle.questionText === "") {
        errMsg += "- Riddle must have a question\n";
    }
    if (riddle.answer === "") {
        errMsg += "- Riddle must have an answer\n";
    }
    //TODO: Add checks for map location

    return errMsg;
}

function getRiddleInServerFormat() {
    var riddle = {
        name: riddleNameInput.value,
        level: parseInt(riddleAppearanceInput.value),
        type: riddleType.innerText,
        questionText: riddleTextQuestion.value,
        questionOptionalImage: getBase64Image(riddleOptionalImage),
        //TODO: after google maps add location
    }, errMsg;
    riddle.answer = riddle.type === TEXT_ANSWER ? riddleTextAnswer.value : getBase64Image(riddleImageAnswer);
    errMsg = checkRiddleErrors(riddle);
    return [riddle, errMsg];
}

$(document).on('change', '#riddle-location-checkbox', function (event) {
    $('.location-info').toggle();
});

// NEW IMPLEMENTATION:
// todo: Functions here should be move upward at refactoring stage

// Page Elements:
var m_eRiddleLevelTable;
// Global vars
var m_iNextRiddleLvl;

function onAddLevelClick() {
    addLevelToRiddleTable();
}

function addLevelToRiddleTable() {
    var $levelRow = $('<tr></tr>');
    var $levelTable = $('<table class="table"></table>');
    $levelTable.append($('<thead><tr><th>Level ' + m_iNextRiddleLvl + '</th></tr></thead>'));
    $levelTable.append($('<tbody id="' + RIDDLE_LVL_ID_FORMAT + m_iNextRiddleLvl + '">' +
        '<tr>' +
        '<td>' +
        '<button id="' + ADD_TO_LVL_BTN_ID_FORMAT + m_iNextRiddleLvl + '" onclick="addRiddleRow(this)" data-toggle="modal" data-target="#myModal">Add Riddle</button>' +
        '</td></tr></tbody>'));
    $levelRow.append($levelTable);
    m_eRiddleLevelTable.append($levelRow);

    m_iNextRiddleLvl++;
}

function addRiddleRow(eButton) {
    var btnID = eButton.id;
    var formatLen = ADD_TO_LVL_BTN_ID_FORMAT.length;
    resetForm();
    editFlag = false;
    riddleAppearanceInput.value = btnID.substr(formatLen, btnID.length - formatLen);
    riddleAppearanceInput.disabled = true;
}

function addRiddleToRiddleLevelTable(riddle) {
    var eBodyToAddTo = $('#' + RIDDLE_LVL_ID_FORMAT + riddle.level + ' > tr').last();
    var $eRow = $('<tr class="iIndex' + riddle.level + '">'); //TODO: Remove redundant class
    $eRow.append('<td hidden>' + riddle.level + '</td>');
    $eRow.append('<td hidden>' + riddle.index + '</td><td></td>');
    $eRow.append('<td>' + riddle.name + '</td>');
    $eRow.append(REMOVE_BUTTON_SVG);
    eBodyToAddTo.before($eRow);
}