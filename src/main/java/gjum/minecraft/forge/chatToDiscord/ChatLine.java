package gjum.minecraft.forge.chatToDiscord;

import net.minecraft.util.text.ITextComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatLine {
    public static final Pattern groupChatPattern = Pattern.compile("\\[(\\w+)\\] (\\w+): (.*)");

    public final String line;
    public final String group;
    public final String player;
    public final String message;

    public ChatLine(String line, String group, String player, String message) {
        this.line = line;
        this.group = group;
        this.player = player;
        this.message = message;
    }

    public static ChatLine fromChat(ITextComponent rawMessage) {
        final String line = stripMinecraftFormattingCodes(rawMessage.getUnformattedText());

        Matcher matcher = groupChatPattern.matcher(line);
        if (!matcher.matches()) {
            return null;
        }

        final String group = matcher.group(1);
        final String player = matcher.group(2);
        final String message = matcher.group(3);

        return new ChatLine(line, group, player, message);
    }

    private static String stripMinecraftFormattingCodes(String str) {
        return str.replaceAll("(?i)\\u00A7[a-z0-9]", "");
    }
}
