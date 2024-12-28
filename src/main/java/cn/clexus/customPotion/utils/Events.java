package cn.clexus.customPotion.utils;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.types.LightningEffect;
import cn.clexus.customPotion.managers.PotionManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class Events implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<CustomEffect> effects = PotionManager.getEffects(player);
        Component message = null;
        Component victim = player.name().append(Component.text("死于"));
        for (CustomEffect effect : effects) {
            if(effect.getEffectType()==CustomEffectType.LIGHTNING){
                Bukkit.broadcastMessage("a");
            }else if(effect.getEffectType() instanceof LightningEffect){
                Bukkit.broadcastMessage("b");
            }else Bukkit.broadcastMessage("c");
            Component source = effect.getSource()!=null ? effect.getSource().name().append(Component.text("的")) : null;
            if(effect.getEffectType()==CustomEffectType.LIGHTNING){
                message = victim;
                if(source!=null) {
                    message = message.append(source);
                }
                message = message.append(Component.text("雷霆之威"));
            }
            effect.unfreeze();
        }
        if(message!=null){
            event.deathMessage(message);
        }
    }
}
