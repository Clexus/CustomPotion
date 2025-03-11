package cn.clexus.customPotion.utils;

import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.managers.PotionManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EventsUtil {
    public static boolean damagerHasEffect(EntityDamageEvent event, CustomEffectType type) {
        LivingEntity damager = getDamager(event);
        if(damager != null) {
            return PotionManager.hasEffect(damager, type);
        }
        return false;
    }
    public static LivingEntity getDamager(EntityDamageEvent event) {
        LivingEntity damager = null;
        if(event instanceof EntityDamageByEntityEvent event1) {
            if(event1.getDamageSource().getCausingEntity() == null && event1.getDamageSource().getDirectEntity() instanceof LivingEntity) {
                damager = (LivingEntity) event1.getDamageSource().getDirectEntity();
            } else if (event1.getDamageSource().getCausingEntity() != null && event1.getDamageSource().getCausingEntity() instanceof LivingEntity) {
                damager = (LivingEntity) event1.getDamageSource().getCausingEntity();
            }
        }
        return damager;
    }
}
