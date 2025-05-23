package cn.clexus.customPotion.events;

import cn.clexus.customPotion.CustomPotion;
import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.effects.StackingModes;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.EventsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

import static cn.clexus.customPotion.CustomPotion.plugin;

public class EventsListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            PotionManager.getAllEffects(entity).forEach(effect -> {
                if (effect.getType() instanceof DamageModifierEffectType damageModifierEffectType) {
                    damageModifierEffectType.modifyDamage(event, effect);
                }
            });
        }
        if (event instanceof EntityDamageByEntityEvent event1) {
            if (EventsUtil.getDamager(event1) != null) {
                LivingEntity damager = EventsUtil.getDamager(event1);
                if (damager == event.getEntity()) return;
                PotionManager.getAllEffects(damager).forEach(effect -> {
                    if (effect.getType() instanceof DamageModifierEffectType damageModifierEffectType) {
                        damageModifierEffectType.modifyDamage(event1, effect);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PotionManager.clearEffects(player);
    }

    // 在玩家事件监听器中使用
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 异步加载效果
        CustomPotion.databaseManager.loadPlayerEffects(player.getUniqueId()).thenAccept(effects -> {
            if (effects.isEmpty()) return;

            // 在主线程应用效果
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (CustomEffect effect : effects) {
                    PotionManager.addEffect(player, effect, StackingModes.REPLACE);
                }
            });
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // 获取所有效果
        List<CustomEffect> effects = PotionManager.getAllEffects(player);

        // 异步保存
        if (!effects.isEmpty()) {
            CustomPotion.databaseManager.savePlayerEffects(player.getUniqueId(), effects);
        }
    }
}
