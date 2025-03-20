package cn.clexus.customPotion.effects;

import cn.clexus.customPotion.utils.I18n;
import org.bukkit.entity.LivingEntity;

public abstract class CustomEffectType {
    private final String id;
    private final String displayName;

    public static CustomEffectType LIGHTNING;
    public static CustomEffectType POISON;
    public static CustomEffectType DEATH;
    public static CustomEffectType REVEALING;
    public static CustomEffectType REFLECTION;
    public static CustomEffectType VIGOR;
    public static CustomEffectType SUNSHINE;
    public static CustomEffectType MOONSHINE;
    public static CustomEffectType SPIN;
    public static CustomEffectType ANGER;
    public static CustomEffectType REVERSE;


    public static void initializeStaticFields() {
        DEATH = EffectRegistry.getById("death");
        POISON = EffectRegistry.getById("poison");
        LIGHTNING = EffectRegistry.getById("lightning");
        REVEALING = EffectRegistry.getById("revealing");
        REFLECTION = EffectRegistry.getById("reflection");
        VIGOR = EffectRegistry.getById("vigor");
        SUNSHINE = EffectRegistry.getById("sunshine");
        MOONSHINE = EffectRegistry.getById("moonshine");
        SPIN = EffectRegistry.getById("spin");
        ANGER = EffectRegistry.getById("anger");
        REVERSE = EffectRegistry.getById("reverse");
    }

    public CustomEffectType(String id) {
        this.id = id;
        this.displayName = I18n.getEffectDisplayName(id);
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

