package gjum.minecraft.forge.chatToDiscord;

import gjum.minecraft.forge.chatToDiscord.config.ChatToDiscordConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

class WebHookLoop extends Thread {
    private final LinkedList<byte[]> chatQueue = new LinkedList<>();

    @Override
    public void run() {
        while (true) {
            try {
                runLoop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void pushJson(String json) {
        chatQueue.add(json.getBytes(StandardCharsets.UTF_8));
        interrupt();
    }

    private synchronized byte[] popChatJson() {
        return chatQueue.poll();
    }

    private void runLoop() throws IOException {
        byte[] json = popChatJson();
        if (json == null) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
            return; // jump back up to popChatJson
        }

        if (ChatToDiscordConfig.instance.webhookUrl == null || ChatToDiscordConfig.instance.webhookUrl.length() <= 0)
            return;

        HttpURLConnection connection = (HttpURLConnection) new URL(ChatToDiscordConfig.instance.webhookUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        connection.setRequestProperty("Content-Length", String.valueOf(json.length));
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(json);
            os.flush();

            if (connection.getResponseCode() < 200 || 300 <= connection.getResponseCode()) {
                ChatToDiscordMod.logger.error(connection.getResponseCode() + ": " + connection.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
