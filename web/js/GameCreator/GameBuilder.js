/**
 * Created by Dean on 20/2/2017.
 */
var TEXT_ANSWER = "Text";
var PHOTO_ANSWER = "Photo";
var RIDDLE_LVL_ID_FORMAT = 'riddle-table-level-';
var ADD_TO_LVL_BTN_ID_FORMAT = 'add-riddle-level-';
var MAX_IMG_WIDTH = 100;
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
var mapHolder;
var riddleModal;
var editFlag = false;
var edittedRiddle;
var edittedRiddleDeleteButton;
var rowIsMidRemoval;
var locationCoords;

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
    mapHolder = $("#mapholder")[0];
    //TODO: Add this after google maps integration: riddleLocation = $("#riddle-location")[0];
    riddleModal = $("#myModal");

    // NEW ELEMENTS: //TODO: Organize this in refactor
    m_eRiddleLevelTable = $("#table-riddles > tbody");
    m_iNextRiddleLvl = 1;
}

$(document).on('click', '#prevPage-btn', function() {
    window.location.href = "/Manager/GameType.html";
});

$(document).on('click', '#nextPage-btn', function() {
        if ($("tbody > tr").length > 0) {
        window.location.href =   "/Manager/GameSettings.html";
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
        var clickedRowCells = clickedEvent.currentTarget.cells;
        if (clickedRowCells.length > 0 && clickedRowCells[0] && clickedRowCells[1]) {
            var clickedRiddle = riddles[clickedRowCells[0].innerText][clickedRowCells[1].innerText];
            edittedRiddleDeleteButton = $(clickedRowCells[clickedRowCells.length - 1]).children();
            editFlag = false;
            editRiddle(clickedRiddle);
        }
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
        data: "requestType=GameBuilder&action=add&riddle=" + JSON.stringify(riddle),token: sessionStorage.getItem("access token"),
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
    resetLocation();
    //TODO: Remove location
}

function resetLocation() {
    getLocation();
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
        data: { requestType: "GameBuilder" ,token: sessionStorage.getItem("access token")},
        type: 'GET',
        success: function(serverRiddles) {
            $(".loader").hide();
            $(".container").show();
            $(".temp").hide();

            if (serverRiddles.length > 0) {
                var levelArrSize = serverRiddles.length;
                for (var i = 0; i < levelArrSize; i++) {
                    if (serverRiddles[i]) {
                        riddles[i] = [];
                        if (serverRiddles[i].m_Riddles) {
                            var currAppearanceSize = serverRiddles[i].m_Riddles.length;
                            for (var j = 0; j < currAppearanceSize; j++) {
                                riddles[i][j] = convertRiddleToClientFormat(serverRiddles[i].m_Riddles[j]);
                            }
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
    riddle.questionOptionalImage = serverRiddle.m_OptionalQuestionImage;
    riddle.location = serverRiddle.m_Location;
    return riddle;
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
        if (riddle.questionOptionalImage && riddle.questionOptionalImage !== 'data:,') {
            riddleOptionalImage.src = riddle.questionOptionalImage;
            riddleOptionalImage.hidden = false;
        } else {
            riddleOptionalImage.src = "";
            riddleOptionalImage.hidden = true;
        } if (riddle.location) {
            showPosition1(riddle.location);
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
            // minisizeImg(image[0], MAX_IMG_HEIGHT, MAX_IMG_WIDTH);
            image[0].width = 100;
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
        data: { requestType: "GameBuilder", action: "delete", riddle: JSON.stringify(riddles[removedFirstIdx][removedSecondIdx]), token: sessionStorage.getItem("access token") },
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
        location: riddleLocationCheckBox.checked ? locationCoords : ""
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
    var $levelTable = $('<table class="table table-hover"></table>');
    $levelTable.append($('<thead>' +
        '<tr>' +
            '<th style="font-size: 110%" class="col-xs-1">Level ' + m_iNextRiddleLvl + '</th>' +
            '<th class="col-xs-2">Name</th>' +
            '<th class="col-xs-4">Question</th>' +
            '<th class="col-xs-4">Answer</th>' +
            '<th class="col-xs-1"></th>' +
        '</tr></thead>'));
    $levelTable.append($('<tbody id="' + RIDDLE_LVL_ID_FORMAT + m_iNextRiddleLvl + '">' +
        '<tr>' +
        '<td style="border: 0px">' +
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
    $eRow.append('<td>' + riddle.questionText + '</td>');
    if (riddle.type === TEXT_ANSWER) {
        $eRow.append('<td>' + riddle.answer + '</td>');
    } else {
        var $td = $('<td></td>');
        var img = new Image();
        img.src = riddle.answer;
        img.height = 100;
        //img.onLoad = minisizeImg(img, 80, 100);
        $td.append(img);
        $eRow.append($td);
    }
    $eRow.append(REMOVE_BUTTON_SVG);
    eBodyToAddTo.before($eRow);
}

function showPosition1(position) {
    //TODO: Change the size of the map to be more dynamic
    var mapWidth = document.body.clientWidth < 500 ? 240 : 400;
    var mapHeight = parseInt((mapWidth * 3) / 4);
    locationCoords = position;
    var img_url = "https://maps.googleapis.com/maps/api/staticmap?markers=color:blue%7Clabel:S%7C"
        +position+"&zoom=14&size=" + mapWidth + "x" + mapHeight + "&sensor=false&key=AIzaSyAsfLflI-UcGro_hBwjIQyIFVndLphZjOE";

    mapHolder.innerHTML = "<img src='"+img_url+"'>";
    showLocationElems();
}

function showPosition(position) {
    var location = position.coords.latitude + "," + position.coords.longitude;
    showPosition1(location)
}

function showLocationElems() {
    riddleLocationCheckBox.checked = true;
    $('.location-info').show();
}