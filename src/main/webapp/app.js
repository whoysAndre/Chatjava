const socket = new WebSocket("ws://localhost:8080/Test4/chat");
const chatMessages = document.getElementById("chat-messages");
const messageInput = document.getElementById("message-input");
const sendButton = document.getElementById("send-button");

// Obtener usuario de las cookies
const username = Cookies.get("username");

if (!username) {
  alert("No estás logueado. Redirigiendo al login...");
  window.location.href = "login.html"; // Cambia por tu página de login
}

document.getElementById("username-display").textContent = `Conectado como ${username}`;

// Eventos del WebSocket
socket.onopen = () => {
  console.log("Conectado al servidor de chat");
  // Enviar username al conectarse
  const authMessage = JSON.stringify({
    type: "auth",
    username: username
  });
  socket.send(authMessage);
};

socket.onerror = (error) => {
  console.error("Error de WebSocket:", error);
};

socket.onclose = (event) => {
  console.log("Desconectado. Motivo:", event.reason);
  if (event.reason === "No logueado") {
    alert("Sesión expirada. Redirigiendo al login...");
    window.location.href = "login.html";
  }
};

socket.onmessage = (event) => {
  const message = JSON.parse(event.data);
  const messageElement = document.createElement("div");
  messageElement.classList.add("message");
  messageElement.classList.add(message.user === username ? "user" : "other");
  messageElement.innerHTML = `
        <strong>${message.user}</strong>
        <p>${message.text}</p>
        <span class="message-time">${message.time}</span>
    `;
  chatMessages.appendChild(messageElement);
  chatMessages.scrollTop = chatMessages.scrollHeight;
};

// Enviar mensaje
sendButton.addEventListener("click", () => {
  const message = messageInput.value.trim();
  if (message) {
    if (socket.readyState === WebSocket.OPEN) {
      const messageObj = JSON.stringify({
        type: "message",
        text: message
      });
      socket.send(messageObj);
      messageInput.value = "";
    } else {
      alert("Error: No estás conectado al chat. Recarga la página.");
    }
  }
});

messageInput.addEventListener("keypress", (e) => {
  if (e.key === "Enter") {
    sendButton.click();
  }
});