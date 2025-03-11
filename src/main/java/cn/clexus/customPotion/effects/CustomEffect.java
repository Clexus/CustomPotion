package cn.clexus.customPotion.effects;

import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

public class CustomEffect {
    private boolean isFrozen = false;
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

    public void freeze() {
        this.isFrozen = true;
    }

    public void unfreeze() {
        this.isFrozen = false;
        this.duration = 0;
    }

    public boolean isFrozen() {
        return isFrozen;
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
        if(!isFrozen) {
            duration--;
            return duration > 0 && amplifier > 0;
        }else return false;
    }

    /**
     * 应用效果
     */
    public void apply(LivingEntity entity) {
        if(!isFrozen()&&!isHidden()) {
            effectType.applyEffect(entity, getEffect());
        }
    }
    public void remove(LivingEntity entity) {
        effectType.onRemove(entity, getEffect());
    }
}
