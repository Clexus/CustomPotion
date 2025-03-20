package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.EventsUtil;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class ReflectionEffect extends CustomEffectType implements DamageModifierEffectType {
    public ReflectionEffect() {
        super("reflection");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {
    }

    @Override
    public void modifyDamage(EntityDamageEvent event, CustomEffect effect) {
        if(event.getEntity() instanceof LivingEntity victim){
            if(PotionManager.hasEffect(victim, CustomEffectType.REFLECTION)){
                if(EventsUtil.getDamager(event)!=null){
                    LivingEntity damager = EventsUtil.getDamager(event);
                    damager.damage(effect.getAmplifier(), DamageSource.builder(DamageType.THORNS).withDirectEntity(victim).build());
                }
            }
        }
    }
}
