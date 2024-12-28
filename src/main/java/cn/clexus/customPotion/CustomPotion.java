package cn.clexus.customPotion;

import cn.clexus.customPotion.commands.CommandHandler;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.Events;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomPotion extends JavaPlugin {
    public static CustomPotion plugin;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        PotionManager.startEffectProcessor(this);
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        this.getServer().getCommandMap().register("customeffect",new CommandHandler("customeffect"));
    }

    @Override
    public void onDisable() {

    }
}
