/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




var initialState = {
    running: false,
    querying: false,
    ready: false,
    status: "",
    currentId: undefined,
    fileName: ""
};

var currentState = {};



var form = $("#form");
var submitButton = $("#submitButton");
var downloadButton = $("#downloadButton");
var progressText = $("#statusText");
var loadingIcon = $("#loadingIcon");
var adaptiveColumnsInput = $("#adaptiveColumns")
var cancelButton = $("#cancelButton");

var form = $("#form");

const PROCESSING = "Cancel"
const SUBMIT = "Submit";
const RETENTION_TIME = 1000 * 60 * 2;


function resetState() {


    // in case a new job started while in  a mean time.
    //if (currentState.running)
    //    return;


    currentState = {};
    Object.assign(currentState, initialState);
    updateView();
}

function updateView() {


    var state = currentState;

    submitButton.prop("value", state.running ? PROCESSING : SUBMIT);
    //submitButton.prop("disabled", state.running);

    state.ready ? downloadButton.show() : downloadButton.hide();
    downloadButton.attr("href", "download?id=" + state.currentId);
    downloadButton.text("Download " + state.fileName);
    progressText.text(state.status);
    state.running ? loadingIcon.show() : loadingIcon.hide();
    currentState = state;
    state.running ? cancelButton.show() : cancelButton.hide();



}

// Check the state of the job by querying the server
function checkState() {
    
    
    if(currentState.running == false) {
        return;
    }
    
    // get request of the job information
    $.ajax({
        url: "job?id=" + currentState.currentId
        , type: "GET"

        , success: function (data) {
            
            currentState.running = data.finished !== true;
            currentState.ready = data.finished;
            currentState.status = data.status;
            currentState.fileName = data.fileName;
            if (currentState.running) {
                setTimeout(checkState, 1000);
            }
            updateView();
        } 
    });
}

//
$(document).ready(function () {



    resetState();

    // add the submit event to the download button
    submitButton.click(function () {
        console.log(currentState);
        if(currentState.running) {
            //resetState();
            $.ajax({
                url:"cancel?id="+currentState.currentId
                ,type: "GET"
                ,success: function (data) {
                    resetState();
                }
            });
            resetState();
        }
        else {

            $.ajax({
                url: "job"
                , type: "POST"
                , data: form.serialize()
                , success: function (data) {
                    currentState.currentId = data.id;
                    currentState.running = true;
                    updateView(currentState);

                    checkState();
                }
            });
    }
    });


});


