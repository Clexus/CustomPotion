package cn.clexus.customPotion.managers;

import cn.clexus.customPotion.CustomPotion;
import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.StackingModes;
import cn.clexus.customPotion.events.CustomPotionAddEvent;
import cn.clexus.customPotion.events.CustomPotionApplyEvent;
import cn.clexus.customPotion.events.CustomPotionRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PotionManager {
    private static final Map<UUID, Map<CustomEffectType, List<CustomEffect>>> activeEffects = new ConcurrentHashMap<>();

    public static void addEffect(LivingEntity entity, CustomEffect effect, StackingModes stackingMode) {
        // 事件处理
        CustomPotionAddEvent event = new CustomPotionAddEvent(entity, effect, stackingMode);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        entity = event.getEntity();
        effect = event.getEffect();
        stackingMode = event.getMode();

        UUID uuid = entity.getUniqueId();
        // 初始化数据结构
        activeEffects.putIfAbsent(uuid, new ConcurrentHashMap<>());
        Map<CustomEffectType, List<CustomEffect>> entityEffects = activeEffects.get(uuid);

        CustomEffectType effectType = effect.getType();
        entityEffects.putIfAbsent(effectType, new CopyOnWriteArrayList<>());
        List<CustomEffect> effects = entityEffects.get(effectType);

        // 处理效果叠加
        boolean replaced = false;
        for (int i = 0; i < effects.size(); i++) {
            CustomEffect current = effects.get(i);
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
            if (replaced) break;
        }

        // 如果未替换现有效果，添加新效果
        if (!replaced) {
            effects.add(effect);
        }
    }

    public static boolean hasEffect(LivingEntity entity, CustomEffectType type) {
        if (entity == null) return false;

        Map<CustomEffectType, List<CustomEffect>> entityEffects = activeEffects.get(entity.getUniqueId());
        if (entityEffects == null) return false;

        List<CustomEffect> effects = entityEffects.get(type);
        return effects != null && !effects.isEmpty();
    }

    public static List<CustomEffect> getEffects(LivingEntity entity, CustomEffectType effectType) {
        if (entity == null) return Collections.emptyList();

        Map<CustomEffectType, List<CustomEffect>> entityEffects = activeEffects.get(entity.getUniqueId());
        if (entityEffects == null) return Collections.emptyList();

        List<CustomEffect> effects = entityEffects.get(effectType);
        return effects != null ? new ArrayList<>(effects) : Collections.emptyList();
    }

    public static List<CustomEffect> getAllEffects(LivingEntity entity) {
        if (entity == null) return Collections.emptyList();

        Map<CustomEffectType, List<CustomEffect>> entityEffects = activeEffects.get(entity.getUniqueId());
        if (entityEffects == null) return Collections.emptyList();

        List<CustomEffect> allEffects = new ArrayList<>();
        entityEffects.values().forEach(allEffects::addAll);
        return allEffects;
    }

    public static void startEffectProcessor(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, Map<CustomEffectType, List<CustomEffect>>>> iterator = activeEffects.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, Map<CustomEffectType, List<CustomEffect>>> entry = iterator.next();
                    LivingEntity entity = getLivingEntity(entry.getKey());

                    if (entity == null || entity.isDead()) {
                        iterator.remove(); // 移除不存在或已死亡实体的效果
                        continue;
                    }

                    Map<CustomEffectType, List<CustomEffect>> typeEffectsMap = entry.getValue();
                    boolean entityHasEffects = false;

                    // 处理每种效果类型
                    Iterator<Map.Entry<CustomEffectType, List<CustomEffect>>> typeIterator = typeEffectsMap.entrySet().iterator();
                    while (typeIterator.hasNext()) {
                        Map.Entry<CustomEffectType, List<CustomEffect>> typeEntry = typeIterator.next();
                        List<CustomEffect> effects = typeEntry.getValue();

                        // 处理效果
                        effects.removeIf(effect -> processEffect(entity, effect, effects));

                        if (effects.isEmpty()) {
                            typeIterator.remove(); // 移除空效果类型
                        } else {
                            entityHasEffects = true; // 标记实体仍有效果
                        }
                    }

                    if (!entityHasEffects) {
                        iterator.remove(); // 移除无效果实体
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1); // 每tick执行
    }

    private static boolean processEffect(LivingEntity entity, CustomEffect effect, List<CustomEffect> effects) {
        if (effect.isHidden()) return false;

        // 应用效果事件
        CustomPotionApplyEvent event = new CustomPotionApplyEvent(entity, effect);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        // 应用效果
        effect.apply(entity);

        // 检查效果持续时间
        if (!effect.tick()) { // 效果持续时间结束
            // 恢复隐藏的同类型效果
            for (CustomEffect hiddenEffect : effects) {
                if (hiddenEffect.isHidden() && hiddenEffect.getType().equals(effect.getType())) {
                    hiddenEffect.setHidden(false);
                    break;
                }
            }

            // 移除当前效果
            effect.setHidden(true);
            Bukkit.getScheduler().runTaskLater(CustomPotion.plugin, () -> effect.remove(entity), 1);
            return true; // 移除此效果
        }

        return false; // 保留效果
    }

    public static void clearEffects(UUID uuid, CustomEffectType effectType) {
        LivingEntity entity = getLivingEntity(uuid);
        if (entity == null) return;

        Map<CustomEffectType, List<CustomEffect>> entityEffects = activeEffects.get(uuid);
        if (entityEffects == null) return;

        List<CustomEffect> effects = entityEffects.get(effectType);
        if (effects != null) {
            removeEffects(entity, effects);

            if (effects.isEmpty()) {
                entityEffects.remove(effectType);

                if (entityEffects.isEmpty()) {
                    activeEffects.remove(uuid);
                }
            }
        }
    }

    public static void clearEffects(UUID uuid) {
        LivingEntity entity = getLivingEntity(uuid);
        if (entity == null) return;

        Map<CustomEffectType, List<CustomEffect>> entityEffects = activeEffects.get(uuid);
        if (entityEffects != null) {
            // 清理所有效果
            for (List<CustomEffect> effects : new ArrayList<>(entityEffects.values())) {
                removeEffects(entity, effects);
            }

            // 移除空记录
            entityEffects.values().removeIf(List::isEmpty);
            if (entityEffects.isEmpty()) {
                activeEffects.remove(uuid);
            }
        }
    }

    private static void removeEffects(LivingEntity entity, List<CustomEffect> effects) {
        if (entity == null || effects == null) return;

        effects.removeIf(effect -> {
            CustomPotionRemoveEvent event = new CustomPotionRemoveEvent(entity, effect);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;

            effect.remove(entity);
            return true;
        });
    }

    public static void clearEffects(LivingEntity entity, CustomEffectType effectType) {
        if (entity == null) return;
        clearEffects(entity.getUniqueId(), effectType);
    }

    public static void clearEffects(LivingEntity entity) {
        if (entity == null) return;
        clearEffects(entity.getUniqueId());
    }

    private static LivingEntity getLivingEntity(UUID uuid) {
        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
        if (entity == null && Bukkit.getPlayer(uuid) != null) {
            entity = Bukkit.getPlayer(uuid); // 处理玩家死亡情况
        }
        return entity;
    }

    public static void shutdown() {
        // 清理所有效果
        for (UUID uuid : new HashSet<>(activeEffects.keySet())) {
            clearEffects(uuid);
        }
        activeEffects.clear();
    }
}