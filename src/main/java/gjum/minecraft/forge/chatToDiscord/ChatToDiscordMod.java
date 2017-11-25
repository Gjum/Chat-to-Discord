package gjum.minecraft.forge.chatToDiscord;

import gjum.minecraft.forge.chatToDiscord.config.ChatToDiscordConfig;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = ChatToDiscordMod.MOD_ID,
        name = ChatToDiscordMod.MOD_NAME,
        version = ChatToDiscordMod.VERSION,
        guiFactory = "gjum.minecraft.forge.chatToDiscord.config.ConfigGuiFactory",
        clientSideOnly = true)
public class ChatToDiscordMod {

    public static final String MOD_ID = "chat-to-discord";
    public static final String MOD_NAME = "Chat to Discord";
    public static final String VERSION = "@VERSION@";
    public static final String BUILD_TIME = "@BUILD_TIME@";

    public static Logger logger;
    private long lastCrash = 0;

    private WebHookLoop webHookLoop;
    private ChatSender chatSender;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        File configFile = event.getSuggestedConfigurationFile();
        logger.info("Loading config from " + configFile);
        ChatToDiscordConfig.instance.load(configFile);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info(String.format("%s version %s built at %s", MOD_NAME, VERSION, BUILD_TIME));

        MinecraftForge.EVENT_BUS.register(this);
        new KeyHandler();
        webHookLoop = new WebHookLoop();
        webHookLoop.start();
        chatSender = new ChatSender(webHookLoop);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        try {
            if (!ChatToDiscordConfig.instance.enabled) return;

            ChatLine chatLine = ChatLine.fromChat(event.getMessage());
            if (chatLine != null) {
                chatSender.pushChat(chatLine, ChatToDiscordConfig.instance);
            }
        } catch (Exception e) {
            if (lastCrash < System.currentTimeMillis() - 5000) {
                lastCrash = System.currentTimeMillis();
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        try {
            if (event.getModID().equals(ChatToDiscordMod.MOD_ID)) {
                ChatToDiscordConfig.instance.afterGuiSave();
            }
        } catch (Exception e) {
            if (lastCrash < System.currentTimeMillis() - 5000) {
                lastCrash = System.currentTimeMillis();
                e.printStackTrace();
            }
        }
    }
}
