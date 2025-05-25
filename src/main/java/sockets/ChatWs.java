
package sockets;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@ServerEndpoint("/chat")
public class ChatWs {
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        // No validamos aquí, esperamos el mensaje de autenticación
        sessions.add(session);
        System.out.println("Nueva conexión WebSocket establecida");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String type = jsonNode.get("type").asText();

            if ("auth".equals(type)) {
                // Mensaje de autenticación
                String username = jsonNode.get("username").asText();
                if (username == null || username.trim().isEmpty()) {
                    session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "No logueado"));
                    return;
                }

                // Guardar username en la sesión
                session.getUserProperties().put("username", username);
                session.getUserProperties().put("authenticated", true);

                // Notificar conexión
                String connectMessage = String.format(
                        "{\"user\": \"Sistema\", \"text\": \"%s se ha conectado al chat\", \"time\": \"%s\"}",
                        username, LocalDateTime.now().format(formatter));
                broadcast(connectMessage, "Sistema");

            } else if ("message".equals(type)) {
                // Mensaje de chat
                Boolean authenticated = (Boolean) session.getUserProperties().get("authenticated");
                String username = (String) session.getUserProperties().get("username");

                if (authenticated == null || !authenticated || username == null) {
                    session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "No logueado"));
                    return;
                }

                String text = jsonNode.get("text").asText();
                String timestamp = LocalDateTime.now().format(formatter);
                String jsonMessage = String.format(
                        "{\"user\": \"%s\", \"text\": \"%s\", \"time\": \"%s\"}",
                        username, escapeJson(text), timestamp);
                broadcast(jsonMessage, username);
            }

        } catch (Exception e) {
            // Si no es JSON válido, tratarlo como mensaje simple (retrocompatibilidad)
            Boolean authenticated = (Boolean) session.getUserProperties().get("authenticated");
            String username = (String) session.getUserProperties().get("username");

            if (authenticated == null || !authenticated || username == null) {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "No logueado"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            String timestamp = LocalDateTime.now().format(formatter);
            String jsonMessage = String.format(
                    "{\"user\": \"%s\", \"text\": \"%s\", \"time\": \"%s\"}",
                    username, escapeJson(message), timestamp);
            broadcast(jsonMessage, username);
        }
    }

    @OnClose
    public void onClose(Session session) {
        String username = (String) session.getUserProperties().get("username");
        sessions.remove(session);
        if (username != null) {
            String disconnectMessage = String.format(
                    "{\"user\": \"Sistema\", \"text\": \"%s ha abandonado el chat\", \"time\": \"%s\"}",
                    username, LocalDateTime.now().format(formatter));
            broadcast(disconnectMessage, "Sistema");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error en WebSocket: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void broadcast(String message, String sender) {
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                sessions.remove(session);
                e.printStackTrace();
            }
        });
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"")
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}