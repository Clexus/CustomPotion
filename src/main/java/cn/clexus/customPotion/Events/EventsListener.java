package cn.clexus.customPotion.Events;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.managers.PotionManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class EventsListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<CustomEffect> effects = PotionManager.getAllEffects(player);
        if(effects==null){
            return;
        }
        Component message = null;
        Component victim = player.name().append(Component.text("死于"));
        for (CustomEffect effect : effects) {
            if(effect.isHidden()) continue;
            Component source = effect.getSource()!=null ? effect.getSource().name().append(Component.text("的")) : null;
            if(effect.getEffectType()==CustomEffectType.LIGHTNING){
                message = victim;
                if(source!=null) {
                    message = message.append(source);
                }
                message = message.append(Component.text("雷霆之威"));
            }
            if(effect.getEffectType()==CustomEffectType.POISON){
                message = victim;
                if(source!=null) {
                    message = message.append(source);
                }
                message = message.append(Component.text("剧毒"));
            }
            effect.unfreeze();
        }
        if(message!=null){
            event.deathMessage(message);
        }
    }
}
