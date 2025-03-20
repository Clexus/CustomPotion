package cn.clexus.customPotion.hooks.placeholderapi;

import cn.clexus.customPotion.CustomPotion;
import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.managers.PotionManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    public static CustomPotion plugin;

    public PlaceholderAPIExpansion(CustomPotion plugin) {
        PlaceholderAPIExpansion.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "Clexus";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "custompotion";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] param = params.split("_");
        if (param.length>=3) {
            LivingEntity p;
            String type = param[param.length-1];
            String id = param[param.length-2];
            String name = String.join("_", Arrays.copyOfRange(param, 0, param.length - 2));
            if(name.equals("me")){
                p = player.isOnline() ? player.getPlayer() : null;
            }else{
                p = Bukkit.getPlayer(name);
                if(p==null){
                    try{
                        p = (LivingEntity) Bukkit.getEntity(UUID.fromString(name));
                    }catch(IllegalArgumentException ignored){
                    }
                }
            }
            if(p!=null){
                List<CustomEffect> effectList = PotionManager.getAllEffects(p);
                for (CustomEffect effect : effectList) {
                    if (effect.isHidden()||effect.isFrozen()) continue;
                    if (effect.getType().getId().equals(id) || effect.getType().getDisplayName().equals(id)) {
                        switch (type) {
                            case "duration":
                                return String.valueOf(effect.getDuration());
                            case "level":
                            case "amplifier":
                                return String.valueOf(effect.getAmplifier());
                            case "displayname":
                            case "display":
                                return effect.getType().getDisplayName();
                            case "source":
                                return effect.getSource() == null ? null : effect.getSource().getName();
                            case "sourceuuid":
                                return effect.getSource() == null ? null : effect.getSource().getUniqueId().toString();
                        }
                    }
                }
            }
        }
        return "";
    }
}
