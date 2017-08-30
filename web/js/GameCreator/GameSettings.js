/**
 * Created by Dean on 24/02/2017.
 */
var TREASURE_CHEST = "Treasure Chest";
var TROPHY = "Trophy";
var MAX_DURATION = 72;
var MIN_DURATION = 1;

var mTreasureType;
var mTreasureLocation;
var mGameName;
var mGameDuration;
var mStartDate;
var mStartTime;

$(function () {
    $(".loader").hide();
    $(".settings-container").show();
    sessionStorage.setItem("PrevPage", "GameSettings");
    initGlobalVars();
    updateDropdownValue(TREASURE_CHEST);
});

$(document).on('click', '#prevPage-btn', function() {
    window.location.href =   "/Manager/GameBuilder.html";
});

$(document).on('click', '#nextPage-btn', function() {
    //TODO: Implement (update server and on success open "published page successfuly" dialog, which will take you to the game or home)
    var settingsAndErr = buildGameSettings();
    if (settingsAndErr[1] === "") {
        $.ajax({
            url: GAME_CREATOR_COMPONENTS_URL,
            data: { requestType: "GameSettings", settings: JSON.stringify(settingsAndErr[0]), token: sessionStorage.getItem("access token")},
            type: 'POST',
            success: function(gameId) {
                //TODO: Show dialog that will take you back home and will contain all the data you need
                confirm("Game settings for have been set, your game code is: " + gameId);
                window.location.href =   "/Home.html";
            }
        });
    }
    else {
        confirm(settingsAndErr[1]);
    }
});

function initGlobalVars() {
    mTreasureType = $('#settings-type');
    //TODO: assign mTreasureLocation
    mGameName = $('#settings-name');
    mGameDuration = $('#settings-duration');
    mStartDate = $('#settings-start-date');
    mStartTime = $('#settings-start-time');
}

function buildGameSettings() {
    var errMsg = "",
        settings = {};
    settings.treasureType = mTreasureType[0].innerText;
    settings.gameName = mGameName[0].value;
    settings.duration = parseFloat(mGameDuration[0].value);
    if (!settings.duration || settings.duration > MAX_DURATION || settings.duration < MIN_DURATION) {
        errMsg += "- Game duration should be between " + MIN_DURATION + "-" + MAX_DURATION + " hours\n";
    }
    settings.startTime = new Date(mStartDate[0].value + " " + mStartTime[0].value).getTime();
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