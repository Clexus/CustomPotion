package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.effects.StackingModes;
import cn.clexus.customPotion.managers.PotionManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class AngerEffect extends CustomEffectType implements DamageModifierEffectType {

    public AngerEffect() {
        super("anger");
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
        if(PotionManager.hasEffect((LivingEntity) event.getEntity(), CustomEffectType.ANGER)){
            PotionManager.addEffect(((LivingEntity) event.getEntity()), new CustomEffect(CustomEffectType.VIGOR, 200, effect.getAmplifier(), null), StackingModes.ADD_AMPLIFIER);
        }
    }
}
