var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var headers= {token: $("#token").val()}
    var socket = new SockJS('/websocket/price-feed');
    stompClient = Stomp.over(socket);
    stompClient.connect(headers, function (frame) {
        setConnected(true);

        var url = stompClient.ws._transport.url;
        console.log("printing url");
        console.log("Your current full url is : " + url);
        url = url.replace(
            /^ws.*price-feed\//,  "");
        url = url.replace("/websocket", "");
        url = url.replace(/^[0-9]+\//, "");
        console.log("Your current session is: " + url);
        var sessionId = url;

        console.log('Connected: ' + frame);
        console.log("connected, session id: " + sessionId);
        stompClient.subscribe('/secured/user/topic/price-feed-user' + sessionId, function (message) {
            showGreeting(message);
        });
        console.log('subscribed 2');
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    console.log(message);
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    //$( "#send" ).click(function() { sendName(); });
});