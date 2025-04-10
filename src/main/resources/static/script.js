// Function to start a new game session
function startSession() {
    fetch(`${window.location.origin}/api/startSession`)
        .then(response => response.json())
        .then(data => {
            console.log('Response data:', data)
            const sessionCode = data.sessionCode;
            document.getElementById("session-code").textContent = sessionCode;
            document.getElementById("session-code-container").style.display = "block";

            // Redirect to the game session page (frontend port 8000)
            window.location.href = `/game/${sessionCode}`;
        })
        .catch(error => {
            console.error('Error creating session:', error);
        });
}

// Function to join an existing game session
function joinGame() {
    // Use embedded sessionCode if available
    const embeddedCode = sessionCode || null; // sessionCode from Thymeleaf
    const inputCode = document.getElementById("code-input").value;
    const sessionCodeToJoin = embeddedCode || inputCode;

    if (!sessionCodeToJoin) {
        alert("Please enter a session code or use an active session.");
        return;
    }

    fetch(`${window.location.origin}/api/joinGame`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ code: sessionCodeToJoin }),
    })
        .then(response => response.json()
            .then(data=> {
                if (data.status === 'success') {
                    alert(data.message); // "Successfully joined the session."
                    window.location.href = `/game/${sessionCodeToJoin}`;
                } else if (data.status === 'full') {
                    alert(data.message); // "Session is full."
                } else {
                    alert(data.message); // "Invalid session code."
                }
            })
        .catch(error => {
            console.error('Error joining the game:', error);
        }));
}
