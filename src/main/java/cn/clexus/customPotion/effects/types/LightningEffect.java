package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.utils.DamageUtil;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;

public class LightningEffect extends CustomEffectType {
    public LightningEffect() {
        super("lightning");
    }
    @Override
    public void applyEffect(LivingEntity target, CustomEffect effect) {
        LivingEntity source = effect.getSource();
        DamageUtil.damage(effect.getAmplifier(), DamageType.LIGHTNING_BOLT,target,source,effect,null,target1->target1.getWorld().strikeLightningEffect(target.getLocation()),null);
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {
    }
}
