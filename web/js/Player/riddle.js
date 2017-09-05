/**
 * Created by Dean on 03/03/2017.
 */
var mGameCode;
var mRiddleCode;
var mRiddleName;
var mQuestionBody;
var mAnswerBox;
var mPhotoAnswer;
var mSubmitBtn;
var mServerAnswer;
var oModal;
var oModalMsg;
var oModalGoToLobbyButton;
var oModalOkButton;

$(function () {
    sessionStorage.setItem("PrevPage", "Riddle");
    document.getElementById('riddle-answer-text').oninput = onTextAnswerInput;
    initGlobalVars();
    initPageElementsFromServer();
});

$(document).on("click", "#prevPage-btn", function() {
    goBackToLobby();
});

function initGlobalVars() {
    mGameCode = getParameterByName("gameCode");
    mRiddleCode = getParameterByName("riddle");
    mRiddleName = $('#riddle-name');
    mQuestionBody = $('#riddle-question-body');
    mAnswerBox = $('#riddle-answer-text')[0];
    mPhotoAnswer = $('#riddle-answer-photo-image')[0];
    mSubmitBtn = $("#riddle-submit-btn")[0];
    oModal = $("#myModal");
    oModalMsg = $("#modal-msg")[0];
    oModalGoToLobbyButton = $("#modal-btn-lobby")
    oModalOkButton = $("#modal-btn-ok");
}

function initPageElementsFromServer() {
    $.ajax({
        url: RIDDLE_URL,
        type: 'GET',
        data: {gameCode: mGameCode, riddleCode: mRiddleCode, token: sessionStorage.getItem("access token")},
        success: function(gameData) {
            var optImg = new Image();
            optImg.src = gameData.optionalImage;
            mRiddleName[0].innerText = gameData.name;
            mQuestionBody[0].innerText = gameData.question;
            if (optImg.src) {
                mQuestionBody.append('<br>');
                mQuestionBody.append(optImg);
            }

            $(".loading-area").hide();
            $(".container").show();
            if (gameData.answer) {
                mServerAnswer = new Image();
                mServerAnswer.src = gameData.answer;
                $(".riddle-answer-photo-area").show();
            } else {
                $(".riddle-answer-text-area").show();
            }
        },
        error: function(err) {
            alert(err);
        }
    });
}

function onTextAnswerInput() {
    mSubmitBtn.disabled = mAnswerBox.value === "";
}

function onPhotoAnswerInput() {
    mSubmitBtn.disabled = !mPhotoAnswer.src;
}

function submitAnswer() {
    var answer = mServerAnswer ? pictureComparison(mServerAnswer, mPhotoAnswer) : mAnswerBox.value;
    mSubmitBtn.disabled = true;
    $.ajax({
        url: RIDDLE_URL,
        type: 'POST',
        data: {gameCode: mGameCode, riddleCode: mRiddleCode, answer: answer, token: sessionStorage.getItem("access token")},
        success: function(data) {
            oModal.modal('toggle');
            if (data) {
                oModalMsg.innerText = "You got the answer right!";
                oModalGoToLobbyButton.show();
                mSubmitBtn.disabled = true;
                mAnswerBox.disabled = true;
            }
            else {
                oModalMsg.innerText = "Nice try, but wrong answer!";
                oModalGoToLobbyButton.hide();
                mSubmitBtn.disabled = false;
            }
        },
        error: function(err) {
            alert("Server has encountered an error, please try again");
            window.location.reload();
        }
    });
}

function goBackToLobby() {
    window.location.href =   "/Player/GameLobby.html?gameCode=" + mGameCode;
}