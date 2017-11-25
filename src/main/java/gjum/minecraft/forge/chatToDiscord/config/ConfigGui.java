package gjum.minecraft.forge.chatToDiscord.config;

import gjum.minecraft.forge.chatToDiscord.ChatToDiscordMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parent) {
        super(parent, getConfigElements(),
                ChatToDiscordMod.MOD_ID, false, false, ChatToDiscordMod.MOD_NAME + " config");
    }

    private static List<IConfigElement> getConfigElements() {
        Configuration config = ChatToDiscordConfig.instance.config;
        return new ConfigElement(config.getCategory(ChatToDiscordConfig.CATEGORY_MAIN)).getChildElements();
    }

}
