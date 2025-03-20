package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.utils.EventsUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class MoonshineEffect extends CustomEffectType implements DamageModifierEffectType {
    public MoonshineEffect() {
        super("moonshine");
    }
    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {
    }

    @Override
    public void modifyDamage(EntityDamageEvent event, CustomEffect effect) {
        if(EventsUtil.getDamager(event)!=null){
            LivingEntity damager = EventsUtil.getDamager(event);
            if(EventsUtil.damagerHasEffect(event, CustomEffectType.MOONSHINE) && ( damager.getWorld().getTime()<1000 || damager.getWorld().getTime()>13000 )){
                event.setDamage(event.getFinalDamage()+effect.getAmplifier());
            }
        }
    }
}
