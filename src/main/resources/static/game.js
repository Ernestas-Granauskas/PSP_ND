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
    };

    // Listen for state updates from the other player
    socket.onmessage = function (event) {
        const data = JSON.parse(event.data);
        console.log(data);
        if(data.sessionCode === sessionCode){
            const newState = data.state;
            const toggleButton = document.getElementById("toggle-button");
            toggleButton.textContent = newState;
        }


        // Update the button state to reflect the change
        //toggleButton.textContent = newState;
    };

    // Function to toggle button state and send it to the backend
    // window.toggleState = function () {
    //     const toggleButton = document.getElementById("toggle-button");
        //const newState = toggleButton.textContent === "0" ? "1" : "0"; // Toggle between 0 and 1
        //toggleButton.textContent = newState;

        // Send the state change to the backend
        window.toggleState = function () {
            fetch(`${window.location.origin}/api/toggle`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({sessionCode: sessionCode})
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
        // fetch(`${window.location.origin}/api/toggle`, {
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
