package cn.clexus.customPotion;

import cn.clexus.customPotion.commands.CommandCompleter;
import cn.clexus.customPotion.commands.CommandHandler;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.events.EventsListener;
import cn.clexus.customPotion.hooks.mythicmobs.MythicMobsEvents;
import cn.clexus.customPotion.hooks.mythicmobs.MythicMobsSupport;
import cn.clexus.customPotion.hooks.placeholderapi.PlaceholderAPISupport;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.EffectLoader;
import cn.clexus.customPotion.utils.I18n;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public final class CustomPotion extends JavaPlugin {
    public static CustomPotion plugin;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        plugin = this;
    }

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this,25175);
        PacketEvents.getAPI().init();
        saveDefaultConfig();
        saveMessageExampleFile();

        File messageFile = new File(getDataFolder(), "message.yml");
        FileConfiguration message = YamlConfiguration.loadConfiguration(messageFile);
        I18n.initialize(message);
        MythicMobsSupport.init(this);
        PlaceholderAPISupport.init(this);
        EffectLoader.registerAllEffects();
        PotionManager.startEffectProcessor(this);
        CustomEffectType.initializeStaticFields();
        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
        if(MythicMobsSupport.hasSupport()) {
            Bukkit.getPluginManager().registerEvents(new MythicMobsEvents(), this);
        }
        this.getCommand("customeffect").setExecutor(new CommandHandler());
        this.getCommand("customeffect").setTabCompleter(new CommandCompleter());
    }
    private void saveMessageExampleFile() {
        File messageFile = new File(getDataFolder(), "message.yml");
        if (!messageFile.exists()) {
            try (InputStream resourceStream = getResource("message.yml")) {
                if (resourceStream != null) {
                    Files.copy(resourceStream, messageFile.toPath());
                }
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "An error occurred when copying files:", e);
            }
        }
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
