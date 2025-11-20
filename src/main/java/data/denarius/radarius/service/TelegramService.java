package data.denarius.radarius.service;

public interface TelegramService {
    void sendMessage(String chatId, String message);
    void sendMessageWithButton(String chatId, String message, String buttonText, String buttonUrl);
}
