/**
 * Created by Dean on 03/03/2017.
 */
var mGameCode;
var mRiddleCode;
var mRiddleName;
var mQuestionBody;
var mAnswerBox;
var mSubmitBtn;

$(function () {
    sessionStorage.setItem("PrevPage", "Riddle");
    document.getElementById('riddle-answer').oninput = onAnswerInput;
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", "#prevPage-btn", function() {
    window.location.href = SITE_URL + "/Player/GameLobby.html?gameCode=" + mGameCode;
});

function initGlobalVars() {
    mGameCode = getParameterByName("gameCode");
    mRiddleCode = getParameterByName("riddle");
    mRiddleName = $('#riddle-name');
    mQuestionBody = $('#riddle-question-body');
    mAnswerBox = $('#riddle-answer')[0];
    mSubmitBtn = $("#riddle-submit-btn")[0];
}

function initPageElementsFromServer() {
    $.ajax({
        url: RIDDLE_URL,
        type: 'GET',
        data: {gameCode: mGameCode, riddleCode: mRiddleCode},
        success: function(gameData) {
            mRiddleName[0].innerText = gameData.name;
            mQuestionBody[0].innerText = gameData.question;
            //TODO: Handle picture
            $(".loading-area").hide();
            $(".container").show();
        },
        error: function(err) {
            alert(err);
        }
    });
}

function onAnswerInput() {
    mSubmitBtn.disabled = mAnswerBox.value === "";
}

function submitAnswer() {
    $.ajax({
        url: RIDDLE_URL,
        type: 'POST',
        data: {gameCode: mGameCode, riddleCode: mRiddleCode, answer: mAnswerBox.value},
        success: function(data) {
            if (data) {
                confirm("You got to answer right!");
                mSubmitBtn.disabled = true;
                mAnswerBox.disabled = true;
            }
            else {
                confirm("Nice try, but wrong answer!");
            }
        },
        error: function(err) {
            alert("Server has encountered an error, please try again");
            window.location.reload();
        }
    });
}