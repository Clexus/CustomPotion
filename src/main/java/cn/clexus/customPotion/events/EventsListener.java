package cn.clexus.customPotion.events;

import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.EventsUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EventsListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity entity) {
            PotionManager.getAllEffects(entity).forEach(effect -> {
                if(effect.getType() instanceof DamageModifierEffectType damageModifierEffectType){
                    damageModifierEffectType.modifyDamage(event, effect);
                }
            });
        }
        if(event instanceof EntityDamageByEntityEvent event1) {
            if(EventsUtil.getDamager(event1)!=null){
                LivingEntity damager = EventsUtil.getDamager(event1);
                if(damager == event.getEntity()) return;
                PotionManager.getAllEffects(damager).forEach(effect -> {
                    if(effect.getType() instanceof DamageModifierEffectType damageModifierEffectType){
                        damageModifierEffectType.modifyDamage(event1, effect);
                    }
                });
            }
        }
    }
}
