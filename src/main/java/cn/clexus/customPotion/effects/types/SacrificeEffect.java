package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.DamageModifierEffectType;
import cn.clexus.customPotion.managers.PotionManager;
import cn.clexus.customPotion.utils.DamageUtil;
import cn.clexus.customPotion.utils.EventsUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class SacrificeEffect extends CustomEffectType implements DamageModifierEffectType {
    public SacrificeEffect() {
        super("sacrifice");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
        DamageUtil.damage(effect.getAmplifier(), DamageType.GENERIC_KILL, entity, entity, effect);
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {

    }

    @Override
    public void modifyDamage(EntityDamageEvent event, CustomEffect effect) {
        LivingEntity damager = EventsUtil.getDamager(event);
        if (PotionManager.hasEffect(damager, CustomEffectType.SACRIFICE)) {
            event.setDamage(event.getFinalDamage() + effect.getAmplifier() * Math.round(damager.getAttribute(Attribute.MAX_HEALTH).getValue() / damager.getHealth()));
        }
    }
}
