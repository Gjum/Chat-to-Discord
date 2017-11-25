package gjum.minecraft.forge.chatToDiscord;

import gjum.minecraft.forge.chatToDiscord.config.ChatToDiscordConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ChatSender {

    private final WebHookLoop sender;

    public ChatSender(WebHookLoop sender) {
        this.sender = sender;
    }

    public void pushChat(ChatLine chatLine, ChatToDiscordConfig config) throws Exception {
        if (chatLine.group != null && config.trackedGroupsOn && !config.trackedGroups.contains(chatLine.group.toLowerCase()))
            return;

        if (chatLine.group != null && config.ignoredGroupsOn && config.ignoredGroups.contains(chatLine.group.toLowerCase()))
            return;

        sender.pushJson(formatChat(chatLine, config.discordMsgFormat));
    }

    private static String formatChat(ChatLine line, String fmt) throws Exception {
        final Date now = new Date();
        final DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String nowLocal = df.format(now);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String nowUtf = df.format(now);

        return fmt
                .replaceAll("<group>", line.group)
                .replaceAll("<player>", line.player)
                .replaceAll("<message>", line.message)
                .replaceAll("<localtime>", nowLocal)
                .replaceAll("<time>", nowUtf);
    }
}
