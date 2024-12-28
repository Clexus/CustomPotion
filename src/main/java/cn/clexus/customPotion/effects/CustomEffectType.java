package cn.clexus.customPotion.effects;

import org.bukkit.entity.LivingEntity;

public abstract class CustomEffectType {
    private final String id;
    private final String displayName;

    public static final CustomEffectType LIGHTNING = getEffectType("lightning");

    public CustomEffectType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        EffectRegistry.register(this);
    }

    private static CustomEffectType getEffectType(String id) {
        return EffectRegistry.getById(id);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 定义效果的具体应用逻辑，由具体子类实现
     */
    public abstract void applyEffect(LivingEntity entity, CustomEffect effect);

    /**
     * 默认持续时间（可选）
     */
    public int getDefaultDuration() {
        return 200;
    }
}

