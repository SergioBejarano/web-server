function sendGreeting() {
    const name = document.getElementById("name").value || "Mundo";

    fetch(`/app/hello?name=${encodeURIComponent(name)}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById("response").innerText = data.mensaje;
        })
        .catch(error => {
            document.getElementById("response").innerText = "Error de conexión";
            console.error(error);
        });
}

function sendEcho() {
    const message = document.getElementById("echo").value || "";

    fetch("/app/hello", {
        method: "POST",
        headers: {
            "Content-Type": "text/plain"
        },
        body: message
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById("echoResponse").innerText = data.echo;
    })
    .catch(error => {
        document.getElementById("echoResponse").innerText = "Error de conexión";
        console.error(error);
    });
}