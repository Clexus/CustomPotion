package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.utils.DamageUtil;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;

public class CombustEffect extends CustomEffectType {
    public CombustEffect() {
        super("combust");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
        DamageUtil.damage(effect.getAmplifier(), DamageType.ON_FIRE, entity, effect.getSource(), effect, effect1 -> effect1.setAmplifier((int) Math.ceil(effect1.getAmplifier() + effect.getAmplifier() * Math.log10(effect.getAmplifier() + 1))), entity1 -> entity1.setVisualFire(true), null);
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {
        entity.setVisualFire(false);
    }
}
