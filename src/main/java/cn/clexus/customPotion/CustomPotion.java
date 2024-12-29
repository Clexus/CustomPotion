package cn.clexus.customPotion;

import cn.clexus.customPotion.commands.CommandCompleter;
import cn.clexus.customPotion.commands.CommandHandler;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.EffectLoader;
import cn.clexus.customPotion.Events.EventsListener;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
        PacketEvents.getAPI().init();
        EffectLoader.registerAllEffects();
        PotionManager.startEffectProcessor(this);
        CustomEffectType.initializeStaticFields();
        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
        this.getCommand("customeffect").setExecutor(new CommandHandler());
        this.getCommand("customeffect").setTabCompleter(new CommandCompleter());
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
