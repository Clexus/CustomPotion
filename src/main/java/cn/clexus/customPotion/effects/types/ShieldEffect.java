package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.managers.PotionManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class ShieldEffect extends CustomEffectType implements DamageModifierEffectType {
    public ShieldEffect() {
        super("shield");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {

    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {

    }

    @Override
    public void modifyDamage(EntityDamageEvent event, CustomEffect effect) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (!PotionManager.hasEffect(livingEntity, CustomEffectType.SHIELD)) return;
            double damage = event.getFinalDamage() - effect.getAmplifier() < 0 ? 0 : event.getFinalDamage() - effect.getAmplifier();
            event.setDamage(damage);
            effect.setAmplifier(effect.getAmplifier() - event.getFinalDamage() < 0 ? 0 : (int) Math.round(effect.getAmplifier() - event.getFinalDamage()));
        }
    }
}
