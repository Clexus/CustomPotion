package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import org.bukkit.entity.LivingEntity;

public class SpinEffect extends CustomEffectType {
    public SpinEffect() {
        super("spin");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
        entity.setRotation(entity.getYaw() + effect.getAmplifier(), entity.getPitch());
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {

    }
}
