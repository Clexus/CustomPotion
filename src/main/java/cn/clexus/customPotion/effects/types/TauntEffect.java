package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.hooks.mythicmobs.MythicMobsSupport;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TauntEffect extends CustomEffectType {
    private final Map<LivingEntity, LivingEntity> originalTargets = new ConcurrentHashMap<>();
    private final Map<LivingEntity, String> originalFactions = new ConcurrentHashMap<>();
    private final Map<Mob, LivingEntity> mobs = new ConcurrentHashMap<>();

    public TauntEffect() {
        super("taunt");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
        if (!(entity instanceof Mob mob)) return;

        int amplifier = effect.getAmplifier();
        originalTargets.putIfAbsent(entity, mob.getTarget());

        for (Entity nearbyEntity : entity.getNearbyEntities(amplifier, amplifier, amplifier)) {
            if (nearbyEntity instanceof Mob nearbyMob) {
                mobs.putIfAbsent(nearbyMob, nearbyMob.getTarget());
                nearbyMob.setTarget(entity);
            }
        }

        if (MythicMobsSupport.hasSupport()) {
            MobExecutor mobManager = MythicBukkit.inst().getMobManager();
            mobManager.getActiveMob(entity.getUniqueId()).ifPresent(activeMob -> {
                originalFactions.putIfAbsent(entity, activeMob.getFaction());
                activeMob.setFaction(entity.getUniqueId().toString());
            });
        }
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {
        if (entity instanceof Mob mob) {
            LivingEntity originalTarget = originalTargets.remove(entity);
            if (originalTarget != null) {
                mob.setTarget(originalTarget);
            }
        }

        mobs.entrySet().removeIf(entry -> {
            Mob mob = entry.getKey();
            LivingEntity originalTarget = entry.getValue();
            if (mob.isValid()) {
                mob.setTarget(originalTarget);
            }
            return true;
        });

        if (MythicMobsSupport.hasSupport()) {
            MobExecutor mobManager = MythicBukkit.inst().getMobManager();
            mobManager.getActiveMob(entity.getUniqueId()).ifPresent(activeMob -> {
                String originalFaction = originalFactions.remove(entity);
                if (originalFaction != null) {
                    activeMob.setFaction(originalFaction);
                }
            });
        }
    }
}
