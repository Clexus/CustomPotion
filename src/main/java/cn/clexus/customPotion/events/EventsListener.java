package cn.clexus.customPotion.events;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.hooks.mythicmobs.CustomEffectMechanic;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.EventsUtil;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class EventsListener implements Listener {
    MythicBukkit mythicBukkit = MythicBukkit.inst();
    MobExecutor mobManager = mythicBukkit.getMobManager();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof LivingEntity entity) {
            PotionManager.getAllEffects(entity).forEach(effect -> {
                Bukkit.broadcastMessage(effect.getType().getId());
                if(effect.getType() instanceof DamageModifierEffectType damageModifierEffectType){
                    damageModifierEffectType.modifyDamage(event, effect);
                }
            });
        }
        if(event instanceof EntityDamageByEntityEvent event1) {
            if(EventsUtil.getDamager(event1)!=null){
                LivingEntity damager = EventsUtil.getDamager(event1);
                PotionManager.getAllEffects(damager).forEach(effect -> {
                    if(effect.getType() instanceof DamageModifierEffectType damageModifierEffectType){
                        damageModifierEffectType.modifyDamage(event1, effect);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event)	{
        if(event.getMechanicName().equalsIgnoreCase("CustomEffect")||event.getMechanicName().equalsIgnoreCase("ce")){
            event.register(new CustomEffectMechanic(event.getContainer().getManager(),event.getContainer().getFile(),event.getConfig().getLine(), event.getConfig()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMythicMobPotionAdd(CustomPotionAddEvent event) {
        Entity entity = event.getEntity();
        CustomEffect effect = event.getEffect();
        String effectID = event.getEffect().getType().getId();
        if(mobManager.isMythicMob(entity)) {
            ActiveMob mob = mobManager.getActiveMob(entity.getUniqueId()).orElse(null);
            if(mob != null) {
                MythicConfig section = mob.getType().getConfig().getNestedConfig("CustomEffects");
                if(section != null) {
                    List<String> immuneList = section.getStringList("Immune");
                    if(immuneList != null) {
                        for (String type : immuneList) {
                            if(type.equalsIgnoreCase(effectID)) {
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                    List<String> multiplierList = section.getStringList("Multiplier");
                    if(multiplierList != null) {
                        for (String multiplier : multiplierList) {
                            String[] parts = multiplier.split(" ");
                            if (parts.length == 3) {
                                String effectType = parts[0];
                                int durationMultiplier = Math.max(Integer.parseInt(parts[1]), 0);
                                int levelMultiplier = Math.max(Integer.parseInt(parts[2]),0);

                                if (effectType.equalsIgnoreCase(effectID)) {
                                    effect.setDuration(durationMultiplier * effect.getDuration());
                                    effect.setAmplifier(levelMultiplier * effect.getAmplifier());
                                    event.setEffect(effect);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
