package gjum.minecraft.forge.chatToDiscord.config;


import gjum.minecraft.forge.chatToDiscord.ChatToDiscordMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ChatToDiscordConfig {
    public static final String CATEGORY_MAIN = "Main";

    public static final ChatToDiscordConfig instance = new ChatToDiscordConfig();

    public Configuration config;

    public boolean enabled;
    public String webhookUrl;
    public String discordMsgFormat;
    private Property propEnabled, propWebhookUrl, propDiscordMsgFormat;

    public boolean ignoredGroupsOn;
    public boolean trackedGroupsOn;
    public HashSet<String> ignoredGroups;
    public HashSet<String> trackedGroups;
    private Property propignoredGroupsOn, proptrackedGroupsOn, propignoredGroups, proptrackedGroups;

    private ChatToDiscordConfig() {
    }

    public void load(File configFile) {
        config = new Configuration(configFile, ChatToDiscordMod.VERSION);
        syncProperties();
        config.load();
        syncProperties();
        syncValues();
    }

    public void afterGuiSave() {
        syncProperties();
        syncValues();
    }

    public void setEnabled(boolean enabled) {
        syncProperties();
        propEnabled.set(enabled);
        syncValues();
    }

    /**
     * no idea why this has to be called so often, ideally the prop* would stay the same,
     * but it looks like they get disassociated from the config sometimes and setting them no longer has any effect
     */
    private void syncProperties() {
        propEnabled = config.get(CATEGORY_MAIN, "enabled", true, "Enable/disable chat sending");
        propDiscordMsgFormat = config.get(CATEGORY_MAIN, "discord message format", "{\"username\":\"<player>\",\"avatar_url\":\"https://minotar.net/avatar/<player>\",\"content\":\"`[<group>]` <message>\"}", "");
        propWebhookUrl = config.get(CATEGORY_MAIN, "webhook url", "", "Get this from the discord channel settings");

        propignoredGroupsOn = config.get(CATEGORY_MAIN, "enable ignored groups", false, "Ignore chat on listed groups.");
        proptrackedGroupsOn = config.get(CATEGORY_MAIN, "enable tracked groups", false, "Only show chat on listed groups");
        propignoredGroups = config.get(CATEGORY_MAIN, "ignored groups",
                new String[]{}, "If enabled, chat on these groups will NOT show up, even if they're also in the ignored groups list.");
        proptrackedGroups = config.get(CATEGORY_MAIN, "tracked groups",
                new String[]{}, "If enabled, ONLY chat on these groups will show up (unless they're also in the ignored groups list).");

        List<String> menuItems = new ArrayList<>(Arrays.asList("webhook url", "enabled",
                "discord message format",
                "enable tracked groups", "tracked groups",
                "enable ignored groups", "ignored groups"));
        config.setCategoryPropertyOrder(CATEGORY_MAIN, menuItems);
    }

    /**
     * called every time a prop is changed, to apply the new values to the fields and to save the values to the config file
     */
    private void syncValues() {
        enabled = propEnabled.getBoolean();
        discordMsgFormat = propDiscordMsgFormat.getString();
        webhookUrl = propWebhookUrl.getString();

        ignoredGroupsOn = propignoredGroupsOn.getBoolean();
        trackedGroupsOn = proptrackedGroupsOn.getBoolean();

        String[] ignoredGroupsArr = propignoredGroups.getStringList();
        ignoredGroups = new HashSet<>(ignoredGroupsArr.length);
        for (String s : ignoredGroupsArr) {
            ignoredGroups.add(s.toLowerCase());
        }

        String[] trackedGroupsArr = proptrackedGroups.getStringList();
        trackedGroups = new HashSet<>(trackedGroupsArr.length);
        for (String s : trackedGroupsArr) {
            trackedGroups.add(s.toLowerCase());
        }

        if (config.hasChanged()) {
            config.save();
            syncProperties();
            ChatToDiscordMod.logger.info("Saved " + ChatToDiscordMod.MOD_NAME + " config.");
        }
    }
}
