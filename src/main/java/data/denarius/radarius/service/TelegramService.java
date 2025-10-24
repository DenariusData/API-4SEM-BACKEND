package data.denarius.radarius.service;

public interface TelegramService {
    void sendMessage(String chatId, String message);
}
