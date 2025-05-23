package cn.clexus.customPotion.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomEffect {
    private final CustomEffectType effectType;
    private int duration;
    private int amplifier;
    private boolean isHidden;
    private LivingEntity source;

    public CustomEffect(CustomEffectType effectType, int duration, int amplifier, @Nullable LivingEntity source) {
        this.effectType = effectType;
        this.duration = duration;
        this.amplifier = amplifier;
        this.source = source;
    }

    public CustomEffect getEffect() {
        return CustomEffect.this;
    }

    public CustomEffectType getType() {
        return effectType;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    @Nullable
    public LivingEntity getSource() {
        return source;
    }

    public void setSource(LivingEntity source) {
        this.source = source;
    }

    /**
     * 每tick调用，返回是否仍然有效
     */
    public boolean tick() {
        duration--;
        return duration > 0 && amplifier > 0;
    }

    /**
     * 应用效果
     */
    public void apply(LivingEntity entity) {
        if (!isHidden()) {
            effectType.applyEffect(entity, getEffect());
        }
    }

    public void remove(LivingEntity entity) {
        effectType.onRemove(entity, getEffect());
    }

    @Override
    public String toString() {
        return "CustomEffect{" +
                "effectType=" + effectType.getId() +
                ",duration=" + duration +
                ",amplifier=" + amplifier +
                ",isHidden=" + isHidden +
                ",source=" + (source == null ? "null" : source.getUniqueId().toString()) +
                '}';
    }

    public static CustomEffect fromString(String string) {
        // 去除开头的 "CustomEffect{" 和结尾的 "}"
        string = string.substring("CustomEffect{".length(), string.length() - 1);

        // 创建一个临时存储解析值的映射
        Map<String, String> values = new HashMap<>();

        // 解析属性对
        String[] pairs = string.split(",");
        for (String pair : pairs) {
            int equalsPos = pair.indexOf('=');
            if (equalsPos > 0) {
                String key = pair.substring(0, equalsPos).trim();
                String value = pair.substring(equalsPos + 1).trim();
                values.put(key, value);
            }
        }

        String effectTypeId = values.get("effectType");
        int duration = Integer.parseInt(values.getOrDefault("duration", "0"));
        int amplifier = Integer.parseInt(values.getOrDefault("amplifier", "0"));
        boolean isHidden = Boolean.parseBoolean(values.getOrDefault("isHidden", "false"));

        // 获取效果类型（需要从effectType字符串中提取ID）
        CustomEffectType effectType = EffectRegistry.getById(effectTypeId);
        if (effectType == null) {
            throw new IllegalArgumentException("Unknown effect type: " + effectTypeId);
        }

        // 创建效果对象
        CustomEffect effect = new CustomEffect(effectType, duration, amplifier, null);
        effect.setHidden(isHidden);

        // 处理来源实体
        String sourceValue = values.get("source");
        if (sourceValue != null && !sourceValue.equals("null")) {
            try {
                UUID sourceUuid = UUID.fromString(sourceValue);
                Entity entity = Bukkit.getEntity(sourceUuid);
                if (entity instanceof LivingEntity) {
                    effect.setSource((LivingEntity) entity);
                }
            } catch (IllegalArgumentException ignored) {
                // UUID格式不正确，忽略
            }
        }

        return effect;
    }
}
