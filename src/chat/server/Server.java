package chat.server;

import chat.auth.AuthenticationService;

public interface Server {
    void broadcastMessage(String message);
    boolean isLoggedIn(String nickname);
    void subscribe(ClientHandler client);
    void unsubscribe(ClientHandler client);
    AuthenticationService getAuthenticationService();
    void addLog(String message);
    void showLog();
}
