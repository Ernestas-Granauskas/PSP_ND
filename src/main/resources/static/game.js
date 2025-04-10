document.body.style.background = "#8a8a8a"
document.addEventListener("DOMContentLoaded", () => {
    // Extract session code from the URL
    const sessionCode = window.location.pathname.split('/').pop();

    // Debug: Log session code to the console
    console.log("Extracted session code:", sessionCode);

    // Update the session-info element if a session code exists
    const sessionInfoElement = document.getElementById("session-info");
    if (sessionCode) {
        sessionInfoElement.textContent = `Session Code: ${sessionCode}`;
    } else {
        console.error("Failed to extract session code. URL may not contain a session code.");
    }

    // WebSocket connection
    const socket = new WebSocket(`${window.location.origin.replace(/^http/, 'ws')}/ws/${sessionCode}`);

    // When the WebSocket connection is open, set up a listener for messages
    socket.onopen = function () {
        console.log("Connected to WebSocket server");

        // Inform backend to join the session
        socket.send(JSON.stringify({
            type: "JOIN_SESSION",
            sessionCode: sessionCode
        }));
    };

    // Listen for state updates from the other player
    socket.onmessage = function (event) {
        const data = JSON.parse(event.data);
        console.log(data);
        if(data.sessionCode === sessionCode){
            const newState = data.state;
            const gameState = document.getElementById("game-state");
            switch(newState){
                case "win":
                    gameState.textContent = "😎";
                    break;
                case "fail":
                    gameState.textContent = "😵";
                    break;
                default:
                    gameState.textContent = "🙂";
                    break;
            }
            const gameBoard = data.gameBoard;

            for(let i=1; i<=12; i++) {
                for(let j=1; j<=12; j++) {
                    let button = document.getElementById(`button-${i}-${j}`);
                    switch(gameBoard[i-1][j-1]){
                        case 'm':
                            button.textContent = "💣";
                            button.style.fontSize = "20px";
                            button.style.background = "#a8a8a8";
                            break;
                        case 'f':
                            button.textContent = "🚩";
                            button.style.fontSize = "20px";
                            button.style.background = "#d0d0d0";
                            break;
                        case '0':
                            button.textContent = " ";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        case '1':
                            button.textContent = "1";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        case '2':
                            button.textContent = "2";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        case '3':
                            button.textContent = "3";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        case '4':
                            button.textContent = "4";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        case '5':
                            button.textContent = "5";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        case '6':
                            button.textContent = "6";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        case '7':
                            button.textContent = "7";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        case '8':
                            button.textContent = "8";
                            button.style.fontSize = "28px";
                            button.style.background = "#a8a8a8";
                            break;
                        default:
                            button.textContent = " ";
                            button.style.fontSize = "28px";
                            button.style.background = "#d0d0d0";
                            break;
                    }
                }
            }

        }


        // Update the button state to reflect the change
        //toggleButton.textContent = newState;
    };

    // Function to toggle button state and send it to the backend
    // window.
    // = function () {
    //     const toggleButton = document.getElementById("toggle-button");
    //const newState = toggleButton.textContent === "0" ? "1" : "0"; // Toggle between 0 and 1
    //toggleButton.textContent = newState;

    window.reset = function(){
        fetch(`${window.location.origin}/api/reset`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                sessionCode: sessionCode,
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Failed to reset board");
                }
            })
            .catch(error => {
                console.error("Error resetting board:", error);
            });
    }

    // Send the state change to the backend
    window.toggleState = function (row, col) {
        fetch(`${window.location.origin}/api/toggle`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                sessionCode: sessionCode,
                row: String(row - 1),
                col: String(col - 1)
            })
        })
    .then(response => {
            if (!response.ok) {
                throw new Error("Failed to toggle state");
            }
        })
            .catch(error => {
                console.error("Error toggling state:", error);
            });
    }

    window.flag = function (row, col) {
        fetch(`${window.location.origin}/api/flag`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                sessionCode: sessionCode,
                row: String(row - 1),
                col: String(col - 1)
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Failed to flag cell");
                }
            })
            .catch(error => {
                console.error("Error flagging cell:", error);
            });
    }
    // fetch(${window.location.origin}/api/toggle, {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json'
    //     },
    //     body: JSON.stringify({
    //         sessionCode: sessionCode,
    //         newState: newState
    //     })
    // })
    //     .then(response => response.json())
    //     .then(data => {
    //         console.log('State updated:', data);
    //
    //         // Notify other player through WebSocket
    //         socket.send(JSON.stringify({
    //             sessionCode: sessionCode,
    //             newState: newState
    //         }));
    //     })
    //     .catch(error => {
    //         console.error('Error updating state:', error);
    //     });
    // };

    // Handle window unload events to notify backend
    window.addEventListener("beforeunload", () => {
        navigator.sendBeacon(`${window.location.origin}/api/leave`, JSON.stringify({
            code: sessionCode
        }));
    });
});