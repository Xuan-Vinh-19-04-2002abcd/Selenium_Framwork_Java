package TelegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.File;
import java.util.List;

public class TelegramService implements ITelegramService {

    private final TelegramBot bot;

    public TelegramService(String botToken) {
        this.bot = new TelegramBot(botToken);
        startListening();
    }

    private void startListening() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                handleUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String text = update.message().text();
            long chatId = update.message().chat().id();

            if ("/start".equals(text)) {
                sendMessage(String.valueOf(chatId), "Xin chào! Tôi là bot của bạn.");
            } else if ("/openchrome".equals(text)) {
                Task.openChrome();
                sendMessage(String.valueOf(chatId), "Chrome đã được mở!");
            } else {
                sendMessage(String.valueOf(chatId), "Bạn vừa gửi: " + text);
            }
        }
    }

    @Override
    public void sendMessage(String groupId, String text) {
        SendMessage request = new SendMessage(groupId, text);
        SendResponse response = bot.execute(request);
        if (!response.isOk()) {
            System.err.println("Lỗi gửi tin nhắn: " + response.description());
        }
    }

    @Override
    public void sendFile(String groupId, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File không tồn tại: " + filePath);
            return;
        }

        SendDocument request = new SendDocument(groupId, file);
        bot.execute(request);
    }

    @Override
    public void replyToMessage(String groupId, String text, int replyToMessageId) {
        SendMessage request = new SendMessage(groupId, text).replyToMessageId(replyToMessageId);
        bot.execute(request);
    }

    public static void main(String[] args) {
    }
}
