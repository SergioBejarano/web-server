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