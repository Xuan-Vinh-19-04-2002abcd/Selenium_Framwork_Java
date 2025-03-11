package TelegramBot;

import java.io.IOException;

public class Task {
    public static void openChrome() {
        String url = "http://www.google.com";
        String command;

        // Kiểm tra hệ điều hành
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            command = "cmd /c start chrome " + url; // Windows
        } else if (os.contains("mac")) {
            command = "open -a 'Google Chrome' " + url; // macOS
        } else {
            command = "google-chrome " + url; // Linux
        }

        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
