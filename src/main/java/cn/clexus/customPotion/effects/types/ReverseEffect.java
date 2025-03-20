package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.managers.PotionManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ReverseEffect extends CustomEffectType implements DamageModifierEffectType {
    public ReverseEffect() {
        super("reverse");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {

    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {

    }

    @Override
    public void modifyDamage(EntityDamageEvent event, CustomEffect effect) {
        if(!(event instanceof EntityDamageByEntityEvent)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if(PotionManager.hasEffect(entity, CustomEffectType.REVERSE)) {
            double finalDamage = event.getFinalDamage() - effect.getAmplifier();
            event.setDamage(finalDamage < 0 ? 0 : finalDamage);
            if(finalDamage < 0){
                entity.heal(event.getFinalDamage());
            }else {
                entity.heal(effect.getAmplifier());
            }
        }
    }
}
