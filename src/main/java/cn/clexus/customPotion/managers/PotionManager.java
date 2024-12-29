package cn.clexus.customPotion.managers;

import cn.clexus.customPotion.Events.CustomPotionAddEvent;
import cn.clexus.customPotion.Events.CustomPotionApplyEvent;
import cn.clexus.customPotion.Events.CustomPotionRemoveEvent;
import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.StackingModes;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PotionManager {
    private static final Map<UUID, List<CustomEffect>> activeEffects = new HashMap<>();
    public static void addEffect(LivingEntity entity, CustomEffect effect, StackingModes stackingMode) {
        CustomPotionAddEvent event = new CustomPotionAddEvent(entity,effect,stackingMode);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            entity = event.getEntity();
            effect = event.getEffect();
            stackingMode = event.getMode();
        }else return;
        UUID uuid = entity.getUniqueId();
        activeEffects.putIfAbsent(uuid, new ArrayList<>());
        List<CustomEffect> effects = activeEffects.get(uuid);

        boolean replaced = false;
        for (int i = 0; i < effects.size(); i++) {
            CustomEffect current = effects.get(i);
            if (current.getEffectType().equals(effect.getEffectType())) {
                switch (stackingMode) {
                    case NORMAL:
                        if (effect.getAmplifier() > current.getAmplifier()) {
                            current.setHidden(true);
                            effects.set(i, effect);
                            replaced = true;
                        } else if (effect.getDuration() > current.getDuration()) {
                            effect.setHidden(true);
                            effects.add(effect);
                            replaced = true;
                        }
                        break;
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
    public static List<CustomEffect> getEffects(LivingEntity entity, CustomEffectType effectType) {
        List<CustomEffect> effects = activeEffects.get(entity.getUniqueId());
        List<CustomEffect> matchingEffects = new ArrayList<>();

        if (effects != null) {
            for (CustomEffect effect : effects) {
                if (effect.getEffectType().equals(effectType)) {
                    matchingEffects.add(effect);
                }
            }
        }
        return matchingEffects;
    }

    public static List<CustomEffect> getAllEffects(LivingEntity entity) {
        return activeEffects.get(entity.getUniqueId());
    }

    public static void startEffectProcessor(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, List<CustomEffect>>> iterator = activeEffects.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, List<CustomEffect>> entry = iterator.next();
                    final LivingEntity[] entity = {(LivingEntity) Bukkit.getEntity(entry.getKey())};

                    if (entity[0] == null || entity[0].isDead()) {
                        iterator.remove(); // 实体不存在或死亡时移除效果
                        continue;
                    }

                    List<CustomEffect> effects = entry.getValue();

                    // 遍历效果并应用逻辑
                    effects.removeIf(effect -> {
                        if (entity[0].isDead()) {
                            effect.freeze();
                            return false;
                        }

                        if (!effect.isFrozen() && !effect.isHidden()) {
                            CustomPotionApplyEvent event = new CustomPotionApplyEvent(entity[0],effect);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                entity[0] = event.getEntity();
                                effect = event.getEffect();
                            }else return false;
                            effect.apply(entity[0]); // 应用当前效果逻辑
                            if (!effect.tick()) { // 持续时间耗尽
                                // 当前效果结束，检查是否有隐藏的同类型效果
                                for (CustomEffect hiddenEffect : effects) {
                                    if (hiddenEffect.isHidden() && hiddenEffect.getEffectType().equals(effect.getEffectType())) {
                                        hiddenEffect.setHidden(false); // 恢复隐藏效果
                                        break;
                                    }
                                }
                                return true; // 移除当前效果
                            }
                        }
                        return false; // 保留效果
                    });

                    if (effects.isEmpty()) {
                        iterator.remove(); // 如果实体无效果，则从 activeEffects 移除
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1); // 每 tick 执行
    }


    public static void clearEffects(UUID uuid, CustomEffectType effectType) {
        List<CustomEffect> effects = activeEffects.get(uuid);
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        if (effects != null) {
            effects.removeIf(effect -> {
                if (effect.getEffectType().equals(effectType)) {
                    CustomPotionRemoveEvent event = new CustomPotionRemoveEvent(entity,effect);
                    Bukkit.getPluginManager().callEvent(event);
                    if(event.isCancelled()) {
                        return false;
                    }
                    effect.remove(entity);
                    return true;
                }
                return false;
            });

            // 如果列表为空，则移除实体的效果记录
            if (effects.isEmpty()) {
                activeEffects.remove(uuid);
            }
        }
    }

    public static void clearEffects(UUID uuid) {
        List<CustomEffect> effects = activeEffects.get(uuid);
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);

        if (effects != null) {
            // 迭代移除未被取消的效果
            effects.removeIf(effect -> {
                CustomPotionRemoveEvent event = new CustomPotionRemoveEvent(entity, effect);
                Bukkit.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return false; // 如果事件被取消，则不移除效果
                }

                effect.remove(entity); // 调用效果的 remove 方法
                return true; // 移除效果
            });

            // 如果列表为空，则从 activeEffects 中移除实体的效果记录
            if (effects.isEmpty()) {
                activeEffects.remove(uuid);
            }
        }
    }

    public static void clearEffects(LivingEntity entity, CustomEffectType effectType) {
        clearEffects(entity.getUniqueId(), effectType);
    }

    public static void clearEffects(LivingEntity entity) {
        clearEffects(entity.getUniqueId());
    }

}
