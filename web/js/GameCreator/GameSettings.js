/**
 * Created by Dean Arbel on 24/02/2017.
 */
var TREASURE_CHEST = "Treasure Chest";
var TROPHY = "Trophy";
var MAX_DURATION = 72;
var MIN_DURATION = 1;

var treasureType;
var treasureLocation;
var gameDuration;
var startDate;
var startTime;

$(function () {
    $(".loader").hide();
    $(".settings-container").show();
    sessionStorage.setItem("PrevPage", "GameSettings");
    initGlobalVars();
    updateDropdownValue(TREASURE_CHEST);
});

$(document).on('click', '#prevPage-btn', function() {
    window.location.href = SITE_URL + "/Manager/GameBuilder.html";
});

$(document).on('click', '#nextPage-btn', function() {
    //TODO: Implement (update server and on success open "published page successfuly" dialog, which will take you to the game or home)
    var settingsAndErr = buildGameSettings();
    if (settingsAndErr[1] === "") {
        $.ajax({
            url: GAME_CREATOR_COMPONENTS_URL,
            data: { requestType: "GameSettings", settings: JSON.stringify(settingsAndErr[0])},
            type: 'POST',
            success: function(gameId) {
                //TODO: Show dialog that will take you back home and will contain all the data you need
                confirm("Game settings for have been set, your game id is: " + gameId);
                window.location.href = SITE_URL + "/Home.html";
            }
        });
    }
    else {
        confirm(settingsAndErr[1]);
    }
});

function initGlobalVars() {
    treasureType = $('#settings-type');
    //TODO: assign treasureLocation
    gameDuration = $('#settings-duration');
    startDate = $('#settings-start-date');
    startTime = $('#settings-start-time');
}

function buildGameSettings() {
    var errMsg = "",
        settings = {};
    settings.treasureType = treasureType[0].innerText;
    settings.duration = parseFloat(gameDuration[0].value);
    if (!settings.duration || settings.duration > MAX_DURATION || settings.duration < MIN_DURATION) {
        errMsg += "- Game duration should be between " + MIN_DURATION + "-" + MAX_DURATION + " hours\n";
    }
    settings.startTime = new Date(startDate[0].value + " " + startTime[0].value).getTime();
    if (!isDateOk(settings.startTime)) {
        errMsg += "- Please give a valid start date and time\n";
    }
    //TODO: Check treasure location
    return [settings, errMsg];
}

function isDateOk(date) {
    var today = new Date().getTime();
    return today <= date;
}

function dropdownChange(innerText) {}