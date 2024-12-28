package cn.clexus.customPotion.managers;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.StackingModes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PotionManager {
    private static final Map<UUID, List<CustomEffect>> activeEffects = new HashMap<>();
    public static void addEffect(Entity entity, CustomEffect effect, StackingModes stackingMode) {
        UUID uuid = entity.getUniqueId();
        activeEffects.putIfAbsent(uuid, new ArrayList<>());
        List<CustomEffect> effects = activeEffects.get(uuid);

        boolean replaced = false;
        for (int i = 0; i < effects.size(); i++) {
            CustomEffect current = effects.get(i);
            if (current.getEffectType().equals(effect.getEffectType())) {
                switch (stackingMode) {
                    case REPLACE:
                        effects.set(i, effect);
                        replaced = true;
                        break;
                    case ADD_ALL:
                        current.setDuration(current.getDuration() + effect.getDuration());
                        current.setAmplifier(Math.max(current.getAmplifier(), effect.getAmplifier()));
                        replaced = true;
                        break;
                    case ADD_TIME:
                        current.setDuration(current.getDuration() + effect.getDuration());
                        replaced = true;
                        break;
                    case ADD_AMPLIFIER:
                        current.setAmplifier(current.getAmplifier() + effect.getAmplifier());
                        replaced = true;
                        break;
                }
                break;
            }
        }

        if (!replaced) {
            effects.add(effect);
        }
    }
    public static CustomEffect getEffect(LivingEntity entity, CustomEffectType effectType) {
        List<CustomEffect> effects = activeEffects.get(entity.getUniqueId());
        if (effects != null) {
            for (CustomEffect effect : effects) {
                if (effect.getEffectType().equals(effectType)) {
                    return effect;
                }
            }
        }
        return null;
    }

    public static List<CustomEffect> getEffects(LivingEntity entity) {
        return activeEffects.get(entity.getUniqueId());
    }

    public static void startEffectProcessor(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, List<CustomEffect>>> iterator = activeEffects.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, List<CustomEffect>> entry = iterator.next();
                    LivingEntity entity = (LivingEntity) Bukkit.getEntity(entry.getKey());

                    if (entity == null) {
                        iterator.remove();
                        continue;
                    }

                    List<CustomEffect> effects = entry.getValue();
                    effects.removeIf(effect -> {
                        if(entity.isDead()){
                            effect.freeze();
                            return false;
                        }
                        if (!effect.isFrozen()) {
                            effect.apply(entity);
                            return !effect.tick();
                        }
                        return false;
                    });

                    if (effects.isEmpty()) {
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1); // 每 tick 执行
    }

    public static void clearEffects(UUID uuid, CustomEffectType effectType) {
        List<CustomEffect> effects = activeEffects.get(uuid);
        if (effects != null) {
            effects.removeIf(effect -> effect.getEffectType().equals(effectType));
            if (effects.isEmpty()) {
                activeEffects.remove(uuid);
            }
        }
    }

    public static void clearEffects(UUID uuid) {
        activeEffects.remove(uuid);
    }

    public static void clearEffects(LivingEntity entity, CustomEffectType effectType) {
        clearEffects(entity.getUniqueId(), effectType);
    }

    public static void clearEffects(LivingEntity entity) {
        clearEffects(entity.getUniqueId());
    }

    public static void clearAllEffects() {
        activeEffects.clear();
    }
}
