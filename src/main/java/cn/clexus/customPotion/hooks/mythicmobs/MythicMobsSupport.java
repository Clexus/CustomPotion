package cn.clexus.customPotion.hooks.mythicmobs;

import cn.clexus.customPotion.CustomPotion;
import io.lumine.mythic.api.MythicPlugin;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class MythicMobsSupport {
    public static boolean useMythicMobs = true;
    static MythicPlugin mythicPlugin;
    private static CustomPotion plugin;
    private static boolean hasSupport = false;
    public static boolean hasSupport(){
        return hasSupport;
    }
    public static void init(CustomPotion plugin) {
        try{
            Plugin MM = plugin.getServer().getPluginManager().getPlugin("MythicMobs");
            if (!(MM instanceof MythicPlugin)) {
                return;
            }
            String mmVersion = MM.getPluginMeta().getVersion();
            CustomPotion.plugin.getLogger().info("Found MythicMobs " + mmVersion);
            hasSupport = true;
        }catch (Exception e) {
            CustomPotion.plugin.getLogger().log(Level.WARNING, "Error enabling MythicMobs support", e);
            hasSupport = false;
        }
    }
}

