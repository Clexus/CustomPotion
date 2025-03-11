package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.effects.StackingModes;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.EventsUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class VigorEffect extends CustomEffectType implements DamageModifierEffectType {
    public VigorEffect() {
        super("vigor", "活力");
    }

    @Override
    public void modifyDamage(EntityDamageEvent event, CustomEffect effect) {
        if(EventsUtil.getDamager(event)!=null){
            LivingEntity damager = EventsUtil.getDamager(event);
            if(EventsUtil.damagerHasEffect(event, CustomEffectType.VIGOR)){
                event.setDamage(event.getFinalDamage()+effect.getAmplifier());
                PotionManager.addEffect(damager, new CustomEffect(CustomEffectType.VIGOR, effect.getDuration(), 1, null), StackingModes.ADD_AMPLIFIER);
            }
        }
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {
    }
}
