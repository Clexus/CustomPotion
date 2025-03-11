package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.utils.EventsUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class RevealingEffect extends CustomEffectType implements DamageModifierEffectType {
    public RevealingEffect() {
        super("revealing","破隐");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {
    }

    @Override
    public void modifyDamage(EntityDamageEvent event, CustomEffect effect) {
        if(event.getEntity() instanceof LivingEntity livingEntity && EventsUtil.damagerHasEffect(event, CustomEffectType.REVEALING)){
            if(livingEntity.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                event.setDamage(event.getFinalDamage()*(1+effect.getAmplifier()/100.0));
            }
        }
    }
}
