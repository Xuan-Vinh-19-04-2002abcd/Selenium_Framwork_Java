package TelegramBot;

public interface ITelegramService {
    void sendMessage(String chatId, String text);
    void sendFile(String chatId, String filePath);
    void replyToMessage(String chatId, String text, int replyToMessageId);
}
