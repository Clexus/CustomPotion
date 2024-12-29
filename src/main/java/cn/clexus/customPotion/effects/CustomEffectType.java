package cn.clexus.customPotion.effects;

import org.bukkit.entity.LivingEntity;

public abstract class CustomEffectType {
    private final String id;
    private final String displayName;

    public static CustomEffectType LIGHTNING;
    public static CustomEffectType POISON;
    public static void initializeStaticFields() {
        POISON = EffectRegistry.getById("poison");
        LIGHTNING = EffectRegistry.getById("lightning");
    }

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

    public abstract void onRemove(LivingEntity entity, CustomEffect effect);
}

